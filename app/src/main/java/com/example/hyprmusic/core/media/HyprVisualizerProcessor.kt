package com.example.hyprmusic.core.media

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Global reactive telemetry hub for the audio visualizer pipeline.
 * Exposes 20-band frequency amplitudes, falling Cava peak caps, beat transient pulses,
 * and true acoustic RMS energy.
 */
object HyprVisualizerState {
    const val BAR_COUNT = 20

    private val _amplitudes = MutableStateFlow(FloatArray(BAR_COUNT) { 0f })
    val amplitudes: StateFlow<FloatArray> = _amplitudes.asStateFlow()

    private val _peakCaps = MutableStateFlow(FloatArray(BAR_COUNT) { 0f })
    val peakCaps: StateFlow<FloatArray> = _peakCaps.asStateFlow()

    private val _beatPulse = MutableStateFlow(0f)
    val beatPulse: StateFlow<Float> = _beatPulse.asStateFlow()

    private val _beatPulseScale = MutableStateFlow(1.0f)
    val beatPulseScale: StateFlow<Float> = _beatPulseScale.asStateFlow()

    private val _rmsEnergy = MutableStateFlow(0f)
    val rmsEnergy: StateFlow<Float> = _rmsEnergy.asStateFlow()

    private val smoothedAmplitudes = FloatArray(BAR_COUNT) { 0f }
    private val peakPositions = FloatArray(BAR_COUNT) { 0f }
    private val peakVelocities = FloatArray(BAR_COUNT) { 0f }
    private var smoothedRms = 0f
    private var smoothedBeatPulse = 0f

    /**
     * Updates visualizer state with authentic FFT frequency band targets, true RMS, and beat intensity.
     */
    fun update(targets: FloatArray, rawRms: Float, beatIntensity: Float) {
        for (i in 0 until BAR_COUNT) {
            val target = targets.getOrElse(i) { 0f }.coerceIn(0f, 1f)
            val current = smoothedAmplitudes[i]
            // Asymmetric Dual-Rate Lerp: Fast snappy attack (0.75f) on beats, fluid smooth decay (0.14f)
            val lerpFactor = if (target > current) 0.75f else 0.14f
            smoothedAmplitudes[i] = current + (target - current) * lerpFactor

            // Arch / Hyprland Cava Falling Peak Caps (gravity simulation)
            if (smoothedAmplitudes[i] >= peakPositions[i]) {
                peakPositions[i] = smoothedAmplitudes[i]
                peakVelocities[i] = 0f
            } else {
                peakVelocities[i] += 0.0035f // simulated gravity acceleration
                peakPositions[i] = max(0f, peakPositions[i] - peakVelocities[i])
            }
        }

        // Beat pulse decay
        if (beatIntensity > smoothedBeatPulse) {
            smoothedBeatPulse = beatIntensity
        } else {
            smoothedBeatPulse = max(0f, smoothedBeatPulse * 0.80f - 0.03f)
        }

        // True acoustic RMS smoothing
        if (rawRms > smoothedRms) {
            smoothedRms = smoothedRms * 0.35f + rawRms * 0.65f
        } else {
            smoothedRms = smoothedRms * 0.82f + rawRms * 0.18f
        }

        _amplitudes.value = smoothedAmplitudes.clone()
        _peakCaps.value = peakPositions.clone()
        _beatPulse.value = smoothedBeatPulse
        _beatPulseScale.value = 1.0f + (smoothedBeatPulse * 0.08f)
        _rmsEnergy.value = smoothedRms
    }

    /**
     * Backwards-compatible raw update fallback.
     */
    fun updateRaw(rawAmps: FloatArray, rawRms: Float) {
        val targets = FloatArray(BAR_COUNT) { i -> rawAmps.getOrElse(i) { 0f } }
        update(targets, rawRms, rawRms.coerceIn(0f, 1f))
    }

