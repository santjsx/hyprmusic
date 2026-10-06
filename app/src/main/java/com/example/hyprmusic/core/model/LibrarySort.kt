package com.example.hyprmusic.core.model

import android.content.Context
import android.content.SharedPreferences
import com.example.hyprmusic.core.data.CustomPlaylist
import java.text.Collator
import java.util.Locale

/**
 * Sorting criteria for Track lists (TRACKS, HI-RES FLAC, FAVORITES).
 */
enum class TrackSortOption(val id: String, val label: String, val shortLabel: String) {
    RECENTLY_ADDED("recent", "Recently Added", "RECENT ▼"),
    ADDED_FIRST("oldest", "Added First (Oldest)", "OLDEST ▲"),
    TITLE_A_Z("title_asc", "Title (A to Z)", "A-Z ▲"),
    TITLE_Z_A("title_desc", "Title (Z to A)", "Z-A ▼"),
    ARTIST_A_Z("artist_asc", "Artist (A to Z)", "ARTIST ▲"),
    ARTIST_Z_A("artist_desc", "Artist (Z to A)", "ARTIST ▼"),
    ALBUM_A_Z("album_asc", "Album (A to Z)", "ALBUM ▲"),
    DURATION_DESC("duration_desc", "Duration (Longest)", "LONG ▼"),
    DURATION_ASC("duration_asc", "Duration (Shortest)", "SHORT ▲"),
    MOST_PLAYED("most_played", "Most Played", "POPULAR ▼"),
    AUDIO_QUALITY("quality", "Audio Quality (Hi-Res first)", "HI-RES ★");

    companion object {
        fun fromId(id: String?): TrackSortOption =
            entries.firstOrNull { it.id == id } ?: RECENTLY_ADDED
    }
}

/**
 * Sorting criteria for Albums tab.
 */
enum class AlbumSortOption(val id: String, val label: String, val shortLabel: String) {
    TITLE_A_Z("title_asc", "Album Title (A to Z)", "A-Z ▲"),
    TITLE_Z_A("title_desc", "Album Title (Z to A)", "Z-A ▼"),
    ARTIST_A_Z("artist_asc", "Artist Name (A to Z)", "ARTIST ▲"),
    YEAR_NEWEST("year_desc", "Release Year (Newest)", "YEAR ▼"),
    YEAR_OLDEST("year_asc", "Release Year (Oldest)", "YEAR ▲"),
    TRACK_COUNT_DESC("tracks_desc", "Track Count (Most)", "TRACKS ▼");

    companion object {
        fun fromId(id: String?): AlbumSortOption =
            entries.firstOrNull { it.id == id } ?: TITLE_A_Z
    }
}

/**
 * Sorting criteria for Artists tab.
 */
enum class ArtistSortOption(val id: String, val label: String, val shortLabel: String) {
    NAME_A_Z("name_asc", "Artist Name (A to Z)", "A-Z ▲"),
    NAME_Z_A("name_desc", "Artist Name (Z to A)", "Z-A ▼"),
    TRACK_COUNT_DESC("tracks_desc", "Track Count (Most)", "TRACKS ▼"),
    ALBUM_COUNT_DESC("albums_desc", "Album Count (Most)", "ALBUMS ▼");

    companion object {
        fun fromId(id: String?): ArtistSortOption =
            entries.firstOrNull { it.id == id } ?: NAME_A_Z
    }
}

/**
 * Sorting criteria for Playlists tab.
 */
enum class PlaylistSortOption(val id: String, val label: String, val shortLabel: String) {
    RECENTLY_CREATED("created_desc", "Recently Created", "RECENT ▼"),
    NAME_A_Z("name_asc", "Playlist Name (A to Z)", "A-Z ▲"),
    NAME_Z_A("name_desc", "Playlist Name (Z to A)", "Z-A ▼"),
    TRACK_COUNT_DESC("tracks_desc", "Track Count (Most)", "TRACKS ▼");

