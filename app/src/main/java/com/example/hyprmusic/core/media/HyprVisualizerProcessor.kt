package com.example.hyprmusic.core.media

import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

object HyprVisualizerState {
    const val BAR_COUNT = 16

    private val _amplitudes = MutableStateFlow(FloatArray(BAR_COUNT) { 0f })
    val amplitudes: StateFlow<FloatArray> = _amplitudes.asStateFlow()

    private val _rmsEnergy = MutableStateFlow(0f)
    val rmsEnergy: StateFlow<Float> = _rmsEnergy.asStateFlow()

    private val smoothedAmplitudes = FloatArray(BAR_COUNT) { 0f }
    private var smoothedRms = 0f

    fun updateRaw(rawAmps: FloatArray, rawRms: Float) {
        // Fast attack, smooth decay
        for (i in 0 until BAR_COUNT) {
            val target = rawAmps.getOrElse(i) { 0f }.coerceIn(0f, 1f)
            if (target > smoothedAmplitudes[i]) {
                smoothedAmplitudes[i] = smoothedAmplitudes[i] * 0.3f + target * 0.7f
            } else {
                smoothedAmplitudes[i] = smoothedAmplitudes[i] * 0.75f + target * 0.25f
            }
        }

        if (rawRms > smoothedRms) {
            smoothedRms = smoothedRms * 0.3f + rawRms * 0.7f
        } else {
            smoothedRms = smoothedRms * 0.8f + rawRms * 0.2f
        }

        _amplitudes.value = smoothedAmplitudes.clone()
        _rmsEnergy.value = smoothedRms
    }

    fun decay() {
        for (i in 0 until BAR_COUNT) {
            smoothedAmplitudes[i] = max(0f, smoothedAmplitudes[i] * 0.7f - 0.02f)
        }
        smoothedRms = max(0f, smoothedRms * 0.7f - 0.02f)
        _amplitudes.value = smoothedAmplitudes.clone()
        _rmsEnergy.value = smoothedRms
    }

    fun reset() {
        for (i in 0 until BAR_COUNT) smoothedAmplitudes[i] = 0f
        smoothedRms = 0f
        _amplitudes.value = FloatArray(BAR_COUNT) { 0f }
        _rmsEnergy.value = 0f
    }
}

class HyprVisualizerProcessor : BaseAudioProcessor() {

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        // We only process standard 16-bit PCM; pass through format
        return inputAudioFormat
    }

    private var lastProcessTime = 0L

    override fun queueInput(inputBuffer: ByteBuffer) {
        val remaining = inputBuffer.remaining()
        if (remaining == 0) return

        val buffer = replaceOutputBuffer(remaining)

        val now = System.currentTimeMillis()
        if (now - lastProcessTime >= 33L) {
            lastProcessTime = now
            // Read samples for visualization without disrupting playback
            val duplicate = inputBuffer.asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN)
            val shortBuffer = duplicate.asShortBuffer()
            val totalSamples = shortBuffer.remaining()

            if (totalSamples > 0) {
                var sumSquare = 0.0
                val bandSize = max(1, totalSamples / HyprVisualizerState.BAR_COUNT)
                val currentAmps = FloatArray(HyprVisualizerState.BAR_COUNT)

                var bandIndex = 0
                var bandPeak = 0f
                var samplesInBand = 0

                for (i in 0 until totalSamples) {
                    val sample = shortBuffer.get()
                    val normalized = abs(sample.toFloat()) / 32768.0f
                    sumSquare += (normalized * normalized)

                    if (normalized > bandPeak) {
                        bandPeak = normalized
                    }
                    samplesInBand++

                    if (samplesInBand >= bandSize && bandIndex < HyprVisualizerState.BAR_COUNT) {
                        currentAmps[bandIndex] = min(1f, bandPeak * 1.5f) // Boost visibility
                        bandIndex++
                        bandPeak = 0f
                        samplesInBand = 0
                    }
                }

                while (bandIndex < HyprVisualizerState.BAR_COUNT) {
                    currentAmps[bandIndex] = currentAmps[max(0, bandIndex - 1)] * 0.8f
                    bandIndex++
                }

                val rms = min(1f, (sqrt(sumSquare / totalSamples) * 2.0).toFloat())
                HyprVisualizerState.updateRaw(currentAmps, rms)
            }
        }

        // Copy raw audio into output buffer to pass to AudioSink
        buffer.put(inputBuffer)
        buffer.flip()
    }
}
