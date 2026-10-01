package com.example.hyprmusic.ui.screens

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
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
import com.example.hyprmusic.core.media.HyprVisualizerState
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
    var isTurntableMode by rememberSaveable { mutableStateOf(true) }

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
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize",
                        tint = theme.textPrimaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onNavigateToAlbum(track.album)
                        }
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "PLAYING FROM STORAGE",
                        color = theme.textSecondaryColor,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = track.album,
                            color = theme.accentColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Album,
                            contentDescription = "Go to Album",
                            tint = theme.accentColor.copy(alpha = 0.8f),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (audioPlayer != null) {
                        SleepTimerButton(
                            theme = theme,
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                showSleepTimerDialog = true
                            }
                        )
                    }

                    if (playlistRepository != null) {
                        IconButton(
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                showAddToPlaylistDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                                contentDescription = "Add to Playlist",
                                tint = theme.accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    IconButton(onClick = onOpenEqualizer) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Equalizer",
                            tint = theme.accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onToggleFavorite(track.id)
                        }
                    ) {
                        Icon(
                            imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (track.isFavorite) theme.accentColor else theme.textSecondaryColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Visual Centerpiece: Album Artwork / Vinyl vs Synced Lyrics
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = showLyrics,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "centerpiece_switch"
                ) { lyricsActive ->
                    if (lyricsActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
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
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        // Audiophile Dual-Mode Centerpiece (Turntable vs Digipak Sleeve)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .aspectRatio(1f)
                                .hyprBounceClick {
                                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    isTurntableMode = !isTurntableMode
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedContent(
                                targetState = isTurntableMode,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "turntable_vs_sleeve"
                            ) { turntableActive ->
                                if (turntableActive) {
                                    MasterVinylTurntable(
                                        theme = theme,
                                        albumArtUri = track.albumArtUri,
                                        trackTitle = track.title,
                                        isPlaying = playbackState.isPlaying,
                                        progress = playbackState.progress
                                    )
                                } else {
                                    DigipakSleeve(
                                        theme = theme,
                                        albumArtUri = track.albumArtUri,
                                        trackTitle = track.title,
                                        isPlaying = playbackState.isPlaying
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Song-Synchronized Audiophile Spectrum Visualizer (PCM Direct, Hardware-Accelerated Canvas)
            AudiophileSpectrumVisualizer(
                theme = theme,
                isPlaying = playbackState.isPlaying,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Track Title, Artist & Audiophile Badges
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

                // High-Res Audio Badge & Studio Headroom Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.surfaceVariantColor)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
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
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.surfaceVariantColor)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
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

            Spacer(modifier = Modifier.height(10.dp))

            // Seek Bar & Timers (Isolated composable prevents parent screen recomposition on playback ticks)
            PlaybackProgressSection(
                theme = theme,
                currentPositionMs = playbackState.currentPositionMs,
                durationMs = playbackState.durationMs,
                progress = playbackState.progress,
                onSeekTo = onSeekTo
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Main Playback Controls Deck (Machined Audiophile Enclosure with Live Telemetry)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtLeast(14).dp))
                    .background(theme.surfaceColor.copy(alpha = 0.94f))
                    .border(
                        width = 1.dp,
                        color = if (playbackState.isPlaying) theme.accentColor.copy(alpha = 0.35f) else theme.inactiveBorderColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(theme.borderRadiusDp.coerceAtLeast(14).dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Column {
                    // Status Telemetry LED bar - 3-column layout mathematically pinned to eliminate layout shifts
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (playbackState.isPlaying) Color(0xFF00E676) else Color(0xFFFFB300))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (playbackState.isPlaying) "ENGAGED" else "PAUSED",
                                color = if (playbackState.isPlaying) Color(0xFF00E676) else Color(0xFFFFB300),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier.weight(1.4f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "BIT-PERFECT DIRECT",
                                color = theme.textSecondaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.5.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = "${track.audioFormat} ${track.bitrate}k",
                                color = theme.accentColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

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
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Action Chips: Turntable/Digipak, Synced Lyrics, Add to Playlist, Sleep Timer, Go to Album & DSP EQ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Turntable vs Digipak Sleeve Toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.surfaceVariantColor)
                        .hyprBounceClick {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            isTurntableMode = !isTurntableMode
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Album,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isTurntableMode) "TURNTABLE" else "DIGIPAK",
                            color = theme.accentColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                // Synced Lyrics Toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (showLyrics) theme.accentColor.copy(alpha = 0.25f) else theme.surfaceVariantColor)
                        .hyprBounceClick {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            showLyrics = !showLyrics
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FormatAlignLeft,
                            contentDescription = null,
                            tint = if (showLyrics) theme.accentColor else theme.textSecondaryColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showLyrics) "COVER ART" else "LYRICS",
                            color = if (showLyrics) theme.accentColor else theme.textSecondaryColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Add to Playlist Action
                if (playlistRepository != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.surfaceVariantColor)
                            .hyprBounceClick {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                showAddToPlaylistDialog = true
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                                contentDescription = null,
                                tint = theme.accentColor,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PLAYLIST",
                                color = theme.textPrimaryColor,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Sleep Timer Action
                if (audioPlayer != null) {
                    SleepTimerActionChip(
                        theme = theme,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            showSleepTimerDialog = true
                        }
                    )
                }

                // Go to Album Action
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.surfaceVariantColor)
                        .hyprBounceClick {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onNavigateToAlbum(track.album)
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Album,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ALBUM",
                            color = theme.textPrimaryColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // DSP Equalizer Action
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.surfaceVariantColor)
                        .hyprBounceClick {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onOpenEqualizer()
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "EQUALIZER",
                            color = theme.accentColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

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
 * Ultra-smooth, hardware-accelerated spectrum visualizer.
 * Renders on a single Canvas DrawScope with zero recompositions of the parent screen.
 */
@Composable
fun AudiophileSpectrumVisualizer(
    theme: HyprThemeConfig,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val amplitudes by HyprVisualizerState.amplitudes.collectAsStateWithLifecycle()
    val gradientColors = remember(theme.accentColor) {
        listOf(theme.accentColor, theme.accentColor.copy(alpha = 0.45f))
    }

    Canvas(modifier = modifier) {
        val barCount = 16
        val totalWidth = size.width
        val barWidth = 5.dp.toPx()
        val totalBarsWidth = barWidth * barCount
        val spacing = if (barCount > 1) {
            ((totalWidth - totalBarsWidth) / (barCount - 1)).coerceAtLeast(2.dp.toPx())
        } else 0f
        val startX = (totalWidth - (totalBarsWidth + spacing * (barCount - 1))) / 2f
        val maxHeight = size.height
        val minHeight = 4.dp.toPx()
        val cornerRadius = CornerRadius(2.5.dp.toPx(), 2.5.dp.toPx())

        for (i in 0 until barCount) {
            val rawAmp = if (isPlaying) amplitudes.getOrElse(i) { 0f } else 0f
            val barH = (minHeight + rawAmp * (maxHeight - minHeight)).coerceIn(minHeight, maxHeight)
            val left = startX + i * (barWidth + spacing)
            val top = (maxHeight - barH) / 2f

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = gradientColors,
                    startY = top,
                    endY = top + barH
                ),
                topLeft = Offset(left, top),
                size = Size(barWidth, barH),
                cornerRadius = cornerRadius
            )
        }
    }
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
                    .clip(RoundedCornerShape(theme.borderRadiusDp.dp))
                    .background(theme.surfaceColor)
                    .border(
                        width = theme.borderThicknessDp.dp,
                        brush = Brush.linearGradient(theme.activeBorderGradient),
                        shape = RoundedCornerShape(theme.borderRadiusDp.dp)
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
                        .clip(RoundedCornerShape(theme.borderRadiusDp.dp))
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
                        .clip(RoundedCornerShape(theme.borderRadiusDp.dp))
                        .background(theme.surfaceVariantColor)
                        .border(
                            1.dp,
                            theme.accentColor.copy(alpha = 0.5f),
                            RoundedCornerShape(theme.borderRadiusDp.dp)
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
private fun SleepTimerButton(
    theme: HyprThemeConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sleepTimerState by HyprSleepTimer.timerState.collectAsStateWithLifecycle()
    val isActive = sleepTimerState.isActive
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Bedtime,
                contentDescription = "Sleep Timer",
                tint = if (isActive) theme.accentColor else theme.textSecondaryColor,
                modifier = Modifier.size(20.dp)
            )
            if (isActive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(theme.accentColor)
                )
            }
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

@Composable
private fun SleepTimerActionChip(
    theme: HyprThemeConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sleepTimerState by HyprSleepTimer.timerState.collectAsStateWithLifecycle()
    val isActive = sleepTimerState.isActive

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) theme.accentColor.copy(alpha = 0.25f) else theme.surfaceVariantColor)
            .border(
                1.dp,
                if (isActive) theme.accentColor else theme.surfaceVariantColor,
                RoundedCornerShape(8.dp)
            )
            .hyprBounceClick(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Bedtime,
                contentDescription = null,
                tint = if (isActive) theme.accentColor else theme.textSecondaryColor,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isActive) "SLEEP ${sleepTimerState.formattedRemaining}" else "SLEEP TIMER",
                color = if (isActive) theme.accentColor else theme.textPrimaryColor,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Master Vinyl Turntable Centerpiece:
 * Heavy direct-drive platter, realistic 12-inch vinyl disc rotating at 33 1/3 RPM,
 * anisotropic dual-cone light sheen, perimeter strobe calibration dots, center circular
 * album art label, and mathematically anchored fixed-pivot tonearm with S-curved tube,
 * headshell, cartridge, and diamond stylus tracking on the vinyl grooves.
 */
@Composable
fun MasterVinylTurntable(
    theme: HyprThemeConfig,
    albumArtUri: String?,
    trackTitle: String,
    isPlaying: Boolean,
    progress: Float = 0f,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val rotationAnimatable = remember { Animatable(0f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive) {
                rotationAnimatable.animateTo(
                    targetValue = rotationAnimatable.value + 360f,
                    animationSpec = tween(durationMillis = 3600, easing = LinearEasing)
                )
            }
        }
    }
    val currentRotation = rotationAnimatable.value % 360f

    // Dynamic Tonearm Angle:
    // Parked (Paused): -12 degrees resting on outer arm-rest clip
    // Playing (Active): 7 degrees (outer run-in groove) to 20 degrees (inner run-out groove), tracking with progress!
    val targetTonearmAngle = if (isPlaying) {
        7f + (progress.coerceIn(0f, 1f) * 13f)
    } else {
        -12f
    }

    val tonearmAngle by animateFloatAsState(
        targetValue = targetTonearmAngle,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow),
        label = "tonearm_angle"
    )

    Box(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtLeast(18).dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF16161B),
                        Color(0xFF0F0F12),
                        Color(0xFF0A0A0C)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = if (isPlaying) theme.accentColor.copy(alpha = 0.35f) else theme.inactiveBorderColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(theme.borderRadiusDp.coerceAtLeast(18).dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Base Plinth Graphics & Recessed Platter Well
        Canvas(modifier = Modifier.fillMaxSize()) {
            val plinthW = size.width
            val plinthH = size.height
            val platterCenter = Offset(plinthW * 0.47f, plinthH * 0.50f)
            val platterRadius = plinthW * 0.41f

            // Recessed Platter Well shadow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF060608), Color(0xFF141418)),
                    center = platterCenter,
                    radius = platterRadius * 1.05f
                ),
                radius = platterRadius * 1.04f,
                center = platterCenter
            )
            drawCircle(
                color = Color(0xFF222228),
                radius = platterRadius * 1.04f,
                center = platterCenter,
                style = Stroke(width = 1.dp.toPx())
            )

            // Heavy Cast Aluminum Platter Bevel Rim
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF40404C), Color(0xFF1C1C22), Color(0xFF383844)),
                    start = Offset(platterCenter.x - platterRadius, platterCenter.y - platterRadius),
                    end = Offset(platterCenter.x + platterRadius, platterCenter.y + platterRadius)
                ),
                radius = platterRadius,
                center = platterCenter
            )

            // 4 Rings of Machined Strobe Calibration Dots
            val dotCount = 36
            val strobeRadius = platterRadius * 0.96f
            for (i in 0 until dotCount) {
                val angle = Math.toRadians((i * (360f / dotCount)).toDouble())
                val x = platterCenter.x + (strobeRadius * Math.cos(angle)).toFloat()
                val y = platterCenter.y + (strobeRadius * Math.sin(angle)).toFloat()
                val isStrobePulse = isPlaying && (i % 3 == 0)
                drawCircle(
                    color = if (isStrobePulse) theme.accentColor.copy(alpha = 0.85f) else Color(0xFF70707C),
                    radius = if (i % 2 == 0) 1.6.dp.toPx() else 1.2.dp.toPx(),
                    center = Offset(x, y)
                )
            }

            // Anti-Static Rubber / Felt Slipmat
            drawCircle(
                color = Color(0xFF111114),
                radius = platterRadius * 0.91f,
                center = platterCenter
            )
            drawCircle(
                color = Color(0xFF1D1D22),
                radius = platterRadius * 0.91f,
                center = platterCenter,
                style = Stroke(width = 1.2.dp.toPx())
            )

            // Fixed Tonearm Mount Gimbal Base on Top Right Plinth
            val pivotCenter = Offset(plinthW * 0.85f, plinthH * 0.17f)

            // Outer Gimbal Aluminum Deck Plate
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF32323C), Color(0xFF1B1B20)),
                    center = pivotCenter,
                    radius = 18.dp.toPx()
                ),
                radius = 16.dp.toPx(),
                center = pivotCenter
            )
            drawCircle(
                color = Color(0xFF555562),
                radius = 16.dp.toPx(),
                center = pivotCenter,
                style = Stroke(width = 1.dp.toPx())
            )

            // Cueing Arm-Rest Clip (Fixed where tonearm parks when stopped)
            val armRestPos = Offset(plinthW * 0.84f, plinthH * 0.40f)
            drawCircle(
                color = Color(0xFF282830),
                radius = 4.dp.toPx(),
                center = armRestPos
            )
            drawCircle(
                color = if (!isPlaying) theme.accentColor else Color(0xFF60606E),
                radius = 2.dp.toPx(),
                center = armRestPos
            )

            // Tactile 33 RPM Speed Indicator Pill (Bottom Left of Plinth)
            val speedIndicatorPos = Offset(plinthW * 0.13f, plinthH * 0.88f)
            drawCircle(
                color = if (isPlaying) theme.accentColor else Color(0xFF33333E),
                radius = 3.dp.toPx(),
                center = speedIndicatorPos
            )
        }

        // Rotating 12-inch Vinyl LP (Aligned over Platter Well)
        Box(
            modifier = Modifier
                .fillMaxSize(0.78f)
                .graphicsLayer { translationX = -size.width * 0.03f }
                .rotate(currentRotation)
                .clip(CircleShape)
                .background(Color(0xFF09090C)),
            contentAlignment = Alignment.Center
        ) {
            // Vinyl Record Surface: Anisotropic Optical Sheen & Microgrooves
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val discRadius = size.minDimension / 2f

                // Outer run-in rim
                drawCircle(
                    color = Color(0xFF18181D),
                    radius = discRadius,
                    center = center
                )

                // Anisotropic Bowtie / Butterfly Optical Reflection Sheen
                // Realistic dual-cone highlight sweeping across the grooves
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.14f),
                            Color.White.copy(alpha = 0.06f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.14f),
                            Color.White.copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        center = center
                    ),
                    radius = discRadius * 0.98f,
                    center = center
                )

                // Microgroove Music Bands (Tracks with subtle separation spaces)
                val grooveCount = 14
                for (i in 0 until grooveCount) {
                    val r = discRadius * (0.42f + (i.toFloat() / grooveCount) * 0.54f)
                    val isTrackGap = i % 4 == 0
                    drawCircle(
                        color = Color.White.copy(alpha = if (isTrackGap) 0.09f else 0.035f),
                        radius = r,
                        center = center,
                        style = Stroke(width = if (isTrackGap) 1.2.dp.toPx() else 0.6.dp.toPx())
                    )
                }

                // Run-out groove before label
                drawCircle(
                    color = Color.White.copy(alpha = 0.09f),
                    radius = discRadius * 0.40f,
                    center = center,
                    style = Stroke(width = 1.4.dp.toPx())
                )

                // Dead-wax matrix etch ring
                drawCircle(
                    color = Color(0xFF1A1A20),
                    radius = discRadius * 0.38f,
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
                if (!albumArtUri.isNullOrBlank()) {
                    val labelReq = remember(albumArtUri) {
                        ImageRequest.Builder(context)
                            .data(albumArtUri)
                            .size(360, 360)
                            .allowHardware(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .crossfade(false)
                            .build()
                    }
                    AsyncImage(
                        model = labelReq,
                        contentDescription = trackTitle,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Inner label perimeter ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                )

                // Center Chrome Spindle Pin
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFFFFFFF), Color(0xFF888892), Color(0xFF1E1E24))
                            )
                        )
                        .border(0.8.dp, Color(0xFFCCCCCC), CircleShape)
                )
            }
        }

        // Mathematically Anchored Fixed-Pivot S-Curved Tonearm
        // Pivot point is FIXED at (width * 0.85f, height * 0.17f)
        // Canvas rotates STRICTLY around `pivotCenter`!
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pivotCenter = Offset(size.width * 0.85f, size.height * 0.17f)

            // Tonearm Gimbal Housing & Counterweight (stationary bearing base)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF555562), Color(0xFF26262E)),
                    center = pivotCenter,
                    radius = 11.dp.toPx()
                ),
                radius = 10.dp.toPx(),
                center = pivotCenter
            )
            drawCircle(
                color = Color(0xFF888896),
                radius = 5.dp.toPx(),
                center = pivotCenter
            )

            // Rotating arm structure around pivotCenter:
            rotate(degrees = tonearmAngle, pivot = pivotCenter) {
                // Rear Counterweight Stub extending upward-right from pivot
                val cwStub = Offset(pivotCenter.x + 14.dp.toPx(), pivotCenter.y - 12.dp.toPx())
                drawLine(
                    color = Color(0xFF444450),
                    start = pivotCenter,
                    end = cwStub,
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Heavy knurled counterweight cylinder
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF6B6B78), Color(0xFF383842)),
                        center = cwStub,
                        radius = 8.dp.toPx()
                    ),
                    radius = 7.dp.toPx(),
                    center = cwStub
                )
                drawCircle(
                    color = Color(0xFF9090A0),
                    radius = 7.dp.toPx(),
                    center = cwStub,
                    style = Stroke(width = 0.8.dp.toPx())
                )

                // Classic Audiophile S-Curved Polished Chrome Tonearm Tube
                val armLength = size.width * 0.52f
                val endPoint = Offset(pivotCenter.x - armLength * 0.65f, pivotCenter.y + armLength * 0.82f)

                val sPath = Path().apply {
                    moveTo(pivotCenter.x, pivotCenter.y)
                    val ctrl1 = Offset(pivotCenter.x - armLength * 0.12f, pivotCenter.y + armLength * 0.35f)
                    val ctrl2 = Offset(pivotCenter.x - armLength * 0.55f, pivotCenter.y + armLength * 0.55f)
                    cubicTo(ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, endPoint.x, endPoint.y)
                }

                // Tonearm tube shadow / outer edge
                drawPath(
                    path = sPath,
                    color = Color(0xFF222228),
                    style = Stroke(width = 3.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                // Tonearm tube polished chrome highlight
                drawPath(
                    path = sPath,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFFFFF), Color(0xFFD6D6DC), Color(0xFFA2A2AC)),
                        start = pivotCenter,
                        end = endPoint
                    ),
                    style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Angled Headshell & High-End Phono Cartridge Body
                val hsLength = 16.dp.toPx()
                val hsAngleRad = Math.toRadians(48.0)
                val hsEndX = endPoint.x - (hsLength * Math.cos(hsAngleRad)).toFloat()
                val hsEndY = endPoint.y + (hsLength * Math.sin(hsAngleRad)).toFloat()
                val hsEnd = Offset(hsEndX, hsEndY)

                // Headshell chassis
                drawLine(
                    color = Color(0xFF1E1E24),
                    start = endPoint,
                    end = hsEnd,
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Square
                )

                // Phono Cartridge Body (Accent highlight / Ortofon style)
                drawLine(
                    color = theme.accentColor,
                    start = Offset(endPoint.x - 2.dp.toPx(), endPoint.y + 2.dp.toPx()),
                    end = Offset(hsEndX - 2.dp.toPx(), hsEndY + 2.dp.toPx()),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Finger Lift Hook extending outward from headshell
                val flEnd = Offset(endPoint.x - 7.dp.toPx(), endPoint.y - 4.dp.toPx())
                drawLine(
                    color = Color(0xFFCCCCCC),
                    start = endPoint,
                    end = flEnd,
                    strokeWidth = 1.2.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Stylus Cantilever & Diamond Needle Tip Touching Groove
                drawCircle(
                    color = Color.White,
                    radius = 1.6.dp.toPx(),
                    center = hsEnd
                )
            }
        }
    }
}