    companion object {
        fun fromId(id: String?): PlaylistSortOption =
            entries.firstOrNull { it.id == id } ?: RECENTLY_CREATED
    }
}

/**
 * High-performance, case-insensitive, locale-aware string comparator.
 */
private val collator: Collator = Collator.getInstance(Locale.getDefault()).apply {
    strength = Collator.PRIMARY
}

/**
 * Stable, null-safe sorting extensions for Track lists.
 */
fun List<Track>.sortTracks(option: TrackSortOption): List<Track> {
    if (size <= 1) return this
    return when (option) {
        TrackSortOption.RECENTLY_ADDED -> sortedWith(
            compareByDescending<Track> { it.dateAdded }
                .thenBy(collator) { it.title }
        )
        TrackSortOption.ADDED_FIRST -> sortedWith(
            compareBy<Track> { if (it.dateAdded > 0) it.dateAdded else Long.MAX_VALUE }
                .thenBy(collator) { it.title }
        )
        TrackSortOption.TITLE_A_Z -> sortedWith(
            compareBy(collator, Track::title)
                .thenBy(collator, Track::artist)
        )
        TrackSortOption.TITLE_Z_A -> sortedWith(
            compareByDescending(collator, Track::title)
                .thenBy(collator, Track::artist)
        )
        TrackSortOption.ARTIST_A_Z -> sortedWith(
            compareBy(collator, Track::artist)
                .thenBy(collator, Track::album)
                .thenBy(collator, Track::title)
        )
        TrackSortOption.ARTIST_Z_A -> sortedWith(
            compareByDescending(collator, Track::artist)
                .thenBy(collator, Track::title)
        )
        TrackSortOption.ALBUM_A_Z -> sortedWith(
            compareBy(collator, Track::album)
                .thenBy(collator, Track::title)
        )
        TrackSortOption.DURATION_DESC -> sortedWith(
            compareByDescending<Track> { it.durationMs }
                .thenBy(collator) { it.title }
        )
        TrackSortOption.DURATION_ASC -> sortedWith(
            compareBy<Track> { it.durationMs }
                .thenBy(collator) { it.title }
        )
        TrackSortOption.MOST_PLAYED -> sortedWith(
            compareByDescending<Track> { it.playCount }
                .thenByDescending { it.dateAdded }
                .thenBy(collator) { it.title }
        )
        TrackSortOption.AUDIO_QUALITY -> sortedWith(
            compareByDescending<Track> { it.isLossless }
                .thenByDescending { it.sampleRate }
                .thenByDescending { it.bitrate }
                .thenBy(collator) { it.title }
        )
    }
}

/**
 * Stable sorting extensions for Album lists.
 */
fun List<Album>.sortAlbums(option: AlbumSortOption): List<Album> {
    if (size <= 1) return this
    return when (option) {
        AlbumSortOption.TITLE_A_Z -> sortedWith(
            compareBy(collator, Album::title)
                .thenBy(collator, Album::artist)
        )
        AlbumSortOption.TITLE_Z_A -> sortedWith(
            compareByDescending(collator, Album::title)
                .thenBy(collator, Album::artist)
        )
        AlbumSortOption.ARTIST_A_Z -> sortedWith(
            compareBy(collator, Album::artist)
                .thenBy(collator, Album::title)
        )
        AlbumSortOption.YEAR_NEWEST -> sortedWith(
            compareByDescending<Album> { it.releaseYear ?: 0 }
                .thenBy(collator) { it.title }
        )
        AlbumSortOption.YEAR_OLDEST -> sortedWith(
            compareBy<Album> { it.releaseYear ?: Int.MAX_VALUE }
                .thenBy(collator) { it.title }
        )
        AlbumSortOption.TRACK_COUNT_DESC -> sortedWith(
            compareByDescending<Album> { it.trackCount }
                .thenBy(collator) { it.title }
        )
    }
}

