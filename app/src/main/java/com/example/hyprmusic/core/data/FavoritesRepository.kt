package com.example.hyprmusic.core.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FavoritesRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("hypr_favorites_prefs", Context.MODE_PRIVATE)
    private val keyFavorites = "favorite_track_ids"

    private val _favoriteIds = MutableStateFlow<Set<String>>(loadFavorites())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private fun loadFavorites(): Set<String> {
        return prefs.getStringSet(keyFavorites, emptySet())?.toSet() ?: emptySet()
    }

    fun isFavorite(trackId: String): Boolean {
        return _favoriteIds.value.contains(trackId)
    }

    fun toggleFavorite(trackId: String): Boolean {
        val current = _favoriteIds.value.toMutableSet()
        val isNowFav = if (current.contains(trackId)) {
            current.remove(trackId)
            false
        } else {
            current.add(trackId)
            true
        }
        prefs.edit().putStringSet(keyFavorites, current).apply()
        _favoriteIds.value = current
        return isNowFav
    }
}