/**
 * Digipak Sleeve Centerpiece:
 * Tangible physical gatefold/digipak record jacket with realistic book spine fold,
 * open right pocket slot with inner depth shadow, and sliding 12-inch vinyl record
 * revealing grooved reflections and the circular center album label.
 */
@Composable
fun DigipakSleeve(
    theme: HyprThemeConfig,
    albumArtUri: String?,
    trackTitle: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val rms = if (isPlaying) HyprVisualizerState.rmsEnergy.value else 0f

    // Rotation for peeking vinyl disc: freezes at exact angle on pause, resumes smoothly
    val rotationAnimatable = remember { Animatable(0f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive) {
                rotationAnimatable.animateTo(
                    targetValue = rotationAnimatable.value + 360f,
                    animationSpec = tween(durationMillis = 3600, easing = LinearEasing)
                )
            }
        }
    }
    val currentRotation = rotationAnimatable.value % 360f

    // Dynamic slide-out distance: 58dp when playing, 36dp when paused
    val slideTarget = if (isPlaying) (58f + rms * 6f) else 36f
    val slideOffset by animateDpAsState(
        targetValue = slideTarget.dp,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
        label = "vinyl_slide_peek"
    )

    val scale = if (isPlaying) (0.98f + rms * 0.03f).coerceIn(0.98f, 1.02f) else 0.96f

    Box(
        modifier = modifier
            .fillMaxWidth(0.90f)
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        // Soft Ambient Drop Shadow / Underglow Behind Jacket
        Box(
            modifier = Modifier
                .fillMaxSize(0.92f)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            theme.accentColor.copy(alpha = if (isPlaying) 0.22f else 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Sliding 12-inch Vinyl LP peeking out of the right opening
        Box(
            modifier = Modifier
                .fillMaxSize(0.86f)
                .graphicsLayer { translationX = slideOffset.toPx() }
                .rotate(currentRotation)
                .clip(CircleShape)
                .background(Color(0xFF0C0C0F))
                .border(0.8.dp, Color(0xFF222228), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2f

                // Optical Anisotropic Sheen on Peeking Disc
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.07f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.07f),
                            Color.Transparent
                        ),
                        center = center
                    ),
                    radius = radius * 0.98f,
                    center = center
                )

                // Concentric Sound Grooves
                for (i in 1..8) {
                    drawCircle(
                        color = Color.White.copy(alpha = if (i % 2 == 0) 0.06f else 0.03f),
                        radius = radius * (0.42f + i * 0.07f),
                        center = center,
                        style = Stroke(width = 0.8.dp.toPx())
                    )
                }

                // Dead-wax runoff ring
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = radius * 0.40f,
                    center = center,
                    style = Stroke(width = 1.2.dp.toPx())
                )
            }

            // Circular Center Album Art Label on Peeking Disc (visible when sliding out!)
            Box(
                modifier = Modifier
                    .fillMaxSize(0.36f)
                    .clip(CircleShape)
                    .background(theme.surfaceVariantColor),
                contentAlignment = Alignment.Center
            ) {
                if (!albumArtUri.isNullOrBlank()) {
                    val labelReq = remember(albumArtUri) {
                        ImageRequest.Builder(context)
                            .data(albumArtUri)
                            .size(240, 240)
                            .allowHardware(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .crossfade(false)
                            .build()
                    }
                    AsyncImage(
                        model = labelReq,
                        contentDescription = trackTitle,
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

                // Central spindle hole
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF09090C))
                        .border(0.6.dp, Color(0xFF888890), CircleShape)
                )
            }
        }

        // Tangible Physical Gatefold Digipak Sleeve
        Box(
            modifier = Modifier
                .fillMaxSize(0.92f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF141418))
        ) {
            if (!albumArtUri.isNullOrBlank()) {
                val sleeveReq = remember(albumArtUri) {
                    ImageRequest.Builder(context)
                        .data(albumArtUri)
                        .size(800, 800)
                        .allowHardware(true)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .crossfade(false)
                        .build()
                }

                // Layer 1: Ambient background filling the sleeve
                AsyncImage(
                    model = sleeveReq,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.40f
                )

                // Layer 2: 100% fitted foreground jacket artwork (Zero-Crop)
                AsyncImage(
                    model = sleeveReq,
                    contentDescription = trackTitle,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(72.dp)
                    )
                }
            }

            // Authentic Physical Sleeve Finishes:
            // 1. Left Book Spine Fold Crease Highlight
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.45f),
                                Color.White.copy(alpha = 0.16f),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.50f)
                            ),
                            startX = 0f,
                            endX = Float.POSITIVE_INFINITY
                        )
                    )
            )

            // 2. Right Open Pocket Shadow & Notch
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(0.8.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            )
        }
    }
}