/**
 * Stable sorting extensions for Artist lists.
 */
fun List<Artist>.sortArtists(option: ArtistSortOption): List<Artist> {
    if (size <= 1) return this
    return when (option) {
        ArtistSortOption.NAME_A_Z -> sortedWith(
            compareBy(collator, Artist::name)
        )
        ArtistSortOption.NAME_Z_A -> sortedWith(
            compareByDescending(collator, Artist::name)
        )
        ArtistSortOption.TRACK_COUNT_DESC -> sortedWith(
            compareByDescending<Artist> { it.trackCount }
                .thenBy(collator) { it.name }
        )
        ArtistSortOption.ALBUM_COUNT_DESC -> sortedWith(
            compareByDescending<Artist> { it.albumCount }
                .thenBy(collator) { it.name }
        )
    }
}

/**
 * Stable sorting extensions for Playlist lists.
 */
fun List<CustomPlaylist>.sortPlaylists(option: PlaylistSortOption): List<CustomPlaylist> {
    if (size <= 1) return this
    return when (option) {
        PlaylistSortOption.RECENTLY_CREATED -> sortedWith(
            compareByDescending<CustomPlaylist> { it.createdAt }
                .thenBy(collator) { it.name }
        )
        PlaylistSortOption.NAME_A_Z -> sortedWith(
            compareBy(collator, CustomPlaylist::name)
        )
        PlaylistSortOption.NAME_Z_A -> sortedWith(
            compareByDescending(collator, CustomPlaylist::name)
        )
        PlaylistSortOption.TRACK_COUNT_DESC -> sortedWith(
            compareByDescending<CustomPlaylist> { it.trackIds.size }
                .thenBy(collator) { it.name }
        )
    }
}

/**
 * Lightweight persistence manager for per-tab library sorting preferences.
 */
object LibrarySortPreferences {
    private const val PREFS_NAME = "hypr_library_sort_prefs"
    private const val KEY_TRACK_SORT = "track_sort"
    private const val KEY_ALBUM_SORT = "album_sort"
    private const val KEY_ARTIST_SORT = "artist_sort"
    private const val KEY_PLAYLIST_SORT = "playlist_sort"

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getTrackSort(context: Context): TrackSortOption {
        val id = getPrefs(context).getString(KEY_TRACK_SORT, TrackSortOption.RECENTLY_ADDED.id)
        return TrackSortOption.fromId(id)
    }

    fun setTrackSort(context: Context, option: TrackSortOption) {
        getPrefs(context).edit().putString(KEY_TRACK_SORT, option.id).apply()
    }

    fun getAlbumSort(context: Context): AlbumSortOption {
        val id = getPrefs(context).getString(KEY_ALBUM_SORT, AlbumSortOption.TITLE_A_Z.id)
        return AlbumSortOption.fromId(id)
    }

    fun setAlbumSort(context: Context, option: AlbumSortOption) {
        getPrefs(context).edit().putString(KEY_ALBUM_SORT, option.id).apply()
    }

    fun getArtistSort(context: Context): ArtistSortOption {
        val id = getPrefs(context).getString(KEY_ARTIST_SORT, ArtistSortOption.NAME_A_Z.id)
        return ArtistSortOption.fromId(id)
    }

    fun setArtistSort(context: Context, option: ArtistSortOption) {
        getPrefs(context).edit().putString(KEY_ARTIST_SORT, option.id).apply()
    }

    fun getPlaylistSort(context: Context): PlaylistSortOption {
        val id = getPrefs(context).getString(KEY_PLAYLIST_SORT, PlaylistSortOption.RECENTLY_CREATED.id)
        return PlaylistSortOption.fromId(id)
    }

    fun setPlaylistSort(context: Context, option: PlaylistSortOption) {
        getPrefs(context).edit().putString(KEY_PLAYLIST_SORT, option.id).apply()
    }
}
