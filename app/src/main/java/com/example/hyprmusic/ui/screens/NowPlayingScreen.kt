package com.example.hyprmusic.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.media.AudioManager
import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.core.theming.HyprThemeConfig
import com.example.hyprmusic.core.theming.hyprBounceClick
import com.example.hyprmusic.ui.components.AddToPlaylistDialog
import com.example.hyprmusic.ui.components.HyprArtworkImage
import com.example.hyprmusic.ui.components.SleepTimerDialog
import com.example.hyprmusic.ui.components.SyncedLyricsView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Centralized Audiophile NowPlaying Design Tokens.
 * Cinematic dark background, pure circular artwork, radial progress, and pink → magenta → violet gradient.
 */
object NowPlayingColors {
    val Background = Color(0xFF0B0A12)
    val Surface = Color(0xFF11101A)
    val SurfaceElevated = Color(0xFF181624)
    val PrimaryText = Color(0xFFF5F3FA)
    val SecondaryText = Color(0xFFAAA7BA)
    val Muted = Color(0xFF686578)
    val AccentStart = Color(0xFFFF5F8F)
    val AccentMiddle = Color(0xFFD84BC4)
    val AccentEnd = Color(0xFF9868FF)
    val ProgressInactive = Color(0xFF343244)
    val Glow = Color(0x33D84BC4)
    val BorderSubtle = Color(0x26FFFFFF)

    val AccentBrush = Brush.linearGradient(
        colors = listOf(AccentStart, AccentMiddle, AccentEnd)
    )
}

