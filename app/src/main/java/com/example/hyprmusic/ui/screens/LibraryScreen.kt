package com.example.hyprmusic.ui.screens

import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.example.hyprmusic.core.cloud.telegram.TelegramMusicRepository
import com.example.hyprmusic.core.data.PlaylistRepository
import com.example.hyprmusic.core.data.CustomPlaylist
import com.example.hyprmusic.core.model.AlbumSortOption
import com.example.hyprmusic.core.model.ArtistSortOption
import com.example.hyprmusic.core.model.LibrarySortPreferences
import com.example.hyprmusic.core.model.PlaylistSortOption
import com.example.hyprmusic.core.model.TrackSortOption
import com.example.hyprmusic.core.model.sortAlbums
import com.example.hyprmusic.core.model.sortArtists
import com.example.hyprmusic.core.model.sortPlaylists
import com.example.hyprmusic.core.model.sortTracks
import com.example.hyprmusic.ui.components.AddToPlaylistDialog
import com.example.hyprmusic.ui.components.MiniEqualizerBars
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.hyprmusic.core.model.Album
import com.example.hyprmusic.core.model.Artist
import com.example.hyprmusic.core.model.PlaybackState
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.core.theming.HyprThemeConfig
import com.example.hyprmusic.core.theming.hyprAnimatedGlow
import com.example.hyprmusic.core.theming.hyprBounceClick
import com.example.hyprmusic.core.theming.hyprTile
import kotlinx.coroutines.launch

enum class LibraryScope(val id: String, val label: String) {
    LOCAL("0", "local"),
    TPMC_CLOUD("1", "tpmc cloud")
}

enum class LibraryFilter(val label: String) {
    ALL("ALL"),
    TRACKS("TRACKS"),
    ALBUMS("ALBUMS"),
    ARTISTS("ARTISTS"),
    PLAYLISTS("PLAYLISTS"),
    HI_RES("HI-RES FLAC"),
    FAVORITES("FAVORITES")
}

private sealed interface LibraryViewState {
    data class Main(val filter: LibraryFilter) : LibraryViewState
    data class AlbumDetail(val album: Album) : LibraryViewState
    data class ArtistDetail(val artist: Artist) : LibraryViewState
    data class PlaylistDetail(val playlist: CustomPlaylist) : LibraryViewState
}

