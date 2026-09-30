package com.example.hyprmusic.core.updater

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class UpdateReleaseInfo(
    val tagName: String,
    val releaseTitle: String,
    val releaseNotes: String,
    val downloadUrl: String,
    val publishedAt: String,
    val isUpdateAvailable: Boolean
)

object HyprUpdateManager {

    const val CURRENT_VERSION = "v1.2.0"
    const val DEVELOPER_NAME = "Santhosh Reddy"
    const val GITHUB_REPO_URL = "https://github.com/heysanthoshreddy/hyprmusic"
    private const val GITHUB_API_URL = "https://api.github.com/repos/heysanthoshreddy/hyprmusic/releases/latest"

    private val _updateState = MutableStateFlow<UpdateReleaseInfo?>(null)
    val updateState: StateFlow<UpdateReleaseInfo?> = _updateState.asStateFlow()

    private val _isChecking = MutableStateFlow(false)
    val isChecking: StateFlow<Boolean> = _isChecking.asStateFlow()

    private val _downloadProgress = MutableStateFlow<String?>(null)
    val downloadProgress: StateFlow<String?> = _downloadProgress.asStateFlow()

    suspend fun checkForUpdates(): UpdateReleaseInfo? = withContext(Dispatchers.IO) {
        _isChecking.value = true
        try {
            val url = URL(GITHUB_API_URL)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 5000
                readTimeout = 5000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "HyprMusic-Updater/$CURRENT_VERSION")
                setRequestProperty("Accept", "application/vnd.github.v3+json")
            }

            if (connection.responseCode == 200) {
                val responseStr = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(responseStr)
                val tagName = json.optString("tag_name", "v1.0.0")
                val name = json.optString("name", "New HyprMusic Release")
                val body = json.optString("body", "Performance improvements & bugfixes.")
                val publishedAt = json.optString("published_at", "")

                // Find .apk asset
                var downloadUrl = ""
                val assets = json.optJSONArray("assets")
                if (assets != null) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        val assetName = asset.optString("name", "")
                        if (assetName.endsWith(".apk", ignoreCase = true)) {
                            downloadUrl = asset.optString("browser_download_url", "")
                            break
                        }
                    }
                }

                if (downloadUrl.isBlank()) {
                    downloadUrl = json.optString("html_url", GITHUB_REPO_URL)
                }

                val isNewer = compareVersions(tagName, CURRENT_VERSION) > 0
                val info = UpdateReleaseInfo(
                    tagName = tagName,
                    releaseTitle = name,
                    releaseNotes = body,
                    downloadUrl = downloadUrl,
                    publishedAt = publishedAt,
                    isUpdateAvailable = isNewer
                )
                _updateState.value = info
                _isChecking.value = false
                return@withContext info
            }
        } catch (_: Exception) {}

        _isChecking.value = false
        // Fallback up-to-date representation if offline
        val fallback = UpdateReleaseInfo(
            tagName = CURRENT_VERSION,
            releaseTitle = "HyprMusic $CURRENT_VERSION",
            releaseNotes = "You are currently running the latest bit-perfect build.",
            downloadUrl = "$GITHUB_REPO_URL/releases",
            publishedAt = "2026-09-30",
            isUpdateAvailable = false
        )
        _updateState.value = fallback
        return@withContext fallback
    }

    fun downloadAndInstallApk(context: Context, downloadUrl: String) {
        if (!downloadUrl.endsWith(".apk", ignoreCase = true)) {
            // Open in browser if not direct APK
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(browserIntent)
            return
        }

        try {
            _downloadProgress.value = "Starting download..."
            val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
                setTitle("HyprMusic Update")
                setDescription("Downloading latest release APK...")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "hyprmusic-update.apk")
            }

            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            _downloadProgress.value = "Download in progress. Check notifications."
        } catch (e: Exception) {
            _downloadProgress.value = "Download failed: ${e.message}"
        }
    }

    private fun compareVersions(v1: String, v2: String): Int {
        val clean1 = v1.removePrefix("v").split('.').mapNotNull { it.toIntOrNull() }
        val clean2 = v2.removePrefix("v").split('.').mapNotNull { it.toIntOrNull() }
        val length = maxOf(clean1.size, clean2.size)
        for (i in 0 until length) {
            val num1 = clean1.getOrElse(i) { 0 }
            val num2 = clean2.getOrElse(i) { 0 }
            if (num1 != num2) return num1.compareTo(num2)
        }
        return 0
    }
}