    /**
     * Gracefully decays visualizer bars and peak caps towards the baseline on pause.
     */
    fun decay() {
        for (i in 0 until BAR_COUNT) {
            smoothedAmplitudes[i] = max(0f, smoothedAmplitudes[i] * 0.75f - 0.015f)
            peakVelocities[i] += 0.005f
            peakPositions[i] = max(0f, peakPositions[i] - peakVelocities[i])
        }
        smoothedRms = max(0f, smoothedRms * 0.75f - 0.02f)
        smoothedBeatPulse = max(0f, smoothedBeatPulse * 0.70f - 0.02f)

        _amplitudes.value = smoothedAmplitudes.clone()
        _peakCaps.value = peakPositions.clone()
        _beatPulse.value = smoothedBeatPulse
        _beatPulseScale.value = 1.0f + (smoothedBeatPulse * 0.08f)
        _rmsEnergy.value = smoothedRms
    }

    /**
     * Resets all visualizer telemetry to complete resting state.
     */
    fun reset() {
        for (i in 0 until BAR_COUNT) {
            smoothedAmplitudes[i] = 0f
            peakPositions[i] = 0f
            peakVelocities[i] = 0f
        }
        smoothedRms = 0f
        smoothedBeatPulse = 0f
        _amplitudes.value = FloatArray(BAR_COUNT) { 0f }
        _peakCaps.value = FloatArray(BAR_COUNT) { 0f }
        _beatPulse.value = 0f
        _beatPulseScale.value = 1.0f
        _rmsEnergy.value = 0f
    }
}

/**
 * High-performance, allocation-free Radix-2 Cooley-Tukey Fast Fourier Transform.
 * Operates on 512 PCM samples with precomputed Hanning window and trigonometric tables.
 */
private class FastFourierTransform(private val n: Int = 512) {
    private val logN: Int = 31 - java.lang.Integer.numberOfLeadingZeros(n)
    private val bitReversal = IntArray(n)
    private val cosTable = FloatArray(n / 2)
    private val sinTable = FloatArray(n / 2)
    val window = FloatArray(n)

    init {
        require(n and (n - 1) == 0) { "FFT size must be a power of 2" }
        for (i in 0 until n) {
            var rev = 0
            var temp = i
            for (j in 0 until logN) {
                rev = (rev shl 1) or (temp and 1)
                temp = temp shr 1
            }
            bitReversal[i] = rev
        }
        for (i in 0 until n / 2) {
            val angle = -2.0 * Math.PI * i / n
            cosTable[i] = cos(angle).toFloat()
            sinTable[i] = sin(angle).toFloat()
        }
        for (i in 0 until n) {
            window[i] = (0.5 * (1.0 - cos(2.0 * Math.PI * i / (n - 1)))).toFloat()
        }
    }

    fun compute(real: FloatArray, imag: FloatArray, magnitudes: FloatArray) {
        for (i in 0 until n) {
            val j = bitReversal[i]
            if (j > i) {
                val tempR = real[i]
                real[i] = real[j]
                real[j] = tempR
                val tempI = imag[i]
                imag[i] = imag[j]
                imag[j] = tempI
            }
        }

        var len = 2
        while (len <= n) {
            val halfLen = len / 2
            val step = n / len
            var i = 0
            while (i < n) {
                var k = 0
                for (j in 0 until halfLen) {
                    val c = cosTable[k]
                    val s = sinTable[k]
                    val tr = real[i + j + halfLen] * c - imag[i + j + halfLen] * s
                    val ti = real[i + j + halfLen] * s + imag[i + j + halfLen] * c

                    real[i + j + halfLen] = real[i + j] - tr
                    imag[i + j + halfLen] = imag[i + j] - ti
                    real[i + j] += tr
                    imag[i + j] += ti
                    k += step
                }
                i += len
            }
            len = len shl 1
        }

        val halfN = n / 2
        for (i in 0 until halfN) {
            magnitudes[i] = sqrt(real[i] * real[i] + imag[i] * imag[i])
        }
    }
}