@Composable
fun LibraryScreen(
    theme: HyprThemeConfig,
    playbackState: PlaybackState,
    tracks: List<Track>,
    albums: List<Album>,
    artists: List<Artist>,
    isScanning: Boolean,
    onRescan: () -> Unit,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onToggleFavorite: (String) -> Unit,
    telegramRepository: TelegramMusicRepository? = null,
    playlistRepository: PlaylistRepository? = null,
    initialAlbum: Album? = null,
    onAlbumCleared: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentScope by remember { mutableStateOf(LibraryScope.LOCAL) }
    var viewState by remember { mutableStateOf<LibraryViewState>(LibraryViewState.Main(LibraryFilter.TRACKS)) }
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf(LibraryFilter.TRACKS) }

    val playlists = playlistRepository?.playlists?.collectAsStateWithLifecycle()?.value ?: emptyList()

    BackHandler(enabled = viewState !is LibraryViewState.Main || searchQuery.isNotBlank()) {
        if (searchQuery.isNotBlank()) {
            searchQuery = ""
        } else {
            viewState = LibraryViewState.Main(activeFilter)
            onAlbumCleared()
        }
    }

    LaunchedEffect(initialAlbum) {
        if (initialAlbum != null) {
            viewState = LibraryViewState.AlbumDetail(initialAlbum)
        }
    }

    val cloudTracks = telegramRepository?.cloudTracks?.collectAsStateWithLifecycle()?.value ?: emptyList()

    val coroutineScope = rememberCoroutineScope()
    val tracksListState = rememberLazyListState()
    val albumsGridState = rememberLazyGridState()
    val artistsListState = rememberLazyListState()

    Column(modifier = modifier.fillMaxSize()) {
        // Scope Switcher Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = theme.windowGapsDp.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LibraryScope.entries.forEach { scope ->
                val isScopeActive = currentScope == scope
                val count = if (scope == LibraryScope.LOCAL) tracks.size else cloudTracks.size
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isScopeActive) theme.accentColor.copy(alpha = 0.2f)
                            else theme.surfaceVariantColor
                        )
                        .border(
                            width = 1.dp,
                            color = if (isScopeActive) theme.accentColor.copy(alpha = 0.8f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { currentScope = scope }
                        .padding(vertical = 7.dp, horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (scope == LibraryScope.LOCAL) Icons.Default.Folder else Icons.Default.Cloud,
                            contentDescription = null,
                            tint = if (isScopeActive) theme.accentColor else theme.textSecondaryColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "[${scope.id}:${scope.label}]",
                            color = if (isScopeActive) theme.accentColor else theme.textSecondaryColor,
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isScopeActive) FontWeight.Bold else FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isScopeActive) theme.accentColor.copy(alpha = 0.25f)
                                    else Color.Black.copy(alpha = 0.3f)
                                )
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "$count",
                                color = if (isScopeActive) theme.accentColor else theme.textSecondaryColor,
                                fontSize = 9.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            if (currentScope == LibraryScope.LOCAL) {
                // Smooth transition between screens
                AnimatedContent(
                    targetState = viewState,
                    transitionSpec = {
                        (fadeIn() + slideInHorizontally { it / 3 }) togetherWith (fadeOut() + slideOutHorizontally { -it / 3 })
                    },
                    label = "library_view_transition",
                    modifier = Modifier.fillMaxSize()
                ) { state ->
                    when (state) {
                        is LibraryViewState.Main -> {
                            MainLibraryView(
                                theme = theme,
                                playbackState = playbackState,
                                tracks = tracks,
                                albums = albums,
                                artists = artists,
                                playlists = playlists,
                                playlistRepository = playlistRepository,
                                onOpenPlaylist = { playlist -> viewState = LibraryViewState.PlaylistDetail(playlist) },
                                searchQuery = searchQuery,
                                onSearchQueryChange = { searchQuery = it },
                                activeFilter = activeFilter,
                                onFilterChange = {
                                    activeFilter = it
                                    viewState = LibraryViewState.Main(it)
                                },
                                isScanning = isScanning,
                                onRescan = onRescan,
                                tracksListState = tracksListState,
                                albumsGridState = albumsGridState,
                                artistsListState = artistsListState,
                                onTrackSelected = onTrackSelected,
                                onToggleFavorite = onToggleFavorite,
                                onOpenAlbum = { album -> viewState = LibraryViewState.AlbumDetail(album) },
                                onOpenArtist = { artist -> viewState = LibraryViewState.ArtistDetail(artist) }
                            )
                        }

                        is LibraryViewState.AlbumDetail -> {
                            AlbumDetailView(
                                theme = theme,
                                album = state.album,
                                allTracks = tracks,
                                playbackState = playbackState,
                                onBack = {
                                    viewState = LibraryViewState.Main(activeFilter)
                                    onAlbumCleared()
                                },
                                onTrackSelected = onTrackSelected,
                                onToggleFavorite = onToggleFavorite
                            )
                        }

                        is LibraryViewState.ArtistDetail -> {
                            ArtistDetailView(
                                theme = theme,
                                artist = state.artist,
                                allTracks = tracks,
                                allAlbums = albums,
                                playbackState = playbackState,
                                onBack = { viewState = LibraryViewState.Main(activeFilter) },
                                onTrackSelected = onTrackSelected,
                                onToggleFavorite = onToggleFavorite,
                                onOpenAlbum = { album -> viewState = LibraryViewState.AlbumDetail(album) }
                            )
                        }

                        is LibraryViewState.PlaylistDetail -> {
                            PlaylistDetailView(
                                theme = theme,
                                playlist = state.playlist,
                                allTracks = tracks,
                                playbackState = playbackState,
                                playlistRepository = playlistRepository,
                                onBack = { viewState = LibraryViewState.Main(activeFilter) },
                                onTrackSelected = onTrackSelected,
                                onToggleFavorite = onToggleFavorite
                            )
                        }
                    }
                }
            } else {
                CloudLibraryView(
                    theme = theme,
                    playbackState = playbackState,
                    telegramRepository = telegramRepository,
                    onTrackSelected = onTrackSelected,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun MainLibraryView(
    theme: HyprThemeConfig,
    playbackState: PlaybackState,
    tracks: List<Track>,
    albums: List<Album>,
    artists: List<Artist>,
    playlists: List<CustomPlaylist> = emptyList(),
    playlistRepository: PlaylistRepository? = null,
    onOpenPlaylist: (CustomPlaylist) -> Unit = {},
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    activeFilter: LibraryFilter,
    onFilterChange: (LibraryFilter) -> Unit,
    isScanning: Boolean,
    onRescan: () -> Unit,
    tracksListState: LazyListState,
    albumsGridState: LazyGridState,
    artistsListState: LazyListState,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenAlbum: (Album) -> Unit,
    onOpenArtist: (Artist) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var draggingLetter by remember { mutableStateOf<Char?>(null) }

    // Persistent sorting states per tab
    var trackSort by remember { mutableStateOf(LibrarySortPreferences.getTrackSort(context)) }
    var albumSort by remember { mutableStateOf(LibrarySortPreferences.getAlbumSort(context)) }
    var artistSort by remember { mutableStateOf(LibrarySortPreferences.getArtistSort(context)) }
    var playlistSort by remember { mutableStateOf(LibrarySortPreferences.getPlaylistSort(context)) }
    var isSortModalOpen by remember { mutableStateOf(false) }

    // Fast memoized filtering & stable sorting
    val filteredTracks = remember(tracks, searchQuery, activeFilter, trackSort) {
        val query = searchQuery.trim().lowercase()
        val baseList = when (activeFilter) {
            LibraryFilter.FAVORITES -> tracks.filter { it.isFavorite }
            LibraryFilter.HI_RES -> tracks.filter {
                it.mimeType.contains("flac", ignoreCase = true) ||
                it.mimeType.contains("wav", ignoreCase = true) ||
                it.bitrate >= 320 ||
                it.filePath.endsWith(".flac", ignoreCase = true)
            }
            else -> tracks
        }
        val searched = if (query.isBlank()) baseList
        else baseList.filter {
            it.title.lowercase().contains(query) ||
            it.artist.lowercase().contains(query) ||
            it.album.lowercase().contains(query)
        }
        searched.sortTracks(trackSort)
    }

    val filteredAlbums = remember(albums, searchQuery, albumSort) {
        val query = searchQuery.trim().lowercase()
        val searched = if (query.isBlank()) albums
        else albums.filter {
            it.title.lowercase().contains(query) ||
            it.artist.lowercase().contains(query)
        }
        searched.sortAlbums(albumSort)
    }

    val filteredArtists = remember(artists, searchQuery, artistSort) {
        val query = searchQuery.trim().lowercase()
        val searched = if (query.isBlank()) artists
        else artists.filter { it.name.lowercase().contains(query) }
        searched.sortArtists(artistSort)
    }

    val filteredPlaylists = remember(playlists, searchQuery, playlistSort) {
        val query = searchQuery.trim().lowercase()
        val searched = if (query.isBlank()) playlists
        else playlists.filter { it.name.lowercase().contains(query) }
        searched.sortPlaylists(playlistSort)
    }

    // Precomputed alphabet jump map for instant fast scrolling (only active for alphabetical sort modes)
    val trackAlphabetMap = remember(filteredTracks, trackSort) {
        if (trackSort != TrackSortOption.TITLE_A_Z && trackSort != TrackSortOption.ARTIST_A_Z) {
            emptyMap()
        } else {
            val map = mutableMapOf<Char, Int>()
            filteredTracks.forEachIndexed { index, track ->
                val str = if (trackSort == TrackSortOption.ARTIST_A_Z) track.artist else track.title
                val firstChar = str.firstOrNull()?.uppercaseChar() ?: '#'
                val key = if (firstChar in 'A'..'Z') firstChar else '#'
                if (!map.containsKey(key)) {
                    map[key] = index
                }
            }
            map
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = theme.windowGapsDp.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar & Rescan Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .hyprTile(theme = theme)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = theme.accentColor,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "grep -i library...",
                            color = theme.textSecondaryColor.copy(alpha = 0.6f),
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        textStyle = TextStyle(
                            color = theme.textPrimaryColor,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(theme.accentColor),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = theme.textSecondaryColor,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // Match count badge
                val totalMatches = when (activeFilter) {
                    LibraryFilter.TRACKS, LibraryFilter.HI_RES, LibraryFilter.FAVORITES -> filteredTracks.size
                    LibraryFilter.ALBUMS -> filteredAlbums.size
                    LibraryFilter.ARTISTS -> filteredArtists.size
                    LibraryFilter.PLAYLISTS -> filteredPlaylists.size
                    LibraryFilter.ALL -> filteredTracks.size + filteredAlbums.size + filteredArtists.size + filteredPlaylists.size
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(theme.surfaceVariantColor)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "$totalMatches",
                        color = theme.accentColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Waybar Sort Capsule
                val currentSortLabel = when (activeFilter) {
                    LibraryFilter.TRACKS, LibraryFilter.HI_RES, LibraryFilter.FAVORITES -> trackSort.shortLabel
                    LibraryFilter.ALBUMS -> albumSort.shortLabel
                    LibraryFilter.ARTISTS -> artistSort.shortLabel
                    LibraryFilter.PLAYLISTS -> playlistSort.shortLabel
                    LibraryFilter.ALL -> trackSort.shortLabel
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(theme.surfaceVariantColor)
                        .border(
                            width = 1.dp,
                            color = theme.accentColor.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { isSortModalOpen = true }
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Sort",
                            tint = theme.accentColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = currentSortLabel,
                            color = theme.accentColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "▼",
                            color = theme.accentColor.copy(alpha = 0.7f),
                            fontSize = 7.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onRescan,
                    modifier = Modifier.size(28.dp),
                    enabled = !isScanning
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = theme.accentColor
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Rescan",
                            tint = theme.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Filter Chips Carousel
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LibraryFilter.entries.forEach { filter ->
                    val isSelected = filter == activeFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) theme.accentColor.copy(alpha = 0.22f)
                                else theme.surfaceVariantColor
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) theme.accentColor.copy(alpha = 0.8f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onFilterChange(filter) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "[ ${filter.label} ]",
                            color = if (isSelected) theme.accentColor else theme.textSecondaryColor,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Dynamic Content depending on Filter
            Box(modifier = Modifier.fillMaxSize()) {
                when (activeFilter) {
                    LibraryFilter.TRACKS, LibraryFilter.HI_RES, LibraryFilter.FAVORITES -> {
                        Row(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(
                                state = tracksListState,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
                            ) {
                                items(
                                    items = filteredTracks,
                                    key = { it.id },
                                    contentType = { "track" }
                                ) { track ->
                                    val isPlaying = playbackState.currentTrack?.id == track.id
                                    HyprTrackRow(
                                        theme = theme,
                                        track = track,
                                        isPlaying = isPlaying,
                                        isAudioActive = isPlaying && playbackState.isPlaying,
                                        onClick = { onTrackSelected(track, filteredTracks) },
                                        onFavoriteClick = { onToggleFavorite(track.id) }
                                    )
                                }

                                item { Spacer(modifier = Modifier.height(90.dp)) }
                            }

                            // Alphabet fast scroll sidebar for 1M / 10 Lakh items (only active for alphabetical sort)
                            if (trackAlphabetMap.isNotEmpty()) {
                                HyprAlphabetScroller(
                                    theme = theme,
                                    onLetterSelected = { letter ->
                                        draggingLetter = letter
                                        trackAlphabetMap[letter]?.let { targetIndex ->
                                            coroutineScope.launch {
                                                tracksListState.scrollToItem(targetIndex)
                                            }
                                        }
                                    },
                                    onDragEnd = { draggingLetter = null }
                                )
                            }
                        }
                    }

                    LibraryFilter.ALBUMS -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = albumsGridState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp),
                            horizontalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
                        ) {
                            items(
                                items = filteredAlbums,
                                key = { it.id },
                                contentType = { "album" }
                            ) { album ->
                                HyprAlbumCard(
                                    theme = theme,
                                    album = album,
                                    onClick = { onOpenAlbum(album) }
                                )
                            }

                            item { Spacer(modifier = Modifier.height(90.dp)) }
                        }
                    }

                    LibraryFilter.ARTISTS -> {
                        LazyColumn(
                            state = artistsListState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
                        ) {
                            items(
                                items = filteredArtists,
                                key = { it.id },
                                contentType = { "artist" }
                            ) { artist ->
                                HyprArtistRow(
                                    theme = theme,
                                    artist = artist,
                                    onClick = { onOpenArtist(artist) }
                                )
                            }

                            item { Spacer(modifier = Modifier.height(90.dp)) }
                        }
                    }

                    LibraryFilter.PLAYLISTS -> {
                        PlaylistsGridView(
                            theme = theme,
                            playlists = filteredPlaylists,
                            allTracks = tracks,
                            playlistRepository = playlistRepository,
                            onOpenPlaylist = onOpenPlaylist
                        )
                    }

                    LibraryFilter.ALL -> {
                        // Categorized multi-search results view
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
                        ) {
                            if (filteredArtists.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "ARTISTS (${filteredArtists.size})",
                                        color = theme.accentColor,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                                item {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
                                    ) {
                                        items(
                                            items = filteredArtists.take(10),
                                            key = { it.id },
                                            contentType = { "artist_chip" }
                                        ) { artist ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(theme.surfaceVariantColor)
                                                    .clickable { onOpenArtist(artist) }
                                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Person,
                                                        contentDescription = null,
                                                        tint = theme.accentColor,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = artist.name,
                                                        color = theme.textPrimaryColor,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (filteredAlbums.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "ALBUMS (${filteredAlbums.size})",
                                        color = theme.accentColor,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                                item {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
                                    ) {
                                        items(
                                            items = filteredAlbums.take(10),
                                            key = { it.id },
                                            contentType = { "album_card" }
                                        ) { album ->
                                            Box(
                                                modifier = Modifier
                                                    .width(130.dp)
                                                    .hyprTile(theme = theme)
                                                    .clickable { onOpenAlbum(album) }
                                                    .padding(8.dp)
                                            ) {
                                                Column {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(114.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(theme.surfaceVariantColor),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        if (!album.coverUri.isNullOrBlank()) {
                                                            val albumReq = remember(album.coverUri) {
                                                                ImageRequest.Builder(context)
                                                                    .data(album.coverUri)
                                                                    .size(240, 240)
                                                                    .allowHardware(true)
                                                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                                                    .diskCachePolicy(CachePolicy.ENABLED)
                                                                    .crossfade(false)
                                                                    .build()
                                                            }
                                                            AsyncImage(
                                                                model = albumReq,
                                                                contentDescription = null,
                                                                modifier = Modifier.fillMaxSize(),
                                                                contentScale = ContentScale.Crop
                                                            )
                                                        } else {
                                                            Icon(
                                                                imageVector = Icons.Default.Album,
                                                                contentDescription = null,
                                                                tint = theme.accentColor,
                                                                modifier = Modifier.size(36.dp)
                                                            )
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(
                                                        text = album.title,
                                                        color = theme.textPrimaryColor,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = album.artist,
                                                        color = theme.textSecondaryColor,
                                                        fontSize = 10.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Text(
                                    text = "TRACKS (${filteredTracks.size})",
                                    color = theme.accentColor,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }

                            items(
                                items = filteredTracks,
                                key = { it.id },
                                contentType = { "track" }
                            ) { track ->
                                val isPlaying = playbackState.currentTrack?.id == track.id
                                HyprTrackRow(
                                    theme = theme,
                                    track = track,
                                    isPlaying = isPlaying,
                                    isAudioActive = isPlaying && playbackState.isPlaying,
                                    onClick = { onTrackSelected(track, filteredTracks) },
                                    onFavoriteClick = { onToggleFavorite(track.id) }
                                )
                            }

                            item { Spacer(modifier = Modifier.height(90.dp)) }
                        }
                    }
                }
            }
        }

        // Floating Alphabet HUD indicator during fast drag
        if (draggingLetter != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(theme.surfaceColor.copy(alpha = 0.95f))
                    .border(2.dp, theme.accentColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = draggingLetter.toString(),
                    color = theme.accentColor,
                    fontSize = 32.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Arch Hyprland Rice Sort Modal Dialog
        if (isSortModalOpen) {
            when (activeFilter) {
                LibraryFilter.TRACKS, LibraryFilter.HI_RES, LibraryFilter.FAVORITES, LibraryFilter.ALL -> {
                    val options = TrackSortOption.entries.map { it.label to (it == trackSort) }
                    HyprSortModalDialog(
                        theme = theme,
                        title = "[ SORT // ${activeFilter.label} ]",
                        options = options,
                        onSelectIndex = { index ->
                            val selected = TrackSortOption.entries[index]
                            trackSort = selected
                            LibrarySortPreferences.setTrackSort(context, selected)
                        },
                        onDismiss = { isSortModalOpen = false }
                    )
                }
                LibraryFilter.ALBUMS -> {
                    val options = AlbumSortOption.entries.map { it.label to (it == albumSort) }
                    HyprSortModalDialog(
                        theme = theme,
                        title = "[ SORT // ALBUMS ]",
                        options = options,
                        onSelectIndex = { index ->
                            val selected = AlbumSortOption.entries[index]
                            albumSort = selected
                            LibrarySortPreferences.setAlbumSort(context, selected)
                        },
                        onDismiss = { isSortModalOpen = false }
                    )
                }
                LibraryFilter.ARTISTS -> {
                    val options = ArtistSortOption.entries.map { it.label to (it == artistSort) }
                    HyprSortModalDialog(
                        theme = theme,
                        title = "[ SORT // ARTISTS ]",
                        options = options,
                        onSelectIndex = { index ->
                            val selected = ArtistSortOption.entries[index]
                            artistSort = selected
                            LibrarySortPreferences.setArtistSort(context, selected)
                        },
                        onDismiss = { isSortModalOpen = false }
                    )
                }
                LibraryFilter.PLAYLISTS -> {
                    val options = PlaylistSortOption.entries.map { it.label to (it == playlistSort) }
                    HyprSortModalDialog(
                        theme = theme,
                        title = "[ SORT // PLAYLISTS ]",
                        options = options,
                        onSelectIndex = { index ->
                            val selected = PlaylistSortOption.entries[index]
                            playlistSort = selected
                            LibrarySortPreferences.setPlaylistSort(context, selected)
                        },
                        onDismiss = { isSortModalOpen = false }
                    )
                }
            }
        }
    }
}

/**
 * Album Detail View: Opens when an album card is tapped.
 */
@Composable
fun AlbumDetailView(
    theme: HyprThemeConfig,
    album: Album,
    allTracks: List<Track>,
    playbackState: PlaybackState,
    onBack: () -> Unit,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val albumTracks = remember(allTracks, album) {
        allTracks.filter { it.album.equals(album.title, ignoreCase = true) }
    }

    val totalDurationMs = remember(albumTracks) { albumTracks.sumOf { it.durationMs } }
    val totalMinutes = totalDurationMs / 60000
    val totalSeconds = (totalDurationMs % 60000) / 1000

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = theme.windowGapsDp.dp)
    ) {
        // Back Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.surfaceVariantColor)
                    .clickable { onBack() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = theme.accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "[ ESC / ALBUMS ]",
                        color = theme.textPrimaryColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Hero Album Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .hyprTile(theme = theme)
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album artwork
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(12).dp))
                        .background(theme.surfaceVariantColor)
                        .border(1.dp, theme.accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!album.coverUri.isNullOrBlank()) {
                        val albumCoverReq = remember(album.coverUri) {
                            ImageRequest.Builder(context)
                                .data(album.coverUri)
                                .size(240, 240)
                                .allowHardware(true)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .crossfade(false)
                                .build()
                        }
                        AsyncImage(
                            model = albumCoverReq,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { alpha = 0.35f },
                            contentScale = ContentScale.Crop
                        )
                        AsyncImage(
                            model = albumCoverReq,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Album,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = album.title,
                        color = theme.textPrimaryColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = album.artist,
                        color = theme.accentColor,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${albumTracks.size} TRACKS • ${totalMinutes}m ${totalSeconds}s",
                        color = theme.textSecondaryColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Action Buttons: Play All & Shuffle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Play All
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(theme.accentColor)
                    .clickable {
                        albumTracks.firstOrNull()?.let { onTrackSelected(it, albumTracks) }
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = theme.backgroundColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PLAY ALL",
                        color = theme.backgroundColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }

            // Shuffle
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(theme.surfaceVariantColor)
                    .border(1.dp, theme.accentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .clickable {
                        if (albumTracks.isNotEmpty()) {
                            val shuffled = albumTracks.shuffled()
                            onTrackSelected(shuffled.first(), shuffled)
                        }
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SHUFFLE",
                        color = theme.accentColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Album Tracks List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
        ) {
            itemsIndexed(
                items = albumTracks,
                key = { _, item -> item.id },
                contentType = { _, _ -> "album_track" }
            ) { index, track ->
                val isPlaying = playbackState.currentTrack?.id == track.id
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .hyprTile(theme = theme, isActive = isPlaying)
                        .clickable { onTrackSelected(track, albumTracks) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Track Number
                        Text(
                            text = "%02d".format(index + 1),
                            color = if (isPlaying) theme.accentColor else theme.textSecondaryColor,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(28.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                color = if (isPlaying) theme.accentColor else theme.textPrimaryColor,
                                fontSize = 14.sp,
                                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = track.artist,
                                color = theme.textSecondaryColor,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (isPlaying && playbackState.isPlaying) {
                            Icon(
                                imageVector = Icons.Default.Equalizer,
                                contentDescription = "Playing",
                                tint = theme.accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Text(
                            text = track.formattedDuration,
                            color = theme.textSecondaryColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        IconButton(
                            onClick = { onToggleFavorite(track.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (track.isFavorite) theme.accentColor else theme.textSecondaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(90.dp)) }
        }
    }
}

/**
 * Artist Detail View: Opens when an artist is tapped.
 */
@Composable
fun ArtistDetailView(
    theme: HyprThemeConfig,
    artist: Artist,
    allTracks: List<Track>,
    allAlbums: List<Album>,
    playbackState: PlaybackState,
    onBack: () -> Unit,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenAlbum: (Album) -> Unit,
    modifier: Modifier = Modifier
) {
    val artistTracks = remember(allTracks, artist) {
        allTracks.filter { it.artist.equals(artist.name, ignoreCase = true) }
    }

    val artistAlbums = remember(allAlbums, artist) {
        allAlbums.filter { it.artist.equals(artist.name, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = theme.windowGapsDp.dp)
    ) {
        // Back Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.surfaceVariantColor)
                    .clickable { onBack() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = theme.accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "[ ESC / ARTISTS ]",
                        color = theme.textPrimaryColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Hero Artist Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .hyprTile(theme = theme)
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.surfaceVariantColor)
                        .border(1.dp, theme.accentColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = artist.name,
                        color = theme.textPrimaryColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${artistTracks.size} TRACKS • ${artistAlbums.size} ALBUMS",
                        color = theme.textSecondaryColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(theme.accentColor)
                    .clickable {
                        artistTracks.firstOrNull()?.let { onTrackSelected(it, artistTracks) }
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = theme.backgroundColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PLAY ALL",
                        color = theme.backgroundColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(theme.surfaceVariantColor)
                    .border(1.dp, theme.accentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .clickable {
                        if (artistTracks.isNotEmpty()) {
                            val shuffled = artistTracks.shuffled()
                            onTrackSelected(shuffled.first(), shuffled)
                        }
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SHUFFLE",
                        color = theme.accentColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Tracks List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
        ) {
            items(
                items = artistTracks,
                key = { it.id },
                contentType = { "artist_track" }
            ) { track ->
                val isPlaying = playbackState.currentTrack?.id == track.id
                HyprTrackRow(
                    theme = theme,
                    track = track,
                    isPlaying = isPlaying,
                    isAudioActive = isPlaying && playbackState.isPlaying,
                    onClick = { onTrackSelected(track, artistTracks) },
                    onFavoriteClick = { onToggleFavorite(track.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(90.dp)) }
        }
    }
}

/**
 * Playlist Detail Screen with high-end hero header, total duration, Play All, Shuffle,
 * and track deletion/reorder controls.
 */
@Composable
private fun PlaylistDetailView(
    theme: HyprThemeConfig,
    playlist: CustomPlaylist,
    allTracks: List<Track>,
    playbackState: PlaybackState,
    playlistRepository: PlaylistRepository?,
    onBack: () -> Unit,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val playlistTracks = remember(allTracks, playlist.trackIds) {
        playlist.trackIds.mapNotNull { id -> allTracks.firstOrNull { it.id == id } }
    }
    val totalDurationMs = remember(playlistTracks) { playlistTracks.sumOf { it.durationMs } }
    val totalMinutes = totalDurationMs / 60000
    val totalSeconds = (totalDurationMs % 60000) / 1000

    var showDeleteConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = theme.windowGapsDp.dp)
    ) {
        // Back Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.surfaceVariantColor)
                    .clickable(onClick = onBack)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = theme.accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "[ ESC / PLAYLISTS ]",
                        color = theme.textPrimaryColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Hero Playlist Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .hyprTile(theme = theme)
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Collage or Icon Artwork Box
                val artUris = remember(playlistTracks) {
                    playlistTracks.mapNotNull { it.albumArtUri }.distinct().take(4)
                }
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(12).dp))
                        .background(theme.surfaceVariantColor)
                        .border(1.dp, theme.accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (artUris.size >= 4) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(modifier = Modifier.weight(1f)) {
                                AsyncImage(
                                    model = artUris[0],
                                    contentDescription = null,
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                    contentScale = ContentScale.Crop
                                )
                                AsyncImage(
                                    model = artUris[1],
                                    contentDescription = null,
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Row(modifier = Modifier.weight(1f)) {
                                AsyncImage(
                                    model = artUris[2],
                                    contentDescription = null,
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                    contentScale = ContentScale.Crop
                                )
                                AsyncImage(
                                    model = artUris[3],
                                    contentDescription = null,
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    } else if (artUris.isNotEmpty()) {
                        AsyncImage(
                            model = artUris.first(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlaylistPlay,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = playlist.name,
                        color = theme.textPrimaryColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "CUSTOM PLAYLIST",
                        color = theme.accentColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${playlistTracks.size} tracks • %02d:%02d".format(totalMinutes, totalSeconds),
                        color = theme.textSecondaryColor,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (playlistTracks.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(theme.accentColor)
                                    .hyprBounceClick {
                                        onTrackSelected(playlistTracks.first(), playlistTracks)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = theme.backgroundColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "PLAY",
                                        color = theme.backgroundColor,
                                        fontSize = 10.5.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(theme.surfaceVariantColor)
                                    .hyprBounceClick {
                                        val shuffled = playlistTracks.shuffled()
                                        onTrackSelected(shuffled.first(), shuffled)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Shuffle,
                                        contentDescription = null,
                                        tint = theme.accentColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "SHUFFLE",
                                        color = theme.accentColor,
                                        fontSize = 10.5.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(theme.surfaceVariantColor)
                                .hyprBounceClick { showDeleteConfirm = true }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Playlist",
                                tint = Color(0xFFFF5555),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (playlistTracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "[!] PLAYLIST IS EMPTY",
                        color = theme.accentColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add tracks from library using '+ Add to Playlist'",
                        color = theme.textSecondaryColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
            ) {
                itemsIndexed(
                    items = playlistTracks,
                    key = { index, track -> "pl_${playlist.id}_${track.id}_$index" },
                    contentType = { _, _ -> "playlist_track" }
                ) { _, track ->
                    val isPlaying = playbackState.currentTrack?.id == track.id
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                HyprTrackRow(
                                    theme = theme,
                                    track = track,
                                    isPlaying = isPlaying,
                                    isAudioActive = isPlaying && playbackState.isPlaying,
                                    onClick = { onTrackSelected(track, playlistTracks) },
                                    onFavoriteClick = { onToggleFavorite(track.id) }
                                )
                            }
                            IconButton(
                                onClick = {
                                    playlistRepository?.removeTrackFromPlaylist(playlist.id, track.id)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove from playlist",
                                    tint = theme.textSecondaryColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(90.dp)) }
            }
        }
    }

    if (showDeleteConfirm) {
        Dialog(onDismissRequest = { showDeleteConfirm = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(theme.borderRadiusDp.dp))
                    .background(theme.surfaceColor)
                    .border(1.dp, Color(0xFFFF5555).copy(alpha = 0.5f), RoundedCornerShape(theme.borderRadiusDp.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "rm -rf playlist?",
                        color = Color(0xFFFF5555),
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Delete playlist '${playlist.name}'? Tracks will remain in your library.",
                        color = theme.textSecondaryColor,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { showDeleteConfirm = false }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "[ CANCEL ]",
                                color = theme.textSecondaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFF5555))
                                .clickable {
                                    showDeleteConfirm = false
                                    playlistRepository?.deletePlaylist(playlist.id)
                                    onBack()
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "[ DELETE ]",
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2-Column Playlists Grid with "+ NEW PLAYLIST" hero creation tile and 4-quadrant dynamic album art collages.
 */
@Composable
private fun PlaylistsGridView(
    theme: HyprThemeConfig,
    playlists: List<CustomPlaylist>,
    allTracks: List<Track>,
    playlistRepository: PlaylistRepository?,
    onOpenPlaylist: (CustomPlaylist) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp),
        horizontalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
    ) {
        // Hero "+ NEW PLAYLIST" Tile
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(12).dp))
                    .background(theme.surfaceVariantColor.copy(alpha = 0.45f))
                    .border(
                        width = 1.dp,
                        color = theme.accentColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(12).dp)
                    )
                    .hyprBounceClick { showCreateDialog = true }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(theme.accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Playlist",
                            tint = theme.accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "+ NEW PLAYLIST",
                        color = theme.accentColor,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "mkplaylist <name>",
                        color = theme.textSecondaryColor,
                        fontSize = 9.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Custom Playlist Cards
        items(
            items = playlists,
            key = { it.id },
            contentType = { "playlist_card" }
        ) { playlist ->
            val playlistTracks = remember(allTracks, playlist.trackIds) {
                playlist.trackIds.mapNotNull { id -> allTracks.firstOrNull { it.id == id } }
            }
            val artUris = remember(playlistTracks) {
                playlistTracks.mapNotNull { it.albumArtUri }.distinct().take(4)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .hyprTile(theme = theme)
                    .hyprBounceClick { onOpenPlaylist(playlist) }
                    .padding(8.dp)
            ) {
                Column {
                    // Artwork collage canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(12).dp))
                            .background(theme.surfaceVariantColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (artUris.size >= 4) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Row(modifier = Modifier.weight(1f)) {
                                    AsyncImage(
                                        model = artUris[0],
                                        contentDescription = null,
                                        modifier = Modifier.weight(1f).fillMaxHeight(),
                                        contentScale = ContentScale.Crop
                                    )
                                    AsyncImage(
                                        model = artUris[1],
                                        contentDescription = null,
                                        modifier = Modifier.weight(1f).fillMaxHeight(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Row(modifier = Modifier.weight(1f)) {
                                    AsyncImage(
                                        model = artUris[2],
                                        contentDescription = null,
                                        modifier = Modifier.weight(1f).fillMaxHeight(),
                                        contentScale = ContentScale.Crop
                                    )
                                    AsyncImage(
                                        model = artUris[3],
                                        contentDescription = null,
                                        modifier = Modifier.weight(1f).fillMaxHeight(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        } else if (artUris.isNotEmpty()) {
                            AsyncImage(
                                model = artUris.first(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.PlaylistPlay,
                                    contentDescription = null,
                                    tint = theme.accentColor,
                                    modifier = Modifier.size(46.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "EMPTY PLAYLIST",
                                    color = theme.textSecondaryColor,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Monospace track count badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .border(0.5.dp, theme.accentColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${playlist.trackIds.size} TRK",
                                color = theme.accentColor,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = playlist.name,
                        color = theme.textPrimaryColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${playlist.trackIds.size} tracks • custom list",
                        color = theme.textSecondaryColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(90.dp)) }
    }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            theme = theme,
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                showCreateDialog = false
                val created = playlistRepository?.createPlaylist(name)
                if (created != null) {
                    onOpenPlaylist(created)
                }
            }
        )
    }
}

/**
 * Tactical Modal Dialog for naming and creating a new custom playlist.
 */
@Composable
private fun CreatePlaylistDialog(
    theme: HyprThemeConfig,
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .clip(RoundedCornerShape(theme.borderRadiusDp.dp))
                .background(theme.surfaceColor)
                .border(1.dp, theme.accentColor.copy(alpha = 0.4f), RoundedCornerShape(theme.borderRadiusDp.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "mkplaylist // CREATE",
                    color = theme.accentColor,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter a name for your custom playlist:",
                    color = theme.textSecondaryColor,
                    fontSize = 11.5.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.surfaceVariantColor)
                        .border(1.dp, theme.accentColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    if (name.isEmpty()) {
                        Text(
                            text = "e.g. Late Night Drives",
                            color = theme.textSecondaryColor.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    BasicTextField(
                        value = name,
                        onValueChange = { name = it },
                        textStyle = TextStyle(
                            color = theme.textPrimaryColor,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(theme.accentColor),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onDismiss)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "[ CANCEL ]",
                            color = theme.textSecondaryColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (name.isNotBlank()) theme.accentColor
                                else theme.surfaceVariantColor
                            )
                            .clickable(enabled = name.isNotBlank()) { onCreate(name.trim()) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "[ CREATE ]",
                            color = if (name.isNotBlank()) theme.backgroundColor else theme.textSecondaryColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Ultra-fast recycled track row item with live VU meters and audiophile codec badges.
 */
@Composable
fun HyprTrackRow(
    theme: HyprThemeConfig,
    track: Track,
    isPlaying: Boolean,
    isAudioActive: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToPlaylist: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .hyprTile(theme = theme, isActive = isPlaying)
            .hyprBounceClick(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Artwork or Live VU Meter
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.surfaceVariantColor),
                contentAlignment = Alignment.Center
            ) {
                if (isAudioActive) {
                    MiniEqualizerBars(
                        theme = theme,
                        isPlaying = true,
                        modifier = Modifier.size(20.dp)
                    )
                } else if (!track.albumArtUri.isNullOrBlank()) {
                    val trackReq = remember(track.albumArtUri) {
                        ImageRequest.Builder(context)
                            .data(track.albumArtUri)
                            .size(120, 120)
                            .allowHardware(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .crossfade(false)
                            .build()
                    }
                    AsyncImage(
                        model = trackReq,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = theme.textSecondaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    color = if (isPlaying) theme.accentColor else theme.textPrimaryColor,
                    fontSize = 13.5.sp,
                    fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${track.artist} • ${track.album}",
                    color = theme.textSecondaryColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Genuine Audio Format Badge (FLAC, M4A, MP3, WAV, etc.)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(theme.surfaceVariantColor)
                    .border(
                        width = 0.5.dp,
                        color = if (track.isLossless) theme.accentColor.copy(alpha = 0.6f) else Color.Transparent,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = track.audioFormat,
                    color = if (track.isLossless) theme.accentColor else theme.textSecondaryColor,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = track.formattedDuration,
                color = theme.textSecondaryColor,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )

            if (onAddToPlaylist != null) {
                IconButton(
                    onClick = onAddToPlaylist,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                        contentDescription = "Add to playlist",
                        tint = theme.textSecondaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (track.isFavorite) theme.accentColor else theme.textSecondaryColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Album Card for 2-column Grid with click-to-open detail view.
 */
/**
 * Vinyl & Digipak Album Sleeve with 1:1 Aspect Ratio, Dual-Layer Zero-Crop Engine,
 * Grooved Vinyl Disc Peek, and Corner Audio Chip Badge.
 */
@Composable
fun HyprAlbumCard(
    theme: HyprThemeConfig,
    album: Album,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .hyprTile(theme = theme)
            .hyprBounceClick(onClick = onClick)
            .padding(8.dp)
    ) {
        Column {
            // Physical Vinyl Sleeve + Ambient Backdrop Engine
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(12).dp))
                    .background(theme.surfaceVariantColor),
                contentAlignment = Alignment.Center
            ) {
                // Vinyl Record Edge Peek (Simulating physical LP sleeve)
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 1.dp)
                ) {
                    val discRadius = size.height * 0.47f
                    val centerOffset = Offset(size.width * 0.99f, size.height * 0.5f)
                    // Outer vinyl black rim
                    drawCircle(
                        color = Color(0xFF141416),
                        radius = discRadius,
                        center = centerOffset
                    )
                    // Concentric vinyl grooves
                    for (i in 1..4) {
                        drawCircle(
                            color = Color(0xFF2E2E36).copy(alpha = 0.55f),
                            radius = discRadius * (0.38f + i * 0.12f),
                            center = centerOffset,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }
                    // Center label ring
                    drawCircle(
                        color = theme.accentColor.copy(alpha = 0.85f),
                        radius = discRadius * 0.28f,
                        center = centerOffset
                    )
                }

                if (!album.coverUri.isNullOrBlank()) {
                    val albumCardReq = remember(album.coverUri) {
                        ImageRequest.Builder(context)
                            .data(album.coverUri)
                            .size(360, 360)
                            .allowHardware(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .crossfade(false)
                            .build()
                    }
                    // Ambient blurred underglow so wide or non-square art fills the sleeve
                    AsyncImage(
                        model = albumCardReq,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { alpha = 0.35f },
                        contentScale = ContentScale.Crop
                    )
                    // Crisp foreground jacket fitted with zero cropping of faces, banners, or titles
                    AsyncImage(
                        model = albumCardReq,
                        contentDescription = album.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(RoundedCornerShape((theme.borderRadiusDp.coerceAtMost(10) - 2).coerceAtLeast(4).dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Sleek Monospace Audio Chip Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .border(0.5.dp, theme.accentColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${album.trackCount} TRK",
                        color = theme.accentColor,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = album.title,
                color = theme.textPrimaryColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = album.artist,
                color = theme.textSecondaryColor,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Artist Row with click-to-open detail view.
 */
@Composable
fun HyprArtistRow(
    theme: HyprThemeConfig,
    artist: Artist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .hyprTile(theme = theme)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.surfaceVariantColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = theme.accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = artist.name,
                    color = theme.textPrimaryColor,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${artist.trackCount} tracks • ${artist.albumCount} albums",
                    color = theme.textSecondaryColor,
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

/**
 * Vertical Alphabet Fast-Scroller for rapid navigation through 10 Lakh / 1M tracks.
 */
@Composable
fun HyprAlphabetScroller(
    theme: HyprThemeConfig,
    onLetterSelected: (Char) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alphabet = remember { listOf('#') + ('A'..'Z').toList() }

    Column(
        modifier = modifier
            .width(20.dp)
            .fillMaxHeight()
            .pointerInput(alphabet) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        val index = (offset.y / size.height * alphabet.size).toInt().coerceIn(0, alphabet.lastIndex)
                        onLetterSelected(alphabet[index])
                    },
                    onVerticalDrag = { change, _ ->
                        val index = (change.position.y / size.height * alphabet.size).toInt().coerceIn(0, alphabet.lastIndex)
                        onLetterSelected(alphabet[index])
                    },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragEnd
                )
            }
            .pointerInput(alphabet) {
                detectTapGestures { offset ->
                    val index = (offset.y / size.height * alphabet.size).toInt().coerceIn(0, alphabet.lastIndex)
                    onLetterSelected(alphabet[index])
                    onDragEnd()
                }
            },
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        alphabet.forEach { letter ->
            Text(
                text = letter.toString(),
                color = theme.textSecondaryColor.copy(alpha = 0.7f),
                fontSize = 8.5.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Cloud Library View: Displays tracks synced from Telegram Personal Music Cloud (TPMC),
 * offering direct HTTP 206 streaming and background Scoped Storage downloads.
 */
@Composable
private fun CloudLibraryView(
    theme: HyprThemeConfig,
    playbackState: PlaybackState,
    telegramRepository: TelegramMusicRepository?,
    onTrackSelected: (Track, List<Track>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var cloudSearchQuery by remember { mutableStateOf("") }

    val cloudTracks = telegramRepository?.cloudTracks?.collectAsStateWithLifecycle()?.value ?: emptyList()
    val isSyncing = telegramRepository?.isSyncing?.collectAsStateWithLifecycle()?.value ?: false
    val isWaking = telegramRepository?.isWakingServer?.collectAsStateWithLifecycle()?.value ?: false
    val serverHealth = telegramRepository?.serverHealth?.collectAsStateWithLifecycle()?.value
    val syncError = telegramRepository?.syncError?.collectAsStateWithLifecycle()?.value
    val downloadedTrackIds = telegramRepository?.downloadedTrackIds?.collectAsStateWithLifecycle()?.value ?: emptySet()
    val downloadingProgress = telegramRepository?.downloadingProgress?.collectAsStateWithLifecycle()?.value ?: emptyMap()
    val config = telegramRepository?.config?.settings?.collectAsStateWithLifecycle()?.value

    val filteredCloudTracks = remember(cloudTracks, cloudSearchQuery) {
        val q = cloudSearchQuery.trim().lowercase()
        if (q.isBlank()) cloudTracks
        else cloudTracks.filter {
            it.title.lowercase().contains(q) ||
            it.artist.lowercase().contains(q) ||
            it.album.lowercase().contains(q)
        }
    }

    val cloudAlphabetMap = remember(filteredCloudTracks) {
        val map = mutableMapOf<Char, Int>()
        filteredCloudTracks.forEachIndexed { index, track ->
            val firstChar = track.title.firstOrNull()?.uppercaseChar() ?: '#'
            val key = if (firstChar in 'A'..'Z') firstChar else '#'
            if (!map.containsKey(key)) {
                map[key] = index
            }
        }
        map
    }

    val listState = rememberLazyListState()
    var draggingLetter by remember { mutableStateOf<Char?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = theme.windowGapsDp.dp)
    ) {
        // Search & Sync Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .hyprTile(theme = theme)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = "Cloud Search",
                tint = theme.accentColor,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (cloudSearchQuery.isEmpty()) {
                    Text(
                        text = "grep -i cloud...",
                        color = theme.textSecondaryColor.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                BasicTextField(
                    value = cloudSearchQuery,
                    onValueChange = { cloudSearchQuery = it },
                    textStyle = TextStyle(
                        color = theme.textPrimaryColor,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(theme.accentColor),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (cloudSearchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { cloudSearchQuery = "" },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = theme.textSecondaryColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // Cloud Count Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(theme.surfaceVariantColor)
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${filteredCloudTracks.size} / ${cloudTracks.size}",
                    color = theme.accentColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        telegramRepository?.syncLibrary(force = true)
                    }
                },
                modifier = Modifier.size(28.dp),
                enabled = !isSyncing && !isWaking
            ) {
                if (isSyncing || isWaking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = theme.accentColor
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sync Cloud",
                        tint = theme.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Status Banner (Waking up / Error / Status)
        if (isWaking) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE5C07B).copy(alpha = 0.15f))
                    .border(1.dp, Color(0xFFE5C07B).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFFE5C07B)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WAKING UP SERVER // Render instance spinning up (up to 30s)...",
                        color = Color(0xFFE5C07B),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        } else if (syncError != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE06C75).copy(alpha = 0.15f))
                    .border(1.dp, Color(0xFFE06C75).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ERR: $syncError",
                        color = Color(0xFFE06C75),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.5.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "[RETRY]",
                        color = theme.accentColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            coroutineScope.launch { telegramRepository?.syncLibrary(force = true) }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        } else if (serverHealth != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "● TPMC ONLINE: ${serverHealth.bot}",
                    color = Color(0xFF98C379),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${cloudTracks.size} CLOUD TRACKS",
                    color = theme.textSecondaryColor,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Main Cloud Content
        if (cloudTracks.isEmpty() && !isSyncing && !isWaking) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .hyprTile(theme = theme)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudQueue,
                        contentDescription = null,
                        tint = theme.accentColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        text = "NO TELEGRAM TRACKS LOADED",
                        color = theme.textPrimaryColor,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (config?.serverUrl.isNullOrBlank()) {
                            "TPMC server is not configured yet.\nNavigate to Settings -> Telegram Cloud Storage to enter your server URL and bot credentials."
                        } else {
                            "Connected to ${config?.serverUrl}.\nTap Sync to load your music library from your Telegram bot."
                        },
                        color = theme.textSecondaryColor,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.accentColor)
                            .clickable {
                                coroutineScope.launch {
                                    telegramRepository?.syncLibrary(force = true)
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "[ SYNC NOW ]",
                            color = theme.backgroundColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
                ) {
                    items(
                        items = filteredCloudTracks,
                        key = { "cloud_${it.id}" },
                        contentType = { "cloud_track" }
                    ) { track ->
                        val isPlaying = playbackState.currentTrack?.id == track.id
                        val isDownloaded = downloadedTrackIds.contains(track.id)
                        val progress = downloadingProgress[track.id]

                        HyprCloudTrackRow(
                            theme = theme,
                            track = track,
                            isPlaying = isPlaying,
                            isAudioActive = isPlaying && playbackState.isPlaying,
                            isDownloaded = isDownloaded,
                            downloadProgress = progress,
                            onClick = {
                                onTrackSelected(track, filteredCloudTracks)
                            },
                            onDownloadClick = {
                                coroutineScope.launch {
                                    Toast.makeText(context, "Downloading: ${track.title}...", Toast.LENGTH_SHORT).show()
                                    if (telegramRepository != null) {
                                        val success = telegramRepository.downloadTrack(track)
                                        if (success) {
                                            Toast.makeText(context, "Saved to library: ${track.title}", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Download failed for ${track.title}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(90.dp)) }
                }

                // Alphabet scroller for cloud
                HyprAlphabetScroller(
                    theme = theme,
                    onLetterSelected = { letter ->
                        draggingLetter = letter
                        cloudAlphabetMap[letter]?.let { targetIndex ->
                            coroutineScope.launch {
                                listState.scrollToItem(targetIndex)
                            }
                        }
                    },
                    onDragEnd = { draggingLetter = null }
                )
            }
        }
    }
}

/**
 * Cloud Track Row with stream playback and direct Scoped Storage MediaStore download integration.
 */
@Composable
private fun HyprCloudTrackRow(
    theme: HyprThemeConfig,
    track: Track,
    isPlaying: Boolean,
    isAudioActive: Boolean,
    isDownloaded: Boolean,
    downloadProgress: Float?,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .hyprTile(theme = theme, isActive = isPlaying)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Artwork or Cloud Note
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.surfaceVariantColor),
                contentAlignment = Alignment.Center
            ) {
                if (isAudioActive) {
                    Icon(
                        imageVector = Icons.Default.Equalizer,
                        contentDescription = "Playing",
                        tint = theme.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                } else if (!track.albumArtUri.isNullOrBlank()) {
                    val cloudTrackReq = remember(track.albumArtUri) {
                        ImageRequest.Builder(context)
                            .data(track.albumArtUri)
                            .size(120, 120)
                            .allowHardware(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .crossfade(false)
                            .build()
                    }
                    AsyncImage(
                        model = cloudTrackReq,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = null,
                        tint = theme.accentColor.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    color = if (isPlaying) theme.accentColor else theme.textPrimaryColor,
                    fontSize = 13.5.sp,
                    fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${track.artist} • ${track.album}",
                    color = theme.textSecondaryColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Audio format badge (FLAC, MP3, etc.)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(theme.surfaceVariantColor)
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = track.audioFormat,
                    color = if (track.isLossless) theme.accentColor else theme.textSecondaryColor,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // File Size / Duration
            Text(
                text = if (track.fileSize > 0) track.formattedFileSize else track.formattedDuration,
                color = theme.textSecondaryColor,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Download Action / Progress / Saved Status
            Box(
                modifier = Modifier.size(34.dp),
                contentAlignment = Alignment.Center
            ) {
                if (downloadProgress != null) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { downloadProgress },
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = theme.accentColor
                        )
                        Text(
                            text = "${(downloadProgress * 100).toInt()}%",
                            color = theme.accentColor,
                            fontSize = 7.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (isDownloaded) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF98C379).copy(alpha = 0.18f))
                            .padding(horizontal = 5.dp, vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Downloaded",
                                tint = Color(0xFF98C379),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                } else {
                    IconButton(
                        onClick = onDownloadClick,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = "Download track",
                            tint = theme.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Arch Hyprland Rice Sort Modal Dialog
 */
@Composable
private fun HyprSortModalDialog(
    theme: HyprThemeConfig,
    title: String,
    options: List<Pair<String, Boolean>>,
    onSelectIndex: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val view = LocalView.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(theme.surfaceColor)
                    .border(
                        width = 1.dp,
                        color = theme.accentColor.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Modal Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(theme.accentColor)
                            )
                            Text(
                                text = title,
                                color = theme.accentColor,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = theme.textSecondaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(theme.surfaceVariantColor)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Options list
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        options.forEachIndexed { index, (label, isSelected) ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) theme.accentColor.copy(alpha = 0.18f)
                                        else theme.surfaceVariantColor.copy(alpha = 0.45f)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) theme.accentColor.copy(alpha = 0.85f) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        try {
                                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                        } catch (_: Exception) {}
                                        onSelectIndex(index)
                                        onDismiss()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = if (isSelected) "▶" else " ",
                                            color = theme.accentColor,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = label,
                                            color = if (isSelected) theme.accentColor else theme.textPrimaryColor,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(theme.accentColor.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                color = theme.accentColor,
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


