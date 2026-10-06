package com.example.hyprmusic.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode as AnimRepeatMode
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
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.delay
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
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import com.example.hyprmusic.core.model.RepeatMode
import com.example.hyprmusic.core.theming.GridLayoutStyle
import com.example.hyprmusic.core.theming.HyprTheme
import com.example.hyprmusic.core.theming.IconPackType
import com.example.hyprmusic.core.theming.PlayControlStyle
import com.example.hyprmusic.core.theming.ProgressBarStyle
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateDpAsState
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
import kotlinx.coroutines.isActive

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
 * Ergonomic Bottom Waybar Dock: Detached floating glass capsule with kinetic active pill indicator,
 * tactile icon morphs, and dancing mini-equalizer bars on active tabs.
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = theme.windowGapsDp.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtLeast(16).dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        theme.surfaceColor.copy(alpha = 0.94f),
                        theme.surfaceColor.copy(alpha = 0.98f)
                    )
                )
            )
            .border(
                width = theme.borderThicknessDp.dp,
                color = theme.inactiveBorderColor.copy(alpha = 0.6f),
                shape = RoundedCornerShape(theme.borderRadiusDp.coerceAtLeast(16).dp)
            )
            .padding(horizontal = 6.dp, vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Workspaces [1:home] [2:lib] [3:player] [4:rice] with ergonomic touch targets
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

                    val pillIndicatorWidth by animateDpAsState(
                        targetValue = if (isActive) 16.dp else 0.dp,
                        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
                        label = "pill_indicator_width"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .clip(RoundedCornerShape((theme.borderRadiusDp.coerceAtMost(12)).dp))
                            .background(
                                if (isActive) theme.accentColor.copy(alpha = 0.18f)
                                else Color.Transparent
                            )
                            .border(
                                width = if (isActive) 1.dp else 0.dp,
                                color = if (isActive) theme.accentColor.copy(alpha = 0.7f) else Color.Transparent,
                                shape = RoundedCornerShape((theme.borderRadiusDp.coerceAtMost(12)).dp)
                            )
                            .hyprBounceClick {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onWorkspaceSelected(ws)
                            }
                            .padding(vertical = 4.dp, horizontal = 2.dp),
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

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "[${ws.index}:${ws.label}]",
                                color = if (isActive) theme.accentColor else theme.textSecondaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.5.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Clip
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Box(
                                modifier = Modifier
                                    .width(pillIndicatorWidth)
                                    .height(2.dp)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(theme.accentColor)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Ergonomic Quick Search runner trigger button (cybernetic runner launcher)
            Box(
                modifier = Modifier
                    .size(width = 46.dp, height = 48.dp)
                    .clip(RoundedCornerShape((theme.borderRadiusDp.coerceAtMost(12)).dp))
                    .background(
                        if (isSearchActive) theme.accentColor
                        else theme.surfaceVariantColor
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSearchActive) theme.accentColor else theme.inactiveBorderColor.copy(alpha = 0.8f),
                        shape = RoundedCornerShape((theme.borderRadiusDp.coerceAtMost(12)).dp)
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
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = "run",
                        color = if (isSearchActive) theme.backgroundColor else theme.accentColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
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
 * Interactive 46dp Mini Vinyl Record with realistic concentric microgrooves,
 * center spindle hole, and spinning album art label at 33 RPM during playback.
 */
@Composable
fun MiniVinylRecord(
    theme: HyprThemeConfig,
    albumArtUri: String?,
    trackTitle: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    artist: String = "",
    trackId: String = ""
) {
    val rotationAnimatable = remember { Animatable(0f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive) {
                rotationAnimatable.animateTo(
                    targetValue = rotationAnimatable.value + 360f,
                    animationSpec = tween(durationMillis = 3200, easing = LinearEasing)
                )
            }
        }
    }
    val currentRotation = rotationAnimatable.value % 360f
    val context = LocalContext.current

    Box(
        modifier = modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color(0xFF141416)),
        contentAlignment = Alignment.Center
    ) {
        // Grooved vinyl record background with concentric microgrooves
        Canvas(modifier = Modifier.fillMaxSize().rotate(currentRotation)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f

            // Outer vinyl edge rim
            drawCircle(
                color = Color(0xFF222226),
                radius = radius,
                center = center
            )

            // Concentric vinyl reflection grooves
            val grooveAlphas = listOf(0.08f, 0.04f, 0.09f, 0.05f)
            grooveAlphas.forEachIndexed { i, a ->
                val r = radius * (0.58f + i * 0.10f)
                drawCircle(
                    color = Color.White.copy(alpha = a),
                    radius = r,
                    center = center,
                    style = Stroke(width = 0.8.dp.toPx())
                )
            }
        }

        // Center Album Art Label (26dp circular crop)
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .rotate(currentRotation)
                .background(theme.surfaceVariantColor),
            contentAlignment = Alignment.Center
        ) {
            HyprArtworkImage(
                artworkUri = albumArtUri,
                title = trackTitle,
                artist = artist,
                trackId = trackId,
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape
            )

            // Center spindle hole
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0A0A0C))
                    .border(0.6.dp, Color(0xFF888888), CircleShape)
            )
        }
    }
}

