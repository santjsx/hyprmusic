package com.example.hyprmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hyprmusic.core.model.PlaybackState
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.core.theming.HyprThemeConfig
import com.example.hyprmusic.core.theming.hyprAnimatedGlow
import com.example.hyprmusic.core.theming.hyprBounceClick
import com.example.hyprmusic.core.theming.hyprTile

@Composable
fun HomeScreen(
    theme: HyprThemeConfig,
    playbackState: PlaybackState,
    tracks: List<Track>,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onTogglePlayPause: () -> Unit,
    onRandomMix: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredTracks = remember(searchQuery, tracks) {
        if (searchQuery.isBlank()) tracks
        else {
            val q = searchQuery.trim().lowercase()
            tracks.filter {
                it.title.lowercase().contains(q) ||
                it.artist.lowercase().contains(q) ||
                it.album.lowercase().contains(q)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = theme.windowGapsDp.dp),
        verticalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
    ) {
        // Quick Terminal Search Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .hyprTile(theme = theme)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = theme.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$",
                        color = theme.accentColor,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = theme.textPrimaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(theme.accentColor),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "grep -i library ~/music...",
                                    color = theme.textSecondaryColor.copy(alpha = 0.6f),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = theme.textSecondaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        if (searchQuery.isNotEmpty()) {
            // Search Results Mode
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MATCHES FOUND",
                        color = theme.textSecondaryColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "[ ${filteredTracks.size} RESULTS ]",
                        color = theme.accentColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (filteredTracks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .hyprTile(theme = theme)
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "[!] NO TRACKS MATCHED",
                                color = theme.accentColor,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Zero results for \"$searchQuery\"",
                                color = theme.textSecondaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(
                    items = filteredTracks,
                    key = { "search_${it.id}" },
                    contentType = { "search_track" }
                ) { track ->
                    HomeTrackRowItem(
                        track = track,
                        theme = theme,
                        isPlaying = playbackState.currentTrack?.id == track.id && playbackState.isPlaying,
                        isCurrent = playbackState.currentTrack?.id == track.id,
                        onClick = { onTrackSelected(track, filteredTracks) }
                    )
                }
            }
        } else {
            // Default Home Feed Mode

            // Hero Now Playing / Quick Play Bento Tile
            item {
                val currentTrack = playbackState.currentTrack ?: tracks.firstOrNull()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .hyprTile(theme = theme, isActive = playbackState.isPlaying)
                        .then(if (playbackState.isPlaying) Modifier.hyprAnimatedGlow(theme) else Modifier)
                        .hyprBounceClick {
                            currentTrack?.let { onTrackSelected(it, tracks) }
                        }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Artwork / Vinyl
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(14).dp))
                                .background(theme.surfaceVariantColor),
                            contentAlignment = Alignment.Center
                        ) {
                            if (currentTrack?.albumArtUri != null) {
                                AsyncImage(
                                    model = currentTrack.albumArtUri,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = theme.accentColor,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = theme.accentColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (playbackState.isPlaying) "NOW STREAMING" else "READY TO PLAY",
                                    color = theme.accentColor,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = currentTrack?.title ?: "Select a track",
                                color = theme.textPrimaryColor,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = currentTrack?.artist ?: "No media loaded",
                                color = theme.textSecondaryColor,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = onTogglePlayPause,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(theme.surfaceVariantColor)
                        ) {
                            Icon(
                                imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = theme.accentColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // Dual Bento Grid: Random Mix & Audio Engine Telemetry
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
                ) {
                    // Random Mix Tile with Spring Bounce
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .hyprTile(theme = theme)
                            .hyprBounceClick { onRandomMix() }
                            .padding(14.dp)
                    ) {
                        Column {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = null,
                                tint = theme.accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "RANDOM MIX",
                                color = theme.textPrimaryColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Instant smart shuffle",
                                color = theme.textSecondaryColor,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Audio Telemetry Tile
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .hyprTile(theme = theme)
                            .padding(14.dp)
                    ) {
                        Column {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = theme.accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "${tracks.size} TRACKS",
                                color = theme.textPrimaryColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Bit-perfect DSP active",
                                color = theme.textSecondaryColor,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Heavy Rotation Carousel
            item {
                Column {
                    Text(
                        text = "HEAVY ROTATION",
                        color = theme.textSecondaryColor,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
                    ) {
                        items(
                            items = tracks.take(10),
                            key = { "hr_${it.id}" },
                            contentType = { "heavy_rotation_track" }
                        ) { track ->
                            val isTrackActive = playbackState.currentTrack?.id == track.id
                            Box(
                                modifier = Modifier
                                    .width(140.dp)
                                    .hyprTile(theme = theme, isActive = isTrackActive)
                                    .hyprBounceClick { onTrackSelected(track, tracks) }
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(10).dp))
                                            .background(theme.surfaceVariantColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (track.albumArtUri != null) {
                                            AsyncImage(
                                                model = track.albumArtUri,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Album,
                                                contentDescription = null,
                                                tint = theme.accentColor,
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = track.title,
                                        color = theme.textPrimaryColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
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
                            }
                        }
                    }
                }
            }

            // Recent Ingestion List
            item {
                Text(
                    text = "LIBRARY AUDIO STACK",
                    color = theme.textSecondaryColor,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                )
            }

            items(
                items = tracks,
                key = { "stack_${it.id}" },
                contentType = { "track" }
            ) { track ->
                HomeTrackRowItem(
                    track = track,
                    theme = theme,
                    isPlaying = playbackState.currentTrack?.id == track.id && playbackState.isPlaying,
                    isCurrent = playbackState.currentTrack?.id == track.id,
                    onClick = { onTrackSelected(track, tracks) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(90.dp)) }
    }
}

@Composable
private fun HomeTrackRowItem(
    track: Track,
    theme: HyprThemeConfig,
    isPlaying: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .hyprTile(theme = theme, isActive = isCurrent)
            .hyprBounceClick { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.surfaceVariantColor),
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    Icon(
                        imageVector = Icons.Default.Equalizer,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                } else if (track.albumArtUri != null) {
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
                    color = if (isCurrent) theme.accentColor else theme.textPrimaryColor,
                    fontSize = 14.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${track.artist} • ${track.formattedDuration}",
                    color = theme.textSecondaryColor,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(theme.surfaceVariantColor)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = track.audioFormat,
                    color = if (track.isLossless) theme.accentColor else theme.textSecondaryColor,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