/**
 * Hardware-accelerated ExoPlayer AudioProcessor performing real-time FFT spectrum analysis.
 * Decomposes 16-bit PCM audio streams into 20 musically spaced frequency bands with
 * AGC normalization, beat transient extraction, and zero impact on playback latency.
 */
class HyprVisualizerProcessor : BaseAudioProcessor() {

    private var is16BitPcm = false
    private var channelCount = 2
    private var sampleRate = 44100
    private var lastProcessTime = 0L

    private val fft = FastFourierTransform(512)
    private val pcmRingBuffer = FloatArray(512)
    private var ringWritePos = 0
    private var accumulatedSamples = 0

    private val fftReal = FloatArray(512)
    private val fftImag = FloatArray(512)
    private val fftMagnitudes = FloatArray(256)
    private val bandTargets = FloatArray(HyprVisualizerState.BAR_COUNT)

    private var dynamicMaxEnergy = 0.25f
    private var bassMovingAvg = 0.15f
    private var currentBeatPulse = 0f

    // Musically distributed 20-band frequency bin ranges for 512-point FFT (44.1kHz - 48kHz)
    private val bandBinRanges = arrayOf(
        1..1,    // Band 0: Sub-bass (~40 - 80 Hz)
        1..2,    // Band 1: Kick drum punch (~80 - 150 Hz)
        2..3,    // Band 2: Upper bass / 808 body (~150 - 240 Hz)
        3..4,    // Band 3: Bass guitar warmth (~240 - 350 Hz)
        4..6,    // Band 4: Low-mid resonance (~350 - 500 Hz)
        6..8,    // Band 5: Snare / Tom fundamental (~500 - 700 Hz)
        8..11,   // Band 6: Vocal lower harmonic (~700 - 950 Hz)
        11..15,  // Band 7: Vocal presence & brass (~950 - 1.3 kHz)
        15..20,  // Band 8: Midrange melody (~1.3 - 1.7 kHz)
        20..27,  // Band 9: Guitar & synth lead (~1.7 - 2.3 kHz)
        27..36,  // Band 10: Percussion attack (~2.3 - 3.1 kHz)
        36..48,  // Band 11: Snare snap & bite (~3.1 - 4.1 kHz)
        48..63,  // Band 12: High presence (~4.1 - 5.4 kHz)
        63..81,  // Band 13: Cymbal bell (~5.4 - 7.0 kHz)
        81..105, // Band 14: Hi-hat sizzle (~7.0 - 9.0 kHz)
        105..134,// Band 15: Open hi-hat shimmer (~9.0 - 11.5 kHz)
        134..168,// Band 16: High sizzle (~11.5 - 14.5 kHz)
        168..198,// Band 17: Air band (~14.5 - 17.0 kHz)
        198..227,// Band 18: Brilliance (~17.0 - 19.5 kHz)
        227..255 // Band 19: Ultra-high air (~19.5 - 22.0 kHz)
    )

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        is16BitPcm = (inputAudioFormat.encoding == C.ENCODING_PCM_16BIT)
        channelCount = inputAudioFormat.channelCount.coerceAtLeast(1)
        sampleRate = inputAudioFormat.sampleRate
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val remaining = inputBuffer.remaining()
        if (remaining == 0) return

        val buffer = replaceOutputBuffer(remaining)

