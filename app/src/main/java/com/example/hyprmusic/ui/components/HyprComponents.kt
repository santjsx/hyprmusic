package com.example.hyprmusic.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import com.example.hyprmusic.core.theming.GridLayoutStyle
import com.example.hyprmusic.core.theming.HyprTheme
import com.example.hyprmusic.core.theming.IconPackType
import com.example.hyprmusic.core.theming.ProgressBarStyle
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.hyprmusic.core.media.HyprVisualizerState
import com.example.hyprmusic.core.model.LyricLine
import com.example.hyprmusic.core.model.PlaybackState
import com.example.hyprmusic.core.theming.HyprThemeConfig
import com.example.hyprmusic.core.theming.hyprAnimatedGlow
import com.example.hyprmusic.core.theming.hyprBounceClick
import com.example.hyprmusic.core.theming.hyprTile

enum class HyprWorkspace(val index: Int, val label: String) {
    HOME(1, "home"),
    LIBRARY(2, "lib"),
    PLAYING(3, "player"),
    SETTINGS(4, "rice")
}

/**
 * Top Waybar telemetry header: Displays terminal path, live bit-perfect audio spec, and EQ quick toggle.
 */
@Composable
fun HyprWaybarHeader(
    theme: HyprThemeConfig,
    title: String = "~ / hypr / audio",
    onOpenEqualizer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = (theme.windowGapsDp + 4).dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Linux terminal path identifier
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(theme.accentColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                color = theme.textPrimaryColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Bit-perfect Audio & Equalizer Telemetry Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(8).dp))
                    .background(theme.surfaceVariantColor)
                    .padding(horizontal = 7.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = theme.accentColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "16-BIT/44.1k",
                    color = theme.textSecondaryColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(8).dp))
                    .background(theme.accentColor.copy(alpha = 0.15f))
                    .border(1.dp, theme.accentColor.copy(alpha = 0.4f), RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(8).dp))
                    .clickable { onOpenEqualizer() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "[EQ]",
                    color = theme.accentColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Ergonomic Bottom Waybar Dock: Houses thumb-friendly workspace navigation & quick-search launcher.
 */
@Composable
fun HyprBottomDock(
    theme: HyprThemeConfig,
    currentWorkspace: HyprWorkspace,
    onWorkspaceSelected: (HyprWorkspace) -> Unit,
    onToggleSearch: () -> Unit = {},
    isSearchActive: Boolean = false,
    isPlaying: Boolean = false,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = theme.windowGapsDp.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(theme.borderRadiusDp.dp))
            .background(theme.surfaceColor.copy(alpha = 0.96f))
            .border(theme.borderThicknessDp.dp, theme.inactiveBorderColor, RoundedCornerShape(theme.borderRadiusDp.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Workspaces [1:home] [2:lib] [3:player] [4:rice] with 48dp+ ergonomic touch targets
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HyprWorkspace.entries.forEach { ws ->
                val isActive = ws == currentWorkspace
                val icon = when (ws) {
                    HyprWorkspace.HOME -> Icons.Default.Home
                    HyprWorkspace.LIBRARY -> Icons.Default.LibraryMusic
                    HyprWorkspace.PLAYING -> Icons.Default.GraphicEq
                    HyprWorkspace.SETTINGS -> Icons.Default.Tune
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 50.dp)
                        .clip(RoundedCornerShape((theme.borderRadiusDp.coerceAtMost(10)).dp))
                        .background(
                            if (isActive) theme.accentColor.copy(alpha = 0.22f)
                            else Color.Transparent
                        )
                        .border(
                            width = if (isActive) 1.dp else 0.dp,
                            color = if (isActive) theme.accentColor else Color.Transparent,
                            shape = RoundedCornerShape((theme.borderRadiusDp.coerceAtMost(10)).dp)
                        )
                        .hyprBounceClick {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onWorkspaceSelected(ws)
                        }
                        .padding(vertical = 5.dp, horizontal = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (ws == HyprWorkspace.PLAYING && isPlaying) {
                            MiniEqualizerBars(
                                theme = theme,
                                isPlaying = true,
                                modifier = Modifier.height(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = icon,
                                contentDescription = ws.label,
                                tint = if (isActive) theme.accentColor else theme.textSecondaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "[${ws.index}:${ws.label}]",
                            color = if (isActive) theme.accentColor else theme.textSecondaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Clip
                        )

                        if (isActive) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(2.dp)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(theme.accentColor)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Ergonomic Quick Search runner trigger button
        Box(
            modifier = Modifier
                .size(width = 46.dp, height = 50.dp)
                .clip(RoundedCornerShape((theme.borderRadiusDp.coerceAtMost(10)).dp))
                .background(
                    if (isSearchActive) theme.accentColor
                    else theme.surfaceVariantColor
                )
                .border(
                    width = 1.dp,
                    color = if (isSearchActive) theme.accentColor else theme.inactiveBorderColor,
                    shape = RoundedCornerShape((theme.borderRadiusDp.coerceAtMost(10)).dp)
                )
                .hyprBounceClick {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onToggleSearch()
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (isSearchActive) theme.backgroundColor else theme.accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "find",
                    color = if (isSearchActive) theme.backgroundColor else theme.accentColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Ultra-smooth, hardware-accelerated equalizer bars for MiniPlayer & Waybar Bottom Dock.
 * Drawn entirely in Canvas DrawScope with zero animation allocations and zero parent recompositions.
 */
@Composable
fun MiniEqualizerBars(
    theme: HyprThemeConfig,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isPlaying) {
        Canvas(modifier = modifier.size(width = 13.dp, height = 14.dp)) {
            val barW = 2.5.dp.toPx()
            val corner = CornerRadius(1.dp.toPx(), 1.dp.toPx())
            val restH = 3.dp.toPx()
            val top = size.height - restH
            val spacing = 2.dp.toPx()
            for (i in 0..2) {
                drawRoundRect(
                    color = theme.accentColor.copy(alpha = 0.45f),
                    topLeft = Offset(i * (barW + spacing), top),
                    size = Size(barW, restH),
                    cornerRadius = corner
                )
            }
        }
        return
    }

    val amplitudes by HyprVisualizerState.amplitudes.collectAsStateWithLifecycle()

    Canvas(modifier = modifier.size(width = 13.dp, height = 14.dp)) {
        val barW = 2.5.dp.toPx()
        val corner = CornerRadius(1.dp.toPx(), 1.dp.toPx())
        val spacing = 2.dp.toPx()
        val maxH = size.height
        val minH = 2.5.dp.toPx()

        val raw1 = amplitudes.getOrElse(1) { 0.2f }
        val raw2 = amplitudes.getOrElse(5) { 0.4f }
        val raw3 = amplitudes.getOrElse(9) { 0.25f }

        val h1 = (minH + raw1 * (maxH - minH)).coerceIn(minH, maxH)
        val h2 = (minH + raw2 * (maxH - minH)).coerceIn(minH, maxH)
        val h3 = (minH + raw3 * (maxH - minH)).coerceIn(minH, maxH)

        val accent = theme.accentColor
        drawRoundRect(
            color = accent,
            topLeft = Offset(0f, maxH - h1),
            size = Size(barW, h1),
            cornerRadius = corner
        )
        drawRoundRect(
            color = accent,
            topLeft = Offset(barW + spacing, maxH - h2),
            size = Size(barW, h2),
            cornerRadius = corner
        )
        drawRoundRect(
            color = accent,
            topLeft = Offset(2f * (barW + spacing), maxH - h3),
            size = Size(barW, h3),
            cornerRadius = corner
        )
    }
}

/**
 * Gesture-enabled, riced MiniPlayer sitting right above the bottom dock.
 * Supports:
 * - Tap or swipe up to expand to fullscreen player
 * - Swipe left: skip next track
 * - Swipe right: skip previous track
 * - Rotating mini vinyl disc & live equalizer bars
 */
@Composable
fun MiniPlayer(
    theme: HyprThemeConfig,
    playbackState: PlaybackState,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit = {},
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack ?: return
    val view = LocalView.current

    var totalDragX by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = theme.windowGapsDp.dp)
            .hyprTile(theme = theme, isActive = playbackState.isPlaying)
            .pointerInput(track.id) {
                detectHorizontalDragGestures(
                    onDragStart = { totalDragX = 0f },
                    onDragEnd = {
                        if (totalDragX < -60f) {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onSkipNext()
                        } else if (totalDragX > 60f) {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onSkipPrevious()
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        totalDragX += dragAmount
                    }
                )
            }
            .clickable { onClick() }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album Cover Art (Clean, Upright)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.surfaceVariantColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (!track.albumArtUri.isNullOrBlank()) {
                        val context = LocalContext.current
                        val miniPlayerReq = remember(track.albumArtUri) {
                            ImageRequest.Builder(context)
                                .data(track.albumArtUri)
                                .size(140, 140)
                                .allowHardware(true)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .crossfade(false)
                                .build()
                        }
                        AsyncImage(
                            model = miniPlayerReq,
                            contentDescription = track.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Title and Artist with live mini equalizer
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = track.title,
                            color = theme.textPrimaryColor,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        MiniEqualizerBars(
                            theme = theme,
                            isPlaying = playbackState.isPlaying
                        )
                    }
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

                // Controls
                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onPlayPause()
                    }
                ) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = theme.accentColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onSkipNext()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = theme.textPrimaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // High Precision Mini Progress Bar
            LinearProgressIndicator(
                progress = { playbackState.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = theme.accentColor,
                trackColor = theme.surfaceVariantColor
            )
        }
    }
}

/**
 * Bottom Quick-Search Runner Bar (Hypr-Run / Wofi inspired)
 */
@Composable
fun HyprBottomSearchRunner(
    theme: HyprThemeConfig,
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = theme.windowGapsDp.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(theme.borderRadiusDp.dp))
            .background(theme.surfaceColor)
            .border(theme.borderThicknessDp.dp, theme.accentColor, RoundedCornerShape(theme.borderRadiusDp.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "find >",
                color = theme.accentColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = "filter tracks, artists, albums...",
                        color = theme.textSecondaryColor.copy(alpha = 0.6f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = theme.textPrimaryColor,
                    unfocusedTextColor = theme.textPrimaryColor,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Search",
                    tint = theme.textSecondaryColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun SyncedLyricsView(
    lyrics: List<LyricLine>?,
    isLoading: Boolean,
    currentPositionMs: Long,
    theme: HyprThemeConfig,
    onSeekTo: (Long) -> Unit,
    onRetry: () -> Unit,
    onShowCoverArt: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp,
                    color = theme.accentColor
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "grep -i lyrics in local storage...",
                    color = theme.textSecondaryColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        }
        return
    }

    if (lyrics == null || lyrics.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.surfaceVariantColor.copy(alpha = 0.6f))
                    .border(1.dp, theme.accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = theme.accentColor,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "[!] NO SYNCED LYRICS FOUND",
                    color = theme.textPrimaryColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Instrumental track or unindexed .lrc file",
                    color = theme.textSecondaryColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.surfaceColor)
                            .clickable(onClick = onRetry)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "[ RETRY ]",
                            color = theme.accentColor,
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.surfaceColor)
                            .clickable(onClick = onShowCoverArt)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "[ SHOW ART ]",
                            color = theme.textSecondaryColor,
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        return
    }

    val activeIndex = remember(lyrics, currentPositionMs) {
        val searchIdx = lyrics.binarySearchBy(currentPositionMs) { it.timestampMs }
        val idx = if (searchIdx >= 0) searchIdx else (-searchIdx - 2).coerceAtLeast(0)
        idx.coerceIn(0, (lyrics.size - 1).coerceAtLeast(0))
    }
    val listState = rememberLazyListState()

    LaunchedEffect(activeIndex) {
        if (activeIndex in lyrics.indices) {
            listState.animateScrollToItem((activeIndex - 2).coerceAtLeast(0))
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(30.dp)) }

        itemsIndexed(
            items = lyrics,
            key = { index, item -> "$index-${item.timestampMs}" }
        ) { index, line ->
            LyricLineItem(
                theme = theme,
                line = line,
                isActive = index == activeIndex,
                onSeekTo = onSeekTo
            )
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
private fun LyricLineItem(
    theme: HyprThemeConfig,
    line: LyricLine,
    isActive: Boolean,
    onSeekTo: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSeekTo(line.timestampMs) }
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = line.text,
            color = if (isActive) theme.accentColor else theme.textSecondaryColor.copy(alpha = 0.45f),
            fontSize = if (isActive) 19.sp else 15.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            fontFamily = if (isActive) FontFamily.Default else FontFamily.Monospace
        )
    }
}

/**
 * Structurally adaptive progress tracker that inspects HyprTheme.spec.progressStyle:
 * - CAPSULE_SEEKER: Modern capsule pill scrubber with smooth interactive thumb
 * - MINIMAL_WAYBAR: Ultra-clean minimal floating line track with timestamps
 * - DYNAMIC_NEON: Audio-reactive neon gradient seek slider with dynamic glow
 */
@Composable
fun AdaptiveProgressBar(
    progressPercent: Float,
    elapsed: String,
    total: String,
    onSeekToPercent: ((Float) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val spec = HyprTheme.spec
    val safePercent = progressPercent.coerceIn(0f, 1f)

    when (spec.progressStyle) {
        ProgressBarStyle.CAPSULE_SEEKER -> {
            // Modern, smooth capsule scrubber inspired by Lucide UI design
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .then(
                            if (onSeekToPercent != null) {
                                Modifier
                                    .pointerInput(Unit) {
                                        detectHorizontalDragGestures { change, _ ->
                                            val newPercent = (change.position.x / size.width).coerceIn(0f, 1f)
                                            onSeekToPercent(newPercent)
                                        }
                                    }
                                    .pointerInput(Unit) {
                                        detectTapGestures { offset ->
                                            val newPercent = (offset.x / size.width).coerceIn(0f, 1f)
                                            onSeekToPercent(newPercent)
                                        }
                                    }
                            } else Modifier
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    val totalWidthPx = constraints.maxWidth.toFloat()
                    val trackHeight = 5.dp

                    // Background track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(trackHeight)
                            .clip(RoundedCornerShape(3.dp))
                            .background(spec.surfaceVariant)
                            .border(0.5.dp, spec.borderInactive.copy(alpha = 0.6f), RoundedCornerShape(3.dp))
                    )

                    // Active progress fill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(safePercent)
                            .height(trackHeight)
                            .clip(RoundedCornerShape(3.dp))
                            .background(spec.borderActive)
                    )

                    // Smooth thumb indicator
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                translationX = (totalWidthPx * safePercent - 6.dp.toPx()).coerceAtLeast(0f)
                            }
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(spec.textPrimary)
                            .border(1.5.dp, spec.borderActive, CircleShape)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = elapsed,
                        fontFamily = spec.fontFamily,
                        color = spec.borderActive,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = total,
                        fontFamily = spec.fontFamily,
                        color = spec.textSecondary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        ProgressBarStyle.MINIMAL_WAYBAR -> {
            // Minimalist waybar linear progress track
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(spec.cornerRadius))
                        .then(
                            if (onSeekToPercent != null) {
                                Modifier
                                    .pointerInput(Unit) {
                                        detectHorizontalDragGestures { change, _ ->
                                            val newPercent = (change.position.x / size.width).coerceIn(0f, 1f)
                                            onSeekToPercent(newPercent)
                                        }
                                    }
                                    .pointerInput(Unit) {
                                        detectTapGestures { offset ->
                                            val newPercent = (offset.x / size.width).coerceIn(0f, 1f)
                                            onSeekToPercent(newPercent)
                                        }
                                    }
                            } else Modifier
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    LinearProgressIndicator(
                        progress = { safePercent },
                        color = spec.borderActive,
                        trackColor = spec.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.5.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = elapsed,
                        fontFamily = spec.fontFamily,
                        color = spec.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = total,
                        fontFamily = spec.fontFamily,
                        color = spec.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        ProgressBarStyle.DYNAMIC_NEON -> {
            // Glowing neon slider with smooth scrub feedback
            Column(
                modifier = modifier.fillMaxWidth()
            ) {
                Slider(
                    value = safePercent,
                    onValueChange = { onSeekToPercent?.invoke(it) },
                    colors = SliderDefaults.colors(
                        thumbColor = spec.borderActive,
                        activeTrackColor = spec.borderActive,
                        inactiveTrackColor = spec.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = elapsed,
                        fontFamily = spec.fontFamily,
                        color = spec.borderActive,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = total,
                        fontFamily = spec.fontFamily,
                        color = spec.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Structurally adaptive play/pause control button inspecting HyprTheme.spec.iconPack:
 * - LUCIDE: Crisp 24x24 2px stroke line geometry inspired by lucide.dev
 * - PHOSPHOR: Refined geometric outline inspired by phosphoricons.com
 * - REMIX: Bold neutral UI icons inspired by remixicon.com
 */
@Composable
fun AdaptivePlayButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 48.dp
) {
    val spec = HyprTheme.spec

    when (spec.iconPack) {
        IconPackType.LUCIDE -> {
            // Lucide Icon design language: clean circular frame, 2px stroke vectors
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(spec.surfaceVariant)
                    .border(1.5.dp, spec.borderActive, CircleShape)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = spec.borderActive,
                    modifier = Modifier.size(size * 0.52f)
                )
            }
        }

        IconPackType.PHOSPHOR -> {
            // Phosphor Icon design language: soft squircle outline frame, balanced weights
            Box(
                modifier = modifier
                    .size(size)
                    .clip(RoundedCornerShape(spec.cornerRadius))
                    .background(spec.borderActive.copy(alpha = 0.16f))
                    .border(1.5.dp, spec.borderActive, RoundedCornerShape(spec.cornerRadius))
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = spec.borderActive,
                    modifier = Modifier.size(size * 0.52f)
                )
            }
        }

        IconPackType.REMIX -> {
            // Remix Icon design language: solid high-contrast accent fill
            Box(
                modifier = modifier
                    .size(size)
                    .clip(RoundedCornerShape(spec.cornerRadius.coerceAtMost(14.dp)))
                    .background(spec.borderActive)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = spec.bg,
                    modifier = Modifier.size(size * 0.52f)
                )
            }
        }
    }
}

/**
 * Structurally adaptive skip next/previous control button inspecting HyprTheme.spec.iconPack.
 * Renders authentic Lucide, Phosphor, or Remix vector icons without any text or ASCII glyphs.
 */
@Composable
fun AdaptiveSkipButton(
    isNext: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 36.dp
) {
    val spec = HyprTheme.spec

    when (spec.iconPack) {
        IconPackType.LUCIDE -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(spec.surfaceVariant.copy(alpha = 0.6f))
                    .border(1.dp, spec.borderInactive, CircleShape)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isNext) Icons.Default.SkipNext else Icons.Default.SkipPrevious,
                    contentDescription = if (isNext) "Next" else "Previous",
                    tint = spec.textPrimary,
                    modifier = Modifier.size(size * 0.58f)
                )
            }
        }

        IconPackType.PHOSPHOR -> {
            IconButton(
                onClick = onClick,
                modifier = modifier.size(size)
            ) {
                Icon(
                    imageVector = if (isNext) Icons.Default.SkipNext else Icons.Default.SkipPrevious,
                    contentDescription = if (isNext) "Next" else "Previous",
                    tint = spec.textPrimary,
                    modifier = Modifier.size(size * 0.65f)
                )
            }
        }

        IconPackType.REMIX -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(RoundedCornerShape(spec.cornerRadius.coerceAtMost(8.dp)))
                    .background(spec.surfaceVariant)
                    .border(1.dp, spec.borderInactive, RoundedCornerShape(spec.cornerRadius.coerceAtMost(8.dp)))
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isNext) Icons.Default.SkipNext else Icons.Default.SkipPrevious,
                    contentDescription = if (isNext) "Next" else "Previous",
                    tint = spec.textPrimary,
                    modifier = Modifier.size(size * 0.58f)
                )
            }
        }
    }
}
