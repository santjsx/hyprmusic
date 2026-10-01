package com.example.hyprmusic.core.media

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class SleepTimerState(
    val isActive: Boolean = false,
    val remainingMillis: Long = 0L,
    val totalDurationMillis: Long = 0L,
    val isEndOfTrack: Boolean = false,
    val targetTrackId: String? = null
) {
    val formattedRemaining: String
        get() {
            if (isEndOfTrack) return "End of Track"
            val totalSeconds = (remainingMillis / 1000).coerceAtLeast(0)
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return if (hours > 0) {
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }
        }
}

object HyprSleepTimer {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timerJob: Job? = null
    private var boundAudioPlayer: HyprAudioPlayer? = null

    private val _timerState = MutableStateFlow(SleepTimerState())
    val timerState: StateFlow<SleepTimerState> = _timerState.asStateFlow()

    fun startTimer(minutes: Int, audioPlayer: HyprAudioPlayer) {
        cancelTimer()
        boundAudioPlayer = audioPlayer
        val durationMs = minutes * 60 * 1000L

        _timerState.value = SleepTimerState(
            isActive = true,
            remainingMillis = durationMs,
            totalDurationMillis = durationMs,
            isEndOfTrack = false
        )

        timerJob = scope.launch {
            val startTime = System.currentTimeMillis()
            val endTime = startTime + durationMs

            while (isActive) {
                val now = System.currentTimeMillis()
                val remaining = endTime - now

                if (remaining <= 0L) {
                    executeShutdown()
                    break
                }

                // Smooth 15-second audiophile volume fade-out
                if (remaining <= 15_000L) {
                    val fadeFraction = (remaining / 15_000f).coerceIn(0f, 1f)
                    audioPlayer.setVolume(fadeFraction)
                } else {
                    audioPlayer.setVolume(1.0f)
                }

                _timerState.value = _timerState.value.copy(
                    remainingMillis = remaining
                )

                delay(500L)
            }
        }
    }

    fun startEndOfTrackTimer(currentTrackId: String, audioPlayer: HyprAudioPlayer) {
        cancelTimer()
        boundAudioPlayer = audioPlayer

        _timerState.value = SleepTimerState(
            isActive = true,
            remainingMillis = 0L,
            totalDurationMillis = 0L,
            isEndOfTrack = true,
            targetTrackId = currentTrackId
        )

        timerJob = scope.launch {
            while (isActive) {
                val state = audioPlayer.playbackState.value
                val currentTrack = state.currentTrack

                // If track changed or completed
                if (currentTrack == null || currentTrack.id != currentTrackId) {
                    executeShutdown()
                    break
                }

                // If remaining duration of track is within 8 seconds, begin gentle fade out
                val trackRemainingMs = (state.durationMs - state.currentPositionMs).coerceAtLeast(0L)
                if (state.durationMs > 0L && trackRemainingMs in 1L..8_000L) {
                    val fadeFraction = (trackRemainingMs / 8_000f).coerceIn(0f, 1f)
                    audioPlayer.setVolume(fadeFraction)
                }

                if (state.durationMs > 0L && trackRemainingMs <= 500L) {
                    executeShutdown()
                    break
                }

                delay(500L)
            }
        }
    }

    fun addMinutes(minutes: Int) {
        val current = _timerState.value
        if (!current.isActive || current.isEndOfTrack) return

        val addedMs = minutes * 60 * 1000L
        val newRemaining = current.remainingMillis + addedMs
        val newTotal = current.totalDurationMillis + addedMs

        boundAudioPlayer?.let { player ->
            player.setVolume(1.0f) // Restore volume in case it was in fade-out window
            cancelTimer()
            startTimer((newRemaining / 60000L).toInt().coerceAtLeast(1), player)
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        boundAudioPlayer?.setVolume(1.0f)
        _timerState.value = SleepTimerState()
    }

    private fun executeShutdown() {
        boundAudioPlayer?.pause()
        boundAudioPlayer?.setVolume(1.0f) // Restore volume for next session
        _timerState.value = SleepTimerState()
        timerJob = null
    }
}
