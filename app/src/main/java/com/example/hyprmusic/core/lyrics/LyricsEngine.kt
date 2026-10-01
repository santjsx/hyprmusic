package com.example.hyprmusic.core.lyrics

import android.content.Context
import com.example.hyprmusic.core.model.LyricLine
import com.example.hyprmusic.core.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object LrcParser {
    private val TIME_REGEX = Regex("""\[(\d{2}):(\d{2})(?:\.(\d{2,3}))?\]""")

    fun parse(rawLrc: String): List<LyricLine> {
        val result = mutableListOf<LyricLine>()
        val lines = rawLrc.lines()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("[ar:") || trimmed.startsWith("[ti:") ||
                trimmed.startsWith("[al:") || trimmed.startsWith("[by:") || trimmed.startsWith("[offset:")
            ) {
                continue
            }

            val matches = TIME_REGEX.findAll(trimmed).toList()
            if (matches.isEmpty()) continue

            // The lyric text is everything after the last timestamp match
            val lastMatch = matches.last()
            val text = trimmed.substring(lastMatch.range.last + 1).trim()

            for (match in matches) {
                val min = match.groupValues[1].toLongOrNull() ?: 0L
                val sec = match.groupValues[2].toLongOrNull() ?: 0L
                val msStr = match.groupValues.getOrNull(3).orEmpty()
                val ms = when (msStr.length) {
                    2 -> (msStr.toLongOrNull() ?: 0L) * 10
                    3 -> msStr.toLongOrNull() ?: 0L
                    else -> 0L
                }
                val totalMs = (min * 60 * 1000) + (sec * 1000) + ms
                if (text.isNotEmpty()) {
                    result.add(LyricLine(timestampMs = totalMs, text = text))
                }
            }
        }

        return result.sortedBy { it.timestampMs }
    }

    /**
     * Converts plain lyrics (without timestamps) into spaced LyricLines.
     */
    fun parsePlainLyrics(plainLyrics: String, totalDurationMs: Long): List<LyricLine> {
        val lines = plainLyrics.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) return emptyList()

        val intervalMs = if (totalDurationMs > 0) totalDurationMs / lines.size else 4000L
        return lines.mapIndexed { index, text ->
            LyricLine(timestampMs = index * intervalMs, text = text)
        }
    }
}

object LyricsRepository {
    private val memoryCache = mutableMapOf<String, List<LyricLine>>()

    /**
     * Strips noise from song titles and artist strings to dramatically increase LRCLIB hit rate.
     */
    fun cleanMetadata(title: String, artist: String): Pair<String, String> {
        // Strip common movie/album tags, brackets, and feat annotations
        var cleanTitle = title
            .replace(Regex("""\(from\s+[^)]+\)""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\[from\s+[^\]]+\]""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\(feat\.?\s+[^)]+\)""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\[feat\.?\s+[^\]]+\]""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\(with\s+[^)]+\)""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\(official[^)]*\)""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\[official[^\]]*\]""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\(remix\)""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\(lyrics?\)""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""-\s*From\s+.*$""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\s+-\s+.*$"""), "") // Remove trailer hyphen metadata
            .trim()

        if (cleanTitle.isBlank()) cleanTitle = title.trim()

        // Extract primary artist if multiple artists separated by comma, ampersand, or 'feat'
        var cleanArtist = artist
            .split(',', '&', ';')
            .firstOrNull()
            ?.replace(Regex("""\s+feat\..*$""", RegexOption.IGNORE_CASE), "")
            ?.trim() ?: artist.trim()

        if (cleanArtist.isBlank()) cleanArtist = artist.trim()

        return Pair(cleanTitle, cleanArtist)
    }

    /**
     * Check if a local .lrc file exists alongside the track on the filesystem.
     */
    private fun findLocalLrc(track: Track): List<LyricLine>? {
        if (track.filePath.isBlank()) return null
        try {
            val audioFile = File(track.filePath)
            if (audioFile.exists()) {
                val lrcName = audioFile.nameWithoutExtension + ".lrc"
                val lrcFile = File(audioFile.parentFile, lrcName)
                if (lrcFile.exists() && lrcFile.canRead()) {
                    val content = lrcFile.readText()
                    val parsed = LrcParser.parse(content)
                    if (parsed.isNotEmpty()) return parsed
                }
            }
        } catch (_: Exception) {}
        return null
    }

