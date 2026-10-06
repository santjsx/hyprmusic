package com.example.hyprmusic.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.rotate
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
import com.example.hyprmusic.ui.components.HyprArtworkImage
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.hyprmusic.core.model.PlaybackState
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.core.theming.HyprThemeConfig
import com.example.hyprmusic.core.theming.hyprAnimatedGlow
import com.example.hyprmusic.core.theming.hyprBounceClick
import com.example.hyprmusic.core.theming.hyprTile

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.hyprmusic.core.theming.GridLayoutStyle
import com.example.hyprmusic.core.theming.HyprTheme
import com.example.hyprmusic.ui.components.AdaptivePlayButton
import com.example.hyprmusic.ui.components.MiniEqualizerBars

@Composable
fun HomeScreen(
    theme: HyprThemeConfig,
    playbackState: PlaybackState,
    tracks: List<Track>,
    heavyRotationTracks: List<Track> = emptyList(),
    isScanning: Boolean = false,
    hasInitialScanCompleted: Boolean = true,
    listState: LazyListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() },
    onTrackSelected: (Track, List<Track>) -> Unit,
    onTogglePlayPause: () -> Unit,
    onRandomMix: () -> Unit,
    onRescan: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
                if (isScanning || !hasInitialScanCompleted) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = theme.accentColor,
                        strokeWidth = 3.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "[ hypr-storage: indexing... ]",
                        color = theme.textPrimaryColor,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Querying MediaStore audio buffers...\nPlease wait.",
                        color = theme.textSecondaryColor,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                } else {
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
        }
        return
    }

    val spec = HyprTheme.spec
    if (spec.gridStyle == GridLayoutStyle.TIGHT_TERMINAL_ROWS) {
        TerminalRowsHomeScreen(
            theme = theme,
            playbackState = playbackState,
            tracks = tracks,
            isScanning = isScanning,
            listState = listState,
            onTrackSelected = onTrackSelected,
            onTogglePlayPause = onTogglePlayPause,
            onRandomMix = onRandomMix,
            onRescan = onRescan,
            modifier = modifier
        )
        return
    }

    val displayedRecentlyIndexed = remember(tracks, heavyRotationTracks) {
        if (heavyRotationTracks.isNotEmpty()) {
            val hrIds = heavyRotationTracks.map { it.id }.toSet()
            tracks.filterNot { it.id in hrIds }
        } else {
            tracks
        }
    }

    LazyColumn(
        state = listState,
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
                        HyprArtworkImage(
                            artworkUri = currentTrack?.albumArtUri,
                            title = currentTrack?.title ?: "Music",
                            artist = currentTrack?.artist ?: "Unknown Artist",
                            shape = RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(14).dp),
                            modifier = Modifier.fillMaxSize()
                        )
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

                    AdaptivePlayButton(
                        isPlaying = playbackState.isPlaying,
                        onClick = onTogglePlayPause,
                        size = 46.dp
                    )
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

        // Heavy Rotation Carousel - only displayed when genuine play history exists
        if (heavyRotationTracks.isNotEmpty()) {
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
                            items = heavyRotationTracks,
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
                                            val hrReq = remember(track.albumArtUri) {
                                                ImageRequest.Builder(context)
                                                    .data(track.albumArtUri)
                                                    .size(260, 260)
                                                    .allowHardware(true)
                                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                                    .diskCachePolicy(CachePolicy.ENABLED)
                                                    .crossfade(false)
                                                    .build()
                                            }
                                            AsyncImage(
                                                model = hrReq,
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

        // Library Tracks Section - deduplicate items already present in Heavy Rotation
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
            items = displayedRecentlyIndexed,
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
    val context = LocalContext.current
    val durationSec = track.durationMs / 1000
    val durationStr = String.format("%d:%02d", durationSec / 60, durationSec % 60)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(10).dp))
            .background(if (isCurrent) theme.accentColor.copy(alpha = 0.12f) else theme.surfaceColor)
            .border(
                width = if (isCurrent) theme.borderThicknessDp.dp else 1.dp,
                color = if (isCurrent) theme.accentColor else theme.inactiveBorderColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(10).dp)
            )
            .hyprBounceClick { onClick() }
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Artwork with vinyl disc edge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.surfaceVariantColor),
                contentAlignment = Alignment.Center
            ) {
                HyprArtworkImage(
                    artworkUri = track.albumArtUri,
                    title = track.title,
                    artist = track.artist,
                    trackId = track.id,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title & Artist
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    color = if (isCurrent) theme.accentColor else theme.textPrimaryColor,
                    fontSize = 13.5.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = track.artist,
                        color = theme.textSecondaryColor,
                        fontSize = 11.5.sp,
                        fontFamily = HyprTheme.spec.fontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Audio codec pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(theme.surfaceVariantColor)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "${track.audioFormat} ${track.bitrate}k",
                            color = if (isCurrent) theme.accentColor else theme.textSecondaryColor,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Duration & Live Equalizer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isPlaying) {
                    MiniEqualizerBars(
                        theme = theme,
                        isPlaying = true,
                        modifier = Modifier.height(14.dp)
                    )
                }

                Text(
                    text = durationStr,
                    color = if (isCurrent) theme.accentColor else theme.textSecondaryColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun TerminalRowsHomeScreen(
    theme: HyprThemeConfig,
    playbackState: PlaybackState,
    tracks: List<Track>,
    isScanning: Boolean,
    listState: LazyListState,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onTogglePlayPause: () -> Unit,
    onRandomMix: () -> Unit,
    onRescan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val spec = HyprTheme.spec
    val currentTrack = playbackState.currentTrack

    val infiniteTransition = rememberInfiniteTransition(label = "rescanSpin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rescanAngle"
    )

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spec.gaps),
        verticalArrangement = Arrangement.spacedBy(spec.gaps.coerceAtMost(8.dp))
    ) {
        // Professional High Density Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(spec.cornerRadius))
                    .background(spec.surface)
                    .border(spec.borderThickness, spec.borderInactive, RoundedCornerShape(spec.cornerRadius))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AUDIO ENGINE",
                            fontFamily = spec.fontFamily,
                            color = spec.borderActive,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        val statusText = when {
                            isScanning -> "SCANNING"
                            playbackState.isPlaying -> "PLAYING"
                            playbackState.currentTrack != null -> "PAUSED"
                            else -> "IDLE"
                        }
                        val isEngineActive = isScanning || playbackState.isPlaying

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isEngineActive) spec.borderActive.copy(alpha = 0.2f) else spec.surfaceVariant)
                                .border(1.dp, if (isEngineActive) spec.borderActive else spec.borderInactive, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = statusText,
                                fontFamily = spec.fontFamily,
                                color = if (isEngineActive) spec.borderActive else spec.textSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Total tracks: ${tracks.size} • Bit-perfect PCM 16-bit",
                        fontFamily = spec.fontFamily,
                        color = spec.textSecondary,
                        fontSize = 11.5.sp
                    )

                    if (currentTrack != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Active: ${currentTrack.artist} - ${currentTrack.title}",
                            fontFamily = spec.fontFamily,
                            color = spec.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Professional quick actions row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Action 1: PLAY / PAUSE
                        val isPlaying = playbackState.isPlaying
                        val isPlayActive = isPlaying || currentTrack != null
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(spec.cornerRadius.coerceAtMost(8.dp)))
                                .background(if (isPlaying) spec.borderActive.copy(alpha = 0.16f) else spec.surfaceVariant)
                                .border(
                                    width = if (isPlayActive) 1.dp else 0.8.dp,
                                    color = if (isPlayActive) spec.borderActive else spec.borderInactive,
                                    shape = RoundedCornerShape(spec.cornerRadius.coerceAtMost(8.dp))
                                )
                                .hyprBounceClick { onTogglePlayPause() }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = if (isPlayActive) spec.borderActive else spec.textPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isPlaying) "PAUSE" else "PLAY",
                                    fontFamily = spec.fontFamily,
                                    color = if (isPlayActive) spec.borderActive else spec.textPrimary,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Action 2: SHUFFLE
                        val isShuffleActive = playbackState.isShuffle
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(spec.cornerRadius.coerceAtMost(8.dp)))
                                .background(if (isShuffleActive) spec.borderActive.copy(alpha = 0.14f) else spec.surfaceVariant)
                                .border(
                                    width = if (isShuffleActive) 1.dp else 0.8.dp,
                                    color = if (isShuffleActive) spec.borderActive else spec.borderInactive,
                                    shape = RoundedCornerShape(spec.cornerRadius.coerceAtMost(8.dp))
                                )
                                .hyprBounceClick { onRandomMix() }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shuffle,
                                    contentDescription = "Shuffle",
                                    tint = if (isShuffleActive) spec.borderActive else spec.textPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SHUFFLE",
                                    fontFamily = spec.fontFamily,
                                    color = if (isShuffleActive) spec.borderActive else spec.textPrimary,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Action 3: RESCAN
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(spec.cornerRadius.coerceAtMost(8.dp)))
                                .background(if (isScanning) spec.borderActive.copy(alpha = 0.14f) else spec.surfaceVariant)
                                .border(
                                    width = if (isScanning) 1.dp else 0.8.dp,
                                    color = if (isScanning) spec.borderActive else spec.borderInactive,
                                    shape = RoundedCornerShape(spec.cornerRadius.coerceAtMost(8.dp))
                                )
                                .hyprBounceClick(enabled = !isScanning) { onRescan() }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Rescan",
                                    tint = if (isScanning) spec.borderActive else spec.textSecondary,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .rotate(if (isScanning) spinAngle else 0f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isScanning) "SCANNING" else "RESCAN",
                                    fontFamily = spec.fontFamily,
                                    color = if (isScanning) spec.borderActive else spec.textSecondary,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section label
        item {
            Text(
                text = "INDEXED TRACKS",
                fontFamily = spec.fontFamily,
                color = spec.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
        }

        // Table Rows
        itemsIndexed(
            items = tracks,
            key = { _, it -> "cli_${it.id}" },
            contentType = { _, _ -> "cli_track_row" }
        ) { index, track ->
            val isCurrent = currentTrack?.id == track.id
            val isPlaying = isCurrent && playbackState.isPlaying

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(spec.cornerRadius.coerceAtMost(8.dp)))
                    .background(if (isCurrent) spec.borderActive.copy(alpha = 0.12f) else spec.surface)
                    .border(
                        width = if (isCurrent) spec.borderThickness else 1.dp,
                        color = if (isCurrent) spec.borderActive else spec.borderInactive.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(spec.cornerRadius.coerceAtMost(8.dp))
                    )
                    .clickable { onTrackSelected(track, tracks) }
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Track Index Number
                    val indexText = if (tracks.size >= 100) {
                        String.format("%03d", index + 1)
                    } else {
                        String.format("%02d", index + 1)
                    }
                    Text(
                        text = indexText,
                        fontFamily = spec.fontFamily,
                        color = if (isCurrent) spec.borderActive else spec.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(26.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Compact Album Artwork Jacket Thumbnail (38.dp x 38.dp)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(spec.surfaceVariant)
                            .border(
                                width = if (isCurrent) 1.dp else 0.5.dp,
                                color = if (isCurrent) spec.borderActive else spec.borderInactive.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        HyprArtworkImage(
                            artworkUri = track.albumArtUri,
                            title = track.title,
                            artist = track.artist,
                            trackId = track.id,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxSize()
                        )

                        // Live Equalizer overlay on art when active
                        if (isPlaying) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(spec.surface.copy(alpha = 0.55f)),
                                contentAlignment = Alignment.Center
                            ) {
                                MiniEqualizerBars(
                                    theme = theme,
                                    isPlaying = true,
                                    modifier = Modifier.height(13.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Track Title & Artist
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            fontFamily = spec.fontFamily,
                            color = if (isCurrent) spec.borderActive else spec.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = track.artist,
                            fontFamily = spec.fontFamily,
                            color = spec.textSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Bitrate badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(spec.surfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${track.bitrate}k",
                            fontFamily = spec.fontFamily,
                            color = if (isCurrent) spec.borderActive else spec.textSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Duration
                    val durationSec = track.durationMs / 1000
                    Text(
                        text = String.format("%d:%02d", durationSec / 60, durationSec % 60),
                        fontFamily = spec.fontFamily,
                        color = spec.textSecondary,
                        fontSize = 11.5.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

