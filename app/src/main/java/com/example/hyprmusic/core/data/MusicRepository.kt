package com.example.hyprmusic.core.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.hyprmusic.core.model.Album
import com.example.hyprmusic.core.model.Artist
import com.example.hyprmusic.core.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class MusicRepository(private val context: Context) {

    val favoritesRepository = FavoritesRepository(context)
    val playbackStatsRepository = PlaybackStatsRepository(context)
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val cacheFile = File(context.filesDir, "tracks_cache.json")
    private val hasCacheOnDisk = cacheFile.exists() && cacheFile.length() > 0

    private val _hasInitialScanCompleted = MutableStateFlow(hasCacheOnDisk)
    val hasInitialScanCompleted: StateFlow<Boolean> = _hasInitialScanCompleted.asStateFlow()

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _isScanning = MutableStateFlow(hasCacheOnDisk)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    init {
        if (hasCacheOnDisk) {
            repositoryScope.launch(Dispatchers.IO) {
                try {
                    val cached = loadCachedTracksFromDisk()
                    if (cached.isNotEmpty()) {
                        val alb = withContext(Dispatchers.Default) { aggregateAlbums(cached) }
                        val art = withContext(Dispatchers.Default) { aggregateArtists(cached) }
                        _tracks.value = cached
                        _albums.value = alb
                        _artists.value = art
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    _isScanning.value = false
                }
            }
        }
    }

    private fun loadCachedTracksFromDisk(): List<Track> {
        return try {
            if (cacheFile.exists()) {
                val content = cacheFile.readText()
                if (content.isNotBlank()) {
                    jsonSerializer.decodeFromString<List<Track>>(content)
                } else emptyList()
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveTracksToDisk(tracks: List<Track>) {
        repositoryScope.launch {
            try {
                val content = jsonSerializer.encodeToString(tracks)
                cacheFile.writeText(content)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun aggregateAlbums(tracks: List<Track>): List<Album> {
        return tracks
            .groupBy { it.album }
            .map { (albumTitle, trackList) ->
                Album(
                    id = albumTitle.hashCode().toString(),
                    title = albumTitle,
                    artist = trackList.firstOrNull()?.artist ?: "Unknown Artist",
                    coverUri = trackList.firstOrNull { it.albumArtUri != null }?.albumArtUri,
                    trackCount = trackList.size
                )
            }
    }

    private fun aggregateArtists(tracks: List<Track>): List<Artist> {
        return tracks
            .groupBy { it.artist }
            .map { (artistName, trackList) ->
                Artist(
                    id = artistName.hashCode().toString(),
                    name = artistName,
                    trackCount = trackList.size,
                    albumCount = trackList.map { it.album }.distinct().size
                )
            }
    }

    fun fetchLocalSongs(): Flow<List<Track>> = flow {
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

                    val trackIdStr = id.toString()
                    val isFav = favoritesRepository.isFavorite(trackIdStr)

                    localTracks.add(
                        Track(
                            id = trackIdStr,
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
                            dateAdded = dateAdded * 1000L,
                            isFavorite = isFav
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emit(localTracks)
    }.flowOn(Dispatchers.IO)

    suspend fun scanLocalMedia() = withContext(Dispatchers.IO) {
        _isScanning.value = true
        try {
            fetchLocalSongs().collect { localTracks ->
                // Real tracks only: no mock data fallback!
                // Prevent wiping cached library if a scan was empty due to delayed permission grant
                if (localTracks.isNotEmpty() || _tracks.value.isEmpty()) {
                    val alb = withContext(Dispatchers.Default) { aggregateAlbums(localTracks) }
                    val art = withContext(Dispatchers.Default) { aggregateArtists(localTracks) }
                    _tracks.value = localTracks
                    _albums.value = alb
                    _artists.value = art
                    if (localTracks.isNotEmpty()) {
                        saveTracksToDisk(localTracks)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _hasInitialScanCompleted.value = true
            _isScanning.value = false
        }
    }

    fun toggleFavorite(trackId: String): Boolean {
        val isNowFav = favoritesRepository.toggleFavorite(trackId)
        val updated = _tracks.value.map { if (it.id == trackId) it.copy(isFavorite = isNowFav) else it }
        _tracks.value = updated
        saveTracksToDisk(updated)
        return isNowFav
    }

    fun getHeavyRotationTracks(limit: Int = 10): List<Track> {
        val all = _tracks.value
        if (all.isEmpty()) return emptyList()

        // Check if there are played tracks
        val withPlays = all.map { track ->
            Pair(track, playbackStatsRepository.getPlayCount(track.id))
        }

        val hasAnyPlays = withPlays.any { it.second > 0 }
        return if (hasAnyPlays) {
            withPlays.sortedByDescending { it.second }
                .take(limit)
                .map { it.first }
        } else {
            // Graceful fallback to recently added or high-bitrate tracks
            all.take(limit)
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
