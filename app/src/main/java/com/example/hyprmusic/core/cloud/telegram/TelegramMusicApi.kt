package com.example.hyprmusic.core.cloud.telegram

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

object TelegramMusicApi {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    suspend fun checkHealth(serverUrl: String): Result<ServerHealthInfo> = withContext(Dispatchers.IO) {
        val base = serverUrl.trim().removeSuffix("/")
        val url = "$base/health"

        try {
            val request = Request.Builder()
                .url(url)
                .get()
                .header("User-Agent", "HyprMusic/1.2.0 (Android)")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        IOException("Server health check failed with HTTP ${response.code}")
                    )
                }

                val body = response.body?.string() ?: "{}"
                val healthInfo = json.decodeFromString<ServerHealthInfo>(body)
                Result.success(healthInfo)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchLibrary(
        serverUrl: String,
        userId: Long,
        apiSecretKey: String?
    ): Result<TelegramLibraryResponseDto> = withContext(Dispatchers.IO) {
        val base = serverUrl.trim().removeSuffix("/")
        val tokenParam = if (!apiSecretKey.isNullOrBlank()) "&token=${apiSecretKey.trim()}" else ""
        val url = "$base/api/library?user_id=$userId$tokenParam"

        try {
            val reqBuilder = Request.Builder()
                .url(url)
                .get()
                .header("User-Agent", "HyprMusic/1.2.0 (Android)")
                .header("X-User-Id", userId.toString())

            if (!apiSecretKey.isNullOrBlank()) {
                reqBuilder.header("X-Api-Key", apiSecretKey.trim())
                reqBuilder.header("Authorization", "Bearer ${apiSecretKey.trim()}")
            }

            client.newCall(reqBuilder.build()).execute().use { response ->
                if (response.code == 403) {
                    return@withContext Result.failure(
                        SecurityException("Access denied by TPMC. Ensure your User ID or API Secret is authorized in the server's access control.")
                    )
                }
                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        IOException("Failed to load catalog: HTTP ${response.code}")
                    )
                }

                val body = response.body?.string() ?: ""
                val libraryData = json.decodeFromString<TelegramLibraryResponseDto>(body)
                Result.success(libraryData)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