        if (is16BitPcm) {
            try {
                val duplicate = inputBuffer.asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN)
                val shortBuffer = duplicate.asShortBuffer()
                val totalShorts = shortBuffer.remaining()
                val channels = channelCount.coerceAtLeast(1)
                val frames = totalShorts / channels

                if (frames > 0) {
                    var sumSquare = 0.0
                    for (f in 0 until frames) {
                        val mono = if (channels == 1) {
                            shortBuffer.get().toFloat() / 32768.0f
                        } else {
                            val left = shortBuffer.get().toFloat()
                            val right = shortBuffer.get().toFloat()
                            for (c in 2 until channels) {
                                if (shortBuffer.hasRemaining()) shortBuffer.get()
                            }
                            ((left + right) * 0.5f) / 32768.0f
                        }

                        sumSquare += (mono * mono)
                        pcmRingBuffer[ringWritePos] = mono
                        ringWritePos = (ringWritePos + 1) % 512
                    }
                    accumulatedSamples += frames

                    val now = System.currentTimeMillis()
                    // Process FFT every ~18ms (~55 FPS) with at least 256 fresh audio frames
                    if (now - lastProcessTime >= 18L && accumulatedSamples >= 256) {
                        lastProcessTime = now
                        accumulatedSamples = 0

                        // Extract latest 512 contiguous mono samples and apply Hanning window
                        val readPos = (ringWritePos - 512 + 512) % 512
                        for (i in 0 until 512) {
                            val sample = pcmRingBuffer[(readPos + i) % 512]
                            fftReal[i] = sample * fft.window[i]
                            fftImag[i] = 0f
                        }

                        // Compute Radix-2 FFT
                        fft.compute(fftReal, fftImag, fftMagnitudes)

                        // Decompose into 20 musically scaled bands
                        var maxBandEnergy = 0.001f
                        for (b in 0 until HyprVisualizerState.BAR_COUNT) {
                            val range = bandBinRanges[b]
                            var sum = 0f
                            var peak = 0f
                            for (bin in range) {
                                val mag = fftMagnitudes[bin]
                                sum += mag
                                if (mag > peak) peak = mag
                            }
                            val count = range.last - range.first + 1
                            val avg = sum / count
                            // 70% peak + 30% average delivers punchy transients without losing body
                            val rawEnergy = peak * 0.70f + avg * 0.30f

                            // Equal-loudness & pink noise compensation (+0.16x per band)
                            val tilt = 1.0f + (b * 0.16f)
                            val weighted = rawEnergy * tilt
                            bandTargets[b] = weighted
                            if (weighted > maxBandEnergy) maxBandEnergy = weighted
                        }

                        // Dynamic AGC (Auto Gain Control) to adapt smoothly to song mastering loudness
                        if (maxBandEnergy > dynamicMaxEnergy) {
                            dynamicMaxEnergy = dynamicMaxEnergy * 0.25f + maxBandEnergy * 0.75f
                        } else {
                            dynamicMaxEnergy = max(0.04f, dynamicMaxEnergy * 0.993f)
                        }

                        // Perceptual dynamic compression (power exponent 0.60 gives punchy kicks and lively highs)
                        for (b in 0 until HyprVisualizerState.BAR_COUNT) {
                            val normalized = (bandTargets[b] / dynamicMaxEnergy).coerceIn(0f, 1f)
                            bandTargets[b] = normalized.pow(0.60f)
                        }

                        // Beat transient detector on sub-bass & kick drum bands 0..2
                        val currentBass = (bandTargets[0] * 0.35f + bandTargets[1] * 0.50f + bandTargets[2] * 0.15f)
                        if (currentBass > bassMovingAvg * 1.35f && currentBass > 0.28f) {
                            currentBeatPulse = 1.0f
                        } else {
                            currentBeatPulse = max(0f, currentBeatPulse * 0.82f - 0.04f)
                        }
                        bassMovingAvg = bassMovingAvg * 0.90f + currentBass * 0.10f

                        // True acoustic RMS loudness calculation
                        val rms = min(1f, (sqrt(sumSquare / frames) * 2.2).toFloat())

                        HyprVisualizerState.update(bandTargets, rms, currentBeatPulse)
                    }
                }
            } catch (e: Exception) {
                // Audio sink playback must never be interrupted
            }
        }

        buffer.put(inputBuffer)
        buffer.flip()
    }
}
