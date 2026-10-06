package com.example.hyprmusic.core.cloud.telegram

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.hyprmusic.core.data.MusicRepository
import com.example.hyprmusic.core.model.Album
import com.example.hyprmusic.core.model.Artist
import com.example.hyprmusic.core.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class TelegramMusicRepository(
    private val context: Context,
    private val localMusicRepository: MusicRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val config = TelegramCloudConfig.getInstance(context)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val cacheFile = File(context.filesDir, "tpmc_cached_catalog.json")

    private val _cloudTracks = MutableStateFlow<List<Track>>(emptyList())
    val cloudTracks: StateFlow<List<Track>> = _cloudTracks.asStateFlow()

    private val _cloudAlbums = MutableStateFlow<List<Album>>(emptyList())
    val cloudAlbums: StateFlow<List<Album>> = _cloudAlbums.asStateFlow()

    private val _cloudArtists = MutableStateFlow<List<Artist>>(emptyList())
    val cloudArtists: StateFlow<List<Artist>> = _cloudArtists.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _isWakingServer = MutableStateFlow(false)
    val isWakingServer: StateFlow<Boolean> = _isWakingServer.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    val syncError: StateFlow<String?> = _syncError.asStateFlow()

    private val _accessState = MutableStateFlow<CloudAccessState>(
        if (config.isConfigured()) CloudAccessState.Authorized else CloudAccessState.ConfigRequired
    )
    val accessState: StateFlow<CloudAccessState> = _accessState.asStateFlow()

    private val _serverHealth = MutableStateFlow<ServerHealthInfo?>(null)
    val serverHealth: StateFlow<ServerHealthInfo?> = _serverHealth.asStateFlow()

    private val _downloadingProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val downloadingProgress: StateFlow<Map<String, Float>> = _downloadingProgress.asStateFlow()

    private val _downloadedTrackIds = MutableStateFlow<Set<String>>(emptySet())
    val downloadedTrackIds: StateFlow<Set<String>> = _downloadedTrackIds.asStateFlow()

    val trackSortOrder = MutableStateFlow(CloudTrackSortOrder.RECENT)
    val albumSortOrder = MutableStateFlow(CloudAlbumSortOrder.TITLE_AZ)
    val artistSortOrder = MutableStateFlow(CloudArtistSortOrder.NAME_AZ)

    init {
        // Hydrate from disk cache immediately (0ms perceived latency)
        loadCachedCatalog()

        // Observe local tracks to keep downloaded set accurate
        scope.launch {
            localMusicRepository.tracks.collect { localTracks ->
                updateDownloadedStatus(localTracks)
            }
        }

        // Auto-sync if configured and enabled
        if (config.settings.value.autoSyncOnStartup && config.isConfigured()) {
            scope.launch {
                syncLibrary()
            }
        }
    }

    private fun loadCachedCatalog() {
        val target = if (cacheFile.exists() && cacheFile.length() > 0L) {
            cacheFile
        } else {
            File(context.filesDir, "tpmc_cached_catalog.json.tmp")
        }
        if (!target.exists() || target.length() == 0L) return
        try {
            val content = target.readText()
            val dto = json.decodeFromString<TelegramLibraryResponseDto>(content)
            val settings = config.settings.value
            val mappedTracks = dto.tracks.map { it.toTrack(settings.serverUrl, settings.userId, settings.apiSecretKey) }
            val mappedAlbums = if (dto.albums.isNotEmpty()) {
                dto.albums.map { it.toAlbum(settings.serverUrl, settings.userId, settings.apiSecretKey) }
            } else {
                synthesizeAlbums(mappedTracks)
            }
            val mappedArtists = if (dto.artists.isNotEmpty()) {
                dto.artists.map { it.toArtist() }
            } else {
                synthesizeArtists(mappedTracks)
            }

            _cloudTracks.value = mappedTracks
            _cloudAlbums.value = mappedAlbums
            _cloudArtists.value = mappedArtists
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun synthesizeAlbums(tracks: List<Track>): List<Album> {
        return tracks.groupBy { it.album.ifBlank { "Unknown Album" } }.map { (albumName, albumTracks) ->
            Album(
                id = "tg_alb_${abs(albumName.hashCode().toLong())}",
                title = albumName,
                artist = albumTracks.firstOrNull()?.artist ?: "Unknown Artist",
                coverUri = albumTracks.firstOrNull { !it.albumArtUri.isNullOrBlank() }?.albumArtUri,
                trackCount = albumTracks.size
            )
        }
    }

    private fun synthesizeArtists(tracks: List<Track>): List<Artist> {
        return tracks.groupBy { it.artist.ifBlank { "Unknown Artist" } }.map { (artistName, artistTracks) ->
            Artist(
                id = "tg_art_${abs(artistName.hashCode().toLong())}",
                name = artistName,
                trackCount = artistTracks.size,
                albumCount = artistTracks.map { it.album }.distinct().size
            )
        }
    }

    private fun updateDownloadedStatus(localTracks: List<Track>) {
        val localKeys = localTracks.map { "${it.title.trim().lowercase()}_${it.artist.trim().lowercase()}" }.toSet()
        val currentCloud = _cloudTracks.value
        val downloaded = currentCloud.filter { ct ->
            val key = "${ct.title.trim().lowercase()}_${ct.artist.trim().lowercase()}"
            localKeys.contains(key)
        }.map { it.id }.toSet()

        _downloadedTrackIds.value = downloaded
    }

    suspend fun testConnection(): Result<ServerHealthInfo> = withContext(Dispatchers.IO) {
        val settings = config.settings.value
        if (settings.serverUrl.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Server URL is empty"))
        }

        _isWakingServer.value = true
        var result: Result<ServerHealthInfo> = Result.failure(Exception("Not started"))

        // Retry up to 3 times with progressive delay in case Render is cold-starting
        for (attempt in 1..3) {
            result = TelegramMusicApi.checkHealth(settings.serverUrl)
            if (result.isSuccess) break
            if (attempt < 3) delay(4000L)
        }

        _isWakingServer.value = false
        result.onSuccess { _serverHealth.value = it }
        result
    }

    suspend fun syncLibrary(force: Boolean = false) = withContext(Dispatchers.IO) {
        val settings = config.settings.value
        if (!config.isConfigured()) {
            _syncError.value = "TPMC Cloud is not configured. Go to Settings > Telegram Cloud."
            _accessState.value = CloudAccessState.ConfigRequired
            return@withContext
        }

        _isSyncing.value = true
        _syncError.value = null
        _accessState.value = CloudAccessState.Checking

        // Check health / wake instance if necessary
        val healthResult = TelegramMusicApi.checkHealth(settings.serverUrl)
        if (healthResult.isFailure) {
            _isWakingServer.value = true
            // Allow up to 35 seconds for Render instance wake-up
            var wokeUp = false
            for (i in 1..7) {
                delay(5000L)
                val check = TelegramMusicApi.checkHealth(settings.serverUrl)
                if (check.isSuccess) {
                    wokeUp = true
                    _serverHealth.value = check.getOrNull()
                    break
                }
            }
            _isWakingServer.value = false
            if (!wokeUp) {
                _isSyncing.value = false
                val msg = "Server unreachable. If on Render free tier, server may still be booting."
                _syncError.value = msg
                _accessState.value = CloudAccessState.ServerOffline(msg)
                return@withContext
            }
        } else {
            _serverHealth.value = healthResult.getOrNull()
        }

        val libResult = TelegramMusicApi.fetchLibrary(
            serverUrl = settings.serverUrl,
            userId = settings.userId,
            apiSecretKey = settings.apiSecretKey
        )

        libResult.fold(
            onSuccess = { dto ->
                val mappedTracks = dto.tracks.map { it.toTrack(settings.serverUrl, settings.userId, settings.apiSecretKey) }
                val mappedAlbums = if (dto.albums.isNotEmpty()) {
                    dto.albums.map { it.toAlbum(settings.serverUrl, settings.userId, settings.apiSecretKey) }
                } else {
                    synthesizeAlbums(mappedTracks)
                }
                val mappedArtists = if (dto.artists.isNotEmpty()) {
                    dto.artists.map { it.toArtist() }
                } else {
                    synthesizeArtists(mappedTracks)
                }

                _cloudTracks.value = mappedTracks
                _cloudAlbums.value = mappedAlbums
                _cloudArtists.value = mappedArtists
                config.recordSyncSuccess(mappedTracks.size)

                // Persist to disk cache
                try {
                    val content = json.encodeToString(dto)
                    val tempFile = File(context.filesDir, "tpmc_cached_catalog.json.tmp")
                    tempFile.writeText(content)
                    if (tempFile.exists() && tempFile.length() > 0) {
                        if (cacheFile.exists()) cacheFile.delete()
                        tempFile.renameTo(cacheFile)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                updateDownloadedStatus(localMusicRepository.tracks.value)
                _syncError.value = null
                _accessState.value = CloudAccessState.Authorized
            },
            onFailure = { err ->
                val errMsg = err.localizedMessage ?: "Sync failed"
                _syncError.value = errMsg
                if (err is CloudAccessDeniedException) {
                    _accessState.value = CloudAccessState.AccessDenied(
                        httpCode = err.httpCode,
                        reason = err.message ?: "Access restricted by server access control.",
                        botUsername = err.botUsername ?: _serverHealth.value?.bot,
                        userId = err.userId
                    )
                } else {
                    _accessState.value = CloudAccessState.ServerOffline(errMsg)
                }
            }
        )

        _isSyncing.value = false
    }

    suspend fun requestDirectAccess(note: String = "Requested via HyprMusic client"): Result<String> = withContext(Dispatchers.IO) {
        val settings = config.settings.value
        _accessState.value = CloudAccessState.RequestPending("Sending access request to server...")
        val res = TelegramMusicApi.requestDirectAccess(settings.serverUrl, settings.userId, note)
        res.fold(
            onSuccess = { msg ->
                _accessState.value = CloudAccessState.RequestPending(msg)
            },
            onFailure = { err ->
                _accessState.value = CloudAccessState.AccessDenied(
                    httpCode = 403,
                    reason = err.localizedMessage ?: "Access denied",
                    botUsername = _serverHealth.value?.bot,
                    userId = settings.userId
                )
            }
        )
        res
    }

    fun sortTracks(tracks: List<Track>, order: CloudTrackSortOrder): List<Track> {
        return when (order) {
            CloudTrackSortOrder.RECENT -> tracks
            CloudTrackSortOrder.OLDEST -> tracks.reversed()
            CloudTrackSortOrder.TITLE_AZ -> tracks.sortedBy { it.title.lowercase() }
            CloudTrackSortOrder.TITLE_ZA -> tracks.sortedByDescending { it.title.lowercase() }
            CloudTrackSortOrder.ARTIST_AZ -> tracks.sortedBy { it.artist.lowercase() }
            CloudTrackSortOrder.DURATION_DESC -> tracks.sortedByDescending { it.durationMs }
            CloudTrackSortOrder.SIZE_DESC -> tracks.sortedByDescending { it.fileSize }
        }
    }

    fun sortAlbums(albums: List<Album>, order: CloudAlbumSortOrder): List<Album> {
        return when (order) {
            CloudAlbumSortOrder.TITLE_AZ -> albums.sortedBy { it.title.lowercase() }
            CloudAlbumSortOrder.TITLE_ZA -> albums.sortedByDescending { it.title.lowercase() }
            CloudAlbumSortOrder.ARTIST_AZ -> albums.sortedBy { it.artist.lowercase() }
            CloudAlbumSortOrder.TRACK_COUNT_DESC -> albums.sortedByDescending { it.trackCount }
        }
    }

    fun sortArtists(artists: List<Artist>, order: CloudArtistSortOrder): List<Artist> {
        return when (order) {
            CloudArtistSortOrder.NAME_AZ -> artists.sortedBy { it.name.lowercase() }
            CloudArtistSortOrder.NAME_ZA -> artists.sortedByDescending { it.name.lowercase() }
            CloudArtistSortOrder.TRACK_COUNT_DESC -> artists.sortedByDescending { it.trackCount }
        }
    }

    suspend fun downloadAlbum(album: Album, albumTracks: List<Track>): Boolean = withContext(Dispatchers.IO) {
        var allSuccess = true
        for (track in albumTracks) {
            val ok = downloadTrack(track)
            if (!ok) allSuccess = false
        }
        allSuccess
    }

    suspend fun downloadTrack(track: Track): Boolean = withContext(Dispatchers.IO) {
        if (_downloadingProgress.value.containsKey(track.id)) return@withContext false
        val downloadUrl = if (track.contentUri.contains("?")) {
            "${track.contentUri}&download=true"
        } else {
            "${track.contentUri}?download=true"
        }

        _downloadingProgress.update { it + (track.id to 0.01f) }

        val downloadClient = TelegramMediaSource.sharedOkHttpClient

        var success = false
        var targetUri: Uri? = null

        try {
            val request = Request.Builder()
                .url(downloadUrl)
                .get()
                .header("User-Agent", "HyprMusic/2.0.0 (Android)")
                .build()

            downloadClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    _downloadingProgress.update { it - track.id }
                    return@withContext false
                }

                val body = response.body ?: run {
                    _downloadingProgress.update { it - track.id }
                    return@withContext false
                }

                val totalBytes = body.contentLength().let { if (it > 0) it else 1L }
                val ext = track.audioFormat.lowercase().let { if (it.isNotBlank()) it else "mp3" }
                val filename = "${track.title.replace("[^a-zA-Z0-9.-]".toRegex(), "_")}_${track.artist.replace("[^a-zA-Z0-9.-]".toRegex(), "_")}.$ext"

                val contentValues = ContentValues().apply {
                    put(MediaStore.Audio.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Audio.Media.TITLE, track.title)
                    put(MediaStore.Audio.Media.ARTIST, track.artist)
                    put(MediaStore.Audio.Media.ALBUM, track.album)
                    put(MediaStore.Audio.Media.MIME_TYPE, track.mimeType)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/HyprMusic")
                        put(MediaStore.Audio.Media.IS_PENDING, 1)
                    }
                }

                val resolver = context.contentResolver
                val audioUri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: run {
                        _downloadingProgress.update { it - track.id }
                        return@withContext false
                    }
                targetUri = audioUri

                resolver.openOutputStream(audioUri)?.use { output ->
                    body.byteStream().use { input ->
                        // Upgraded 256KB buffer chunking for maximum download throughput
                        val buffer = ByteArray(256 * 1024)
                        var bytesRead: Int
                        var accumulated = 0L

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            accumulated += bytesRead
                            val progress = (accumulated.toFloat() / totalBytes.toFloat()).coerceIn(0.01f, 0.99f)
                            _downloadingProgress.update { it + (track.id to progress) }
                        }
                        output.flush()
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val finishValues = ContentValues().apply {
                        put(MediaStore.Audio.Media.IS_PENDING, 0)
                    }
                    resolver.update(audioUri, finishValues, null, null)
                }

                success = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            val uriToClean = targetUri
            if (!success && uriToClean != null) {
                try {
                    context.contentResolver.delete(uriToClean, null, null)
                } catch (ignored: Exception) {}
            }
            _downloadingProgress.update { it - track.id }
        }

        if (success) {
            _downloadedTrackIds.update { it + track.id }
            localMusicRepository.scanLocalMedia()
        }

        return@withContext success
    }
}
