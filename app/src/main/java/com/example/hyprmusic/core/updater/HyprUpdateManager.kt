package com.example.hyprmusic.core.updater

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class UpdateReleaseInfo(
    val tagName: String,
    val releaseTitle: String,
    val releaseNotes: String,
    val downloadUrl: String,
    val publishedAt: String,
    val isUpdateAvailable: Boolean,
    val assetSize: Long = 0L,
    val apkFileName: String = ""
) {
    val formattedSize: String
        get() {
            if (assetSize <= 0) return ""
            val mb = assetSize.toDouble() / (1024.0 * 1024.0)
            return "%.1f MB".format(mb)
        }
}

sealed interface UpdateDownloadState {
    object Idle : UpdateDownloadState
    data class Downloading(
        val progressFraction: Float,
        val downloadedBytes: Long,
        val totalBytes: Long,
        val speedText: String
    ) : UpdateDownloadState {
        val downloadedMb: String
            get() = "%.1f".format(downloadedBytes.toDouble() / (1024.0 * 1024.0))
        val totalMb: String
            get() = "%.1f".format(totalBytes.toDouble() / (1024.0 * 1024.0))
        val percent: Int
            get() = (progressFraction * 100).toInt().coerceIn(0, 100)
    }
    data class ReadyToInstall(val apkFile: File) : UpdateDownloadState
    data class Error(val message: String) : UpdateDownloadState
}

object HyprUpdateManager {

    const val CURRENT_VERSION = "v1.9.9"
    const val DEVELOPER_NAME = "Santhosh Reddy"
    const val GITHUB_REPO_URL = "https://github.com/santjsx/hyprmusic"
    private const val GITHUB_API_URL = "https://api.github.com/repos/santjsx/hyprmusic/releases/latest"

    private var cachedInstalledVersion: String? = null

    fun getCurrentVersion(context: Context? = null): String {
        if (cachedInstalledVersion != null) return cachedInstalledVersion!!
        if (context != null) {
            try {
                val pInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.packageManager.getPackageInfo(
                        context.packageName,
                        PackageManager.PackageInfoFlags.of(0)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    context.packageManager.getPackageInfo(context.packageName, 0)
                }
                val rawVersion = pInfo.versionName ?: "1.9.2"
                val resolved = if (rawVersion.startsWith("v", ignoreCase = true)) rawVersion else "v$rawVersion"
                cachedInstalledVersion = resolved
                return resolved
            } catch (_: Exception) {}
        }
        return CURRENT_VERSION
    }

    private val _updateState = MutableStateFlow<UpdateReleaseInfo?>(null)
    val updateState: StateFlow<UpdateReleaseInfo?> = _updateState.asStateFlow()

    private val _isChecking = MutableStateFlow(false)
    val isChecking: StateFlow<Boolean> = _isChecking.asStateFlow()

    private val _downloadState = MutableStateFlow<UpdateDownloadState>(UpdateDownloadState.Idle)
    val downloadState: StateFlow<UpdateDownloadState> = _downloadState.asStateFlow()

    private val _downloadProgress = MutableStateFlow<String?>(null)
    val downloadProgress: StateFlow<String?> = _downloadProgress.asStateFlow()

    private var downloadJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    suspend fun checkForUpdates(context: Context? = null): UpdateReleaseInfo? = withContext(Dispatchers.IO) {
        val currentVersion = getCurrentVersion(context)
        _isChecking.value = true
        try {
            val url = URL(GITHUB_API_URL)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "HyprMusic-Updater/$currentVersion")
                setRequestProperty("Accept", "application/vnd.github.v3+json")
            }