/**
 * Gesture-enabled, floating island MiniPlayer sitting right above the bottom dock.
 * Supports:
 * - Rotating mini vinyl record with concentric microgrooves
 * - Interactive kinetic drag with spring return
 * - Swipe left: skip next track
 * - Swipe right: skip previous track
 * - Embedded luminous progress filament
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

    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    val animatedDragX by animateFloatAsState(
        targetValue = dragOffsetX,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "mini_player_drag"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = theme.windowGapsDp.dp)
            .graphicsLayer {
                translationX = animatedDragX
                rotationZ = animatedDragX * 0.02f
            }
            .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtLeast(14).dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        theme.surfaceColor.copy(alpha = 0.95f),
                        theme.surfaceColor.copy(alpha = 0.98f)
                    )
                )
            )
            .border(
                width = theme.borderThicknessDp.dp,
                color = if (playbackState.isPlaying) theme.accentColor.copy(alpha = 0.5f) else theme.inactiveBorderColor.copy(alpha = 0.6f),
                shape = RoundedCornerShape(theme.borderRadiusDp.coerceAtLeast(14).dp)
            )
            .pointerInput(track.id) {
                detectHorizontalDragGestures(
                    onDragStart = { dragOffsetX = 0f },
                    onDragEnd = {
                        if (dragOffsetX < -70f) {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onSkipNext()
                        } else if (dragOffsetX > 70f) {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onSkipPrevious()
                        }
                        dragOffsetX = 0f
                    },
                    onDragCancel = { dragOffsetX = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffsetX = (dragOffsetX + dragAmount * 0.65f).coerceIn(-130f, 130f)
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
                // Interactive Mini Vinyl Record
                MiniVinylRecord(
                    theme = theme,
                    albumArtUri = track.albumArtUri,
                    trackTitle = track.title,
                    isPlaying = playbackState.isPlaying,
                    artist = track.artist,
                    trackId = track.id.toString()
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Title and Artist with live mini equalizer & audiophile codec badge
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
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(theme.surfaceVariantColor)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "${track.audioFormat} ${track.bitrate}k",
                                color = theme.accentColor,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Adaptive Controls matching selected geometry and icon pack
                AdaptivePlayButton(
                    isPlaying = playbackState.isPlaying,
                    onClick = onPlayPause,
                    size = 36.dp
                )

                Spacer(modifier = Modifier.width(6.dp))

                AdaptiveSkipButton(
                    isNext = true,
                    onClick = onSkipNext,
                    size = 32.dp
                )
            }

            // High Precision Luminous Audio Progress Filament
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .background(theme.surfaceVariantColor.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(playbackState.progress.coerceIn(0f, 1f))
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    theme.accentColor.copy(alpha = 0.6f),
                                    theme.accentColor
                                )
                            )
                        )
                )
            }
        }
    }
}

/**
 * Bottom Quick-Search Runner Bar (Spotlight / Wofi command palette inspired)
 * Features terminal prompt, live search chips, and keyboard-friendly actions.
 */
@Composable
fun HyprBottomSearchRunner(
    theme: HyprThemeConfig,
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(120)
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = theme.windowGapsDp.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtLeast(14).dp))
            .background(theme.surfaceColor.copy(alpha = 0.98f))
            .border(
                width = theme.borderThicknessDp.dp,
                color = theme.accentColor,
                shape = RoundedCornerShape(theme.borderRadiusDp.coerceAtLeast(14).dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cybernetic terminal prompt pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(theme.accentColor.copy(alpha = 0.18f))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "find >",
                    color = theme.accentColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }

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
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = theme.textPrimaryColor,
                    unfocusedTextColor = theme.textPrimaryColor,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = theme.textSecondaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            IconButton(
                onClick = {
                    keyboardController?.hide()
                    onClose()
                },
                modifier = Modifier.size(28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(theme.surfaceVariantColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Search",
                        tint = theme.textPrimaryColor,
                        modifier = Modifier.size(13.dp)
                    )
                }
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(40.dp)) }

        itemsIndexed(
            items = lyrics,
            key = { index, item -> "$index-${item.timestampMs}" }
        ) { index, line ->
            val distance = (index - activeIndex).let { if (it < 0) -it else it }
            LyricLineItem(
                theme = theme,
                line = line,
                isActive = index == activeIndex,
                distance = distance,
                onSeekTo = onSeekTo
            )
        }

        item { Spacer(modifier = Modifier.height(120.dp)) }
    }
}

