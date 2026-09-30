package com.example.hyprmusic.core.media

import android.content.Context
import android.content.SharedPreferences
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
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

    private val _currentPreset = MutableStateFlow("Flat")
    val currentPreset: StateFlow<String> = _currentPreset.asStateFlow()

    val availablePresets = listOf(
        "Flat",
        "Bass Boost",
        "Rock",
        "Pop",
        "Electronic",
        "Vocal",
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
        val isClean = p.getBoolean("eq_v2_clean", false)
        if (!isClean) {
            p.edit()
                .putBoolean("eq_v2_clean", true)
                .putBoolean("eq_enabled", false)
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
                // Keep effects completely detached when disabled for pure, zero-overhead direct audio
                return
            }

            equalizer = Equalizer(0, sessionId).apply {
                enabled = true
            }

            if (_bassBoostStrength.value > 0) {
                try {
                    bassBoost = BassBoost(0, sessionId).apply {
                        if (strengthSupported) {
                            setStrength(_bassBoostStrength.value.toShort())
                        }
                        enabled = true
                    }
                } catch (_: Exception) {}
            }

            if (_virtualizerStrength.value > 0) {
                try {
                    virtualizer = Virtualizer(0, sessionId).apply {
                        if (strengthSupported) {
                            setStrength(_virtualizerStrength.value.toShort())
                        }
                        enabled = true
                    }
                } catch (_: Exception) {}
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
     * Professional Headroom Compensation (Pre-Cut Attenuation).
     * Calculates the exact gain reduction factor (0.25f .. 1.0f) needed
     * to prevent digital clipping when equalizer bands or bass boost are boosted.
     */
    fun getHeadroomVolumeFactor(): Float {
        if (!_isEnabled.value) return 1.0f

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
                    bassBoost = BassBoost(0, currentAttachedSessionId)
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
                    virtualizer = Virtualizer(0, currentAttachedSessionId)
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

        // Balanced, audiophile-calibrated gain curves centered near 0 dB (preventing digital clipping)
        val gains: List<Short> = when (presetName) {
            "Bass Boost" -> listOf(300, 150, 0, -100, -100)
            "Rock" -> listOf(250, 100, -150, 100, 250)
            "Pop" -> listOf(-100, 100, 250, 100, -100)
            "Electronic" -> listOf(250, 150, 0, 100, 200)
            "Vocal" -> listOf(-150, -100, 250, 100, 0)
            "Jazz" -> listOf(150, 100, -50, 100, 150)
            "Acoustic" -> listOf(150, 100, 0, 100, 150)
            else -> listOf(0, 0, 0, 0, 0) // Flat
        }

        _bands.value = _bands.value.mapIndexed { idx, band ->
            val gain = gains.getOrElse(idx) { 0.toShort() }
            prefs?.edit()?.putInt("band_${band.bandIndex}", gain.toInt())?.apply()
            try {
                equalizer?.setBandLevel(band.bandIndex, gain)
            } catch (_: Exception) {}
            band.copy(levelMb = gain)
        }
    }

    private fun releaseEffects() {
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
        equalizer = null
        bassBoost = null
        virtualizer = null
    }

    fun release() {
        releaseEffects()
        currentAttachedSessionId = 0
    }
}
