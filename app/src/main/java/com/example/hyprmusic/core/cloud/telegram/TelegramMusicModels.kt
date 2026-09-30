package com.example.hyprmusic.core.cloud.telegram

import com.example.hyprmusic.core.model.Album
import com.example.hyprmusic.core.model.Artist
import com.example.hyprmusic.core.model.Track
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerHealthInfo(
    val status: String = "unknown",
    val telegram: String = "unknown",
    val bot: String = "unknown",
    val index: String = "unknown",
    @SerialName("total_tracks") val totalTracks: Int = 0,
    @SerialName("tracks") val tracks: Int = 0,
    @SerialName("memory_mb") val memoryMb: Double = 0.0
) {
    val displayTracks: Int
        get() = if (totalTracks > 0) totalTracks else tracks
}

@Serializable
data class TelegramTrackDto(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String = "Single",
    val genre: String = "Music",
    val duration: Int = 0,
    @SerialName("duration_str") val durationStr: String = "0:00",
    @SerialName("is_favorite") val isFavorite: Boolean = false,
    @SerialName("mime_type") val mimeType: String = "audio/mpeg",
    @SerialName("audio_format") val audioFormat: String = "MP3",
    @SerialName("file_size") val fileSize: Long = 0L,
    @SerialName("file_size_str") val fileSizeStr: String = "0 MB",
    @SerialName("artwork_url") val artworkUrl: String = "",
    @SerialName("stream_url") val streamUrl: String = ""
) {
    fun toTrack(serverUrl: String, userId: Long, apiSecretKey: String?): Track {
        val base = serverUrl.trim().removeSuffix("/")
        val tokenParam = if (!apiSecretKey.isNullOrBlank()) "&token=${apiSecretKey.trim()}" else ""
        val authParams = "?user_id=$userId$tokenParam"

        val resolvedContentUri = "$base/api/stream/$id$authParams"
        val resolvedArtUri = "$base/api/artwork/$id$authParams"

        val ext = when (audioFormat.uppercase()) {
            "FLAC" -> "flac"
            "M4A" -> "m4a"
            "WAV" -> "wav"
            "OGG" -> "ogg"
            "OPUS" -> "opus"
            else -> "mp3"
        }

        return Track(
            id = "tg_$id",
            title = title.ifBlank { "Telegram Track $id" },
            artist = artist.ifBlank { "Unknown Artist" },
            album = album.ifBlank { "Telegram Music Cloud" },
            durationMs = (duration * 1000L).coerceAtLeast(1000L),
            contentUri = resolvedContentUri,
            filePath = "cloud://$id/${title.replace("/", "_")}.$ext",
            albumArtUri = resolvedArtUri,
            bitrate = if (audioFormat.equals("FLAC", true)) 960 else 320,
            sampleRate = 44100,
            mimeType = mimeType,
            isFavorite = isFavorite,
            dateAdded = System.currentTimeMillis(),
            fileSize = fileSize
        )
    }
}

@Serializable
data class TelegramAlbumDto(
    val name: String,
    val artist: String,
    @SerialName("track_count") val trackCount: Int = 0,
    @SerialName("artwork_url") val artworkUrl: String = ""
) {
    fun toAlbum(serverUrl: String, userId: Long, apiSecretKey: String?): Album {
        val base = serverUrl.trim().removeSuffix("/")
        val tokenParam = if (!apiSecretKey.isNullOrBlank()) "&token=${apiSecretKey.trim()}" else ""
        val authParams = "?user_id=$userId$tokenParam"
        val resolvedCover = if (artworkUrl.isNotBlank()) "$base$artworkUrl$authParams" else null

        return Album(
            id = "tg_alb_${absHash(name)}",
            title = name,
            artist = artist,
            coverUri = resolvedCover,
            trackCount = trackCount
        )
    }
}

@Serializable
data class TelegramArtistDto(
    val name: String,
    @SerialName("track_count") val trackCount: Int = 0
) {
    fun toArtist(): Artist {
        return Artist(
            id = "tg_art_${absHash(name)}",
            name = name,
            trackCount = trackCount,
            albumCount = 0
        )
    }
}

@Serializable
data class TelegramLibraryResponseDto(
    @SerialName("total_tracks") val totalTracks: Int = 0,
    val tracks: List<TelegramTrackDto> = emptyList(),
    val albums: List<TelegramAlbumDto> = emptyList(),
    val artists: List<TelegramArtistDto> = emptyList(),
    @SerialName("authenticated_user_id") val authenticatedUserId: Long? = null
)

private fun absHash(value: String): Long {
    return Math.abs(value.hashCode().toLong())
}
