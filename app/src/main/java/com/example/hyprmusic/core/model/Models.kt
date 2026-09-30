package com.example.hyprmusic.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val contentUri: String,
    val filePath: String = "",
    val albumArtUri: String? = null,
    val bitrate: Int = 320,
    val sampleRate: Int = 44100,
    val mimeType: String = "audio/mpeg",
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val dateAdded: Long = System.currentTimeMillis(),
    val fileSize: Long = 0L
) {
    val isCloudTrack: Boolean
        get() = contentUri.startsWith("http://") || contentUri.startsWith("https://")

    val formattedFileSize: String
        get() {
            if (fileSize <= 0) return ""
            val mb = fileSize.toDouble() / (1024.0 * 1024.0)
            return "%.1f MB".format(mb)
        }

    val formattedDuration: String
        get() {
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }

    val audioFormat: String
        get() {
            val ext = filePath.substringAfterLast('.', "").uppercase().trim()
            return when {
                ext.isNotEmpty() && ext.length in 2..5 -> ext
                mimeType.contains("flac", ignoreCase = true) -> "FLAC"
                mimeType.contains("wav", ignoreCase = true) -> "WAV"
                mimeType.contains("mp4", ignoreCase = true) || mimeType.contains("m4a", ignoreCase = true) || mimeType.contains("aac", ignoreCase = true) -> "M4A"
                mimeType.contains("ogg", ignoreCase = true) -> "OGG"
                mimeType.contains("opus", ignoreCase = true) -> "OPUS"
                mimeType.contains("mpeg", ignoreCase = true) || mimeType.contains("mp3", ignoreCase = true) -> "MP3"
                else -> "MP3"
            }
        }

    val isLossless: Boolean
        get() = audioFormat in listOf("FLAC", "WAV", "ALAC", "AIFF") ||
                mimeType.contains("flac", ignoreCase = true) ||
                mimeType.contains("wav", ignoreCase = true)

    val audioQualityBadge: String
        get() {
            val format = audioFormat
            return when {
                isLossless -> {
                    val rate = when {
                        sampleRate >= 192000 -> "192 kHz / 24-bit"
                        sampleRate >= 96000 -> "96 kHz / 24-bit"
                        sampleRate >= 48000 -> "48 kHz / 24-bit"
                        sampleRate >= 44100 -> "44.1 kHz / 16-bit"
                        else -> "Lossless"
                    }
                    "$format • $rate Lossless"
                }
                bitrate > 0 -> {
                    val fidelity = when {
                        bitrate >= 320 -> "High Fidelity"
                        bitrate >= 256 -> "HQ Audio"
                        bitrate >= 192 -> "Standard"
                        else -> "Compressed"
                    }
                    "$format • $bitrate kbps $fidelity"
                }
                else -> "$format • Audio Stream"
            }
        }
}

@Serializable
data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val coverUri: String? = null,
    val trackCount: Int = 0,
    val releaseYear: Int? = null
)

@Serializable
data class Artist(
    val id: String,
    val name: String,
    val trackCount: Int = 0,
    val albumCount: Int = 0
)

@Serializable
data class Playlist(
    val id: String,
    val name: String,
    val trackCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class LyricLine(
    val timestampMs: Long,
    val text: String
)

enum class RepeatMode {
    OFF, ALL, ONE
}

data class PlaybackState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isShuffle: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val queue: List<Track> = emptyList(),
    val currentIndex: Int = -1
) {
    val progress: Float
        get() = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}
