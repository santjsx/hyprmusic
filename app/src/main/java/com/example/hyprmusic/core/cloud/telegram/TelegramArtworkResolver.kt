package com.example.hyprmusic.core.cloud.telegram

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * 4-Tier High Reliability Artwork Engine:
 * Tier 1: TPMC Gateway Direct (/api/artwork/{id})
 * Tier 2: Real-time Cloud Metadata Fallback (iTunes Search API / Apple CDN 600x600)
 * Tier 3: Local ID3 Tag Extraction (MediaMetadataRetriever APIC frame)
 * Tier 4: Handled in UI via Procedural Hyprland Vinyl Identicon (Zero Broken Covers)
 */
object TelegramArtworkResolver {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // In-memory lookup cache: lookupKey -> resolvedUrl
    private val memoryCache = ConcurrentHashMap<String, String>()

    // Disk cache file
    private var cacheFile: File? = null

    fun init(context: Context) {
        if (cacheFile == null) {
            cacheFile = File(context.filesDir, "hypr_artwork_cache.json")
            loadDiskCache()
        }
    }

    private fun loadDiskCache() {
        val file = cacheFile ?: return
        if (!file.exists() || file.length() == 0L) return
        try {
            val content = file.readText()
            val parsed = json.decodeFromString<Map<String, String>>(content)
            memoryCache.putAll(parsed)
        } catch (_: Exception) {}
    }

    private fun persistDiskCache() {
        val file = cacheFile ?: return
        try {
            val map = HashMap<String, String>(memoryCache)
            val content = json.encodeToString(map)
            file.writeText(content)
        } catch (_: Exception) {}
    }

    /**
     * Resolves the highest quality artwork URL for a track.
     * Guaranteed non-blocking with local memory/disk cache first.
     */
    suspend fun resolveTrackArtwork(
        context: Context,
        trackId: String,
        title: String,
        artist: String,
        currentArtUri: String?
    ): String? = withContext(Dispatchers.IO) {
        init(context)

        val cleanTitle = title.trim()
        val cleanArtist = artist.trim()
        val lookupKey = "track_${cleanArtist.lowercase()}_${cleanTitle.lowercase()}"

        // Check memory/disk cache first
        memoryCache[lookupKey]?.let { cachedUrl ->
            if (cachedUrl.isNotBlank()) return@withContext cachedUrl
        }

        // Tier 1: Check if currentArtUri is already an external high-res URL
        if (!currentArtUri.isNullOrBlank() && (currentArtUri.startsWith("http://") || currentArtUri.startsWith("https://"))) {
            if (!currentArtUri.contains("/api/artwork/")) {
                memoryCache[lookupKey] = currentArtUri
                persistDiskCache()
                return@withContext currentArtUri
            }

            // If it is a TPMC endpoint, check if the server actually has an artwork image (HTTP 200 with image/*)
            try {
                val headReq = Request.Builder()
                    .url(currentArtUri)
                    .head()
                    .header("User-Agent", "HyprMusic/1.9.7 (Android)")
                    .build()

                httpClient.newCall(headReq).execute().use { resp ->
                    val contentType = resp.header("Content-Type") ?: ""
                    if (resp.isSuccessful && (contentType.startsWith("image/") || resp.body?.contentLength() ?: 0L > 500L)) {
                        memoryCache[lookupKey] = currentArtUri
                        persistDiskCache()
                        return@withContext currentArtUri
                    }
                }
            } catch (_: Exception) {}
        }

        // Tier 2: Real-time Cloud Metadata Search via iTunes Search API (Apple CDN 600x600)
        val cloudArtUrl = fetchFromItunesSearch(cleanArtist, cleanTitle, isAlbum = false)
        if (!cloudArtUrl.isNullOrBlank()) {
            memoryCache[lookupKey] = cloudArtUrl
            persistDiskCache()
            return@withContext cloudArtUrl
        }

        // Tier 3: Local ID3 Tag Extraction if track is locally present
        if (!currentArtUri.isNullOrBlank() && (currentArtUri.startsWith("content://") || currentArtUri.startsWith("file://"))) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, Uri.parse(currentArtUri))
                val picture = retriever.embeddedPicture
                retriever.release()
                if (picture != null && picture.isNotEmpty()) {
                    return@withContext currentArtUri
                }
            } catch (_: Exception) {}
        }

        // Return currentArtUri as fallback if nothing else, or null to trigger Tier 4 procedural art
        return@withContext currentArtUri?.ifBlank { null }
    }

    /**
     * Resolves artwork URL for an Album.
     */
    suspend fun resolveAlbumArtwork(
        context: Context,
        albumName: String,
        artist: String,
        currentArtUri: String?
    ): String? = withContext(Dispatchers.IO) {
        init(context)

        val cleanAlbum = albumName.trim()
        val cleanArtist = artist.trim()
        val lookupKey = "album_${cleanArtist.lowercase()}_${cleanAlbum.lowercase()}"

        memoryCache[lookupKey]?.let { cachedUrl ->
            if (cachedUrl.isNotBlank()) return@withContext cachedUrl
        }

        if (!currentArtUri.isNullOrBlank() && (currentArtUri.startsWith("http://") || currentArtUri.startsWith("https://"))) {
            if (!currentArtUri.contains("/api/artwork/")) {
                memoryCache[lookupKey] = currentArtUri
                persistDiskCache()
                return@withContext currentArtUri
            }
        }

        val cloudArtUrl = fetchFromItunesSearch(cleanArtist, cleanAlbum, isAlbum = true)
        if (!cloudArtUrl.isNullOrBlank()) {
            memoryCache[lookupKey] = cloudArtUrl
            persistDiskCache()
            return@withContext cloudArtUrl
        }

        return@withContext currentArtUri?.ifBlank { null }
    }

    private fun fetchFromItunesSearch(artist: String, query: String, isAlbum: Boolean): String? {
        try {
            val searchTerm = if (artist.isNotBlank() && artist != "Unknown Artist" && artist != "Various Artists") {
                "$artist $query"
            } else {
                query
            }

            val encoded = URLEncoder.encode(searchTerm, "UTF-8")
            val entity = if (isAlbum) "album" else "song"
            val url = "https://itunes.apple.com/search?term=$encoded&media=music&entity=$entity&limit=1"

            val req = Request.Builder()
                .url(url)
                .get()
                .header("User-Agent", "HyprMusic/1.9.7 (Android)")
                .build()

            httpClient.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return null
                val bodyStr = resp.body?.string() ?: return null
                val root = json.parseToJsonElement(bodyStr).jsonObject
                val results = root["results"]?.jsonArray ?: return null
                if (results.isEmpty()) return null

                val firstItem = results[0].jsonObject
                val rawArt = firstItem["artworkUrl100"]?.jsonPrimitive?.content ?: return null

                // Upscale from 100x100 to 600x600 for crisp visual display
                val highRes = rawArt.replace("100x100bb.jpg", "600x600bb.jpg")
                    .replace("100x100bb.png", "600x600bb.png")
                return highRes
            }
        } catch (_: Exception) {
            return null
        }
    }
}