/**
 * Redesigned Now Playing Screen:
 * - Minimal, chrome-free, premium audiophile music layout
 * - Centered circular album artwork with radial playback progress arc & tick marks
 * - Subtle volume controls (Mute on left, Volume on right)
 * - Left-aligned track title and artist with play-count readout and subtle action icons
 * - Clean audio quality metadata (FLAC · 1822 kbps       16-BIT PCM)
 * - Thin, elegant horizontal waveform seekbar with tap & drag seek
 * - Centered elapsed/duration time display
 * - Pure floating playback controls with 78dp hero gradient play/pause button
 * - 4 lightweight secondary actions (LYRICS, PLAYLIST, TIMER, EQ)
 * - Expansive modern bottom sheet for Synced Lyrics & quick settings
 */
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

    // Dialog & Panel States
    var showLyricsSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showAddToPlaylistDialog by remember { mutableStateOf(false) }

    // Synced Lyrics Loader
    var lyricsState by remember { mutableStateOf<List<LyricLine>?>(null) }
    var isLyricsLoading by remember { mutableStateOf(false) }
    var lyricsRetryKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(track.id, lyricsRetryKey) {
        delay(120)
        isLyricsLoading = true
        lyricsState = withContext(Dispatchers.IO) {
            LyricsRepository.fetchLyrics(track)
        }
        isLyricsLoading = false
    }

    // Audio stream volume integration
    val audioManager = remember {
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }
    var playerVolume by remember {
        mutableFloatStateOf(audioPlayer?.getVolume() ?: 1.0f)
    }
    var lastNonZeroVolume by remember { mutableFloatStateOf(1.0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NowPlayingColors.Background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        // 4-Layer Cinematic Ambient Blurred Artwork Background
        NowPlayingBackground(artworkUri = track.albumArtUri)

        // Main Responsive Layout
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            val availableHeight = maxHeight
            val isCompact = availableHeight < 720.dp
            val isUltraCompact = availableHeight < 640.dp

            val artworkSize = when {
                isUltraCompact -> 200.dp
                isCompact -> 230.dp
                availableHeight < 840.dp -> 260.dp
                else -> 285.dp
            }

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // TOP HEADER: Settings icon | "Made for you" | Share icon
                NowPlayingHeader(
                    onOpenSettings = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        showSettingsSheet = true
                    },
                    onShare = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        shareTrack(context, track)
                    },
                    onDismiss = onDismiss
                )

                Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 10.dp))

                // CENTERPIECE: Centered Circular Album Artwork with Surrounding Radial Progress Arc
                AlbumProgressControl(
                    track = track,
                    playbackState = playbackState,
                    artworkSize = artworkSize,
                    playerVolume = playerVolume,
                    onSeekTo = onSeekTo,
                    onToggleMute = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        if (playerVolume > 0.01f) {
                            lastNonZeroVolume = playerVolume
                            audioPlayer?.setVolume(0f)
                            playerVolume = 0f
                        } else {
                            val restored = if (lastNonZeroVolume > 0.05f) lastNonZeroVolume else 1.0f
                            audioPlayer?.setVolume(restored)
                            playerVolume = restored
                        }
                    },
                    onAdjustVolume = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        audioManager?.adjustStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_SAME,
                            AudioManager.FLAG_SHOW_UI
                        )
                    }
                )

                Spacer(modifier = Modifier.height(if (isCompact) 6.dp else 12.dp))

                // TRACK INFORMATION & TRACK ACTIONS (Left Aligned Title/Artist with Right Action Icons)
                TrackInfoSection(
                    track = track,
                    onToggleFavorite = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onToggleFavorite(track.id)
                    },
                    onAddToPlaylist = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        if (playlistRepository != null) {
                            showAddToPlaylistDialog = true
                        }
                    },
                    onNavigateToAlbum = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onNavigateToAlbum(track.album)
                    }
                )

                Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))

                // COMPACT AUDIO QUALITY METADATA (e.g. FLAC · 1822 kbps       16-BIT PCM)
                AudioQualityMetadataRow(track = track)

                Spacer(modifier = Modifier.height(if (isCompact) 6.dp else 10.dp))

                // HORIZONTAL WAVEFORM SEEKBAR
                WaveformSeekBar(
                    track = track,
                    progress = playbackState.progress,
                    durationMs = playbackState.durationMs,
                    onSeekTo = onSeekTo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                )

                Spacer(modifier = Modifier.height(2.dp))

                // PLAYBACK TIME (Centered: 4:26 / 4:37)
                PlaybackTimeDisplay(
                    currentPositionMs = playbackState.currentPositionMs,
                    durationMs = playbackState.durationMs
                )

                Spacer(modifier = Modifier.height(if (isCompact) 6.dp else 12.dp))

                // PRIMARY PLAYBACK CONTROLS (Floating, no container: Shuffle, Prev, Hero Play/Pause, Next, Repeat)
                PrimaryPlaybackControls(
                    isPlaying = playbackState.isPlaying,
                    isShuffle = playbackState.isShuffle,
                    repeatMode = playbackState.repeatMode,
                    heroButtonSize = if (isCompact) 72.dp else 78.dp,
                    onPlayPause = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onPlayPause()
                    },
                    onSkipPrevious = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onSkipPrevious()
                    },
                    onSkipNext = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onSkipNext()
                    },
                    onToggleShuffle = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onToggleShuffle()
                    },
                    onToggleRepeat = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onToggleRepeat()
                    }
                )

                Spacer(modifier = Modifier.height(if (isCompact) 6.dp else 12.dp))

                // SECONDARY ACTIONS (LYRICS, PLAYLIST, TIMER, EQ)
                SecondaryActionsRow(
                    isLyricsActive = showLyricsSheet,
                    onOpenLyrics = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        showLyricsSheet = true
                    },
                    onOpenPlaylist = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        if (playlistRepository != null) {
                            showAddToPlaylistDialog = true
                        }
                    },
                    onOpenTimer = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        if (audioPlayer != null) {
                            showSleepTimerDialog = true
                        }
                    },
                    onOpenEqualizer = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onOpenEqualizer()
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // MODERN EXPANSIVE SYNCED LYRICS BOTTOM SHEET
        AnimatedVisibility(
            visible = showLyricsSheet,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(220)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(200))
        ) {
            NowPlayingLyricsSheet(
                theme = theme,
                track = track,
                lyrics = lyricsState,
                isLoading = isLyricsLoading,
                currentPositionMs = playbackState.currentPositionMs,
                onSeekTo = onSeekTo,
                onRetry = { lyricsRetryKey++ },
                onClose = { showLyricsSheet = false }
            )
        }

        // QUICK SETTINGS / OPTIONS BOTTOM SHEET
        AnimatedVisibility(
            visible = showSettingsSheet,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 260)
            ) + fadeIn(tween(200)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 220)
            ) + fadeOut(tween(180))
        ) {
            NowPlayingSettingsSheet(
                track = track,
                onDismissPlayer = {
                    showSettingsSheet = false
                    onDismiss()
                },
                onNavigateToAlbum = {
                    showSettingsSheet = false
                    onNavigateToAlbum(track.album)
                },
                onOpenEqualizer = {
                    showSettingsSheet = false
                    onOpenEqualizer()
                },
                onOpenTimer = {
                    showSettingsSheet = false
                    if (audioPlayer != null) {
                        showSleepTimerDialog = true
                    }
                },
                onShare = {
                    showSettingsSheet = false
                    shareTrack(context, track)
                },
                onClose = { showSettingsSheet = false }
            )
        }

        // Sleep Timer Dialog
        if (showSleepTimerDialog && audioPlayer != null) {
            SleepTimerDialog(
                theme = theme,
                audioPlayer = audioPlayer,
                onDismiss = { showSleepTimerDialog = false }
            )
        }

        // Add to Playlist Dialog
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
 * 4-Layer Cinematic Ambient Background:
 * Layer 1: Dark base #0B0A12
 * Layer 2: Enlarged, heavily blurred album art
 * Layer 3: Dark translucent scrim (ensuring high text contrast)
 * Layer 4: Subtle perimeter vignette
 */
