package com.example.hyprmusic.ui.screens

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode as AnimRepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import com.example.hyprmusic.ui.components.HyprArtworkImage
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.hyprmusic.core.data.PlaylistRepository
import com.example.hyprmusic.core.lyrics.LyricsRepository
import com.example.hyprmusic.core.media.HyprAudioPlayer
import com.example.hyprmusic.core.media.HyprSleepTimer
import com.example.hyprmusic.core.model.LyricLine
import com.example.hyprmusic.core.model.PlaybackState
import com.example.hyprmusic.core.model.RepeatMode
import com.example.hyprmusic.core.theming.HyprThemeConfig
import com.example.hyprmusic.core.theming.hyprAnimatedGlow
import com.example.hyprmusic.core.theming.hyprBounceClick
import com.example.hyprmusic.core.theming.hyprTile
import com.example.hyprmusic.ui.components.AddToPlaylistDialog
import com.example.hyprmusic.ui.components.AdaptivePlayButton
import com.example.hyprmusic.ui.components.AdaptiveProgressBar
import com.example.hyprmusic.ui.components.AdaptiveSkipButton
import com.example.hyprmusic.ui.components.SleepTimerDialog
import com.example.hyprmusic.ui.components.SyncedLyricsView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext


@Composable
fun NowPlayingScreen(
    theme: HyprThemeConfig,
    playbackState: PlaybackState,
    audioPlayer: HyprAudioPlayer? = null,
    playlistRepository: PlaylistRepository? = null,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenEqualizer: () -> Unit = {},
    onNavigateToAlbum: (String) -> Unit = {},
    onBrowseLibrary: () -> Unit = {},
    onRandomMix: () -> Unit = {},
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack
    if (track == null) {
        NowPlayingEmptyScreen(
            theme = theme,
            onDismiss = onDismiss,
            onBrowseLibrary = onBrowseLibrary,
            onRandomMix = onRandomMix,
            modifier = modifier
        )
        return
    }

    val context = LocalContext.current
    val view = LocalView.current
    var showLyrics by remember { mutableStateOf(false) }

    val ambientBlurEffect = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RenderEffect.createBlurEffect(
                80f, 80f, Shader.TileMode.CLAMP
            ).asComposeRenderEffect()
        } else null
    }

    var lyricsState by remember { mutableStateOf<List<LyricLine>?>(null) }
    var isLyricsLoading by remember { mutableStateOf(false) }
    var lyricsRetryKey by remember { mutableIntStateOf(0) }

    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showAddToPlaylistDialog by remember { mutableStateOf(false) }

    // Unified Hyprland Rice Window Tokens
    val uniformRadius = theme.borderRadiusDp.coerceIn(8, 12).dp
    val riceShape = RoundedCornerShape(uniformRadius)
    val engagedGreen = Color(0xFF00E676)
    val activeWindowBorder = if (playbackState.isPlaying) engagedGreen.copy(alpha = 0.40f) else theme.inactiveBorderColor.copy(alpha = 0.45f)

    // Fetch real lyrics from local storage (.lrc) or LRCLIB multi-search
    LaunchedEffect(track.id, lyricsRetryKey) {
        // Yield to allow NowPlaying entry animation (slide-in) to complete smoothly without IO contention
        delay(120)
        isLyricsLoading = true
        lyricsState = withContext(Dispatchers.IO) {
            LyricsRepository.fetchLyrics(track)
        }
        isLyricsLoading = false
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        // Ambient blurred album art backdrop
        if (!track.albumArtUri.isNullOrBlank()) {
            val ambientReq = remember(track.albumArtUri) {
                ImageRequest.Builder(context)
                    .data(track.albumArtUri)
                    .size(300, 300)
                    .allowHardware(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .crossfade(false)
                    .build()
            }
            AsyncImage(
                model = ambientReq,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 0.22f
                        if (ambientBlurEffect != null) {
                            renderEffect = ambientBlurEffect
                        }
                    },
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = theme.windowGapsDp.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cohesive Waybar-Style Top Bar (Single Line with Standard 6dp Spacing & Matching 34dp Height)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Minimize Capsule Module
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .clip(riceShape)
                        .background(theme.surfaceVariantColor)
                        .border(1.dp, theme.inactiveBorderColor.copy(alpha = 0.45f), riceShape)
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onDismiss()
                        }
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Minimize",
                            tint = theme.accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "HIDE",
                            color = theme.accentColor,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Center Album Window Module with Direct Navigation
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .weight(1f, fill = false)
                        .padding(horizontal = 6.dp)
                        .clip(riceShape)
                        .background(theme.surfaceVariantColor.copy(alpha = 0.70f))
                        .border(1.dp, theme.inactiveBorderColor.copy(alpha = 0.40f), riceShape)
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onNavigateToAlbum(track.album)
                        }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[ ${track.album.ifBlank { "STORAGE" }} ]",
                        color = theme.textPrimaryColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Right Utility: Favorite Toggle
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(riceShape)
                        .background(
                            if (track.isFavorite) theme.accentColor.copy(alpha = 0.18f)
                            else theme.surfaceVariantColor
                        )
                        .border(
                            width = 1.dp,
                            color = if (track.isFavorite) theme.accentColor.copy(alpha = 0.65f)
                                   else theme.inactiveBorderColor.copy(alpha = 0.45f),
                            shape = riceShape
                        )
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onToggleFavorite(track.id)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (track.isFavorite) theme.accentColor else theme.textSecondaryColor,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Visual Centerpiece: Floating Vinyl vs Synced Lyrics
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = showLyrics,
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(220)) },
                    label = "centerpiece_switch"
                ) { lyricsActive ->
                    if (lyricsActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(riceShape)
                                .border(1.dp, activeWindowBorder, riceShape)
                                .hyprTile(theme = theme)
                        ) {
                            SyncedLyricsView(
                                lyrics = lyricsState,
                                isLoading = isLyricsLoading,
                                currentPositionMs = playbackState.currentPositionMs,
                                theme = theme,
                                onSeekTo = onSeekTo,
                                onRetry = { lyricsRetryKey++ },
                                onShowCoverArt = { showLyrics = false },
                                trackTitle = track.title,
                                artist = track.artist,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        // Singular Floating Vinyl Centerpiece
                        FloatingVinylCenterpiece(
                            theme = theme,
                            albumArtUri = track.albumArtUri,
                            trackTitle = track.title,
                            artist = track.artist,
                            isPlaying = playbackState.isPlaying,
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .aspectRatio(1f)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = !showLyrics,
                enter = expandVertically(tween(200)) + fadeIn(tween(200)),
                exit = shrinkVertically(tween(200)) + fadeOut(tween(200))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = track.title,
                        color = theme.textPrimaryColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = track.artist,
                        color = theme.textSecondaryColor,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // High-Res Audio Badge & Studio Headroom Indicator (Uniform Rice Pill Badges)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(riceShape)
                                .background(theme.surfaceVariantColor)
                                .border(1.dp, theme.inactiveBorderColor.copy(alpha = 0.40f), riceShape)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${track.audioFormat} ${track.bitrate}kbps",
                                color = theme.accentColor,
                                fontSize = 10.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(riceShape)
                                .background(theme.surfaceVariantColor)
                                .border(1.dp, theme.inactiveBorderColor.copy(alpha = 0.40f), riceShape)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "16-BIT PCM",
                                color = theme.textSecondaryColor,
                                fontSize = 10.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Seek Bar & Timers
            PlaybackProgressSection(
                theme = theme,
                currentPositionMs = playbackState.currentPositionMs,
                durationMs = playbackState.durationMs,
                progress = playbackState.progress,
                onSeekTo = onSeekTo
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Main Playback Controls Deck (Clean, Minimal & Professional)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(riceShape)
                    .background(theme.surfaceColor.copy(alpha = 0.94f))
                    .border(
                        width = 1.dp,
                        color = activeWindowBorder,
                        shape = riceShape
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Shuffle
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onToggleShuffle()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (playbackState.isShuffle) theme.accentColor else theme.textSecondaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Previous
                    AdaptiveSkipButton(
                        isNext = false,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onSkipPrevious()
                        },
                        size = 44.dp
                    )

                    // Play / Pause Hero Button with Adaptive Icon Pack
                    AdaptivePlayButton(
                        isPlaying = playbackState.isPlaying,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onPlayPause()
                        },
                        size = 68.dp
                    )

                    // Next
                    AdaptiveSkipButton(
                        isNext = true,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onSkipNext()
                        },
                        size = 44.dp
                    )

                    // Repeat Mode
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onToggleRepeat()
                        }
                    ) {
                        Icon(
                            imageVector = if (playbackState.repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                            contentDescription = "Repeat",
                            tint = if (playbackState.repeatMode != RepeatMode.OFF) theme.accentColor else theme.textSecondaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Unified Waybar Bottom Controls Dock (Lyrics, Playlist, Sleep Timer, Album, EQ)
            WaybarBottomControlsDock(
                theme = theme,
                showLyrics = showLyrics,
                riceShape = riceShape,
                onToggleLyrics = { showLyrics = !showLyrics },
                onOpenPlaylist = {
                    if (playlistRepository != null) {
                        showAddToPlaylistDialog = true
                    }
                },
                onOpenSleepTimer = {
                    if (audioPlayer != null) {
                        showSleepTimerDialog = true
                    }
                },
                onNavigateToAlbum = {
                    onNavigateToAlbum(track.album)
                },
                onOpenEqualizer = onOpenEqualizer
            )

            Spacer(modifier = Modifier.height(10.dp))

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Modals / Dialogs
        if (showSleepTimerDialog && audioPlayer != null) {
            SleepTimerDialog(
                theme = theme,
                audioPlayer = audioPlayer,
                onDismiss = { showSleepTimerDialog = false }
            )
        }

        if (showAddToPlaylistDialog && playlistRepository != null) {
            AddToPlaylistDialog(
                theme = theme,
                track = track,
                playlistRepository = playlistRepository,
                onDismiss = { showAddToPlaylistDialog = false }
            )
        }
    }
}

/**
 * Unified Waybar Bottom Controls Dock (Hyprland Rice Architecture):
 * Houses all 5 primary music utilities (Lyrics, Playlist, Sleep Timer, Album, EQ)
 * in an evenly proportioned, single-capsule frosted glass enclosure with subtle vertical dividers.
 */
@Composable
private fun WaybarBottomControlsDock(
    theme: HyprThemeConfig,
    showLyrics: Boolean,
    onToggleLyrics: () -> Unit,
    onOpenPlaylist: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    onNavigateToAlbum: () -> Unit,
    onOpenEqualizer: () -> Unit,
    modifier: Modifier = Modifier,
    riceShape: RoundedCornerShape = RoundedCornerShape(theme.borderRadiusDp.coerceIn(8, 12).dp)
) {
    val sleepTimerState by HyprSleepTimer.timerState.collectAsStateWithLifecycle()
    val isTimerActive = sleepTimerState.isActive

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(riceShape)
            .background(theme.surfaceColor.copy(alpha = 0.92f))
            .border(
                width = 1.dp,
                color = if (isTimerActive || showLyrics) Color(0xFF00E676).copy(alpha = 0.35f) else theme.inactiveBorderColor.copy(alpha = 0.40f),
                shape = riceShape
            )
            .padding(horizontal = 4.dp, vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Synced Lyrics
            WaybarDockModule(
                theme = theme,
                icon = Icons.AutoMirrored.Filled.FormatAlignLeft,
                label = "LYRICS",
                isActive = showLyrics,
                indicator = if (showLyrics) "●" else null,
                riceShape = riceShape,
                onClick = onToggleLyrics,
                modifier = Modifier.weight(1f)
            )

            // Divider
            WaybarDockDivider(theme = theme)

            // 2. Playlist
            WaybarDockModule(
                theme = theme,
                icon = Icons.AutoMirrored.Filled.PlaylistAdd,
                label = "PLAYLIST",
                isActive = false,
                riceShape = riceShape,
                onClick = onOpenPlaylist,
                modifier = Modifier.weight(1f)
            )

            // Divider
            WaybarDockDivider(theme = theme)

            // 3. Sleep Timer
            WaybarDockModule(
                theme = theme,
                icon = Icons.Default.Bedtime,
                label = if (isTimerActive) sleepTimerState.formattedRemaining else "TIMER",
                isActive = isTimerActive,
                indicator = if (isTimerActive) "●" else null,
                riceShape = riceShape,
                onClick = onOpenSleepTimer,
                modifier = Modifier.weight(1f)
            )

            // Divider
            WaybarDockDivider(theme = theme)

            // 4. Album Details
            WaybarDockModule(
                theme = theme,
                icon = Icons.Default.Album,
                label = "ALBUM",
                isActive = false,
                riceShape = riceShape,
                onClick = onNavigateToAlbum,
                modifier = Modifier.weight(1f)
            )

            // Divider
            WaybarDockDivider(theme = theme)

            // 5. Studio DSP Equalizer
            WaybarDockModule(
                theme = theme,
                icon = Icons.Default.Tune,
                label = "EQ",
                isActive = false,
                riceShape = riceShape,
                onClick = onOpenEqualizer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WaybarDockModule(
    theme: HyprThemeConfig,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    indicator: String? = null,
    riceShape: RoundedCornerShape = RoundedCornerShape(8.dp)
) {
    val view = LocalView.current
    Box(
        modifier = modifier
            .clip(riceShape)
            .background(
                if (isActive) theme.accentColor.copy(alpha = 0.20f)
                else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = if (isActive) theme.accentColor.copy(alpha = 0.75f) else Color.Transparent,
                shape = riceShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onClick()
                }
            )
            .padding(vertical = 5.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) theme.accentColor else theme.textSecondaryColor,
                modifier = Modifier.size(17.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = label,
                    color = if (isActive) theme.accentColor else theme.textSecondaryColor,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (indicator != null) {
                    Text(
                        text = indicator,
                        color = theme.accentColor,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun WaybarDockDivider(theme: HyprThemeConfig) {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(20.dp)
            .background(theme.surfaceVariantColor.copy(alpha = 0.8f))
    )
}

/**
 * Aesthetic Hyprland terminal idle screen displayed when no track is actively cued.
 */
@Composable
fun NowPlayingEmptyScreen(
    theme: HyprThemeConfig,
    onDismiss: () -> Unit,
    onBrowseLibrary: () -> Unit = {},
    onRandomMix: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val riceRadius = theme.borderRadiusDp.coerceIn(8, 12).dp
    val riceShape = RoundedCornerShape(riceRadius)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
            .safeDrawingPadding()
            .padding(horizontal = theme.windowGapsDp.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dismiss",
                        tint = theme.textPrimaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "WAYLAND // AUDIO ENGINE",
                    color = theme.textSecondaryColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Box(modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.weight(0.4f))

            // Vinyl / Idle Deck Display
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(riceShape)
                    .background(theme.surfaceColor)
                    .border(
                        width = 1.dp,
                        color = theme.inactiveBorderColor.copy(alpha = 0.5f),
                        shape = riceShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(theme.surfaceVariantColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "AUDIO ENGINE STANDBY",
                        color = theme.textPrimaryColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$ mpv --idle --wayland",
                        color = theme.textSecondaryColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Status message
            Text(
                text = "No Audio Stream Active",
                color = theme.textPrimaryColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Select a song from your library or start instant random playback",
                color = theme.textSecondaryColor,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(riceShape)
                        .background(theme.accentColor)
                        .hyprBounceClick { onRandomMix() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = null,
                            tint = theme.backgroundColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "RANDOM MIX",
                            color = theme.backgroundColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(riceShape)
                        .background(theme.surfaceVariantColor)
                        .border(
                            1.dp,
                            theme.accentColor.copy(alpha = 0.5f),
                            riceShape
                        )
                        .hyprBounceClick { onBrowseLibrary() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LibraryMusic,
                            contentDescription = null,
                            tint = theme.textPrimaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OPEN LIBRARY",
                            color = theme.textPrimaryColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.6f))
        }
    }
}



@Composable
private fun PlaybackProgressSection(
    theme: HyprThemeConfig,
    currentPositionMs: Long,
    durationMs: Long,
    progress: Float,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentDisplaySec = (currentPositionMs / 1000).coerceAtLeast(0)
    val totalSec = (durationMs / 1000).coerceAtLeast(0)
    val elapsed = "%d:%02d".format(currentDisplaySec / 60, currentDisplaySec % 60)
    val total = "%d:%02d".format(totalSec / 60, totalSec % 60)

    AdaptiveProgressBar(
        progressPercent = progress,
        elapsed = elapsed,
        total = total,
        onSeekToPercent = { pct ->
            if (durationMs > 0L) {
                // Safeguard against seeking directly onto the end frame which triggers auto-skipNext()
                val safePct = pct.coerceIn(0f, 0.995f)
                val targetMs = (safePct * durationMs).toLong().coerceIn(0L, (durationMs - 500L).coerceAtLeast(0L))
                onSeekTo(targetMs)
            }
        },
        modifier = modifier
    )
}



/**
 * Authentic 12-inch Audiophile Vinyl LP:
 * Precision circular record with anisotropic microgroove optical sheen, 5 concentric
 * song-track groove bands with inter-track dead-wax gaps, run-out groove, etched matrix ring,
 * and high-resolution circular album art label with polished perimeter ring and chrome spindle pin.
 */
@Composable
fun PureVinylDisc(
    theme: HyprThemeConfig,
    albumArtUri: String?,
    trackTitle: String,
    artist: String,
    currentRotation: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .rotate(currentRotation)
            .clip(CircleShape)
            .background(Color(0xFF09090C)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val discRadius = size.minDimension / 2f

            // Outer Lead-in Vinyl Rim with Polished Bevel Highlight
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF1A1A22), Color(0xFF08080A)),
                    center = center,
                    radius = discRadius
                ),
                radius = discRadius,
                center = center
            )

            // Anisotropic Optical Bowtie Sheen (Light reflecting off real microgrooves)
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.05f),
                        Color.White.copy(alpha = 0.16f),
                        Color.White.copy(alpha = 0.05f),
                        Color.Transparent,
                        Color.Transparent,
                        Color.White.copy(alpha = 0.05f),
                        Color.White.copy(alpha = 0.16f),
                        Color.White.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = center
                ),
                radius = discRadius * 0.97f,
                center = center
            )

            // 5 Concentric Music Bands (Song Tracks) with subtle inter-track "dead-wax" gaps
            val trackGrooveBands = listOf(
                0.93f to 0.84f,
                0.83f to 0.74f,
                0.73f to 0.64f,
                0.63f to 0.54f,
                0.53f to 0.44f
            )

            trackGrooveBands.forEachIndexed { bandIdx, (outerR, innerR) ->
                val steps = 4
                for (s in 0..steps) {
                    val frac = s.toFloat() / steps
                    val r = discRadius * (innerR + frac * (outerR - innerR))
                    drawCircle(
                        color = Color.White.copy(alpha = if (s == 0 || s == steps) 0.06f else 0.035f),
                        radius = r,
                        center = center,
                        style = Stroke(width = 0.7.dp.toPx())
                    )
                }
                // Inter-track dead wax groove gap
                if (bandIdx < trackGrooveBands.lastIndex) {
                    drawCircle(
                        color = Color(0xFF181820),
                        radius = discRadius * (innerR - 0.005f),
                        center = center,
                        style = Stroke(width = 1.4.dp.toPx())
                    )
                }
            }

            // Run-out dead wax groove before center label
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = discRadius * 0.41f,
                center = center,
                style = Stroke(width = 1.4.dp.toPx())
            )

            // Etched matrix runout ring
            drawCircle(
                color = Color(0xFF1C1C24),
                radius = discRadius * 0.39f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Center Circular Album Artwork Label (36% of disc diameter)
        Box(
            modifier = Modifier
                .fillMaxSize(0.36f)
                .clip(CircleShape)
                .background(theme.surfaceVariantColor),
            contentAlignment = Alignment.Center
        ) {
            HyprArtworkImage(
                artworkUri = albumArtUri,
                title = trackTitle,
                artist = artist,
                shape = CircleShape,
                modifier = Modifier.fillMaxSize()
            )

            // Inner label perimeter ring & vintage record typography outline
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(1.2.dp, Color.White.copy(alpha = 0.28f), CircleShape)
            )

            // Heavy Chrome Center Spindle Pin with 3D Conical Highlight
            Box(
                modifier = Modifier
                    .size(11.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFFFFFFFF),
                                Color(0xFF8E8E98),
                                Color(0xFFFFFFFF),
                                Color(0xFF70707C),
                                Color(0xFFFFFFFF)
                            )
                        )
                    )
                    .border(0.8.dp, Color(0xFFDDDDDD), CircleShape)
            )
        }
    }
}

