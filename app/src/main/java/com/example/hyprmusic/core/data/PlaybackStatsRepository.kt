package com.example.hyprmusic.core.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaybackStatsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("hypr_playback_stats", Context.MODE_PRIVATE)

    private val _playCountsChanged = MutableStateFlow(0L)
    val playCountsChanged: StateFlow<Long> = _playCountsChanged.asStateFlow()

    fun recordPlay(trackId: String) {
        val currentCount = prefs.getInt("play_count_$trackId", 0)
        prefs.edit()
            .putInt("play_count_$trackId", currentCount + 1)
            .putLong("last_played_$trackId", System.currentTimeMillis())
            .apply()
        _playCountsChanged.value = System.currentTimeMillis()
    }

    fun getPlayCount(trackId: String): Int {
        return prefs.getInt("play_count_$trackId", 0)
    }

    fun getLastPlayedTime(trackId: String): Long {
        return prefs.getLong("last_played_$trackId", 0L)
    }
}
