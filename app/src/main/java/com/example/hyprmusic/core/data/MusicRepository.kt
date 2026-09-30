package com.example.hyprmusic.core.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.hyprmusic.core.model.Album
import com.example.hyprmusic.core.model.Artist
import com.example.hyprmusic.core.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class MusicRepository(private val context: Context) {

    private val _tracks = MutableStateFlow<List<Track>>(DemoTracks.sampleTracks)
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(DemoTracks.sampleAlbums)
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(DemoTracks.sampleArtists)
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    suspend fun scanLocalMedia() = withContext(Dispatchers.IO) {
        _isScanning.value = true
        val localTracks = mutableListOf<Track>()

        val projection = mutableListOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.SIZE
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(MediaStore.Audio.Media.BITRATE)
            }
        }.toTypedArray()

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} > 10000"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        try {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )

            cursor?.use { c ->
                val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val albumIdCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val dateAddedCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val mimeTypeCol = c.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)
                val sizeCol = c.getColumnIndex(MediaStore.Audio.Media.SIZE)
                val bitrateCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    c.getColumnIndex(MediaStore.Audio.Media.BITRATE)
                } else -1

                while (c.moveToNext()) {
                    val id = c.getLong(idCol)
                    val title = c.getString(titleCol) ?: "Unknown Title"
                    val artist = c.getString(artistCol) ?: "Unknown Artist"
                    val album = c.getString(albumCol) ?: "Unknown Album"
                    val durationMs = c.getLong(durationCol)
                    val filePath = c.getString(dataCol) ?: ""
                    val albumId = c.getLong(albumIdCol)
                    val dateAdded = c.getLong(dateAddedCol)
                    val rawMime = if (mimeTypeCol >= 0) c.getString(mimeTypeCol) else null
                    val sizeBytes = if (sizeCol >= 0) c.getLong(sizeCol) else 0L
                    val rawBitrate = if (bitrateCol >= 0) c.getInt(bitrateCol) else 0

                    val ext = filePath.substringAfterLast('.', "").lowercase().trim()
                    val resolvedMimeType = when {
                        !rawMime.isNullOrBlank() -> rawMime
                        ext == "flac" -> "audio/flac"
                        ext == "wav" -> "audio/wav"
                        ext in listOf("m4a", "aac", "mp4") -> "audio/mp4"
                        ext == "ogg" -> "audio/ogg"
                        ext == "opus" -> "audio/opus"
                        else -> "audio/mpeg"
                    }

                    val calculatedBitrate = when {
                        rawBitrate > 1000 -> (rawBitrate / 1000)
                        rawBitrate in 32..1500 -> rawBitrate
                        sizeBytes > 0 && durationMs > 0 -> {
                            val durationSec = durationMs / 1000L
                            if (durationSec > 0) {
                                ((sizeBytes * 8L) / durationSec / 1000L).toInt().coerceIn(32, 4608)
                            } else 320
                        }
                        ext == "flac" -> 850
                        ext == "wav" -> 1411
                        ext in listOf("m4a", "aac") -> 256
                        else -> 320
                    }

                    val resolvedSampleRate = when {
                        ext == "flac" -> 48000
                        ext == "wav" -> 44100
                        else -> 44100
                    }

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    ).toString()

                    val albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    ).toString()

                    localTracks.add(
                        Track(
                            id = id.toString(),
                            title = title,
                            artist = artist,
                            album = album,
                            durationMs = durationMs,
                            contentUri = contentUri,
                            filePath = filePath,
                            albumArtUri = albumArtUri,
                            bitrate = calculatedBitrate,
                            sampleRate = resolvedSampleRate,
                            mimeType = resolvedMimeType,
                            dateAdded = dateAdded * 1000L
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Prioritize local tracks if available; only fall back to demo tracks if device has none
        val finalTracks = if (localTracks.isNotEmpty()) {
            localTracks
        } else {
            DemoTracks.sampleTracks
        }

        _tracks.value = finalTracks

        // Aggregate Albums
        val aggregatedAlbums = finalTracks
            .groupBy { it.album }
            .map { (albumTitle, tracks) ->
                Album(
                    id = albumTitle.hashCode().toString(),
                    title = albumTitle,
                    artist = tracks.firstOrNull()?.artist ?: "Unknown Artist",
                    coverUri = tracks.firstOrNull { it.albumArtUri != null }?.albumArtUri,
                    trackCount = tracks.size
                )
            }
        _albums.value = aggregatedAlbums

        // Aggregate Artists
        val aggregatedArtists = finalTracks
            .groupBy { it.artist }
            .map { (artistName, tracks) ->
                Artist(
                    id = artistName.hashCode().toString(),
                    name = artistName,
                    trackCount = tracks.size,
                    albumCount = tracks.map { it.album }.distinct().size
                )
            }
        _artists.value = aggregatedArtists

        _isScanning.value = false
    }

    fun toggleFavorite(trackId: String) {
        _tracks.update { list ->
            list.map { if (it.id == trackId) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    fun search(query: String): List<Track> {
        if (query.isBlank()) return _tracks.value
        val lower = query.lowercase().trim()
        return _tracks.value.filter {
            it.title.lowercase().contains(lower) ||
            it.artist.lowercase().contains(lower) ||
            it.album.lowercase().contains(lower)
        }
    }
}