/**
 * Ultra-Clean Floating Vinyl Centerpiece:
 * Pure circular vinyl disc floating freely over the ambient blurred album art backdrop.
 * Ditching the skeuomorphic outer frame completely, with a soft neon backlight aura and
 * a razor-thin active border.
 */
@Composable
fun FloatingVinylCenterpiece(
    theme: HyprThemeConfig,
    albumArtUri: String?,
    trackTitle: String,
    artist: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val rotationAnimatable = remember { Animatable(0f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive) {
                rotationAnimatable.animateTo(
                    targetValue = rotationAnimatable.value + 360f,
                    animationSpec = tween(durationMillis = 1800, easing = LinearEasing)
                )
            }
        }
    }
    val currentRotation = rotationAnimatable.value % 360f

    val pulseScale by animateFloatAsState(
        targetValue = if (isPlaying) 1.0f else 0.97f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "floating_vinyl_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth(0.88f)
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = pulseScale
                scaleY = pulseScale
            },
        contentAlignment = Alignment.Center
    ) {
        // Soft Ambient Neon Underglow behind floating disc
        Box(
            modifier = Modifier
                .fillMaxSize(0.96f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            (if (isPlaying) Color(0xFF00E676) else theme.accentColor).copy(
                                alpha = if (isPlaying) 0.22f else 0.08f
                            ),
                            Color.Transparent
                        )
                    )
                )
        )

        // Free-floating circular vinyl disc with thin 1px border
        PureVinylDisc(
            theme = theme,
            albumArtUri = albumArtUri,
            trackTitle = trackTitle,
            artist = artist,
            currentRotation = currentRotation,
            modifier = Modifier
                .fillMaxSize(0.92f)
                .border(
                    width = 1.dp,
                    color = if (isPlaying) Color(0xFF00E676).copy(alpha = 0.45f)
                           else Color.White.copy(alpha = 0.12f),
                    shape = CircleShape
                )
        )
    }
}

/**
 * Backwards-compatible alias for MasterVinylTurntable delegating to FloatingVinylCenterpiece.
 */
@Composable
fun MasterVinylTurntable(
    theme: HyprThemeConfig,
    albumArtUri: String?,
    trackTitle: String,
    isPlaying: Boolean,
    progress: Float = 0f,
    modifier: Modifier = Modifier,
    artist: String = ""
) {
    FloatingVinylCenterpiece(
        theme = theme,
        albumArtUri = albumArtUri,
        trackTitle = trackTitle,
        artist = artist,
        isPlaying = isPlaying,
        modifier = modifier
    )
}