@Composable
private fun NowPlayingBackground(artworkUri: String?) {
    val context = LocalContext.current
    val ambientBlurEffect = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RenderEffect.createBlurEffect(100f, 100f, Shader.TileMode.CLAMP).asComposeRenderEffect()
        } else null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NowPlayingColors.Background)
    ) {
        if (!artworkUri.isNullOrBlank()) {
            val ambientReq = remember(artworkUri) {
                ImageRequest.Builder(context)
                    .data(artworkUri)
                    .size(400, 400)
                    .allowHardware(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .crossfade(true)
                    .build()
            }

            AsyncImage(
                model = ambientReq,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 0.28f
                        if (ambientBlurEffect != null) {
                            renderEffect = ambientBlurEffect
                        }
                    },
                contentScale = ContentScale.Crop
            )
        }

        // Dark Translucent Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NowPlayingColors.Background.copy(alpha = 0.72f))
        )

        // Radial Vignette
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, NowPlayingColors.Background.copy(alpha = 0.85f)),
                    center = center,
                    radius = size.maxDimension / 1.35f
                )
            )
        }
    }
}

/**
 * Minimal Floating Top Header:
 * Left: Settings icon
 * Center: "Made for you"
 * Right: Share icon
 * Floating, no cards, no borders, 44-48dp touch targets.
 */
