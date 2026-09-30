package com.example.hyprmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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
    heavyRotationTracks: List<Track> = emptyList(),
    onTrackSelected: (Track, List<Track>) -> Unit,
    onTogglePlayPause: () -> Unit,
    onRandomMix: () -> Unit,
    onRescan: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (tracks.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(theme.windowGapsDp.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(theme.borderRadiusDp.dp))
                    .background(theme.surfaceColor)
                    .border(theme.borderThicknessDp.dp, theme.inactiveBorderColor, RoundedCornerShape(theme.borderRadiusDp.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = theme.accentColor,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "[ hypr-storage: 0 tracks ]",
                    color = theme.textPrimaryColor,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "No audio tracks indexed on device storage.\nPlace FLAC, WAV, or MP3 files into your Music or Downloads folder.",
                    color = theme.textSecondaryColor,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(8).dp))
                        .background(theme.accentColor)
                        .clickable { onRescan() }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = theme.backgroundColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "[ SCAN STORAGE ]",
                            color = theme.backgroundColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = theme.windowGapsDp.dp),
        verticalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
    ) {
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
                            text = "Shuffled queue",
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
                            text = "Bit-perfect 16-bit PCM",
                            color = theme.textSecondaryColor,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Heavy Rotation Carousel
        val rotationList = if (heavyRotationTracks.isNotEmpty()) heavyRotationTracks else tracks.take(10)
        if (rotationList.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HEAVY ROTATION",
                            color = theme.textSecondaryColor,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "[ most played ]",
                            color = theme.accentColor,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
                    ) {
                        items(
                            items = rotationList,
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
                                        fontSize = 12.5.sp,
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
        }

        // Library Tracks Section
        item {
            Text(
                text = "RECENTLY INDEXED",
                color = theme.textSecondaryColor,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
            )
        }

        items(
            items = tracks.take(30),
            key = { "rec_${it.id}" },
            contentType = { "recent_track" }
        ) { track ->
            val isPlaying = playbackState.currentTrack?.id == track.id && playbackState.isPlaying
            val isCurrent = playbackState.currentTrack?.id == track.id
            HomeTrackRowItem(
                track = track,
                theme = theme,
                isPlaying = isPlaying,
                isCurrent = isCurrent,
                onClick = { onTrackSelected(track, tracks) }
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun HomeTrackRowItem(
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
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(8.dp))
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
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    color = if (isCurrent) theme.accentColor else theme.textPrimaryColor,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${track.artist} • ${track.bitrate}kbps",
                    color = theme.textSecondaryColor,
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isPlaying) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = theme.accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