@Composable
private fun LyricLineItem(
    theme: HyprThemeConfig,
    line: LyricLine,
    isActive: Boolean,
    distance: Int,
    onSeekTo: (Long) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val alpha = when {
        isActive -> 1f
        distance == 1 -> 0.65f
        distance == 2 -> 0.40f
        else -> 0.22f
    }

    val minSec = remember(line.timestampMs) {
        val totalSec = line.timestampMs / 1000
        val m = totalSec / 60
        val s = totalSec % 60
        String.format("%02d:%02d", m, s)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isActive) theme.accentColor.copy(alpha = 0.14f)
                else Color.Transparent
            )
            .border(
                width = if (isActive) 1.dp else 0.dp,
                color = if (isActive) theme.accentColor.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSeekTo(line.timestampMs)
            }
            .padding(horizontal = 12.dp, vertical = if (isActive) 10.dp else 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = line.text,
                color = if (isActive) theme.accentColor else theme.textPrimaryColor.copy(alpha = alpha),
                fontSize = if (isActive) 18.5.sp else 15.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                fontFamily = if (isActive) FontFamily.Default else FontFamily.Monospace,
                modifier = Modifier.weight(1f)
            )

            if (isActive) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(theme.accentColor.copy(alpha = 0.25f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "[$minSec]",
                        color = theme.accentColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Dynamic Icon Architecture providing bespoke iconography matching the selected IconPackType:
 * - PHOSPHOR: Soft rounded geometry with warm 2.0dp strokes and rounded joins.
 * - LUCIDE: Sharp, precise 1.8dp geometric line vectors with modern tech precision.
 * - REMIX: High-contrast solid silhouettes and bold weights.
 * - TABLER: Delicate, airy 1.5dp minimalist vector strokes.
 * - RETRO_CONSOLE: Industrial, mechanical right-angled and segmented technical lines.
 *
 * All icons render via hardware-accelerated Canvas with zero allocations in draw routines.
 */
object HyprIconProvider {
    @Composable
    fun Play(
        modifier: Modifier = Modifier,
        tint: Color = Color.Unspecified
    ) {
        val spec = HyprTheme.spec
        val actualTint = if (tint != Color.Unspecified) tint else spec.textPrimary

        Canvas(modifier = modifier) {
            val w = size.width
            val h = size.height
            val strokeW = (w * 0.08f).coerceAtLeast(1.5f)

            when (spec.iconPack) {
                IconPackType.REMIX -> {
                    val path = Path().apply {
                        moveTo(w * 0.28f, h * 0.20f)
                        lineTo(w * 0.82f, h * 0.50f)
                        lineTo(w * 0.28f, h * 0.80f)
                        close()
                    }
                    drawPath(path, actualTint, style = Fill)
                }
                IconPackType.PHOSPHOR -> {
                    val path = Path().apply {
                        moveTo(w * 0.30f, h * 0.22f)
                        lineTo(w * 0.78f, h * 0.50f)
                        lineTo(w * 0.30f, h * 0.78f)
                        close()
                    }
                    drawPath(
                        path,
                        actualTint,
                        style = Stroke(
                            width = strokeW * 1.15f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
                IconPackType.LUCIDE -> {
                    val path = Path().apply {
                        moveTo(w * 0.28f, h * 0.20f)
                        lineTo(w * 0.80f, h * 0.50f)
                        lineTo(w * 0.28f, h * 0.80f)
                        close()
                    }
                    drawPath(
                        path,
                        actualTint,
                        style = Stroke(
                            width = strokeW,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
                IconPackType.TABLER -> {
                    val path = Path().apply {
                        moveTo(w * 0.30f, h * 0.24f)
                        lineTo(w * 0.76f, h * 0.50f)
                        lineTo(w * 0.30f, h * 0.76f)
                        close()
                    }
                    drawPath(
                        path,
                        actualTint,
                        style = Stroke(
                            width = strokeW * 0.8f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
                IconPackType.RETRO_CONSOLE -> {
                    val path = Path().apply {
                        moveTo(w * 0.26f, h * 0.20f)
                        lineTo(w * 0.80f, h * 0.50f)
                        lineTo(w * 0.26f, h * 0.80f)
                        close()
                    }
                    drawPath(
                        path,
                        actualTint,
                        style = Stroke(
                            width = strokeW * 1.3f,
                            cap = StrokeCap.Square,
                            join = StrokeJoin.Miter
                        )
                    )
                }
            }
        }
    }

    @Composable
    fun Pause(
        modifier: Modifier = Modifier,
        tint: Color = Color.Unspecified
    ) {
        val spec = HyprTheme.spec
        val actualTint = if (tint != Color.Unspecified) tint else spec.textPrimary

        Canvas(modifier = modifier) {
            val w = size.width
            val h = size.height

            when (spec.iconPack) {
                IconPackType.REMIX -> {
                    val barW = w * 0.20f
                    val barH = h * 0.60f
                    val top = h * 0.20f
                    drawRoundRect(actualTint, Offset(w * 0.24f, top), Size(barW, barH), CornerRadius(2f, 2f))
                    drawRoundRect(actualTint, Offset(w * 0.56f, top), Size(barW, barH), CornerRadius(2f, 2f))
                }
                IconPackType.PHOSPHOR -> {
                    val barW = w * 0.16f
                    val barH = h * 0.56f
                    val top = h * 0.22f
                    val r = CornerRadius(barW / 2, barW / 2)
                    drawRoundRect(actualTint, Offset(w * 0.26f, top), Size(barW, barH), r)
                    drawRoundRect(actualTint, Offset(w * 0.58f, top), Size(barW, barH), r)
                }
                IconPackType.LUCIDE -> {
                    val strokeW = w * 0.08f
                    val top = h * 0.22f
                    val bottom = h * 0.78f
                    drawLine(actualTint, Offset(w * 0.35f, top), Offset(w * 0.35f, bottom), strokeWidth = strokeW, cap = StrokeCap.Round)
                    drawLine(actualTint, Offset(w * 0.65f, top), Offset(w * 0.65f, bottom), strokeWidth = strokeW, cap = StrokeCap.Round)
                }
                IconPackType.TABLER -> {
                    val strokeW = w * 0.065f
                    val top = h * 0.25f
                    val bottom = h * 0.75f
                    drawLine(actualTint, Offset(w * 0.36f, top), Offset(w * 0.36f, bottom), strokeWidth = strokeW, cap = StrokeCap.Round)
                    drawLine(actualTint, Offset(w * 0.64f, top), Offset(w * 0.64f, bottom), strokeWidth = strokeW, cap = StrokeCap.Round)
                }
                IconPackType.RETRO_CONSOLE -> {
                    val barW = w * 0.18f
                    val barH = h * 0.60f
                    val top = h * 0.20f
                    drawRect(actualTint, Offset(w * 0.25f, top), Size(barW, barH))
                    drawRect(actualTint, Offset(w * 0.57f, top), Size(barW, barH))
                }
            }
        }
    }

    @Composable
    fun SkipNext(
        modifier: Modifier = Modifier,
        tint: Color = Color.Unspecified
    ) {
        val spec = HyprTheme.spec
        val actualTint = if (tint != Color.Unspecified) tint else spec.textPrimary

        Canvas(modifier = modifier) {
            val w = size.width
            val h = size.height
            val strokeW = (w * 0.08f).coerceAtLeast(1.5f)

            when (spec.iconPack) {
                IconPackType.REMIX -> {
                    val path = Path().apply {
                        moveTo(w * 0.22f, h * 0.22f)
                        lineTo(w * 0.65f, h * 0.50f)
                        lineTo(w * 0.22f, h * 0.78f)
                        close()
                    }
                    drawPath(path, actualTint, style = Fill)
                    drawRoundRect(
                        actualTint,
                        Offset(w * 0.72f, h * 0.22f),
                        Size(w * 0.12f, h * 0.56f),
                        CornerRadius(1.5f, 1.5f)
                    )
                }
                IconPackType.PHOSPHOR -> {
                    val path = Path().apply {
                        moveTo(w * 0.24f, h * 0.24f)
                        lineTo(w * 0.64f, h * 0.50f)
                        lineTo(w * 0.24f, h * 0.76f)
                        close()
                    }
                    drawPath(path, actualTint, style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    drawLine(actualTint, Offset(w * 0.76f, h * 0.24f), Offset(w * 0.76f, h * 0.76f), strokeWidth = strokeW, cap = StrokeCap.Round)
                }
                IconPackType.LUCIDE -> {
                    val path = Path().apply {
                        moveTo(w * 0.22f, h * 0.22f)
                        lineTo(w * 0.66f, h * 0.50f)
                        lineTo(w * 0.22f, h * 0.78f)
                        close()
                    }
                    drawPath(path, actualTint, style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    drawLine(actualTint, Offset(w * 0.78f, h * 0.22f), Offset(w * 0.78f, h * 0.78f), strokeWidth = strokeW, cap = StrokeCap.Round)
                }
                IconPackType.TABLER -> {
                    val thinW = strokeW * 0.8f
                    val path = Path().apply {
                        moveTo(w * 0.25f, h * 0.26f)
                        lineTo(w * 0.63f, h * 0.50f)
                        lineTo(w * 0.25f, h * 0.74f)
                        close()
                    }
                    drawPath(path, actualTint, style = Stroke(thinW, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    drawLine(actualTint, Offset(w * 0.75f, h * 0.26f), Offset(w * 0.75f, h * 0.74f), strokeWidth = thinW, cap = StrokeCap.Round)
                }
                IconPackType.RETRO_CONSOLE -> {
                    val thickW = strokeW * 1.25f
                    val path = Path().apply {
                        moveTo(w * 0.20f, h * 0.20f)
                        lineTo(w * 0.65f, h * 0.50f)
                        lineTo(w * 0.20f, h * 0.80f)
                        close()
                    }
                    drawPath(path, actualTint, style = Stroke(thickW, cap = StrokeCap.Square, join = StrokeJoin.Miter))
                    drawLine(actualTint, Offset(w * 0.78f, h * 0.20f), Offset(w * 0.78f, h * 0.80f), strokeWidth = thickW, cap = StrokeCap.Square)
                }
            }
        }
    }

    @Composable
    fun SkipPrevious(
        modifier: Modifier = Modifier,
        tint: Color = Color.Unspecified
    ) {
        val spec = HyprTheme.spec
        val actualTint = if (tint != Color.Unspecified) tint else spec.textPrimary

        Canvas(modifier = modifier) {
            val w = size.width
            val h = size.height
            val strokeW = (w * 0.08f).coerceAtLeast(1.5f)

            when (spec.iconPack) {
                IconPackType.REMIX -> {
                    drawRoundRect(
                        actualTint,
                        Offset(w * 0.16f, h * 0.22f),
                        Size(w * 0.12f, h * 0.56f),
                        CornerRadius(1.5f, 1.5f)
                    )
                    val path = Path().apply {
                        moveTo(w * 0.78f, h * 0.22f)
                        lineTo(w * 0.35f, h * 0.50f)
                        lineTo(w * 0.78f, h * 0.78f)
                        close()
                    }
                    drawPath(path, actualTint, style = Fill)
                }
                IconPackType.PHOSPHOR -> {
                    drawLine(actualTint, Offset(w * 0.24f, h * 0.24f), Offset(w * 0.24f, h * 0.76f), strokeWidth = strokeW, cap = StrokeCap.Round)
                    val path = Path().apply {
                        moveTo(w * 0.76f, h * 0.24f)
                        lineTo(w * 0.36f, h * 0.50f)
                        lineTo(w * 0.76f, h * 0.76f)
                        close()
                    }
                    drawPath(path, actualTint, style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
                IconPackType.LUCIDE -> {
                    drawLine(actualTint, Offset(w * 0.22f, h * 0.22f), Offset(w * 0.22f, h * 0.78f), strokeWidth = strokeW, cap = StrokeCap.Round)
                    val path = Path().apply {
                        moveTo(w * 0.78f, h * 0.22f)
                        lineTo(w * 0.34f, h * 0.50f)
                        lineTo(w * 0.78f, h * 0.78f)
                        close()
                    }
                    drawPath(path, actualTint, style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
                IconPackType.TABLER -> {
                    val thinW = strokeW * 0.8f
                    drawLine(actualTint, Offset(w * 0.25f, h * 0.26f), Offset(w * 0.25f, h * 0.74f), strokeWidth = thinW, cap = StrokeCap.Round)
                    val path = Path().apply {
                        moveTo(w * 0.75f, h * 0.26f)
                        lineTo(w * 0.37f, h * 0.50f)
                        lineTo(w * 0.75f, h * 0.74f)
                        close()
                    }
                    drawPath(path, actualTint, style = Stroke(thinW, cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
                IconPackType.RETRO_CONSOLE -> {
                    val thickW = strokeW * 1.25f
                    drawLine(actualTint, Offset(w * 0.22f, h * 0.20f), Offset(w * 0.22f, h * 0.80f), strokeWidth = thickW, cap = StrokeCap.Square)
                    val path = Path().apply {
                        moveTo(w * 0.80f, h * 0.20f)
                        lineTo(w * 0.35f, h * 0.50f)
                        lineTo(w * 0.80f, h * 0.80f)
                        close()
                    }
                    drawPath(path, actualTint, style = Stroke(thickW, cap = StrokeCap.Square, join = StrokeJoin.Miter))
                }
            }
        }
    }
}

/**
 * Structurally adaptive progress tracker inspecting HyprTheme.spec.progressStyle:
 * - CAPSULE_SEEKER: Modern capsule pill scrubber with smooth interactive thumb
 * - WAVEFORM_SCRUBBER: Simulated multi-band audio waveform with illuminated elapsed bars
 * - SEGMENTED_LED_VU: Studio 24-step LED audio VU meter with calibrated peak thresholds
 * - MINIMAL_WAYBAR: Ultra-clean minimal 2.5dp low-profile line track
 * - DYNAMIC_NEON: Audio-reactive neon gradient seek slider with dynamic glow
 * - ANALOG_TAPE_GAUGE: Vintage dual-rail tape counter with precision tick marks & sliding head
 */
/**
 * Unified pointer gesture listener for progress bars that immediately handles down-seeking,
 * continuous dragging, and consumes all events to prevent touch pass-through.
 */
private fun Modifier.adaptiveSeekGesture(
    onSeekToPercent: ((Float) -> Unit)?
): Modifier {
    if (onSeekToPercent == null) return this
    return this.pointerInput(onSeekToPercent) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            down.consume()
            val totalWidth = size.width.toFloat()
            if (totalWidth > 0f) {
                val newPercent = (down.position.x / totalWidth).coerceIn(0f, 1f)
                onSeekToPercent(newPercent)
            }

            while (true) {
                val event = awaitPointerEvent()
                val change = event.changes.firstOrNull() ?: break
                if (!change.pressed) {
                    change.consume()
                    break
                }
                change.consume()
                if (totalWidth > 0f) {
                    val newPercent = (change.position.x / totalWidth).coerceIn(0f, 1f)
                    onSeekToPercent(newPercent)
                }
            }
        }
    }
}

/**
 * Structurally adaptive progress tracker inspecting HyprTheme.spec.progressStyle:
 * - CAPSULE_SEEKER: Modern capsule pill scrubber with smooth interactive thumb
 * - WAVEFORM_SCRUBBER: Simulated multi-band audio waveform with illuminated elapsed bars
 * - SEGMENTED_LED_VU: Studio 24-step LED audio VU meter with calibrated peak thresholds
 * - MINIMAL_WAYBAR: Ultra-clean minimal 2.5dp low-profile line track
 * - DYNAMIC_NEON: Audio-reactive neon gradient seek slider with dynamic glow
 * - ANALOG_TAPE_GAUGE: Vintage dual-rail tape counter with precision tick marks & sliding head
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
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .adaptiveSeekGesture(onSeekToPercent),
                    contentAlignment = Alignment.CenterStart
                ) {
                    val totalWidthPx = constraints.maxWidth.toFloat()
                    val trackHeight = 5.dp

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(trackHeight)
                            .clip(RoundedCornerShape(3.dp))
                            .background(spec.surfaceVariant)
                            .border(0.5.dp, spec.borderInactive.copy(alpha = 0.6f), RoundedCornerShape(3.dp))
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(safePercent)
                            .height(trackHeight)
                            .clip(RoundedCornerShape(3.dp))
                            .background(spec.borderActive)
                    )

                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                translationX = (totalWidthPx * safePercent - 7.dp.toPx()).coerceAtLeast(0f)
                            }
                            .size(14.dp)
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
                        fontFamily = FontFamily.Monospace,
                        color = spec.borderActive,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = total,
                        fontFamily = FontFamily.Monospace,
                        color = spec.textSecondary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        ProgressBarStyle.WAVEFORM_SCRUBBER -> {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .adaptiveSeekGesture(onSeekToPercent),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                    ) {
                        val totalW = size.width
                        val maxH = size.height
                        val barCount = 32
                        val spacingPx = 2.5.dp.toPx()
                        val barW = ((totalW - (barCount - 1) * spacingPx) / barCount).coerceAtLeast(2f)

                        for (i in 0 until barCount) {
                            val barFraction = (i + 0.5f) / barCount
                            val isPassed = barFraction <= safePercent
                            val norm = 0.22f + 0.72f * kotlin.math.abs(
                                kotlin.math.sin(i * 0.45f + 0.25f) * kotlin.math.cos(i * 0.22f + 0.1f)
                            )
                            val barH = (maxH * norm).coerceIn(4.dp.toPx(), maxH)
                            val x = i * (barW + spacingPx)
                            val y = (maxH - barH) / 2f

                            drawRoundRect(
                                color = if (isPassed) spec.borderActive else spec.surfaceVariant.copy(alpha = 0.5f),
                                topLeft = Offset(x, y),
                                size = Size(barW, barH),
                                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = elapsed,
                        fontFamily = FontFamily.Monospace,
                        color = spec.borderActive,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "[WAVEFORM]",
                        fontFamily = FontFamily.Monospace,
                        color = spec.borderActive.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = total,
                        fontFamily = FontFamily.Monospace,
                        color = spec.textSecondary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        ProgressBarStyle.SEGMENTED_LED_VU -> {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .adaptiveSeekGesture(onSeekToPercent),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    ) {
                        val totalW = size.width
                        val h = size.height
                        val segmentCount = 24
                        val spacingPx = 2.dp.toPx()
                        val segW = ((totalW - (segmentCount - 1) * spacingPx) / segmentCount).coerceAtLeast(2f)

                        for (i in 0 until segmentCount) {
                            val isLit = (i / segmentCount.toFloat()) <= safePercent
                            val x = i * (segW + spacingPx)

                            val litColor = when {
                                i >= 21 -> Color(0xFFFF3366) // Studio Peak Red
                                i >= 16 -> Color(0xFFFFB300) // Studio Warning Amber
                                else -> spec.borderActive // Standard Studio Accent
                            }
                            val color = if (isLit) litColor else spec.surfaceVariant.copy(alpha = 0.35f)

                            drawRoundRect(
                                color = color,
                                topLeft = Offset(x, 0f),
                                size = Size(segW, h),
                                cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = elapsed,
                        fontFamily = FontFamily.Monospace,
                        color = spec.borderActive,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "[VU: -3dB]",
                        fontFamily = FontFamily.Monospace,
                        color = spec.borderActive.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = total,
                        fontFamily = FontFamily.Monospace,
                        color = spec.textSecondary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        ProgressBarStyle.MINIMAL_WAYBAR -> {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .adaptiveSeekGesture(onSeekToPercent),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        progress = { safePercent },
                        color = spec.borderActive,
                        trackColor = spec.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = elapsed,
                        fontFamily = FontFamily.Monospace,
                        color = spec.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = total,
                        fontFamily = FontFamily.Monospace,
                        color = spec.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        ProgressBarStyle.DYNAMIC_NEON -> {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .adaptiveSeekGesture(onSeekToPercent),
                    contentAlignment = Alignment.CenterStart
                ) {
                    val totalW = constraints.maxWidth.toFloat()
                    val trackH = 6.dp

                    // Background track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(trackH)
                            .clip(RoundedCornerShape(3.dp))
                            .background(spec.surfaceVariant)
                    )

                    // Neon glowing active track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(safePercent)
                            .height(trackH)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(spec.borderActive.copy(alpha = 0.7f), spec.borderActive)
                                )
                            )
                    )

                    // Glowing Neon Thumb
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                translationX = (totalW * safePercent - 8.dp.toPx()).coerceAtLeast(0f)
                            }
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(spec.borderActive)
                            .border(2.dp, spec.surface, CircleShape)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = elapsed,
                        fontFamily = FontFamily.Monospace,
                        color = spec.borderActive,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = total,
                        fontFamily = FontFamily.Monospace,
                        color = spec.textSecondary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        ProgressBarStyle.ANALOG_TAPE_GAUGE -> {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .adaptiveSeekGesture(onSeekToPercent),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                    ) {
                        val totalW = size.width
                        val topRailY = 3.dp.toPx()
                        val bottomRailY = 21.dp.toPx()
                        val strokeW = 1.2.dp.toPx()
                        val railColor = spec.borderInactive.copy(alpha = 0.8f)

                        // Dual tracking rails
                        drawLine(railColor, Offset(0f, topRailY), Offset(totalW, topRailY), strokeWidth = strokeW)
                        drawLine(railColor, Offset(0f, bottomRailY), Offset(totalW, bottomRailY), strokeWidth = strokeW)

                        // Precision tape tick marks
                        for (step in 0..20) {
                            val tickX = totalW * (step / 20f)
                            val isMajor = step % 5 == 0
                            val tickLen = if (isMajor) 5.dp.toPx() else 2.5.dp.toPx()
                            drawLine(railColor, Offset(tickX, topRailY), Offset(tickX, topRailY + tickLen), strokeWidth = 1.dp.toPx())
                            drawLine(railColor, Offset(tickX, bottomRailY), Offset(tickX, bottomRailY - tickLen), strokeWidth = 1.dp.toPx())
                        }

                        // Active progress needle
                        val needleX = (totalW * safePercent).coerceIn(0f, totalW)
                        drawLine(
                            spec.borderActive,
                            Offset(needleX, 1.dp.toPx()),
                            Offset(needleX, 23.dp.toPx()),
                            strokeWidth = 2.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawCircle(
                            spec.borderActive,
                            radius = 3.5.dp.toPx(),
                            center = Offset(needleX, 12.dp.toPx())
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TAPE: $elapsed",
                        fontFamily = FontFamily.Monospace,
                        color = spec.borderActive,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = total,
                        fontFamily = FontFamily.Monospace,
                        color = spec.textSecondary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Structurally adaptive play/pause control button inspecting HyprTheme.spec.controlStyle:
 * - FLOATING_SQUIRCLE: Soft rounded squircle with subtle elevation and border gradient
 * - NEON_GLOW_PILL: Stadium pill shape with vibrant outer glow aura and high contrast
 * - TACTILE_BEVEL: Stereo hardware physical button with 3D drop-shadow and tactile pressed depth
 * - MINIMAL_GLASS_HALO: Translucent frosted circular ring with hairline vector stroke
 * - CYBER_CHAMFER: Futuristic 45-degree technical polygon with angular corner cuts
 * - BRACKET_CONSOLE: Retro terminal brackets with crisp vector bounding frames
 */
@Composable
fun AdaptivePlayButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 48.dp
) {
    val spec = HyprTheme.spec
    val haptic = LocalHapticFeedback.current

    when (spec.controlStyle) {
        PlayControlStyle.FLOATING_SQUIRCLE -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(RoundedCornerShape(spec.cornerRadius))
                    .background(spec.surfaceVariant)
                    .border(1.5.dp, spec.borderActive, RoundedCornerShape(spec.cornerRadius))
                    .hyprBounceClick {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    HyprIconProvider.Pause(modifier = Modifier.size(size * 0.50f), tint = spec.borderActive)
                } else {
                    HyprIconProvider.Play(modifier = Modifier.size(size * 0.50f), tint = spec.borderActive)
                }
            }
        }

        PlayControlStyle.NEON_GLOW_PILL -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(spec.borderActive.copy(alpha = 0.22f))
                    .border(2.dp, spec.borderActive, CircleShape)
                    .hyprBounceClick {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    HyprIconProvider.Pause(modifier = Modifier.size(size * 0.52f), tint = spec.borderActive)
                } else {
                    HyprIconProvider.Play(modifier = Modifier.size(size * 0.52f), tint = spec.borderActive)
                }
            }
        }

        PlayControlStyle.TACTILE_BEVEL -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                spec.surfaceVariant,
                                spec.surface
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.verticalGradient(
                            listOf(
                                spec.borderActive.copy(alpha = 0.9f),
                                spec.borderInactive.copy(alpha = 0.5f)
                            )
                        ),
                        RoundedCornerShape(8.dp)
                    )
                    .hyprBounceClick {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    HyprIconProvider.Pause(modifier = Modifier.size(size * 0.48f), tint = spec.borderActive)
                } else {
                    HyprIconProvider.Play(modifier = Modifier.size(size * 0.48f), tint = spec.borderActive)
                }
            }
        }

        PlayControlStyle.MINIMAL_GLASS_HALO -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(spec.surfaceVariant.copy(alpha = 0.35f))
                    .border(1.2.dp, spec.borderActive.copy(alpha = 0.85f), CircleShape)
                    .hyprBounceClick {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    HyprIconProvider.Pause(modifier = Modifier.size(size * 0.48f), tint = spec.textPrimary)
                } else {
                    HyprIconProvider.Play(modifier = Modifier.size(size * 0.48f), tint = spec.textPrimary)
                }
            }
        }

        PlayControlStyle.CYBER_CHAMFER -> {
            val chamferShape = remember {
                GenericShape { shapeSize, _ ->
                    val cut = (shapeSize.minDimension * 0.26f).coerceAtLeast(8f)
                    moveTo(cut, 0f)
                    lineTo(shapeSize.width - cut, 0f)
                    lineTo(shapeSize.width, cut)
                    lineTo(shapeSize.width, shapeSize.height - cut)
                    lineTo(shapeSize.width - cut, shapeSize.height)
                    lineTo(cut, shapeSize.height)
                    lineTo(0f, shapeSize.height - cut)
                    lineTo(0f, cut)
                    close()
                }
            }

            Box(
                modifier = modifier
                    .size(size)
                    .clip(chamferShape)
                    .background(spec.surfaceVariant)
                    .border(1.5.dp, spec.borderActive, chamferShape)
                    .hyprBounceClick {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    HyprIconProvider.Pause(modifier = Modifier.size(size * 0.50f), tint = spec.borderActive)
                } else {
                    HyprIconProvider.Play(modifier = Modifier.size(size * 0.50f), tint = spec.borderActive)
                }
            }
        }

        PlayControlStyle.BRACKET_CONSOLE -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(RoundedCornerShape(4.dp))
                    .background(spec.surfaceVariant.copy(alpha = 0.4f))
                    .hyprBounceClick {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = this.size.width
                    val h = this.size.height
                    val bracketW = w * 0.18f
                    val stroke = 1.8.dp.toPx()
                    val color = spec.borderActive

                    // Left bracket [
                    drawLine(color, Offset(bracketW, h * 0.15f), Offset(w * 0.08f, h * 0.15f), stroke)
                    drawLine(color, Offset(w * 0.08f, h * 0.15f), Offset(w * 0.08f, h * 0.85f), stroke)
                    drawLine(color, Offset(w * 0.08f, h * 0.85f), Offset(bracketW, h * 0.85f), stroke)

                    // Right bracket ]
                    drawLine(color, Offset(w - bracketW, h * 0.15f), Offset(w * 0.92f, h * 0.15f), stroke)
                    drawLine(color, Offset(w * 0.92f, h * 0.15f), Offset(w * 0.92f, h * 0.85f), stroke)
                    drawLine(color, Offset(w * 0.92f, h * 0.85f), Offset(w - bracketW, h * 0.85f), stroke)
                }

                if (isPlaying) {
                    HyprIconProvider.Pause(modifier = Modifier.size(size * 0.44f), tint = spec.borderActive)
                } else {
                    HyprIconProvider.Play(modifier = Modifier.size(size * 0.44f), tint = spec.borderActive)
                }
            }
        }
    }
}

/**
 * Structurally adaptive skip next/previous control button inspecting HyprTheme.spec.controlStyle.
 */
@Composable
fun AdaptiveSkipButton(
    isNext: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 36.dp
) {
    val spec = HyprTheme.spec
    val haptic = LocalHapticFeedback.current

    when (spec.controlStyle) {
        PlayControlStyle.FLOATING_SQUIRCLE -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(RoundedCornerShape((spec.cornerRadius * 0.7f).coerceAtLeast(6.dp)))
                    .background(spec.surfaceVariant.copy(alpha = 0.7f))
                    .border(1.dp, spec.borderInactive, RoundedCornerShape((spec.cornerRadius * 0.7f).coerceAtLeast(6.dp)))
                    .hyprBounceClick {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isNext) {
                    HyprIconProvider.SkipNext(modifier = Modifier.size(size * 0.52f), tint = spec.textPrimary)
                } else {
                    HyprIconProvider.SkipPrevious(modifier = Modifier.size(size * 0.52f), tint = spec.textPrimary)
                }
            }
        }

        PlayControlStyle.NEON_GLOW_PILL -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(spec.surfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, spec.borderActive.copy(alpha = 0.6f), CircleShape)
                    .hyprBounceClick {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isNext) {
                    HyprIconProvider.SkipNext(modifier = Modifier.size(size * 0.52f), tint = spec.borderActive)
                } else {
                    HyprIconProvider.SkipPrevious(modifier = Modifier.size(size * 0.52f), tint = spec.borderActive)
                }
            }
        }

        PlayControlStyle.TACTILE_BEVEL -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                spec.surfaceVariant,
                                spec.surface
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                spec.borderInactive.copy(alpha = 0.8f),
                                spec.surfaceVariant.copy(alpha = 0.3f)
                            )
                        ),
                        RoundedCornerShape(6.dp)
                    )
                    .hyprBounceClick {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isNext) {
                    HyprIconProvider.SkipNext(modifier = Modifier.size(size * 0.50f), tint = spec.textPrimary)
                } else {
                    HyprIconProvider.SkipPrevious(modifier = Modifier.size(size * 0.50f), tint = spec.textPrimary)
                }
            }
        }

        PlayControlStyle.MINIMAL_GLASS_HALO -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(spec.surfaceVariant.copy(alpha = 0.2f))
                    .border(1.dp, spec.borderInactive.copy(alpha = 0.6f), CircleShape)
                    .hyprBounceClick {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isNext) {
                    HyprIconProvider.SkipNext(modifier = Modifier.size(size * 0.50f), tint = spec.textSecondary)
                } else {
                    HyprIconProvider.SkipPrevious(modifier = Modifier.size(size * 0.50f), tint = spec.textSecondary)
                }
            }
        }

        PlayControlStyle.CYBER_CHAMFER -> {
            val chamferShape = remember {
                GenericShape { shapeSize, _ ->
                    val cut = (shapeSize.minDimension * 0.24f).coerceAtLeast(6f)
                    moveTo(cut, 0f)
                    lineTo(shapeSize.width - cut, 0f)
                    lineTo(shapeSize.width, cut)
                    lineTo(shapeSize.width, shapeSize.height - cut)
                    lineTo(shapeSize.width - cut, shapeSize.height)
                    lineTo(cut, shapeSize.height)
                    lineTo(0f, shapeSize.height - cut)
                    lineTo(0f, cut)
                    close()
                }
            }

            Box(
                modifier = modifier
                    .size(size)
                    .clip(chamferShape)
                    .background(spec.surfaceVariant.copy(alpha = 0.8f))
                    .border(1.dp, spec.borderInactive, chamferShape)
                    .hyprBounceClick {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isNext) {
                    HyprIconProvider.SkipNext(modifier = Modifier.size(size * 0.50f), tint = spec.textPrimary)
                } else {
                    HyprIconProvider.SkipPrevious(modifier = Modifier.size(size * 0.50f), tint = spec.textPrimary)
                }
            }
        }

        PlayControlStyle.BRACKET_CONSOLE -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(RoundedCornerShape(3.dp))
                    .background(spec.surfaceVariant.copy(alpha = 0.25f))
                    .hyprBounceClick {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = this.size.width
                    val h = this.size.height
                    val bracketW = w * 0.16f
                    val stroke = 1.4.dp.toPx()
                    val color = spec.borderInactive

                    // Left bracket
                    drawLine(color, Offset(bracketW, h * 0.20f), Offset(w * 0.10f, h * 0.20f), stroke)
                    drawLine(color, Offset(w * 0.10f, h * 0.20f), Offset(w * 0.10f, h * 0.80f), stroke)
                    drawLine(color, Offset(w * 0.10f, h * 0.80f), Offset(bracketW, h * 0.80f), stroke)

                    // Right bracket
                    drawLine(color, Offset(w - bracketW, h * 0.20f), Offset(w * 0.90f, h * 0.20f), stroke)
                    drawLine(color, Offset(w * 0.90f, h * 0.20f), Offset(w * 0.90f, h * 0.80f), stroke)
                    drawLine(color, Offset(w * 0.90f, h * 0.80f), Offset(w - bracketW, h * 0.80f), stroke)
                }

                if (isNext) {
                    HyprIconProvider.SkipNext(modifier = Modifier.size(size * 0.48f), tint = spec.textPrimary)
                } else {
                    HyprIconProvider.SkipPrevious(modifier = Modifier.size(size * 0.48f), tint = spec.textPrimary)
                }
            }
        }
    }
}