@Composable
private fun NowPlayingHeader(
    onOpenSettings: () -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Settings / Options Button (also allows downward swipe/dismiss)
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Player Settings",
                tint = NowPlayingColors.PrimaryText.copy(alpha = 0.88f),
                modifier = Modifier.size(22.dp)
            )
        }

        // Center Title: "Made for you"
        Text(
            text = "Made for you",
            color = NowPlayingColors.PrimaryText,
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
        )

        // Right: Native Share Button
        IconButton(
            onClick = onShare,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share Track",
                tint = NowPlayingColors.PrimaryText.copy(alpha = 0.88f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/**
 * Album Progress Control:
 * Centered Circular Album Artwork encircled by a smooth Radial Playback Progress Arc.
 * Volume Controls (Mute left, Volume right) are subtly positioned around the radial arc.
 * Dragging or tapping the radial progress arc seeks playback smoothly.
 */
@Composable
private fun AlbumProgressControl(
    track: Track,
    playbackState: PlaybackState,
    artworkSize: Dp,
    playerVolume: Float,
    onSeekTo: (Long) -> Unit,
    onToggleMute: () -> Unit,
    onAdjustVolume: () -> Unit,
    modifier: Modifier = Modifier
) {
    val durationMs = playbackState.durationMs
    val currentProgress = playbackState.progress

    // Smooth animated progress value for the radial arc
    val animatedProgress by animateFloatAsState(
        targetValue = currentProgress,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "radial_progress"
    )

    // Artwork pulse scale when playing
    val pulseScale by animateFloatAsState(
        targetValue = if (playbackState.isPlaying) 1.0f else 0.98f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "artwork_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(artworkSize + 48.dp),
        contentAlignment = Alignment.Center
    ) {
        // Soft Glow underneath circular artwork
        Box(
            modifier = Modifier
                .size(artworkSize * 0.92f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NowPlayingColors.Glow,
                            Color.Transparent
                        )
                    )
                )
        )

        // Radial Playback Progress Arc & Outer Tick Marks (Canvas with touch seek)
        RadialProgressBar(
            progress = animatedProgress,
            durationMs = durationMs,
            artworkSize = artworkSize,
            onSeekTo = onSeekTo,
            modifier = Modifier.size(artworkSize + 44.dp)
        )

        // Centered Circular Album Artwork (100% vinyl-free)
        Box(
            modifier = Modifier
                .size(artworkSize)
                .graphicsLayer {
                    scaleX = pulseScale
                    scaleY = pulseScale
                }
                .clip(CircleShape)
                .background(NowPlayingColors.Surface)
                .border(1.2.dp, NowPlayingColors.BorderSubtle, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            HyprArtworkImage(
                artworkUri = track.albumArtUri,
                title = track.title,
                artist = track.artist,
                trackId = track.id,
                shape = CircleShape,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Left Volume: Mute Icon Button
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 2.dp)
        ) {
            IconButton(
                onClick = onToggleMute,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = if (playerVolume <= 0.01f) Icons.AutoMirrored.Filled.VolumeOff
                                  else Icons.AutoMirrored.Filled.VolumeMute,
                    contentDescription = if (playerVolume <= 0.01f) "Unmute" else "Mute",
                    tint = if (playerVolume <= 0.01f) NowPlayingColors.AccentStart
                           else NowPlayingColors.SecondaryText.copy(alpha = 0.85f),
                    modifier = Modifier.size(19.dp)
                )
            }
        }

        // Right Volume: System Volume Stream Trigger Button
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 2.dp)
        ) {
            IconButton(
                onClick = onAdjustVolume,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Adjust Volume",
                    tint = NowPlayingColors.SecondaryText.copy(alpha = 0.85f),
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

/**
 * Radial Playback Progress Canvas:
 * Inactive arc: Dark violet/blue-gray (#343244)
 * Active arc: Pink → Magenta → Violet gradient (#FF5F8F → #D84BC4 → #9868FF)
 * Outer subtle tick marks radiating outward
 * Knob thumb: Small white circular knob with violet center point directly on the arc.
 * Supports tap and drag to seek.
 */
@Composable
private fun RadialProgressBar(
    progress: Float,
    durationMs: Long,
    artworkSize: Dp,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(progress) }

    val effectiveProgress = if (isDragging) dragProgress else progress

    // Geometry angles: Start at 185° (left side) down through 90° (bottom) to -5° (right side)
    val startAngleDeg = 185f
    val sweepSpanDeg = 190f // sweeping counter-clockwise across bottom

    Canvas(
        modifier = modifier
            .semantics {
                contentDescription = "Playback progress"
                progressBarRangeInfo = ProgressBarRangeInfo(
                    current = effectiveProgress,
                    range = 0f..1f
                )
            }
            .pointerInput(durationMs) {
                fun calculateProgressFromOffset(offset: Offset): Float {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val dx = offset.x - cx
                    val dy = offset.y - cy

                    // Calculate angle in degrees [-180..180]
                    var angleDeg = (atan2(dy, dx) * (180f / PI.toFloat()))
                    // Map angle into the [185° down to -5°] span
                    // In screen coords, bottom is +Y, so dy > 0 gives positive angles [0..180]
                    if (angleDeg < -90f) {
                        angleDeg += 360f
                    }
                    val rawFrac = (startAngleDeg - angleDeg) / sweepSpanDeg
                    return rawFrac.coerceIn(0f, 1f)
                }

                detectTapGestures(
                    onPress = { offset ->
                        val p = calculateProgressFromOffset(offset)
                        dragProgress = p
                        val targetMs = (p * durationMs).toLong().coerceIn(0L, (durationMs - 500L).coerceAtLeast(0L))
                        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        onSeekTo(targetMs)
                    }
                )
            }
            .pointerInput(durationMs) {
                fun calculateProgressFromOffset(offset: Offset): Float {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val dx = offset.x - cx
                    val dy = offset.y - cy
                    var angleDeg = (atan2(dy, dx) * (180f / PI.toFloat()))
                    if (angleDeg < -90f) angleDeg += 360f
                    val rawFrac = (startAngleDeg - angleDeg) / sweepSpanDeg
                    return rawFrac.coerceIn(0f, 1f)
                }

                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        dragProgress = calculateProgressFromOffset(offset)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val p = calculateProgressFromOffset(change.position)
                        dragProgress = p
                        if (durationMs > 0L) {
                            val targetMs = (p * durationMs).toLong().coerceIn(0L, (durationMs - 500L).coerceAtLeast(0L))
                            onSeekTo(targetMs)
                        }
                    },
                    onDragEnd = {
                        isDragging = false
                    },
                    onDragCancel = {
                        isDragging = false
                    }
                )
            }
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val center = Offset(cx, cy)
        val arcRadius = (artworkSize.toPx() / 2f) + 14.dp.toPx()
        val strokeWidth = 3.2.dp.toPx()

        val arcTopLeft = Offset(cx - arcRadius, cy - arcRadius)
        val arcSize = Size(arcRadius * 2f, arcRadius * 2f)

        // 1. Subtle Outer Tick Marks (36 ticks along the arc)
        val tickCount = 36
        for (i in 0..tickCount) {
            val frac = i.toFloat() / tickCount
            val tickAngleDeg = startAngleDeg - (sweepSpanDeg * frac)
            val tickRad = tickAngleDeg * (PI.toFloat() / 180f)

            val innerR = arcRadius + 5.5.dp.toPx()
            val outerR = arcRadius + 9.5.dp.toPx()

            val p1 = Offset(cx + innerR * cos(tickRad), cy + innerR * sin(tickRad))
            val p2 = Offset(cx + outerR * cos(tickRad), cy + outerR * sin(tickRad))

            val isPassed = frac <= effectiveProgress
            val tickColor = if (isPassed) {
                NowPlayingColors.AccentMiddle.copy(alpha = 0.65f)
            } else {
                NowPlayingColors.ProgressInactive.copy(alpha = 0.35f)
            }

            drawLine(
                color = tickColor,
                start = p1,
                end = p2,
                strokeWidth = 1.1.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // 2. Inactive Background Arc (Dark violet/blue-gray)
        drawArc(
            color = NowPlayingColors.ProgressInactive,
            startAngle = startAngleDeg,
            sweepAngle = -sweepSpanDeg,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // 3. Active Playback Progress Arc (Pink → Magenta → Violet Gradient)
        if (effectiveProgress > 0.001f) {
            val activeSweep = -sweepSpanDeg * effectiveProgress
            val activeBrush = Brush.sweepGradient(
                colors = listOf(
                    NowPlayingColors.AccentStart,
                    NowPlayingColors.AccentMiddle,
                    NowPlayingColors.AccentEnd,
                    NowPlayingColors.AccentStart
                ),
                center = center
            )

            drawArc(
                brush = activeBrush,
                startAngle = startAngleDeg,
                sweepAngle = activeSweep,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // 4. Progress Thumb Knob (White knob with violet center point)
        val thumbAngleDeg = startAngleDeg - (sweepSpanDeg * effectiveProgress)
        val thumbRad = thumbAngleDeg * (PI.toFloat() / 180f)
        val thumbCenter = Offset(
            cx + arcRadius * cos(thumbRad),
            cy + arcRadius * sin(thumbRad)
        )

        // Outer glow
        drawCircle(
            color = NowPlayingColors.AccentMiddle.copy(alpha = 0.35f),
            radius = 10.dp.toPx(),
            center = thumbCenter
        )
        // White circular knob
        drawCircle(
            color = Color.White,
            radius = 7.dp.toPx(),
            center = thumbCenter
        )
        // Inner violet center point
        drawCircle(
            color = NowPlayingColors.AccentMiddle,
            radius = 3.2.dp.toPx(),
            center = thumbCenter
        )
    }
}

/**
 * Track Information Section:
 * Intentional Left Alignment contrasting with the centered artwork.
 * Left: Play count metadata (if available), Track Title (28-32sp Bold), Artist (17-19sp Medium).
 * Right: 3 subtle outline action icons (Add to playlist, Album/Artist info, Favorite heart).
 */
@Composable
private fun TrackInfoSection(
    track: Track,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onNavigateToAlbum: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Column: Metadata, Song Title, Artist
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            // Small Play Count Metadata (real value only, hidden if 0)
            if (track.playCount > 0) {
                val formattedPlays = remember(track.playCount) {
                    NumberFormat.getIntegerInstance().format(track.playCount)
                }
                Text(
                    text = "▶ $formattedPlays",
                    color = NowPlayingColors.AccentStart,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            // Main Track Title (28-32sp Bold, Primary Text)
            Text(
                text = track.title,
                color = NowPlayingColors.PrimaryText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Artist Name (17-19sp Medium, Secondary Text)
            Text(
                text = track.artist.ifBlank { "Unknown Artist" },
                color = NowPlayingColors.SecondaryText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right Row: 3 Subtle Action Icons (Add to Playlist, Album Info, Favorite)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Add to Playlist
            IconButton(
                onClick = onAddToPlaylist,
                modifier = Modifier.size(42.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                    contentDescription = "Add to playlist",
                    tint = NowPlayingColors.SecondaryText,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Album / Artist Info
            IconButton(
                onClick = onNavigateToAlbum,
                modifier = Modifier.size(42.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Album,
                    contentDescription = "View album",
                    tint = NowPlayingColors.SecondaryText,
                    modifier = Modifier.size(21.dp)
                )
            }

            // Favorite (Outline when unselected, Accent-colored filled heart when selected)
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.size(42.dp)
            ) {
                Icon(
                    imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (track.isFavorite) "Remove favorite" else "Add favorite",
                    tint = if (track.isFavorite) NowPlayingColors.AccentStart else NowPlayingColors.SecondaryText,
                    modifier = Modifier.size(23.dp)
                )
            }
        }
    }
}

/**
 * Compact Audio Quality Metadata:
 * Subtle technical readout (e.g. FLAC · 1822 kbps       16-BIT PCM).
 * No heavy pill badges or rectangular containers.
 */
@Composable
private fun AudioQualityMetadataRow(track: Track) {
    val qualityText = remember(track.audioFormat, track.bitrate, track.sampleRate, track.isLossless) {
        val format = track.audioFormat
        val bitrate = if (track.bitrate > 0) "${track.bitrate} kbps" else ""
        val pcm = if (track.isLossless) "16-BIT PCM" else "${track.sampleRate / 1000} kHz"
        if (bitrate.isNotEmpty()) {
            "$format · $bitrate       $pcm"
        } else {
            "$format       $pcm"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = qualityText,
            color = NowPlayingColors.Muted,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * Thin Horizontal Waveform Seek Bar:
 * Deterministic waveform bars seeded by track properties.
 * Light/white bars on played section, muted violet-gray on unplayed section.
 * Tapping and dragging seeks playback smoothly.
 */
@Composable
private fun WaveformSeekBar(
    track: Track,
    progress: Float,
    durationMs: Long,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    // Deterministic lightweight waveform heights (58 bars)
    val barAmplitudes = remember(track.id, track.durationMs) {
        val count = 58
        val seed = (track.id.hashCode() xor track.durationMs.toInt()).let { if (it == 0) 42 else it }
        FloatArray(count) { i ->
            val phase = (i.toFloat() / count.toFloat()) * PI.toFloat() * 3.5f
            val envelope = sin((i.toFloat() / count.toFloat()) * PI.toFloat()) // natural curve
            val pseudoNoise = (kotlin.math.abs(sin(seed.toFloat() + i * 1.7f))) * 0.45f
            val base = (0.35f + pseudoNoise) * envelope
            base.coerceIn(0.18f, 1.0f)
        }
    }

    Canvas(
        modifier = modifier
            .pointerInput(durationMs) {
                detectTapGestures { offset ->
                    if (durationMs > 0L) {
                        val pct = (offset.x / size.width).coerceIn(0f, 0.995f)
                        val targetMs = (pct * durationMs).toLong().coerceIn(0L, (durationMs - 500L).coerceAtLeast(0L))
                        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        onSeekTo(targetMs)
                    }
                }
            }
            .pointerInput(durationMs) {
                detectDragGestures { change, _ ->
                    change.consume()
                    if (durationMs > 0L) {
                        val pct = (change.position.x / size.width).coerceIn(0f, 0.995f)
                        val targetMs = (pct * durationMs).toLong().coerceIn(0L, (durationMs - 500L).coerceAtLeast(0L))
                        onSeekTo(targetMs)
                    }
                }
            }
    ) {
        val totalBars = barAmplitudes.size
        val availableWidth = size.width
        val barWidth = 2.4.dp.toPx()
        val spacing = (availableWidth - (totalBars * barWidth)) / (totalBars - 1).coerceAtLeast(1)
        val maxHeight = size.height * 0.95f
        val cy = size.height / 2f

        for (i in 0 until totalBars) {
            val amp = barAmplitudes[i]
            val barHeight = (amp * maxHeight).coerceAtLeast(3.dp.toPx())
            val x = i * (barWidth + spacing)
            val barProgressFrac = (i.toFloat() / totalBars.toFloat())

            val isPlayed = barProgressFrac <= progress
            val barColor = if (isPlayed) {
                NowPlayingColors.PrimaryText
            } else {
                NowPlayingColors.ProgressInactive.copy(alpha = 0.55f)
            }

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, cy - (barHeight / 2f)),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(1.2.dp.toPx(), 1.2.dp.toPx())
            )
        }
    }
}

/**
 * Centered Playback Time Display (e.g. 4:26 / 4:37).
 * Secondary text, 16-18sp, gracefully handles unknown/zero duration.
 */
@Composable
private fun PlaybackTimeDisplay(
    currentPositionMs: Long,
    durationMs: Long
) {
    val currentSec = (currentPositionMs / 1000).coerceAtLeast(0)
    val totalSec = (durationMs / 1000).coerceAtLeast(0)

    val elapsed = "%d:%02d".format(currentSec / 60, currentSec % 60)
    val total = if (durationMs > 0L) "%d:%02d".format(totalSec / 60, totalSec % 60) else "--:--"

    Text(
        text = "$elapsed / $total",
        color = NowPlayingColors.SecondaryText,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
    )
}

/**
 * Primary Playback Controls:
 * Floating row: Shuffle, Previous, Play/Pause Hero Gradient Button (78dp), Next, Repeat.
 * Free of containers, cards, or rectangular borders.
 */
@Composable
private fun PrimaryPlaybackControls(
    isPlaying: Boolean,
    isShuffle: Boolean,
    repeatMode: RepeatMode,
    heroButtonSize: Dp,
    onPlayPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Shuffle (Outline)
        IconButton(
            onClick = onToggleShuffle,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = "Shuffle",
                tint = if (isShuffle) NowPlayingColors.AccentStart else NowPlayingColors.SecondaryText,
                modifier = Modifier.size(24.dp)
            )
        }

        // Previous Track
        IconButton(
            onClick = onSkipPrevious,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Previous track",
                tint = NowPlayingColors.PrimaryText,
                modifier = Modifier.size(32.dp)
            )
        }

        // PLAY / PAUSE HERO BUTTON (Large 78dp circular gradient with soft glow)
        Box(
            modifier = Modifier.size(heroButtonSize + 12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Soft Radial Glow
            Box(
                modifier = Modifier
                    .size(heroButtonSize + 10.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                NowPlayingColors.AccentMiddle.copy(alpha = 0.40f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Hero Circular Gradient Button
            Box(
                modifier = Modifier
                    .size(heroButtonSize)
                    .clip(CircleShape)
                    .background(NowPlayingColors.AccentBrush)
                    .hyprBounceClick { onPlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // Next Track
        IconButton(
            onClick = onSkipNext,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next track",
                tint = NowPlayingColors.PrimaryText,
                modifier = Modifier.size(32.dp)
            )
        }

        // Repeat (Outline)
        IconButton(
            onClick = onToggleRepeat,
            modifier = Modifier.size(44.dp)
        ) {
            val repeatIcon = if (repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat
            val isRepeatActive = repeatMode != RepeatMode.OFF
            Icon(
                imageVector = repeatIcon,
                contentDescription = "Repeat",
                tint = if (isRepeatActive) NowPlayingColors.AccentStart else NowPlayingColors.SecondaryText,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Lightweight Secondary Actions Row:
 * LYRICS | PLAYLIST | TIMER | EQ
 * Icon + small label. No cards, no borders, no pills.
 */
@Composable
private fun SecondaryActionsRow(
    isLyricsActive: Boolean,
    onOpenLyrics: () -> Unit,
    onOpenPlaylist: () -> Unit,
    onOpenTimer: () -> Unit,
    onOpenEqualizer: () -> Unit
) {
    val sleepTimerState by HyprSleepTimer.timerState.collectAsStateWithLifecycle()
    val isTimerActive = sleepTimerState.isActive

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SecondaryActionButton(
            icon = Icons.AutoMirrored.Filled.FormatAlignLeft,
            label = "LYRICS",
            isActive = isLyricsActive,
            onClick = onOpenLyrics
        )

        SecondaryActionButton(
            icon = Icons.AutoMirrored.Filled.PlaylistAdd,
            label = "PLAYLIST",
            isActive = false,
            onClick = onOpenPlaylist
        )

        SecondaryActionButton(
            icon = Icons.Default.Bedtime,
            label = if (isTimerActive) sleepTimerState.formattedRemaining else "TIMER",
            isActive = isTimerActive,
            onClick = onOpenTimer
        )

        SecondaryActionButton(
            icon = Icons.Default.Tune,
            label = "EQ",
            isActive = false,
            onClick = onOpenEqualizer
        )
    }
}

@Composable
private fun SecondaryActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isActive) NowPlayingColors.AccentStart else NowPlayingColors.SecondaryText

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = tint,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
            letterSpacing = 0.4.sp
        )
    }
}

/**
 * Modern Expansive Synced Lyrics Sheet:
 * Full-height modal surface with 28dp top rounded corners and terminal styled SyncedLyricsView.
 */
@Composable
private fun NowPlayingLyricsSheet(
    theme: HyprThemeConfig,
    track: Track,
    lyrics: List<LyricLine>?,
    isLoading: Boolean,
    currentPositionMs: Long,
    onSeekTo: (Long) -> Unit,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClose
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(NowPlayingColors.Surface)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .padding(top = 12.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag Handle Bar
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(NowPlayingColors.ProgressInactive)
            )

            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Synced Lyrics",
                        color = NowPlayingColors.PrimaryText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${track.title} · ${track.artist}",
                        color = NowPlayingColors.SecondaryText,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close lyrics",
                        tint = NowPlayingColors.PrimaryText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Synced Lyrics Body
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                SyncedLyricsView(
                    lyrics = lyrics,
                    isLoading = isLoading,
                    currentPositionMs = currentPositionMs,
                    theme = theme,
                    onSeekTo = onSeekTo,
                    onRetry = onRetry,
                    onShowCoverArt = onClose,
                    trackTitle = track.title,
                    artist = track.artist,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Now Playing Settings / Quick Options Bottom Sheet.
 */
@Composable
private fun NowPlayingSettingsSheet(
    track: Track,
    onDismissPlayer: () -> Unit,
    onNavigateToAlbum: () -> Unit,
    onOpenEqualizer: () -> Unit,
    onOpenTimer: () -> Unit,
    onShare: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClose
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(NowPlayingColors.Surface)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag Handle
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(NowPlayingColors.ProgressInactive)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Player Options",
                color = NowPlayingColors.PrimaryText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSheetItem(
                icon = Icons.Default.KeyboardArrowDown,
                title = "Minimize Player",
                subtitle = "Keep listening while browsing",
                onClick = onDismissPlayer
            )

            SettingsSheetItem(
                icon = Icons.Default.Album,
                title = "View Album",
                subtitle = track.album.ifBlank { "Storage" },
                onClick = onNavigateToAlbum
            )

            SettingsSheetItem(
                icon = Icons.Default.Tune,
                title = "DSP Audio Equalizer",
                subtitle = "Bass, Virtualizer & 5-band EQ",
                onClick = onOpenEqualizer
            )

            SettingsSheetItem(
                icon = Icons.Default.Bedtime,
                title = "Sleep Timer",
                subtitle = "Turn off playback automatically",
                onClick = onOpenTimer
            )

            SettingsSheetItem(
                icon = Icons.Default.Share,
                title = "Share Song",
                subtitle = "${track.title} by ${track.artist}",
                onClick = onShare
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SettingsSheetItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NowPlayingColors.AccentStart,
            modifier = Modifier.size(24.dp)
        )

        Column {
            Text(
                text = title,
                color = NowPlayingColors.PrimaryText,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = NowPlayingColors.SecondaryText,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Shares track info via native Android Chooser.
 */
private fun shareTrack(context: Context, track: Track) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, track.title)
        putExtra(
            Intent.EXTRA_TEXT,
            "Listening to \"${track.title}\" by ${track.artist} on HyprMusic"
        )
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share Track"))
}

/**
 * Minimal idle screen displayed when no track is actively cued.
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
            .background(NowPlayingColors.Background)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize",
                        tint = NowPlayingColors.PrimaryText,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.4f))

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(NowPlayingColors.Surface)
                    .border(1.2.dp, NowPlayingColors.BorderSubtle, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = NowPlayingColors.AccentMiddle,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "No Audio Stream Active",
                color = NowPlayingColors.PrimaryText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Select a song from your library or start instant random playback",
                color = NowPlayingColors.SecondaryText,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NowPlayingColors.AccentBrush)
                        .clickable { onRandomMix() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RANDOM MIX",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NowPlayingColors.SurfaceElevated)
                        .border(1.dp, NowPlayingColors.BorderSubtle, RoundedCornerShape(12.dp))
                        .clickable { onBrowseLibrary() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "OPEN LIBRARY",
                        color = NowPlayingColors.PrimaryText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.6f))
        }
    }
}

/**
 * Backwards-compatible aliases retained to ensure zero external breakage.
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
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(NowPlayingColors.Surface)
            .border(1.2.dp, NowPlayingColors.BorderSubtle, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        HyprArtworkImage(
            artworkUri = albumArtUri,
            title = trackTitle,
            artist = artist,
            shape = CircleShape,
            modifier = Modifier.fillMaxSize()
        )
    }
}

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

@Composable
fun PureVinylDisc(
    theme: HyprThemeConfig,
    albumArtUri: String?,
    trackTitle: String,
    artist: String,
    currentRotation: Float,
    modifier: Modifier = Modifier
) {
    FloatingVinylCenterpiece(
        theme = theme,
        albumArtUri = albumArtUri,
        trackTitle = trackTitle,
        artist = artist,
        isPlaying = false,
        modifier = modifier
    )
}
