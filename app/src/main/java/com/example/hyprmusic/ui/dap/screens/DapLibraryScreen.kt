package com.example.hyprmusic.ui.dap.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hyprmusic.core.model.Album
import com.example.hyprmusic.core.model.Artist
import com.example.hyprmusic.core.model.Playlist
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.ui.dap.components.DapEmptyState
import com.example.hyprmusic.ui.dap.state.DapHapticFeedback
import com.example.hyprmusic.ui.dap.theme.DapColors
import com.example.hyprmusic.ui.dap.theme.DapTypography

/**
 * Universal Track Browser with Instant Search, Filter Chips,
 * Von Restorff Playing State Highlight, and 1-Tap Favorite & Playlist Actions.
 */
@Composable
fun DapTrackListDisplay(
    title: String,
    tracks: List<Track>,
    currentTrackId: String?,
    selectedIndex: Int,
    listState: LazyListState,
    onTrackSelect: (Track, List<Track>) -> Unit,
    onBack: () -> Unit,
    onToggleFavorite: ((String) -> Unit)? = null,
    onAddToPlaylist: ((Track) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val haptic = remember(view) { DapHapticFeedback(view) }
    var searchQuery by remember { mutableStateOf("") }
    var showOnlyFavorites by remember { mutableStateOf(false) }

    val filteredTracks = remember(tracks, searchQuery, showOnlyFavorites) {
        tracks.filter {
            val matchesQuery = searchQuery.isBlank() ||
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.artist.contains(searchQuery, ignoreCase = true) ||
                it.album.contains(searchQuery, ignoreCase = true)
            val matchesFav = !showOnlyFavorites || it.isFavorite
            matchesQuery && matchesFav
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DapColors.TextPrimary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = DapTypography.MenuHeader,
                    color = DapColors.TextPrimary,
                    maxLines = 1
                )
            }

            Text(
                text = "${filteredTracks.size} tracks",
                style = DapTypography.TimeCounter,
                color = DapColors.TextSecondary
            )
        }

        // Instant Substring Search Field (Choice Overload Mitigation)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search title, artist, album...", color = DapColors.TextSecondary, fontSize = 12.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = DapColors.TextSecondary, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = DapColors.TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = DapColors.TextPrimary,
                unfocusedTextColor = DapColors.TextPrimary,
                focusedBorderColor = DapColors.AccentIceBlue,
                unfocusedBorderColor = DapColors.ScreenCardBorder,
                cursorColor = DapColors.AccentIceBlue
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !showOnlyFavorites,
                onClick = { showOnlyFavorites = false },
                label = { Text("All Tracks (${tracks.size})", fontSize = 11.5.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DapColors.AccentIceBlue.copy(alpha = 0.2f),
                    selectedLabelColor = DapColors.AccentIceBlue,
                    labelColor = DapColors.TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = !showOnlyFavorites,
                    borderColor = DapColors.ScreenCardBorder,
                    selectedBorderColor = DapColors.AccentIceBlue
                )
            )

            FilterChip(
                selected = showOnlyFavorites,
                onClick = { showOnlyFavorites = true },
                label = { Text("⭐ Favorites (${tracks.count { it.isFavorite }})", fontSize = 11.5.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFFC107).copy(alpha = 0.2f),
                    selectedLabelColor = Color(0xFFFFC107),
                    labelColor = DapColors.TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = showOnlyFavorites,
                    borderColor = DapColors.ScreenCardBorder,
                    selectedBorderColor = Color(0xFFFFC107)
                )
            )

            SuggestionChip(
                onClick = {
                    if (filteredTracks.isNotEmpty()) {
                        haptic.heavyPulse()
                        val shuffled = filteredTracks.shuffled()
                        onTrackSelect(shuffled.first(), shuffled)
                    }
                },
                label = { Text("🔀 Shuffle", fontSize = 11.5.sp, color = Color.White) },
                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = DapColors.AccentIceBlue.copy(alpha = 0.3f)),
                border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = DapColors.AccentIceBlue)
            )
        }

        HorizontalDivider(
            color = DapColors.ScreenCardBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        if (filteredTracks.isEmpty()) {
            DapEmptyState(
                title = if (showOnlyFavorites) "No Favorites Yet" else "No Tracks Found",
                message = if (showOnlyFavorites) "Tap the star on any song to add it to your favorites." else "Try adjusting your search query or rescan storage.",
                icon = if (showOnlyFavorites) Icons.Default.StarBorder else Icons.Default.MusicNote
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(filteredTracks) { index, track ->
                    val isWheelSelected = index == selectedIndex
                    val isPlayingNow = track.id == currentTrackId

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    isPlayingNow -> DapColors.AccentIceBlue.copy(alpha = 0.16f) // Von Restorff Highlight
                                    isWheelSelected -> DapColors.ScreenCard
                                    else -> Color.Transparent
                                }
                            )
                            .border(
                                width = if (isPlayingNow) 1.dp else 0.dp,
                                color = if (isPlayingNow) DapColors.AccentIceBlue.copy(alpha = 0.6f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                haptic.buttonClick()
                                onTrackSelect(track, filteredTracks)
                            }
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: Equalizer animation or index + Title/Artist
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isPlayingNow) {
                                EqualizerMiniBars(tint = DapColors.AccentIceBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            Column {
                                Text(
                                    text = track.title,
                                    style = DapTypography.MenuItem,
                                    color = when {
                                        isPlayingNow -> DapColors.AccentIceBlue
                                        isWheelSelected -> DapColors.TextPrimary
                                        else -> DapColors.TextPrimary.copy(alpha = 0.9f)
                                    },
                                    fontWeight = if (isPlayingNow || isWheelSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${track.artist} • ${track.album}",
                                    style = DapTypography.MenuSecondary,
                                    color = DapColors.TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Right: Duration + Star + More Actions
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = track.formattedDuration,
                                style = DapTypography.TimeCounter,
                                color = DapColors.TextSecondary,
                                fontSize = 11.5.sp
                            )

                            if (onToggleFavorite != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = if (track.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Favorite",
                                    tint = if (track.isFavorite) Color(0xFFFFC107) else DapColors.TextSecondary.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable {
                                            haptic.buttonClick()
                                            onToggleFavorite(track.id)
                                        }
                                )
                            }

                            if (onAddToPlaylist != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = DapColors.TextSecondary.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable {
                                            haptic.buttonClick()
                                            onAddToPlaylist(track)
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Animated 3-Bar Mini Audio Equalizer (Von Restorff Effect).
 */
@Composable
fun EqualizerMiniBars(
    tint: Color = DapColors.AccentIceBlue,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "EqTransition")

    val h1 by infiniteTransition.animateFloat(
        initialValue = 4f, targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(420, easing = LinearEasing), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 14f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(360, easing = LinearEasing), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 6f, targetValue = 16f,
        animationSpec = infiniteRepeatable(tween(510, easing = LinearEasing), RepeatMode.Reverse),
        label = "h3"
    )

    Row(
        modifier = modifier.size(14.dp, 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Box(modifier = Modifier.width(3.dp).height(h1.dp).clip(RoundedCornerShape(1.dp)).background(tint))
        Box(modifier = Modifier.width(3.dp).height(h2.dp).clip(RoundedCornerShape(1.dp)).background(tint))
        Box(modifier = Modifier.width(3.dp).height(h3.dp).clip(RoundedCornerShape(1.dp)).background(tint))
    }
}

/**
 * Album Browser Display with Artwork Thumbnails.
 */
@Composable
fun DapAlbumListDisplay(
    albums: List<Album>,
    selectedIndex: Int,
    listState: LazyListState,
    onAlbumSelect: (Album) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DapColors.TextPrimary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Albums",
                    style = DapTypography.MenuHeader,
                    color = DapColors.TextPrimary
                )
            }

            Text(
                text = "${albums.size} albums",
                style = DapTypography.TimeCounter,
                color = DapColors.TextSecondary
            )
        }

        HorizontalDivider(
            color = DapColors.ScreenCardBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        if (albums.isEmpty()) {
            DapEmptyState(
                title = "No Albums Found",
                message = "Scan your audio collection to index albums.",
                icon = Icons.Default.Album
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(albums) { index, album ->
                    val isWheelSelected = index == selectedIndex

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isWheelSelected) DapColors.ScreenCard else Color.Transparent
                            )
                            .clickable { onAlbumSelect(album) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DapColors.ScreenCard),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!album.coverUri.isNullOrBlank()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(album.coverUri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Cover for ${album.title}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Album,
                                        contentDescription = null,
                                        tint = DapColors.TextSecondary.copy(alpha = 0.5f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = album.title,
                                    style = DapTypography.MenuItem,
                                    color = if (isWheelSelected) DapColors.AccentIceBlue else DapColors.TextPrimary,
                                    fontWeight = if (isWheelSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${album.artist} • ${album.trackCount} tracks",
                                    style = DapTypography.MenuSecondary,
                                    color = DapColors.TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = if (isWheelSelected) DapColors.AccentIceBlue else DapColors.TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Artist Browser Display.
 */
@Composable
fun DapArtistListDisplay(
    artists: List<Artist>,
    selectedIndex: Int,
    listState: LazyListState,
    onArtistSelect: (Artist) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DapColors.TextPrimary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Artists",
                    style = DapTypography.MenuHeader,
                    color = DapColors.TextPrimary
                )
            }

            Text(
                text = "${artists.size} artists",
                style = DapTypography.TimeCounter,
                color = DapColors.TextSecondary
            )
        }

        HorizontalDivider(
            color = DapColors.ScreenCardBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        if (artists.isEmpty()) {
            DapEmptyState(
                title = "No Artists Found",
                message = "Scan your audio collection to index artists.",
                icon = Icons.Default.Person
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(artists) { index, artist ->
                    val isWheelSelected = index == selectedIndex

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isWheelSelected) DapColors.ScreenCard else Color.Transparent
                            )
                            .clickable { onArtistSelect(artist) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = artist.name,
                                style = DapTypography.MenuItem,
                                color = if (isWheelSelected) DapColors.AccentIceBlue else DapColors.TextPrimary,
                                fontWeight = if (isWheelSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${artist.trackCount} tracks",
                                style = DapTypography.MenuSecondary,
                                color = DapColors.TextSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = if (isWheelSelected) DapColors.AccentIceBlue else DapColors.TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Playlist Browser Display with Inline New Playlist Button.
 */
@Composable
fun DapPlaylistListDisplay(
    playlists: List<Playlist>,
    selectedIndex: Int,
    listState: LazyListState,
    onPlaylistSelect: (Playlist) -> Unit,
    onBack: () -> Unit,
    onCreateNewPlaylist: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DapColors.TextPrimary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Playlists",
                    style = DapTypography.MenuHeader,
                    color = DapColors.TextPrimary
                )
            }

            if (onCreateNewPlaylist != null) {
                Text(
                    text = "+ NEW",
                    color = DapColors.AccentIceBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onCreateNewPlaylist() }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        HorizontalDivider(
            color = DapColors.ScreenCardBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        if (playlists.isEmpty()) {
            DapEmptyState(
                title = "No Playlists",
                message = "Create custom playlists to organize your favorite music.",
                icon = Icons.AutoMirrored.Filled.QueueMusic
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(playlists) { index, playlist ->
                    val isWheelSelected = index == selectedIndex

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isWheelSelected) DapColors.ScreenCard else Color.Transparent
                            )
                            .clickable { onPlaylistSelect(playlist) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = playlist.name,
                                style = DapTypography.MenuItem,
                                color = if (isWheelSelected) DapColors.AccentIceBlue else DapColors.TextPrimary,
                                fontWeight = if (isWheelSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${playlist.trackCount} tracks",
                                style = DapTypography.MenuSecondary,
                                color = DapColors.TextSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = if (isWheelSelected) DapColors.AccentIceBlue else DapColors.TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
