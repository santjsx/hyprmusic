package com.example.hyprmusic.core.media

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.DefaultAudioTrackBufferSizeProvider
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.session.MediaSession
import com.example.hyprmusic.core.model.PlaybackState
import com.example.hyprmusic.core.model.RepeatMode
import com.example.hyprmusic.core.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HyprAudioPlayer(private val context: Context) {

    companion object {
        var activeMediaSession: MediaSession? = null
    }

    private val applicationScope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null
    private var mediaSession: MediaSession? = null

    private val exoPlayer: ExoPlayer by lazy {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        // Generous buffer thresholds for lossless high-resolution audio (FLAC, WAV, M4A)
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs = */ 30000,
                /* maxBufferMs = */ 60000,
                /* bufferForPlaybackMs = */ 1000,
                /* bufferForPlaybackAfterRebufferMs = */ 2000
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        // Native 16-bit PCM AudioSink with generous hardware buffers to prevent HAL stalls & I/O errors
        val renderersFactory = object : DefaultRenderersFactory(context) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean
            ): AudioSink? {
                return DefaultAudioSink.Builder(context)
                    .setEnableFloatOutput(false)
                    .setAudioTrackBufferSizeProvider(
                        DefaultAudioTrackBufferSizeProvider.Builder()
                            .setMinPcmBufferDurationUs(500_000)
                            .setMaxPcmBufferDurationUs(2_000_000)
                            .build()
                    )
                    .build()
            }
        }.apply {
            setEnableAudioFloatOutput(false)
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
        }

        val extractorsFactory = DefaultExtractorsFactory()
            .setConstantBitrateSeekingEnabled(true)

        val mediaSourceFactory = DefaultMediaSourceFactory(context, extractorsFactory)

        ExoPlayer.Builder(context, renderersFactory)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build().apply {
                addListener(PlayerEventListener())
                addAnalyticsListener(PlayerAnalyticsListener())
            }
    }

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    val audioSessionId: Int
        get() = exoPlayer.audioSessionId

    init {
        try {
            val sessionActivityIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            val sessionActivityPendingIntent = sessionActivityIntent?.let {
                PendingIntent.getActivity(
                    context,
                    0,
                    it,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

            mediaSession = MediaSession.Builder(context, exoPlayer)
                .apply {
                    sessionActivityPendingIntent?.let { setSessionActivity(it) }
                }
                .build()
            activeMediaSession = mediaSession
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Observe headroom volume factor to automatically prevent digital clipping on audio boosts
        applicationScope.launch {
            HyprEqualizer.isEnabled.collect {
                exoPlayer.volume = HyprEqualizer.getHeadroomVolumeFactor()
            }
        }
        applicationScope.launch {
            HyprEqualizer.bands.collect {
                exoPlayer.volume = HyprEqualizer.getHeadroomVolumeFactor()
            }
        }
        applicationScope.launch {
            HyprEqualizer.bassBoostStrength.collect {
                exoPlayer.volume = HyprEqualizer.getHeadroomVolumeFactor()
            }
        }
    }

    fun playTrack(track: Track, queue: List<Track> = listOf(track), autoPlay: Boolean = true) {
        val index = queue.indexOfFirst { it.id == track.id }.let { if (it >= 0) it else 0 }
        _playbackState.update {
            it.copy(
                currentTrack = track,
                queue = queue,
                currentIndex = index,
                durationMs = track.durationMs,
                currentPositionMs = 0L,
                isPlaying = autoPlay
            )
        }

        try {
            val uri = Uri.parse(track.contentUri)
            val metadata = MediaMetadata.Builder()
                .setTitle(track.title)
                .setArtist(track.artist)
                .setAlbumTitle(track.album)
                .setArtworkUri(track.albumArtUri?.let { Uri.parse(it) })
                .build()

            val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .setMediaId(track.id)
                .setMediaMetadata(metadata)
                .build()

            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.volume = HyprEqualizer.getHeadroomVolumeFactor()
            if (autoPlay) {
                exoPlayer.play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun prepareTrack(track: Track, queue: List<Track> = listOf(track)) {
        playTrack(track, queue, autoPlay = false)
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            if (_playbackState.value.currentTrack != null) {
                resume()
            }
        }
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun resume() {
        exoPlayer.play()
    }

    fun seekTo(positionMs: Long) {
        val duration = _playbackState.value.durationMs
        val target = positionMs.coerceIn(0L, if (duration > 0) duration else Long.MAX_VALUE)
        exoPlayer.seekTo(target)
        _playbackState.update { it.copy(currentPositionMs = target) }
    }

    fun skipNext() {
        val state = _playbackState.value
        if (state.queue.isEmpty()) return

        val nextIndex = if (state.isShuffle) {
            state.queue.indices.random()
        } else {
            (state.currentIndex + 1) % state.queue.size
        }

        val nextTrack = state.queue.getOrNull(nextIndex) ?: return
        playTrack(nextTrack, state.queue)
    }

    fun skipPrevious() {
        val state = _playbackState.value
        if (state.queue.isEmpty()) return

        // If played more than 3 seconds, restart current track
        if (state.currentPositionMs > 3000) {
            seekTo(0)
            return
        }

        val prevIndex = if (state.isShuffle) {
            state.queue.indices.random()
        } else {
            if (state.currentIndex - 1 < 0) state.queue.size - 1 else state.currentIndex - 1
        }

        val prevTrack = state.queue.getOrNull(prevIndex) ?: return
        playTrack(prevTrack, state.queue)
    }

    fun toggleShuffle() {
        _playbackState.update { it.copy(isShuffle = !it.isShuffle) }
    }

    fun toggleRepeat() {
        _playbackState.update {
            val nextMode = when (it.repeatMode) {
                RepeatMode.OFF -> RepeatMode.ALL
                RepeatMode.ALL -> RepeatMode.ONE
                RepeatMode.ONE -> RepeatMode.OFF
            }
            it.copy(repeatMode = nextMode)
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = applicationScope.launch {
            while (isActive) {
                if (exoPlayer.isPlaying) {
                    val currentPos = exoPlayer.currentPosition
                    val duration = exoPlayer.duration.let { if (it > 0) it else _playbackState.value.durationMs }
                    _playbackState.update {
                        it.copy(
                            currentPositionMs = currentPos,
                            durationMs = duration
                        )
                    }
                }
                delay(120L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTracker()
        try {
            mediaSession?.release()
            mediaSession = null
            activeMediaSession = null
        } catch (_: Exception) {}
        exoPlayer.release()
    }

    private inner class PlayerAnalyticsListener : AnalyticsListener {
        override fun onAudioSinkError(
            eventTime: AnalyticsListener.EventTime,
            audioSinkError: Exception
        ) {
            audioSinkError.printStackTrace()
            // Seamless auto-recovery from audio sink underrun or driver stall
            val currentPos = exoPlayer.currentPosition
            try {
                exoPlayer.seekTo(currentPos)
                exoPlayer.prepare()
                exoPlayer.play()
            } catch (_: Exception) {}
        }
    }

    private inner class PlayerEventListener : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playbackState.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) {
                startProgressTracker()
            } else {
                stopProgressTracker()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            error.printStackTrace()
            // Seamless auto-recovery from audio sink underruns or transient decoder hiccups
            val currentPos = exoPlayer.currentPosition
            val currentTrack = _playbackState.value.currentTrack
            if (currentTrack != null) {
                try {
                    exoPlayer.seekTo(currentPos)
                    exoPlayer.prepare()
                    exoPlayer.play()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    when (_playbackState.value.repeatMode) {
                        RepeatMode.ONE -> {
                            seekTo(0)
                            resume()
                        }
                        RepeatMode.ALL -> skipNext()
                        RepeatMode.OFF -> {
                            if (_playbackState.value.currentIndex < _playbackState.value.queue.lastIndex) {
                                skipNext()
                            } else {
                                seekTo(0)
                                pause()
                            }
                        }
                    }
                }
                Player.STATE_READY -> {
                    val duration = exoPlayer.duration
                    val sessionId = exoPlayer.audioSessionId
                    if (sessionId != 0) {
                        HyprEqualizer.bindAudioSession(context, sessionId)
                        exoPlayer.volume = HyprEqualizer.getHeadroomVolumeFactor()
                    }

                    val format = exoPlayer.audioFormat
                    val current = _playbackState.value.currentTrack
                    val updatedTrack = if (format != null && current != null) {
                        val rate = if (format.sampleRate > 0) format.sampleRate else current.sampleRate
                        val br = if (format.bitrate > 0) (format.bitrate / 1000) else current.bitrate
                        val mime = format.sampleMimeType ?: current.mimeType
                        current.copy(sampleRate = rate, bitrate = br, mimeType = mime)
                    } else current

                    _playbackState.update {
                        it.copy(
                            durationMs = if (duration > 0) duration else it.durationMs,
                            currentTrack = updatedTrack ?: it.currentTrack
                        )
                    }
                }
                else -> {}
            }
        }

        override fun onTracksChanged(tracks: androidx.media3.common.Tracks) {
            val format = exoPlayer.audioFormat
            if (format != null) {
                val current = _playbackState.value.currentTrack
                if (current != null) {
                    val rate = if (format.sampleRate > 0) format.sampleRate else current.sampleRate
                    val br = if (format.bitrate > 0) (format.bitrate / 1000) else current.bitrate
                    val mime = format.sampleMimeType ?: current.mimeType
                    _playbackState.update { state ->
                        state.copy(
                            currentTrack = current.copy(
                                sampleRate = rate,
                                bitrate = br,
                                mimeType = mime
                            )
                        )
                    }
                }
            }
        }
    }
}
