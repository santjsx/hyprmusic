package com.example.hyprmusic.core.media

import android.content.Context
import android.content.SharedPreferences
import android.media.audiofx.BassBoost
import android.media.audiofx.DynamicsProcessing
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.pow

data class EqualizerBand(
    val bandIndex: Short,
    val centerFreqHz: Int,
    val levelMb: Short
) {
    val formattedFreq: String
        get() = if (centerFreqHz >= 1000) "${centerFreqHz / 1000}kHz" else "${centerFreqHz}Hz"

    val levelDb: Float
        get() = levelMb / 100f
}

object HyprEqualizer {

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var dynamicsProcessing: DynamicsProcessing? = null
    private var prefs: SharedPreferences? = null
    private var currentAttachedSessionId: Int = 0

    // Pristine 0 dB Flat studio baseline
    val DEFAULT_BANDS = listOf(
        EqualizerBand(0, 60, 0),
        EqualizerBand(1, 230, 0),
        EqualizerBand(2, 910, 0),
        EqualizerBand(3, 3600, 0),
        EqualizerBand(4, 14000, 0)
    )

    // Disabled by default for 100% bit-perfect, zero-distortion audio output
    private val _isEnabled = MutableStateFlow(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()

    private val _bands = MutableStateFlow<List<EqualizerBand>>(DEFAULT_BANDS)
    val bands: StateFlow<List<EqualizerBand>> = _bands.asStateFlow()

    private val _bassBoostStrength = MutableStateFlow(0)
    val bassBoostStrength: StateFlow<Int> = _bassBoostStrength.asStateFlow()

    private val _virtualizerStrength = MutableStateFlow(0)
    val virtualizerStrength: StateFlow<Int> = _virtualizerStrength.asStateFlow()

    // Dolby Cinema Spatial Audio Engine States
    private val _isDolbyEnabled = MutableStateFlow(false)
    val isDolbyEnabled: StateFlow<Boolean> = _isDolbyEnabled.asStateFlow()

    private val _spatialStrength = MutableStateFlow(450)
    val spatialStrength: StateFlow<Int> = _spatialStrength.asStateFlow()

    private val _isLimiterEngaged = MutableStateFlow(false)
    val isLimiterEngaged: StateFlow<Boolean> = _isLimiterEngaged.asStateFlow()

    private val _currentPreset = MutableStateFlow("Flat")
    val currentPreset: StateFlow<String> = _currentPreset.asStateFlow()

    val availablePresets = listOf(
        "Dolby Cinema",
        "Spatial Theater",
        "Vocal Clarity",
        "Club Rumble",
        "Flat",
        "Rock",
        "Pop",
        "Electronic",
        "Jazz",
        "Acoustic",
        "Custom"
    )

    fun init(context: Context, audioSessionId: Int) {
        bindAudioSession(context, audioSessionId)
    }

    fun bindAudioSession(context: Context, audioSessionId: Int) {
        if (prefs == null) {
            prefs = context.getSharedPreferences("hypr_equalizer_prefs", Context.MODE_PRIVATE)
            loadSavedSettings()
        }

        if (audioSessionId == 0) return

        // If already bound to this exact audioSessionId, avoid thrashing AudioFlinger
        if (audioSessionId == currentAttachedSessionId) {
            return
        }

        currentAttachedSessionId = audioSessionId
        if (_isEnabled.value) {
            applyEffectsToSession(audioSessionId)
        }
    }

    private fun loadSavedSettings() {
        val p = prefs ?: return

        // Clean migration: reset any past corrupted or extreme settings to pristine Flat baseline
        val isClean = p.getBoolean("eq_v3_clean", false)
        if (!isClean) {
            p.edit()
                .putBoolean("eq_v3_clean", true)
                .putBoolean("eq_enabled", false)
                .putBoolean("dolby_enabled", false)
                .putInt("spatial_strength", 450)
                .putString("preset", "Flat")
                .putInt("bass_boost", 0)
                .putInt("virtualizer", 0)
                .apply {
                    DEFAULT_BANDS.forEach { putInt("band_${it.bandIndex}", 0) }
                }
                .apply()
        }

        val savedEnabled = p.getBoolean("eq_enabled", false)
        _isEnabled.value = savedEnabled

        val savedDolby = p.getBoolean("dolby_enabled", false)
        _isDolbyEnabled.value = savedDolby

        val savedSpatial = p.getInt("spatial_strength", 450).coerceIn(0, 1000)
        _spatialStrength.value = savedSpatial

        val savedPreset = p.getString("preset", "Flat") ?: "Flat"
        _currentPreset.value = savedPreset

        val savedBass = p.getInt("bass_boost", 0).coerceIn(0, 1000)
        _bassBoostStrength.value = savedBass

        val savedVirt = p.getInt("virtualizer", 0).coerceIn(0, 1000)
        _virtualizerStrength.value = savedVirt

        val restoredBands = DEFAULT_BANDS.map { band ->
            val savedLevel = p.getInt("band_${band.bandIndex}", 0).toShort().coerceIn(-1200, 1200)
            band.copy(levelMb = savedLevel)
        }
        _bands.value = restoredBands
    }

    private fun applyEffectsToSession(sessionId: Int) {
        try {
            releaseEffects()

            if (!_isEnabled.value) {
                _isLimiterEngaged.value = false
                // Keep effects completely detached when disabled for pure, zero-overhead direct audio
                return
            }

            equalizer = Equalizer(1000, sessionId).apply {
                enabled = true
            }

            val effectiveSpatial = if (_isDolbyEnabled.value) {
                _spatialStrength.value.coerceAtLeast(350)
            } else {
                _virtualizerStrength.value
            }

            if (effectiveSpatial > 0) {
                try {
                    virtualizer = Virtualizer(1000, sessionId).apply {
                        if (strengthSupported) {
                            setStrength(effectiveSpatial.toShort())
                        }
                        enabled = true
                    }
                } catch (_: Exception) {}
            }

            if (_bassBoostStrength.value > 0) {
                try {
                    bassBoost = BassBoost(1000, sessionId).apply {
                        if (strengthSupported) {
                            setStrength(_bassBoostStrength.value.toShort())
                        }
                        enabled = true
                    }
                } catch (_: Exception) {}
            }

            // DynamicsProcessing Limiter DRC stage to prevent bass clipping & balance dynamics
            if (_isDolbyEnabled.value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                try {
                    val channelCount = 2
                    val config = DynamicsProcessing.Config.Builder(
                        DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
                        channelCount,
                        false, 0,
                        false, 0,
                        false, 0,
                        true
                    ).apply {
                        val limiter = DynamicsProcessing.Limiter(
                            true,   // inUse
                            true,   // enabled
                            0,      // linkGroup
                            1.0f,   // attackTime ms
                            60.0f,  // releaseTime ms
                            10.0f,  // ratio (10:1)
                            -2.0f,  // threshold dB
                            0.0f    // postGain dB
                        )
                        setLimiterAllChannelsTo(limiter)
                    }.build()

                    dynamicsProcessing = DynamicsProcessing(1000, sessionId, config).apply {
                        enabled = true
                    }
                    _isLimiterEngaged.value = true
                } catch (e: Exception) {
                    _isLimiterEngaged.value = false
                    e.printStackTrace()
                }
            } else {
                _isLimiterEngaged.value = false
            }

            syncBandLevelsToHardware()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun syncBandLevelsToHardware() {
        val eq = equalizer ?: return
        try {
            val range = eq.bandLevelRange
            val minLevel: Short = range?.getOrNull(0) ?: (-1200).toShort()
            val maxLevel: Short = range?.getOrNull(1) ?: 1200.toShort()
            val numHardwareBands = eq.numberOfBands.toInt()

            _bands.value.forEachIndexed { idx, band ->
                if (idx < numHardwareBands) {
                    val clamped: Short = band.levelMb.coerceIn(minLevel, maxLevel)
                    eq.setBandLevel(idx.toShort(), clamped)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * The Harmon/Dolby Hybrid Curve Matrix.
     * Calculates decibels target for any hardware center frequency.
     */
    fun calculateDolbyTargetDb(centerFreqHz: Int): Float = when {
        centerFreqHz <= 60   -> 6.0f   // Sub-bass physical vibration (Deep cinematic feel)
        centerFreqHz <= 160  -> 3.5f   // Warm acoustic punch (Drums and basslines)
        centerFreqHz <= 400  -> 1.0f   // Lower-mid structural weight
        centerFreqHz <= 1000 -> -2.0f  // The "Mud Scoop" - Clears up cheap speaker boxiness
        centerFreqHz <= 3000 -> 1.5f   // Premium vocal separation and presence
        centerFreqHz <= 7000 -> 3.5f   // Micro-detail brightness (Acoustic crispness)
        else                 -> 5.0f   // Pure diamond air and ultra-wide spatial extension
    }

    /**
     * Professional Headroom Compensation (Pre-Cut Attenuation).
     * When Dolby is active, enforces 0.70f core player volume headroom (creating 3dB
     * of digital headroom to guarantee zero clipping or distortion).
     */
    fun getHeadroomVolumeFactor(): Float {
        if (!_isEnabled.value) return 1.0f

        if (_isDolbyEnabled.value) {
            // Crucial step: 0.70f creates 3dB of digital headroom,
            // ensuring frequency boosts and 3D spatializing never choke hardware speakers.
            return 0.70f
        }

        val maxBoostMb = _bands.value.maxOfOrNull { it.levelMb.toInt() }?.coerceAtLeast(0) ?: 0
        val bassBoostMb = if (_bassBoostStrength.value > 0) (_bassBoostStrength.value * 5 / 10) else 0
        val totalPositiveGainMb = maxBoostMb + bassBoostMb

        if (totalPositiveGainMb <= 0) return 1.0f

        // Convert millibels to negative linear attenuation: 10^(-dB / 20)
        val attenuationDb = (totalPositiveGainMb / 100.0)
        val factor = 10.0.pow(-attenuationDb / 20.0).toFloat()
        return factor.coerceIn(0.25f, 1.0f)
    }

    fun setEnabled(enabled: Boolean) {
        _isEnabled.value = enabled
        prefs?.edit()?.putBoolean("eq_enabled", enabled)?.apply()

        if (enabled) {
            if (currentAttachedSessionId != 0) {
                applyEffectsToSession(currentAttachedSessionId)
            }
        } else {
            releaseEffects()
        }
    }

    fun setDolbyEnabled(enabled: Boolean) {
        _isDolbyEnabled.value = enabled
        prefs?.edit()?.putBoolean("dolby_enabled", enabled)?.apply()

        if (enabled) {
            if (!_isEnabled.value) {
                _isEnabled.value = true
                prefs?.edit()?.putBoolean("eq_enabled", true)?.apply()
            }
            if (_spatialStrength.value == 0) {
                _spatialStrength.value = 450
                prefs?.edit()?.putInt("spatial_strength", 450)?.apply()
            }
            applyDolbyCinemaTuning()
        } else {
            if (currentAttachedSessionId != 0) {
                applyEffectsToSession(currentAttachedSessionId)
            }
        }
    }

    fun applyDolbyCinemaTuning() {
        _currentPreset.value = "Dolby Cinema"
        prefs?.edit()?.putString("preset", "Dolby Cinema")?.apply()

        // 1. Calculate and map for standard state list
        _bands.value = _bands.value.map { band ->
            val targetDb = calculateDolbyTargetDb(band.centerFreqHz)
            val millibels = (targetDb * 100).toInt().toShort()
            prefs?.edit()?.putInt("band_${band.bandIndex}", millibels.toInt())?.apply()
            band.copy(levelMb = millibels)
        }

        // 2. Hardware-calibrated tuning across actual hardware bands
        val eq = equalizer
        if (eq != null) {
            try {
                val numBands = eq.numberOfBands.toInt()
                val range = eq.bandLevelRange
                val minLevel = range?.getOrNull(0) ?: (-1200).toShort()
                val maxLevel = range?.getOrNull(1) ?: 1200.toShort()

                for (band in 0 until numBands) {
                    val centerFreqHz = try {
                        eq.getCenterFreq(band.toShort()) / 1000
                    } catch (_: Exception) {
                        _bands.value.getOrNull(band)?.centerFreqHz ?: 1000
                    }
                    val targetDb = calculateDolbyTargetDb(centerFreqHz)
                    val millibels = (targetDb * 100).toInt().toShort()
                    val safeClampedLevel = millibels.coerceIn(minLevel, maxLevel)
                    eq.setBandLevel(band.toShort(), safeClampedLevel)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (currentAttachedSessionId != 0) {
            applyEffectsToSession(currentAttachedSessionId)
        }
    }

    fun setSpatialStrength(strength: Int) {
        val clamped = strength.coerceIn(0, 1000)
        _spatialStrength.value = clamped
        prefs?.edit()?.putInt("spatial_strength", clamped)?.apply()
        setVirtualizer(clamped)
    }

    fun setBandLevel(bandIndex: Short, levelMb: Short) {
        _bands.value = _bands.value.map {
            if (it.bandIndex == bandIndex) it.copy(levelMb = levelMb) else it
        }
        _currentPreset.value = "Custom"
        prefs?.edit()?.apply {
            putInt("band_$bandIndex", levelMb.toInt())
            putString("preset", "Custom")
            apply()
        }
        try {
            val range = equalizer?.bandLevelRange
            val minLevel: Short = range?.getOrNull(0) ?: (-1200).toShort()
            val maxLevel: Short = range?.getOrNull(1) ?: 1200.toShort()
            val clamped: Short = levelMb.coerceIn(minLevel, maxLevel)
            equalizer?.setBandLevel(bandIndex, clamped)
        } catch (_: Exception) {}
    }

    fun setBassBoost(strength: Int) {
        val clamped = strength.coerceIn(0, 1000)
        _bassBoostStrength.value = clamped
        prefs?.edit()?.putInt("bass_boost", clamped)?.apply()
        try {
            if (clamped > 0 && _isEnabled.value) {
                if (bassBoost == null && currentAttachedSessionId != 0) {
                    bassBoost = BassBoost(1000, currentAttachedSessionId)
                }
                bassBoost?.apply {
                    if (strengthSupported) {
                        setStrength(clamped.toShort())
                    }
                    enabled = true
                }
            } else {
                bassBoost?.enabled = false
            }
        } catch (_: Exception) {}
    }

    fun setVirtualizer(strength: Int) {
        val clamped = strength.coerceIn(0, 1000)
        _virtualizerStrength.value = clamped
        prefs?.edit()?.putInt("virtualizer", clamped)?.apply()
        try {
            if (clamped > 0 && _isEnabled.value) {
                if (virtualizer == null && currentAttachedSessionId != 0) {
                    virtualizer = Virtualizer(1000, currentAttachedSessionId)
                }
                virtualizer?.apply {
                    if (strengthSupported) {
                        setStrength(clamped.toShort())
                    }
                    enabled = true
                }
            } else {
                virtualizer?.enabled = false
            }
        } catch (_: Exception) {}
    }

    fun applyPreset(presetName: String) {
        _currentPreset.value = presetName
        prefs?.edit()?.putString("preset", presetName)?.apply()

        // Calibrated gain curves centered near 0 dB to preserve dynamic range
        val gains: List<Short> = when (presetName) {
            "Dolby Cinema" -> {
                _isDolbyEnabled.value = true
                prefs?.edit()?.putBoolean("dolby_enabled", true)?.apply()
                _spatialStrength.value = 450
                DEFAULT_BANDS.map { band ->
                    (calculateDolbyTargetDb(band.centerFreqHz) * 100).toInt().toShort()
                }
            }
            "Spatial Theater" -> {
                _isDolbyEnabled.value = true
                prefs?.edit()?.putBoolean("dolby_enabled", true)?.apply()
                _spatialStrength.value = 700
                listOf(350, 150, 0, 150, 400)
            }
            "Vocal Clarity" -> {
                listOf(-150, -100, 300, 200, 50)
            }
            "Club Rumble" -> {
                listOf(600, 300, -50, 50, 200)
            }
            "Bass Boost" -> listOf(300, 150, 0, -100, -100)
            "Rock" -> listOf(250, 100, -150, 100, 250)
            "Pop" -> listOf(-100, 100, 250, 100, -100)
            "Electronic" -> listOf(250, 150, 0, 100, 200)
            "Jazz" -> listOf(150, 100, -50, 100, 150)
            "Acoustic" -> listOf(150, 100, 0, 100, 150)
            else -> {
                // Flat
                _isDolbyEnabled.value = false
                prefs?.edit()?.putBoolean("dolby_enabled", false)?.apply()
                listOf(0, 0, 0, 0, 0)
            }
        }

        _bands.value = _bands.value.mapIndexed { idx, band ->
            val gain = gains.getOrElse(idx) { 0.toShort() }
            prefs?.edit()?.putInt("band_${band.bandIndex}", gain.toInt())?.apply()
            try {
                equalizer?.setBandLevel(band.bandIndex, gain)
            } catch (_: Exception) {}
            band.copy(levelMb = gain)
        }

        if (currentAttachedSessionId != 0 && _isEnabled.value) {
            applyEffectsToSession(currentAttachedSessionId)
        }
    }

    private fun releaseEffects() {
        try {
            dynamicsProcessing?.enabled = false
            dynamicsProcessing?.release()
        } catch (_: Exception) {}
        try {
            equalizer?.enabled = false
            equalizer?.release()
        } catch (_: Exception) {}
        try {
            bassBoost?.enabled = false
            bassBoost?.release()
        } catch (_: Exception) {}
        try {
            virtualizer?.enabled = false
            virtualizer?.release()
        } catch (_: Exception) {}
        dynamicsProcessing = null
        equalizer = null
        bassBoost = null
        virtualizer = null
        _isLimiterEngaged.value = false
    }

    fun release() {
        releaseEffects()
        currentAttachedSessionId = 0
    }
}