    /**
     * Master multi-tiered lyrics fetcher.
     * 1. Memory cache
     * 2. Local .lrc file in audio directory
     * 3. LRCLIB Search API with cleaned metadata
     * 4. LRCLIB Search API with fuzzy combined query
     * 5. LRCLIB Exact GET API
     */
    suspend fun fetchLyrics(track: Track): List<LyricLine>? = withContext(Dispatchers.IO) {
        val cacheKey = track.id
        memoryCache[cacheKey]?.let { return@withContext it }

        // Tier 1: Local .lrc file
        val localLrc = findLocalLrc(track)
        if (localLrc != null && localLrc.isNotEmpty()) {
            memoryCache[cacheKey] = localLrc
            return@withContext localLrc
        }

        val (cleanTitle, cleanArtist) = cleanMetadata(track.title, track.artist)

        // Tier 2: LRCLIB Search API with cleaned title and artist
        val searchResult = queryLrclibSearch(cleanTitle, cleanArtist, track.durationMs)
        if (searchResult != null && searchResult.isNotEmpty()) {
            memoryCache[cacheKey] = searchResult
            return@withContext searchResult
        }

        // Tier 3: LRCLIB Search with broader query
        val broadResult = queryLrclibSearchBroad("$cleanTitle $cleanArtist", track.durationMs)
        if (broadResult != null && broadResult.isNotEmpty()) {
            memoryCache[cacheKey] = broadResult
            return@withContext broadResult
        }

        // Tier 4: LRCLIB Exact GET endpoint
        val exactResult = queryLrclibGet(cleanTitle, cleanArtist, track.durationMs)
        if (exactResult != null && exactResult.isNotEmpty()) {
            memoryCache[cacheKey] = exactResult
            return@withContext exactResult
        }

        // No lyrics found anywhere
        null
    }

    private fun queryLrclibSearch(title: String, artist: String, durationMs: Long): List<LyricLine>? {
        try {
            val encTitle = URLEncoder.encode(title, "UTF-8")
            val encArtist = URLEncoder.encode(artist, "UTF-8")
            val urlStr = "https://lrclib.net/api/search?track_name=$encTitle&artist_name=$encArtist"
            val jsonArray = fetchJsonArray(urlStr) ?: return null

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.optJSONObject(i) ?: continue
                val syncedLyrics = item.optString("syncedLyrics")
                if (syncedLyrics.isNotBlank()) {
                    val parsed = LrcParser.parse(syncedLyrics)
                    if (parsed.isNotEmpty()) return parsed
                }
            }

            // Fallback to plain lyrics if synced not available
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.optJSONObject(i) ?: continue
                val plainLyrics = item.optString("plainLyrics")
                if (plainLyrics.isNotBlank()) {
                    return LrcParser.parsePlainLyrics(plainLyrics, durationMs)
                }
            }
        } catch (_: Exception) {}
        return null
    }

    private fun queryLrclibSearchBroad(query: String, durationMs: Long): List<LyricLine>? {
        try {
            val encQuery = URLEncoder.encode(query, "UTF-8")
            val urlStr = "https://lrclib.net/api/search?q=$encQuery"
            val jsonArray = fetchJsonArray(urlStr) ?: return null

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.optJSONObject(i) ?: continue
                val syncedLyrics = item.optString("syncedLyrics")
                if (syncedLyrics.isNotBlank()) {
                    val parsed = LrcParser.parse(syncedLyrics)
                    if (parsed.isNotEmpty()) return parsed
                }
            }

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.optJSONObject(i) ?: continue
                val plainLyrics = item.optString("plainLyrics")
                if (plainLyrics.isNotBlank()) {
                    return LrcParser.parsePlainLyrics(plainLyrics, durationMs)
                }
            }
        } catch (_: Exception) {}
        return null
    }

    private fun queryLrclibGet(title: String, artist: String, durationMs: Long): List<LyricLine>? {
        try {
            val encTitle = URLEncoder.encode(title, "UTF-8")
            val encArtist = URLEncoder.encode(artist, "UTF-8")
            val durSec = if (durationMs > 0) durationMs / 1000 else 0
            val urlStr = "https://lrclib.net/api/get?track_name=$encTitle&artist_name=$encArtist" +
                    if (durSec > 0) "&duration=$durSec" else ""
            val json = fetchJsonObject(urlStr) ?: return null

            val syncedLyrics = json.optString("syncedLyrics")
            if (syncedLyrics.isNotBlank()) {
                val parsed = LrcParser.parse(syncedLyrics)
                if (parsed.isNotEmpty()) return parsed
            }

            val plainLyrics = json.optString("plainLyrics")
            if (plainLyrics.isNotBlank()) {
                return LrcParser.parsePlainLyrics(plainLyrics, durationMs)
            }
        } catch (_: Exception) {}
        return null
    }

    private fun fetchJsonArray(urlStr: String): JSONArray? {
        val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
            connectTimeout = 4000
            readTimeout = 4000
            requestMethod = "GET"
            setRequestProperty("User-Agent", "HyprMusic/1.7.0 (contact@hyprmusic.dev)")
        }
        return try {
            if (conn.responseCode == 200) {
                val raw = conn.inputStream.bufferedReader().use { it.readText() }
                JSONArray(raw)
            } else null
        } catch (_: Exception) {
            null
        } finally {
            conn.disconnect()
        }
    }

    private fun fetchJsonObject(urlStr: String): JSONObject? {
        val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
            connectTimeout = 4000
            readTimeout = 4000
            requestMethod = "GET"
            setRequestProperty("User-Agent", "HyprMusic/1.7.0 (contact@hyprmusic.dev)")
        }
        return try {
            if (conn.responseCode == 200) {
                val raw = conn.inputStream.bufferedReader().use { it.readText() }
                JSONObject(raw)
            } else null
        } catch (_: Exception) {
            null
        } finally {
            conn.disconnect()
        }
    }
}
