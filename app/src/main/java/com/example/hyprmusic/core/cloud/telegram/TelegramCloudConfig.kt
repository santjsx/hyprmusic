package com.example.hyprmusic.core.cloud.telegram

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TelegramCloudSettings(
    val serverUrl: String = "http://10.0.2.2:8080",
    val userId: Long = 0L,
    val apiSecretKey: String = "",
    val autoSyncOnStartup: Boolean = true,
    val isEnabled: Boolean = false,
    val lastSyncTimeMs: Long = 0L,
    val cachedTrackCount: Int = 0
)

class TelegramCloudConfig private constructor(context: Context) {

    companion object {
        private const val PREFS_NAME = "hypr_tpmc_cloud_prefs"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_API_SECRET_KEY = "api_secret_key"
        private const val KEY_AUTO_SYNC = "auto_sync"
        private const val KEY_ENABLED = "is_enabled"
        private const val KEY_LAST_SYNC_TIME = "last_sync_time"
        private const val KEY_CACHED_TRACK_COUNT = "cached_track_count"

        @Volatile
        private var instance: TelegramCloudConfig? = null

        fun getInstance(context: Context): TelegramCloudConfig {
            return instance ?: synchronized(this) {
                instance ?: TelegramCloudConfig(context.applicationContext).also { instance = it }
            }
        }
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<TelegramCloudSettings> = _settings.asStateFlow()

    private fun loadSettings(): TelegramCloudSettings {
        return TelegramCloudSettings(
            serverUrl = prefs.getString(KEY_SERVER_URL, "http://10.0.2.2:8080") ?: "http://10.0.2.2:8080",
            userId = prefs.getLong(KEY_USER_ID, 0L),
            apiSecretKey = prefs.getString(KEY_API_SECRET_KEY, "") ?: "",
            autoSyncOnStartup = prefs.getBoolean(KEY_AUTO_SYNC, true),
            isEnabled = prefs.getBoolean(KEY_ENABLED, false),
            lastSyncTimeMs = prefs.getLong(KEY_LAST_SYNC_TIME, 0L),
            cachedTrackCount = prefs.getInt(KEY_CACHED_TRACK_COUNT, 0)
        )
    }

    fun updateSettings(
        serverUrl: String,
        userId: Long,
        apiSecretKey: String,
        isEnabled: Boolean = true,
        autoSyncOnStartup: Boolean = true
    ) {
        val cleanUrl = serverUrl.trim().removeSuffix("/")
        prefs.edit()
            .putString(KEY_SERVER_URL, cleanUrl)
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_API_SECRET_KEY, apiSecretKey.trim())
            .putBoolean(KEY_ENABLED, isEnabled)
            .putBoolean(KEY_AUTO_SYNC, autoSyncOnStartup)
            .apply()

        _settings.value = _settings.value.copy(
            serverUrl = cleanUrl,
            userId = userId,
            apiSecretKey = apiSecretKey.trim(),
            isEnabled = isEnabled,
            autoSyncOnStartup = autoSyncOnStartup
        )
    }

    fun recordSyncSuccess(trackCount: Int) {
        val now = System.currentTimeMillis()
        prefs.edit()
            .putLong(KEY_LAST_SYNC_TIME, now)
            .putInt(KEY_CACHED_TRACK_COUNT, trackCount)
            .apply()

        _settings.value = _settings.value.copy(
            lastSyncTimeMs = now,
            cachedTrackCount = trackCount
        )
    }

    fun isConfigured(): Boolean {
        val s = _settings.value
        return s.serverUrl.isNotBlank() && (s.userId > 0L || s.apiSecretKey.isNotBlank())
    }
}
