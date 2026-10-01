package com.example.hyprmusic.ui.screens

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode as AnimRepeatMode
import androidx.compose.animation.core.Spring
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
                                        isPlaying = playbackState.isPlaying
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
                    // Status Telemetry LED bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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

                        Text(
                            text = "BIT-PERFECT DIRECT",
                            color = theme.textSecondaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.5.sp
                        )

                        Text(
                            text = "${track.audioFormat} ${track.bitrate}k",
                            color = theme.accentColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
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
 * strobe perimeter calibration dots, center circular album art label, and
 * animated tonearm tracking onto the outer groove when playing.
 */
@Composable
fun MasterVinylTurntable(
    theme: HyprThemeConfig,
    albumArtUri: String?,
    trackTitle: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_turntable_spin")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = AnimRepeatMode.Restart
        ),
        label = "vinyl_turntable_angle"
    )

    val currentRotation = if (isPlaying) rotationAngle else 0f

    // Animated Tonearm angle: ~22 degrees tracking on the record when playing, ~-10 degrees parked when paused
    val tonearmAngle by animateFloatAsState(
        targetValue = if (isPlaying) 22f else -10f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow),
        label = "tonearm_angle"
    )

    Box(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtLeast(16).dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        theme.surfaceVariantColor.copy(alpha = 0.85f),
                        theme.surfaceColor.copy(alpha = 0.98f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = if (isPlaying) theme.accentColor.copy(alpha = 0.4f) else theme.inactiveBorderColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(theme.borderRadiusDp.coerceAtLeast(16).dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Direct-Drive Platter Base with Strobe Perimeter Dots
        Canvas(modifier = Modifier.fillMaxSize(0.90f)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val platterRadius = size.minDimension / 2f

            // Platter beveled edge
            drawCircle(
                color = Color(0xFF18181C),
                radius = platterRadius,
                center = center
            )
            drawCircle(
                color = Color(0xFF282830),
                radius = platterRadius * 0.98f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Strobe calibration dots around platter rim
            val dotCount = 36
            val dotRadius = platterRadius * 0.94f
            for (i in 0 until dotCount) {
                val angle = Math.toRadians((i * (360f / dotCount)).toDouble())
                val x = center.x + (dotRadius * Math.cos(angle)).toFloat()
                val y = center.y + (dotRadius * Math.sin(angle)).toFloat()
                drawCircle(
                    color = if (isPlaying && i % 4 == 0) theme.accentColor.copy(alpha = 0.7f) else Color(0xFF555560),
                    radius = 1.5.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }

        // Rotating 12-inch Vinyl Disc
        Box(
            modifier = Modifier
                .fillMaxSize(0.82f)
                .rotate(currentRotation)
                .clip(CircleShape)
                .background(Color(0xFF0F0F11)),
            contentAlignment = Alignment.Center
        ) {
            // Vinyl Grooves Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val discRadius = size.minDimension / 2f

                // Outer run-in groove
                drawCircle(
                    color = Color(0xFF1C1C20),
                    radius = discRadius * 0.98f,
                    center = center
                )

                // Concentric music grooves
                val grooveSteps = 8
                for (i in 0..grooveSteps) {
                    val r = discRadius * (0.48f + (i.toFloat() / grooveSteps) * 0.46f)
                    val alpha = if (i % 2 == 0) 0.08f else 0.04f
                    drawCircle(
                        color = Color.White.copy(alpha = alpha),
                        radius = r,
                        center = center,
                        style = Stroke(width = 0.9.dp.toPx())
                    )
                }

                // Run-out groove near label
                drawCircle(
                    color = Color.White.copy(alpha = 0.07f),
                    radius = discRadius * 0.44f,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Center Circular Album Artwork Label (38% of disc diameter)
            Box(
                modifier = Modifier
                    .fillMaxSize(0.40f)
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
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Center Spindle Hole
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF08080A))
                        .border(1.dp, Color(0xFF9E9E9E), CircleShape)
                )
            }
        }

        // Realistic Machined Tonearm Assembly (Top-Right Pivot)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(tonearmAngle)
        ) {
            val pivotX = size.width * 0.88f
            val pivotY = size.height * 0.14f
            val pivotCenter = Offset(pivotX, pivotY)

            // Tonearm Gimbal / Pivot Base
            drawCircle(
                color = Color(0xFF2E2E36),
                radius = 12.dp.toPx(),
                center = pivotCenter
            )
            drawCircle(
                color = Color(0xFF4A4A58),
                radius = 7.dp.toPx(),
                center = pivotCenter
            )

            // Counterweight behind pivot
            val cwX = pivotX + 10.dp.toPx()
            val cwY = pivotY - 10.dp.toPx()
            drawCircle(
                color = Color(0xFF555566),
                radius = 8.dp.toPx(),
                center = Offset(cwX, cwY)
            )

            // Polished Steel Tonearm Tube
            val armEndX = size.width * 0.52f
            val armEndY = size.height * 0.62f
            drawLine(
                color = Color(0xFFCCCCCC),
                start = pivotCenter,
                end = Offset(armEndX, armEndY),
                strokeWidth = 2.8.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Headshell & Phono Cartridge
            val hsEndX = armEndX - 10.dp.toPx()
            val hsEndY = armEndY + 12.dp.toPx()
            drawLine(
                color = theme.accentColor,
                start = Offset(armEndX, armEndY),
                end = Offset(hsEndX, hsEndY),
                strokeWidth = 4.5.dp.toPx(),
                cap = StrokeCap.Square
            )

            // Needle Stylus Tip
            drawCircle(
                color = Color.White,
                radius = 1.8.dp.toPx(),
                center = Offset(hsEndX, hsEndY)
            )
        }
    }
}

/**
 * Digipak Sleeve Centerpiece:
 * 1:1 digipak jacket with ambient reactive underglow, beat-synchronized scaling,
 * and vinyl disc peeking out of the right sleeve opening.
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
    val scale = if (isPlaying) (0.98f + rms * 0.04f).coerceIn(0.98f, 1.03f) else 0.95f

    Box(
        modifier = modifier
            .fillMaxWidth(0.88f)
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        // Vinyl disc peeking out of right opening (32dp shift)
        Box(
            modifier = Modifier
                .fillMaxSize(0.88f)
                .graphicsLayer { translationX = 32.dp.toPx() }
                .clip(CircleShape)
                .background(Color(0xFF111114))
                .border(0.8.dp, Color(0xFF222228), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2f
                for (i in 1..4) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.06f),
                        radius = radius * (0.5f + i * 0.1f),
                        center = center,
                        style = Stroke(width = 0.8.dp.toPx())
                    )
                }
            }
        }

        // Dual-Layer Digipak Jacket
        Box(
            modifier = Modifier
                .fillMaxSize(0.94f)
                .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtLeast(14).dp))
                .background(theme.surfaceVariantColor)
                .border(
                    width = 1.dp,
                    color = if (isPlaying) theme.accentColor.copy(alpha = 0.6f) else theme.inactiveBorderColor.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(theme.borderRadiusDp.coerceAtLeast(14).dp)
                )
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

                // Layer 1: Ambient background
                AsyncImage(
                    model = sleeveReq,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.35f
                )

                // Layer 2: 100% fitted foreground jacket
                AsyncImage(
                    model = sleeveReq,
                    contentDescription = trackTitle,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                        .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(10).dp)),
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
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
        }
    }
}

