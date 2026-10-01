package com.example.hyprmusic.core.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import androidx.compose.runtime.Immutable
import java.io.File
import java.util.UUID

@Immutable
@Serializable
data class CustomPlaylist(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val trackIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

class PlaylistRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val file = File(context.filesDir, "playlists.json")
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _playlists = MutableStateFlow<List<CustomPlaylist>>(loadFromDisk())
    val playlists: StateFlow<List<CustomPlaylist>> = _playlists.asStateFlow()

    private fun loadFromDisk(): List<CustomPlaylist> {
        return try {
            if (!file.exists()) return emptyList()
            val text = file.readText()
            if (text.isBlank()) return emptyList()
            json.decodeFromString<List<CustomPlaylist>>(text)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun saveToDisk(playlists: List<CustomPlaylist>) {
        scope.launch {
            try {
                val text = json.encodeToString(playlists)
                file.writeText(text)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createPlaylist(name: String): CustomPlaylist? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return null

        val newPlaylist = CustomPlaylist(name = trimmed)
        _playlists.update { current ->
            val updated = current + newPlaylist
            saveToDisk(updated)
            updated
        }
        return newPlaylist
    }

    fun toggleTrackInPlaylist(playlistId: String, trackId: String): Boolean {
        var added = false
        _playlists.update { current ->
            val updated = current.map { pl ->
                if (pl.id == playlistId) {
                    if (pl.trackIds.contains(trackId)) {
                        added = false
                        pl.copy(trackIds = pl.trackIds - trackId)
                    } else {
                        added = true
                        pl.copy(trackIds = pl.trackIds + trackId)
                    }
                } else {
                    pl
                }
            }
            saveToDisk(updated)
            updated
        }
        return added
    }

    fun addTrackToPlaylist(playlistId: String, trackId: String): Boolean {
        var didAdd = false
        _playlists.update { current ->
            val updated = current.map { pl ->
                if (pl.id == playlistId && !pl.trackIds.contains(trackId)) {
                    didAdd = true
                    pl.copy(trackIds = pl.trackIds + trackId)
                } else {
                    pl
                }
            }
            saveToDisk(updated)
            updated
        }
        return didAdd
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: String): Boolean {
        var didRemove = false
        _playlists.update { current ->
            val updated = current.map { pl ->
                if (pl.id == playlistId && pl.trackIds.contains(trackId)) {
                    didRemove = true
                    pl.copy(trackIds = pl.trackIds - trackId)
                } else {
                    pl
                }
            }
            saveToDisk(updated)
            updated
        }
        return didRemove
    }

    fun deletePlaylist(playlistId: String) {
        _playlists.update { current ->
            val updated = current.filter { it.id != playlistId }
            saveToDisk(updated)
            updated
        }
    }

    fun renamePlaylist(playlistId: String, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isEmpty()) return
        _playlists.update { current ->
            val updated = current.map {
                if (it.id == playlistId) it.copy(name = trimmed) else it
            }
            saveToDisk(updated)
            updated
        }
    }
}