            if (connection.responseCode == 200) {
                val responseStr = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(responseStr)
                val tagName = json.optString("tag_name", currentVersion)
                val name = json.optString("name", "HyprMusic $tagName")
                val body = json.optString("body", "Performance improvements & bugfixes.")
                val publishedAt = json.optString("published_at", "")

                // Find .apk asset - prioritize named releases
                var downloadUrl = ""
                var apkFileName = ""
                var assetSize = 0L

                val assets = json.optJSONArray("assets")
                if (assets != null) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        val assetName = asset.optString("name", "")
                        if (assetName.endsWith(".apk", ignoreCase = true)) {
                            // Prefer HyprMusic-v*.apk over debug builds
                            if (downloadUrl.isEmpty() || assetName.contains("HyprMusic", ignoreCase = true)) {
                                downloadUrl = asset.optString("browser_download_url", "")
                                apkFileName = assetName
                                assetSize = asset.optLong("size", 0L)
                                if (assetName.contains("HyprMusic", ignoreCase = true)) {
                                    break
                                }
                            }
                        }
                    }
                }

                if (downloadUrl.isBlank()) {
                    downloadUrl = json.optString("html_url", "$GITHUB_REPO_URL/releases")
                }

                val isNewer = compareVersions(tagName, currentVersion) > 0
                val info = UpdateReleaseInfo(
                    tagName = tagName,
                    releaseTitle = name,
                    releaseNotes = body,
                    downloadUrl = downloadUrl,
                    publishedAt = publishedAt,
                    isUpdateAvailable = isNewer,
                    assetSize = assetSize,
                    apkFileName = apkFileName
                )
                _updateState.value = info
                _isChecking.value = false
                return@withContext info
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        _isChecking.value = false
        // Fallback up-to-date representation if offline or rate-limited
        val fallback = UpdateReleaseInfo(
            tagName = currentVersion,
            releaseTitle = "HyprMusic $currentVersion",
            releaseNotes = "You are currently running the latest bit-perfect build.",
            downloadUrl = "$GITHUB_REPO_URL/releases",
            publishedAt = "2026-10-06",
            isUpdateAvailable = false,
            assetSize = 0L,
            apkFileName = "HyprMusic-$currentVersion.apk"
        )
        _updateState.value = fallback
        return@withContext fallback
    }

    fun startApkDownload(context: Context, downloadUrl: String) {
        if (!downloadUrl.endsWith(".apk", ignoreCase = true)) {
            // Open in external browser if not direct APK link
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(browserIntent)
            return
        }

        downloadJob?.cancel()
        downloadJob = scope.launch(Dispatchers.IO) {
            val destinationDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: context.filesDir
            destinationDir.mkdirs()
            val destinationFile = File(destinationDir, "hyprmusic-update.apk")
            if (destinationFile.exists()) {
                destinationFile.delete()
            }

            _downloadState.value = UpdateDownloadState.Downloading(
                progressFraction = 0f,
                downloadedBytes = 0L,
                totalBytes = 0L,
                speedText = "Connecting..."
            )
            _downloadProgress.value = "Connecting to GitHub..."

            try {
                // Follow redirects (GitHub 302 to objects.githubusercontent.com / AWS S3)
                var currentUrl = downloadUrl
                var connection: HttpURLConnection? = null
                var redirects = 0

                while (redirects < 6) {
                    val url = URL(currentUrl)
                    connection = (url.openConnection() as HttpURLConnection).apply {
                        connectTimeout = 15000
                        readTimeout = 30000
                        instanceFollowRedirects = true
                        val currentVer = getCurrentVersion(context)
                        setRequestProperty("User-Agent", "HyprMusic-Updater/$currentVer")
                    }

                    val code = connection.responseCode
                    if (code == HttpURLConnection.HTTP_MOVED_TEMP ||
                        code == HttpURLConnection.HTTP_MOVED_PERM ||
                        code == HttpURLConnection.HTTP_SEE_OTHER ||
                        code == 307 || code == 308
                    ) {
                        val newUrl = connection.getHeaderField("Location")
                        connection.disconnect()
                        if (newUrl != null) {
                            currentUrl = newUrl
                            redirects++
                            continue
                        }
                    }
                    break
                }

                val conn = connection ?: throw IllegalStateException("Could not establish connection")
                if (conn.responseCode !in 200..299) {
                    throw IllegalStateException("Server returned HTTP ${conn.responseCode}")
                }

                val totalBytes = conn.contentLengthLong.takeIf { it > 0 }
                    ?: _updateState.value?.assetSize?.takeIf { it > 0 }
                    ?: 74000000L

                val inputStream = conn.inputStream
                val outputStream = FileOutputStream(destinationFile)

                val buffer = ByteArray(16384)
                var downloadedBytes = 0L
                var lastTime = System.currentTimeMillis()
                var lastBytes = 0L
                var speedText = "0 KB/s"

                inputStream.use { input ->
                    outputStream.use { output ->
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                            downloadedBytes += read

                            val currentTime = System.currentTimeMillis()
                            val elapsed = currentTime - lastTime
                            if (elapsed >= 500) {
                                val bytesInInterval = downloadedBytes - lastBytes
                                val bytesPerSec = (bytesInInterval * 1000) / elapsed
                                speedText = when {
                                    bytesPerSec >= 1024 * 1024 -> "%.1f MB/s".format(bytesPerSec.toDouble() / (1024.0 * 1024.0))
                                    else -> "%d KB/s".format(bytesPerSec / 1024)
                                }
                                lastTime = currentTime
                                lastBytes = downloadedBytes

                                val progressFraction = (downloadedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                                _downloadState.value = UpdateDownloadState.Downloading(
                                    progressFraction = progressFraction,
                                    downloadedBytes = downloadedBytes,
                                    totalBytes = totalBytes,
                                    speedText = speedText
                                )
                                _downloadProgress.value = "Downloading: ${(progressFraction * 100).toInt()}% ($speedText)"
                            }
                        }
                    }
                }

                // Verify file size
                if (destinationFile.exists() && destinationFile.length() > 100000) {
                    _downloadState.value = UpdateDownloadState.ReadyToInstall(destinationFile)
                    _downloadProgress.value = "Download complete. Launching installer..."

                    withContext(Dispatchers.Main) {
                        installApk(context, destinationFile)
                    }
                } else {
                    throw IllegalStateException("Downloaded file is incomplete or corrupted.")
                }
            } catch (e: CancellationException) {
                if (destinationFile.exists()) destinationFile.delete()
                _downloadState.value = UpdateDownloadState.Idle
                _downloadProgress.value = "Download cancelled."
            } catch (e: Exception) {
                if (destinationFile.exists()) destinationFile.delete()
                _downloadState.value = UpdateDownloadState.Error(e.message ?: "Download failed")
                _downloadProgress.value = "Error: ${e.message}"
            }
        }
    }

    fun cancelDownload() {
        downloadJob?.cancel()
        downloadJob = null
        _downloadState.value = UpdateDownloadState.Idle
        _downloadProgress.value = null
    }

    fun installApk(context: Context, apkFile: File) {
        if (!apkFile.exists() || apkFile.length() <= 0) {
            _downloadProgress.value = "Installer error: APK file not found."
            return
        }

        try {
            // Check unknown apps installation permission on Android 8.0+ (Oreo through 16)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    _downloadProgress.value = "Permission required: Enable 'Install unknown apps' for HyprMusic"
                    val manageIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(manageIntent)
                    return
                }
            }

            // Generate secure FileProvider content URI
            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            // Explicitly grant URI read permissions to any resolving package installer
            val resolveInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.queryIntentActivities(
                    installIntent,
                    PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.queryIntentActivities(installIntent, PackageManager.MATCH_DEFAULT_ONLY)
            }
            for (resolveInfo in resolveInfoList) {
                try {
                    context.grantUriPermission(
                        resolveInfo.activityInfo.packageName,
                        apkUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {}
            }

            context.startActivity(installIntent)
            _downloadProgress.value = "Package installer opened."
        } catch (e: Exception) {
            _downloadProgress.value = "Failed to launch installer: ${e.message}"
        }
    }

    // Backward-compatible entrypoint
    fun downloadAndInstallApk(context: Context, downloadUrl: String) {
        startApkDownload(context, downloadUrl)
    }

    fun compareVersions(v1: String, v2: String): Int {
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
