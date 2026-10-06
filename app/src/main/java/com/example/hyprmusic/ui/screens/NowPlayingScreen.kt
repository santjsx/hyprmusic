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
 * Direct-drive audiophile turntable with weighted plinth, corner isolation feet,
 * Technics-style pitch strobe illuminator tower, heavy cast-aluminum platter with
 * 4-row strobe calibration dots, anti-static slipmat, realistic 12-inch vinyl disc
 * with anisotropic dual-cone optical sheen, concentric song-track microgrooves,
 * dead-wax matrix etching, vintage center label, and high-precision S-curved tonearm
 * with 3D drop shadow, counterweight tracking-force scale, cueing lever, and calibrated
 * physical tracking geometry that never touches the center label.
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

    // Physical 33 1/3 RPM speed = 1.8 seconds (1800ms) per full 360-degree revolution
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

    // Calibrated Tonearm Physical Tracking Geometry:
    // Parked (Paused): 0° resting on the arm-rest clip at the right plinth
    // Playing (Active): 18° at track start (outer lead-in groove) to 36° at track end (inner dead wax before label)
    // The needle strictly tracks within the vinyl music grooves and never crosses into the center label!
    val targetTonearmAngle = if (isPlaying) {
        18f + (progress.coerceIn(0f, 1f) * 18f)
    } else {
        0f
    }

    val tonearmAngle by animateFloatAsState(
        targetValue = targetTonearmAngle,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessLow),
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
                        Color(0xFF101014),
                        Color(0xFF09090C)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = if (isPlaying) theme.accentColor.copy(alpha = 0.45f) else theme.inactiveBorderColor.copy(alpha = 0.35f),
                shape = RoundedCornerShape(theme.borderRadiusDp.coerceAtLeast(18).dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Base Plinth Graphics, Platter Well, Strobe Illuminator, and Gimbal Assembly
        Canvas(modifier = Modifier.fillMaxSize()) {
            val plinthW = size.width
            val plinthH = size.height
            val platterCenter = Offset(plinthW * 0.44f, plinthH * 0.50f)
            val platterRadius = plinthW * 0.40f

            // 4 Corner Machined Aluminum Isolation Damping Feet
            val footOffset = 18.dp.toPx()
            val footRadius = 8.dp.toPx()
            val footCenters = listOf(
                Offset(footOffset, footOffset),
                Offset(plinthW - footOffset, footOffset),
                Offset(footOffset, plinthH - footOffset),
                Offset(plinthW - footOffset, plinthH - footOffset)
            )
            footCenters.forEach { pos ->
                // Outer rubber damping ring
                drawCircle(color = Color(0xFF08080A), radius = footRadius + 2.dp.toPx(), center = pos)
                // Machined aluminum foot cap
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF6A6A76), Color(0xFF2E2E36)),
                        center = pos,
                        radius = footRadius
                    ),
                    radius = footRadius,
                    center = pos
                )
                drawCircle(color = Color(0xFF888898), radius = footRadius, center = pos, style = Stroke(width = 0.8.dp.toPx()))
            }

            // Recessed Platter Well with Deep Radial Ambient Occlusion Shadow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF040406), Color(0xFF121216)),
                    center = platterCenter,
                    radius = platterRadius * 1.06f
                ),
                radius = platterRadius * 1.05f,
                center = platterCenter
            )
            drawCircle(
                color = Color(0xFF22222A),
                radius = platterRadius * 1.05f,
                center = platterCenter,
                style = Stroke(width = 1.2.dp.toPx())
            )

            // Heavy Cast Aluminum Platter with Beveled Machined Rim
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF484856),
                        Color(0xFF26262E),
                        Color(0xFF565666),
                        Color(0xFF2A2A34),
                        Color(0xFF484856)
                    ),
                    center = platterCenter
                ),
                radius = platterRadius,
                center = platterCenter
            )

            // 4 Rings of Machined Strobe Calibration Dots (50Hz / 60Hz calibration rings)
            val dotCount = 44
            val strobeRadius = platterRadius * 0.97f
            for (i in 0 until dotCount) {
                val angle = Math.toRadians((i * (360f / dotCount)).toDouble())
                val x = platterCenter.x + (strobeRadius * Math.cos(angle)).toFloat()
                val y = platterCenter.y + (strobeRadius * Math.sin(angle)).toFloat()
                val isNearStrobeTower = x < platterCenter.x && y > platterCenter.y * 0.7f
                val isStrobeGlint = isPlaying && isNearStrobeTower && (i % 2 == 0)

                drawCircle(
                    color = if (isStrobeGlint) theme.accentColor else Color(0xFF828292),
                    radius = if (i % 2 == 0) 1.6.dp.toPx() else 1.1.dp.toPx(),
                    center = Offset(x, y)
                )
            }

            // Heavy Anti-Static Felt / Rubber Slipmat (Charcoal matte with concentric grip rings)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF141418), Color(0xFF0B0B0E)),
                    center = platterCenter,
                    radius = platterRadius * 0.94f
                ),
                radius = platterRadius * 0.93f,
                center = platterCenter
            )
            for (ring in 1..3) {
                drawCircle(
                    color = Color(0xFF1F1F26),
                    radius = platterRadius * (0.35f + ring * 0.18f),
                    center = platterCenter,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Technics-style Strobe Illuminator Tower (Bottom-Left of Plinth)
            val strobeTowerPos = Offset(plinthW * 0.12f, plinthH * 0.86f)
            // Strobe housing base
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF3E3E4A), Color(0xFF1A1A20)),
                    center = strobeTowerPos,
                    radius = 12.dp.toPx()
                ),
                radius = 10.dp.toPx(),
                center = strobeTowerPos
            )
            drawCircle(color = Color(0xFF555562), radius = 10.dp.toPx(), center = strobeTowerPos, style = Stroke(width = 1.dp.toPx()))

            // Strobe prism lamp window
            drawCircle(
                color = if (isPlaying) theme.accentColor else Color(0xFF282832),
                radius = 4.5.dp.toPx(),
                center = strobeTowerPos
            )
            if (isPlaying) {
                // Subtle horizontal neon glow beaming towards the platter strobe dots
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(theme.accentColor.copy(alpha = 0.5f), Color.Transparent),
                        startX = strobeTowerPos.x,
                        endX = platterCenter.x - platterRadius * 0.6f
                    ),
                    start = strobeTowerPos,
                    end = Offset(platterCenter.x - platterRadius * 0.6f, strobeTowerPos.y),
                    strokeWidth = 3.dp.toPx()
                )
            }

            // 33 1/3 RPM Tactile Speed Selector Pill (Bottom Plinth)
            val speedBtnPos = Offset(plinthW * 0.24f, plinthH * 0.86f)
            drawRoundRect(
                color = Color(0xFF1E1E24),
                topLeft = Offset(speedBtnPos.x - 14.dp.toPx(), speedBtnPos.y - 7.dp.toPx()),
                size = Size(28.dp.toPx(), 14.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            drawCircle(
                color = if (isPlaying) theme.accentColor else Color(0xFF40404C),
                radius = 2.2.dp.toPx(),
                center = speedBtnPos
            )

            // Fixed Gimbal Base & Tonearm Mounting Assembly (Top-Right Plinth)
            val pivotCenter = Offset(plinthW * 0.83f, plinthH * 0.18f)

            // Brushed aluminum outer gimbal deck ring
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF4A4A58),
                        Color(0xFF24242C),
                        Color(0xFF5A5A6C),
                        Color(0xFF282832),
                        Color(0xFF4A4A58)
                    ),
                    center = pivotCenter
                ),
                radius = 20.dp.toPx(),
                center = pivotCenter
            )
            drawCircle(color = Color(0xFF6E6E80), radius = 20.dp.toPx(), center = pivotCenter, style = Stroke(width = 1.dp.toPx()))

            // Inner bearing housing collar
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF2C2C36), Color(0xFF141418)),
                    center = pivotCenter,
                    radius = 14.dp.toPx()
                ),
                radius = 13.dp.toPx(),
                center = pivotCenter
            )

            // Anti-Skate Bias Knob with scale marks (North-East of pivot)
            val antiSkatePos = Offset(pivotCenter.x + 12.dp.toPx(), pivotCenter.y - 12.dp.toPx())
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF50505E), Color(0xFF22222A)),
                    center = antiSkatePos,
                    radius = 5.dp.toPx()
                ),
                radius = 4.5.dp.toPx(),
                center = antiSkatePos
            )
            drawCircle(color = Color(0xFF888898), radius = 4.5.dp.toPx(), center = antiSkatePos, style = Stroke(width = 0.6.dp.toPx()))

            // Cueing Arm-Rest Clip & Lift Lever (Where tonearm parks when stopped)
            val armRestPos = Offset(plinthW * 0.83f, plinthH * 0.44f)
            // Arm rest base post
            drawCircle(color = Color(0xFF2A2A34), radius = 5.dp.toPx(), center = armRestPos)
            // U-shaped arm cradle clip
            drawCircle(
                color = if (!isPlaying) theme.accentColor else Color(0xFF606070),
                radius = 3.dp.toPx(),
                center = armRestPos
            )

            // Curved rubber cueing lift bar running from near pivot down to arm rest
            val cueBarPath = Path().apply {
                moveTo(pivotCenter.x - 3.dp.toPx(), pivotCenter.y + 16.dp.toPx())
                quadraticTo(
                    pivotCenter.x - 8.dp.toPx(),
                    armRestPos.y - 10.dp.toPx(),
                    armRestPos.x - 4.dp.toPx(),
                    armRestPos.y
                )
            }
            drawPath(
                path = cueBarPath,
                color = Color(0xFF1E1E26),
                style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Rotating 12-inch Audiophile Vinyl LP (Directly aligned over platter well)
        Box(
            modifier = Modifier
                .fillMaxSize(0.77f)
                .graphicsLayer { translationX = -size.width * 0.06f }
                .rotate(currentRotation)
                .clip(CircleShape)
                .background(Color(0xFF09090C)),
            contentAlignment = Alignment.Center
        ) {
            // Vinyl Surface: Anisotropic Optical Sheen, Song Tracks, & Dead Wax
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

        // Precision Audiophile S-Curved Tonearm with 3D Drop Shadow & Calibrated Tracking
        Canvas(modifier = Modifier.fillMaxSize()) {
            val plinthW = size.width
            val plinthH = size.height
            val pivotCenter = Offset(plinthW * 0.83f, plinthH * 0.18f)

            // Stationary Pivot Bearing Core (Stationary top gimbal housing)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF6E6E7C), Color(0xFF26262E)),
                    center = pivotCenter,
                    radius = 9.dp.toPx()
                ),
                radius = 8.dp.toPx(),
                center = pivotCenter
            )
            drawCircle(color = Color(0xFFAAAAAA), radius = 3.5.dp.toPx(), center = pivotCenter)

            // Rotating Arm Structure strictly anchored to pivotCenter
            rotate(degrees = tonearmAngle, pivot = pivotCenter) {
                // 1. Rear Stainless Steel Stub & Stepped Heavy Counterweight
                val cwStubEnd = Offset(pivotCenter.x + 16.dp.toPx(), pivotCenter.y - 14.dp.toPx())
                drawLine(
                    color = Color(0xFF50505C),
                    start = pivotCenter,
                    end = cwStubEnd,
                    strokeWidth = 3.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Counterweight cylinder with tracking force ring
                val cwCenter = Offset(pivotCenter.x + 11.dp.toPx(), pivotCenter.y - 10.dp.toPx())
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF828292), Color(0xFF383842)),
                        center = cwCenter,
                        radius = 8.5.dp.toPx()
                    ),
                    radius = 7.5.dp.toPx(),
                    center = cwCenter
                )
                drawCircle(color = Color(0xFFB0B0C0), radius = 7.5.dp.toPx(), center = cwCenter, style = Stroke(width = 0.8.dp.toPx()))
                // Numbered gram tracking dial line
                drawCircle(color = Color(0xFF202026), radius = 5.dp.toPx(), center = cwCenter, style = Stroke(width = 1.dp.toPx()))

                // 2. Tonearm Tube Geometry (Authentic Audiophile S-Curve)
                // In local frame from pivotCenter (0,0):
                // Total arm length L = plinthW * 0.37f
                val armLength = plinthW * 0.37f

                // Tube endpoints:
                // Starts at (0,0), curves right (+X), then sweeps left (-X), ending at headshell connector
                val p0 = pivotCenter
                val c1 = Offset(pivotCenter.x + armLength * 0.08f, pivotCenter.y + armLength * 0.30f)
                val c2 = Offset(pivotCenter.x - armLength * 0.06f, pivotCenter.y + armLength * 0.65f)
                val tubeEnd = Offset(pivotCenter.x - armLength * 0.03f, pivotCenter.y + armLength * 0.88f)

                val sPath = Path().apply {
                    moveTo(p0.x, p0.y)
                    cubicTo(c1.x, c1.y, c2.x, c2.y, tubeEnd.x, tubeEnd.y)
                }

                // Headshell offset tangent to grooves:
                val hsLength = 14.dp.toPx()
                val hsAngleRad = Math.toRadians(24.0)
                val hsEndX = tubeEnd.x + (hsLength * Math.sin(hsAngleRad)).toFloat()
                val hsEndY = tubeEnd.y + (hsLength * Math.cos(hsAngleRad)).toFloat()
                val hsEnd = Offset(hsEndX, hsEndY)

                // Finger lift hook extending outward from headshell
                val flEnd = Offset(tubeEnd.x + 8.dp.toPx(), tubeEnd.y + 4.dp.toPx())

                // 3. PHYSICAL 3D DROP SHADOW:
                // Projects the tonearm onto the spinning platter beneath it!
                translate(left = 4.dp.toPx(), top = 6.dp.toPx()) {
                    drawPath(
                        path = sPath,
                        color = Color.Black.copy(alpha = 0.38f),
                        style = Stroke(width = 4.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    drawLine(
                        color = Color.Black.copy(alpha = 0.38f),
                        start = tubeEnd,
                        end = hsEnd,
                        strokeWidth = 6.5.dp.toPx(),
                        cap = StrokeCap.Square
                    )
                }

                // 4. Polished Chrome Tonearm Tube Multi-Pass Rendering
                // Outer tube dark rim
                drawPath(
                    path = sPath,
                    color = Color(0xFF1E1E24),
                    style = Stroke(width = 3.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                // Cylindrical chrome highlight gradient
                drawPath(
                    path = sPath,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFFFFF), Color(0xFFDDDDDC), Color(0xFF9494A0)),
                        start = p0,
                        end = tubeEnd
                    ),
                    style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                // Specular reflection center line
                drawPath(
                    path = sPath,
                    color = Color.White.copy(alpha = 0.85f),
                    style = Stroke(width = 0.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // 5. Knurled Connector Collar & Precision Headshell
                // Aluminum locking collar
                drawCircle(
                    color = Color(0xFF6E6E7C),
                    radius = 2.4.dp.toPx(),
                    center = tubeEnd
                )

                // Technics/Ortofon Precision Headshell Body
                drawLine(
                    color = Color(0xFF18181E),
                    start = tubeEnd,
                    end = hsEnd,
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Square
                )
                // Dual ventilation slots
                val slotMid1 = Offset(
                    tubeEnd.x + (hsEnd.x - tubeEnd.x) * 0.35f,
                    tubeEnd.y + (hsEnd.y - tubeEnd.y) * 0.35f
                )
                drawCircle(color = Color(0xFF0A0A0E), radius = 1.dp.toPx(), center = slotMid1)

                // Phono Cartridge Body (Accent highlight)
                val cartStart = Offset(
                    tubeEnd.x + (hsEnd.x - tubeEnd.x) * 0.45f,
                    tubeEnd.y + (hsEnd.y - tubeEnd.y) * 0.45f
                )
                drawLine(
                    color = theme.accentColor,
                    start = cartStart,
                    end = hsEnd,
                    strokeWidth = 3.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Finger Lift Hook for manual cueing
                drawLine(
                    color = Color(0xFFD6D6E0),
                    start = tubeEnd,
                    end = flEnd,
                    strokeWidth = 1.3.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Diamond Stylus Tip Touching the Vinyl Groove
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

