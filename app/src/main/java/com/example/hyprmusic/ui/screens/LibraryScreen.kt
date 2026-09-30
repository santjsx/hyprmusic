package com.example.hyprmusic.ui.screens

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.hyprmusic.core.model.Album
import com.example.hyprmusic.core.model.Artist
import com.example.hyprmusic.core.model.PlaybackState
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.core.theming.HyprThemeConfig
import com.example.hyprmusic.core.theming.hyprAnimatedGlow
import com.example.hyprmusic.core.theming.hyprTile
import kotlinx.coroutines.launch

enum class LibraryFilter(val label: String) {
    ALL("ALL"),
    TRACKS("TRACKS"),
    ALBUMS("ALBUMS"),
    ARTISTS("ARTISTS"),
    HI_RES("HI-RES FLAC"),
    FAVORITES("FAVORITES")
}

private sealed interface LibraryViewState {
    data class Main(val filter: LibraryFilter) : LibraryViewState
    data class AlbumDetail(val album: Album) : LibraryViewState
    data class ArtistDetail(val artist: Artist) : LibraryViewState
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
    modifier: Modifier = Modifier
) {
    var viewState by remember { mutableStateOf<LibraryViewState>(LibraryViewState.Main(LibraryFilter.TRACKS)) }
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf(LibraryFilter.TRACKS) }

    val coroutineScope = rememberCoroutineScope()
    val tracksListState = rememberLazyListState()
    val albumsGridState = rememberLazyGridState()
    val artistsListState = rememberLazyListState()

    // Smooth transition between screens
    AnimatedContent(
        targetState = viewState,
        transitionSpec = {
            (fadeIn() + slideInHorizontally { it / 3 }) togetherWith (fadeOut() + slideOutHorizontally { -it / 3 })
        },
        label = "library_view_transition",
        modifier = modifier.fillMaxSize()
    ) { state ->
        when (state) {
            is LibraryViewState.Main -> {
                MainLibraryView(
                    theme = theme,
                    playbackState = playbackState,
                    tracks = tracks,
                    albums = albums,
                    artists = artists,
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
                    onBack = { viewState = LibraryViewState.Main(activeFilter) },
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
    val coroutineScope = rememberCoroutineScope()
    var draggingLetter by remember { mutableStateOf<Char?>(null) }

    // Fast memoized filtering
    val filteredTracks = remember(tracks, searchQuery, activeFilter) {
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
        if (query.isBlank()) baseList
        else baseList.filter {
            it.title.lowercase().contains(query) ||
            it.artist.lowercase().contains(query) ||
            it.album.lowercase().contains(query)
        }
    }

    val filteredAlbums = remember(albums, searchQuery) {
        val query = searchQuery.trim().lowercase()
        if (query.isBlank()) albums
        else albums.filter {
            it.title.lowercase().contains(query) ||
            it.artist.lowercase().contains(query)
        }
    }

    val filteredArtists = remember(artists, searchQuery) {
        val query = searchQuery.trim().lowercase()
        if (query.isBlank()) artists
        else artists.filter { it.name.lowercase().contains(query) }
    }

    // Precomputed alphabet jump map for instant 1M track fast scrolling
    val trackAlphabetMap = remember(filteredTracks) {
        val map = mutableMapOf<Char, Int>()
        filteredTracks.forEachIndexed { index, track ->
            val firstChar = track.title.firstOrNull()?.uppercaseChar() ?: '#'
            val key = if (firstChar in 'A'..'Z') firstChar else '#'
            if (!map.containsKey(key)) {
                map[key] = index
            }
        }
        map
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
                    LibraryFilter.ALL -> filteredTracks.size + filteredAlbums.size + filteredArtists.size
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
                LibraryFilter.values().forEach { filter ->
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

                            // Alphabet fast scroll sidebar for 1M / 10 Lakh items
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
                                            key = { it.id }
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
                                            key = { it.id }
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
                                                            AsyncImage(
                                                                model = album.coverUri,
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
                        AsyncImage(
                            model = album.coverUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
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
 * Ultra-fast recycled track row item for 60/120 FPS scrolling.
 */
@Composable
fun HyprTrackRow(
    theme: HyprThemeConfig,
    track: Track,
    isPlaying: Boolean,
    isAudioActive: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            // Artwork or Equalizer
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
                    AsyncImage(
                        model = track.albumArtUri,
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
@Composable
fun HyprAlbumCard(
    theme: HyprThemeConfig,
    album: Album,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .hyprTile(theme = theme)
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(10).dp))
                    .background(theme.surfaceVariantColor),
                contentAlignment = Alignment.Center
            ) {
                if (!album.coverUri.isNullOrBlank()) {
                    AsyncImage(
                        model = album.coverUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(44.dp)
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
            Text(
                text = "${album.artist} • ${album.trackCount} tracks",
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
