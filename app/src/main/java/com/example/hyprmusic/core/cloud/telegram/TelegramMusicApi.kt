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
                if (response.code == 401 || response.code == 403) {
                    val errorBody = response.body?.string().orEmpty()
                    var reason = if (response.code == 401) {
                        "Unauthorized: Invalid or missing API secret key."
                    } else {
                        "Access Denied: Telegram user ID is not authorized in server whitelist or channel."
                    }
                    var botUser: String? = null

                    try {
                        val parsed = json.parseToJsonElement(errorBody)
                        if (parsed is kotlinx.serialization.json.JsonObject) {
                            parsed["message"]?.let { reason = it.toString().trim('"') }
                            parsed["detail"]?.let { reason = it.toString().trim('"') }
                            parsed["error"]?.let { reason = it.toString().trim('"') }
                            parsed["bot_username"]?.let { botUser = it.toString().trim('"') }
                            parsed["bot"]?.let { botUser = it.toString().trim('"') }
                        }
                    } catch (_: Exception) {}

                    return@withContext Result.failure(
                        CloudAccessDeniedException(
                            httpCode = response.code,
                            message = reason,
                            botUsername = botUser,
                            userId = userId
                        )
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

    suspend fun requestDirectAccess(
        serverUrl: String,
        userId: Long,
        note: String = "Requested from HyprMusic client"
    ): Result<String> = withContext(Dispatchers.IO) {
        val base = serverUrl.trim().removeSuffix("/")
        val url = "$base/api/access/request"
        try {
            val jsonPayload = """{"user_id":$userId,"note":"$note","device":"HyprMusic-Android"}"""
            val mediaType = okhttp3.MediaType.Companion.run { "application/json; charset=utf-8".toMediaType() }
            val reqBody = okhttp3.RequestBody.Companion.run { jsonPayload.toRequestBody(mediaType) }
            val req = Request.Builder()
                .url(url)
                .post(reqBody)
                .header("User-Agent", "HyprMusic/1.9.7 (Android)")
                .build()

            client.newCall(req).execute().use { response ->
                if (response.isSuccessful) {
                    Result.success("Access request dispatched. Admin notified on Telegram.")
                } else if (response.code == 404) {
                    Result.failure(IOException("Server does not have automated in-app access requests enabled. Please use the Telegram Bot direct link."))
                } else {
                    Result.failure(IOException("Server returned HTTP ${response.code}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
