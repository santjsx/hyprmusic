package com.example.hyprmusic.ui.elovaire.screens

import android.content.Context
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.hyprmusic.core.data.PlaylistRepository
import com.example.hyprmusic.core.lyrics.LyricsRepository
import com.example.hyprmusic.core.media.HyprAudioPlayer
import com.example.hyprmusic.core.model.LyricLine
import com.example.hyprmusic.core.model.PlaybackState
import com.example.hyprmusic.core.model.RepeatMode
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.ui.elovaire.components.ElovaireAddToPlaylistDialog
import com.example.hyprmusic.ui.elovaire.components.ElovaireSleepTimerDialog
import com.example.hyprmusic.ui.elovaire.components.ElovaireArtworkImage
import com.example.hyprmusic.ui.elovaire.motion.ElovaireMotionSpecs
import com.example.hyprmusic.ui.elovaire.motion.elovairePressScale
import com.example.hyprmusic.ui.elovaire.theme.ElovaireColors
import com.example.hyprmusic.ui.elovaire.theme.ElovaireRadii
import com.example.hyprmusic.ui.elovaire.theme.ElovaireSpacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Flagship Elovaire Now Playing Experience.
 * - 4-Layer Cinematic Ambient Artwork Blur Backdrop
 * - Centered 280dp artwork with subtle playing scale pulse and glow
 * - High-Resolution Audio Spec Chip (e.g. FLAC · 24-bit / 96kHz)
 * - Tactile Scrubber with real-time drag seek
 * - 76dp Hero Play/Pause button with spring bounce
 * - Seamless bottom sheets for Synced Karaoke Lyrics & Queue
 */
@Composable
fun ElovaireNowPlayingScreen(
    playbackState: PlaybackState,
    audioPlayer: HyprAudioPlayer?,
    playlistRepository: PlaylistRepository?,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenEqualizer: () -> Unit,
    onNavigateToAlbum: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack
    if (track == null) {
        onDismiss()
        return
    }

    val context = LocalContext.current
    val view = LocalView.current

    // Bottom Sheets & Dialogs State
    var showLyricsSheet by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showAddToPlaylistDialog by remember { mutableStateOf(false) }

    // Lyrics Loading
    var lyricsList by remember { mutableStateOf<List<LyricLine>?>(null) }
    var isLyricsLoading by remember { mutableStateOf(false) }
    var lyricsRetryKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(track.id, lyricsRetryKey) {
        delay(120)
        isLyricsLoading = true
        lyricsList = withContext(Dispatchers.IO) {
            LyricsRepository.fetchLyrics(track)
        }
        isLyricsLoading = false
    }

    BackHandler(enabled = true) {
        when {
            showLyricsSheet -> showLyricsSheet = false
            showQueueSheet -> showQueueSheet = false
            else -> onDismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElovaireColors.Night)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        // 4-Layer Ambient Blurred Background
        ElovaireAmbientBackdrop(artworkUri = track.albumArtUri)

        // Main Layout Container
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 22.dp, vertical = 6.dp)
        ) {
            val availableHeight = maxHeight
            val isCompact = availableHeight < 720.dp
            val artworkSize = if (isCompact) 230.dp else 290.dp

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header: Dismiss chevron | "Playing from" | Add to playlist + Share
                NowPlayingHeader(
                    playingFrom = track.album.ifBlank { "Library" },
                    onDismiss = onDismiss,
                    onAddToPlaylist = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        showAddToPlaylistDialog = true
                    },
                    onShare = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        shareTrack(context, track)
                    }
                )

                Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 12.dp))

                // Hero Artwork Presentation with Subtle Playing Pulse
                NowPlayingArtworkHero(
                    track = track,
                    isPlaying = playbackState.isPlaying,
                    artworkSize = artworkSize,
                    onSkipNext = onSkipNext,
                    onSkipPrevious = onSkipPrevious
                )

                Spacer(modifier = Modifier.height(if (isCompact) 6.dp else 16.dp))

                // Track Title, Artist, & Favorite Heart
                NowPlayingMetadataRow(
                    track = track,
                    isFavorite = track.isFavorite,
                    onToggleFavorite = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onToggleFavorite(track.id)
                    },
                    onNavigateToAlbum = { onNavigateToAlbum(track.album) }
                )

                Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))

                // Audio Quality Specification Chip
                AudioQualityBadge(track = track)

                Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 10.dp))

                // Scrubber & Duration Timestamps
                NowPlayingScrubber(
                    progress = playbackState.progress,
                    currentPositionMs = playbackState.currentPositionMs,
                    durationMs = playbackState.durationMs,
                    onSeekTo = onSeekTo
                )

                Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 12.dp))

                // Primary Transport Controls (Shuffle, Prev, Hero Play/Pause, Next, Repeat)
                NowPlayingTransportControls(
                    isPlaying = playbackState.isPlaying,
                    isShuffle = playbackState.isShuffle,
                    repeatMode = playbackState.repeatMode,
                    heroSize = if (isCompact) 70.dp else 76.dp,
                    onPlayPause = onPlayPause,
                    onSkipNext = onSkipNext,
                    onSkipPrevious = onSkipPrevious,
                    onToggleShuffle = onToggleShuffle,
                    onToggleRepeat = onToggleRepeat
                )

                Spacer(modifier = Modifier.height(if (isCompact) 6.dp else 14.dp))

                // Secondary Action Pills: Lyrics, Queue, Timer, EQ
                NowPlayingSecondaryActions(
                    isLyricsActive = showLyricsSheet,
                    onOpenLyrics = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        showLyricsSheet = true
                    },
                    onOpenQueue = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        showQueueSheet = true
                    },
                    onOpenTimer = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        showSleepTimerDialog = true
                    },
                    onOpenEqualizer = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onOpenEqualizer()
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // Synced Karaoke Lyrics Bottom Sheet
        AnimatedVisibility(
            visible = showLyricsSheet,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = ElovaireMotionSpecs.overlayEnter<IntOffset>()) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = ElovaireMotionSpecs.overlayExit<IntOffset>()) + fadeOut()
        ) {
            ElovaireLyricsSheet(
                track = track,
                lyrics = lyricsList,
                isLoading = isLyricsLoading,
                currentPositionMs = playbackState.currentPositionMs,
                onSeekTo = onSeekTo,
                onRetry = { lyricsRetryKey++ },
                onClose = { showLyricsSheet = false }
            )
        }

        // Interactive Queue Bottom Sheet
        AnimatedVisibility(
            visible = showQueueSheet,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = ElovaireMotionSpecs.overlayEnter<IntOffset>()) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = ElovaireMotionSpecs.overlayExit<IntOffset>()) + fadeOut()
        ) {
            ElovaireQueueSheet(
                currentTrack = track,
                queue = playbackState.queue,
                onTrackSelected = { targetTrack, newQueue ->
                    audioPlayer?.playTrack(targetTrack, newQueue)
                },
                onClose = { showQueueSheet = false }
            )
        }

        // Sleep Timer Dialog
        if (showSleepTimerDialog && audioPlayer != null) {
            ElovaireSleepTimerDialog(
                audioPlayer = audioPlayer,
                onDismiss = { showSleepTimerDialog = false }
            )
        }

        // Add to Playlist Dialog
        if (showAddToPlaylistDialog && playlistRepository != null) {
            ElovaireAddToPlaylistDialog(
                track = track,
                playlistRepository = playlistRepository,
                onDismiss = { showAddToPlaylistDialog = false }
            )
        }
    }
}

/**
 * 4-Layer Ambient Blurred Background.
 */
@Composable
private fun ElovaireAmbientBackdrop(artworkUri: String?) {
    val context = LocalContext.current
    val blurEffect = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RenderEffect.createBlurEffect(120f, 120f, Shader.TileMode.CLAMP).asComposeRenderEffect()
        } else null
    }

    Box(modifier = Modifier.fillMaxSize().background(ElovaireColors.Night)) {
        if (!artworkUri.isNullOrBlank()) {
            val req = remember(artworkUri) {
                ImageRequest.Builder(context)
                    .data(artworkUri)
                    .size(450, 450)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()
            }

            AsyncImage(
                model = req,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 0.32f
                        if (blurEffect != null) renderEffect = blurEffect
                    }
            )
        }

        // Dark Translucent Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ElovaireColors.Night.copy(alpha = 0.72f))
        )

        // Radial Vignette
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, ElovaireColors.Night.copy(alpha = 0.88f)),
                    center = center,
                    radius = size.maxDimension / 1.3f
                )
            )
        }
    }
}

@Composable
private fun NowPlayingHeader(
    playingFrom: String,
    onDismiss: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onShare: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(42.dp).elovairePressScale(pressedScale = 0.88f)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Dismiss",
                tint = ElovaireColors.TextPrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        ) {
            Text(
                text = "PLAYING FROM",
                color = ElovaireColors.TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = playingFrom,
                color = ElovaireColors.TextPrimary,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onAddToPlaylist,
                modifier = Modifier.size(42.dp).elovairePressScale(pressedScale = 0.88f)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                    contentDescription = "Add to Playlist",
                    tint = ElovaireColors.TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            IconButton(
                onClick = onShare,
                modifier = Modifier.size(42.dp).elovairePressScale(pressedScale = 0.88f)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = ElovaireColors.TextPrimary,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}

@Composable
private fun NowPlayingArtworkHero(
    track: Track,
    isPlaying: Boolean,
    artworkSize: Dp,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit
) {
    val view = LocalView.current
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    val animatedDragX by animateFloatAsState(
        targetValue = dragOffsetX,
        animationSpec = ElovaireMotionSpecs.settleSpring(),
        label = "hero_art_drag"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPlaying) 1.0f else 0.96f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "art_playing_scale"
    )

    Box(
        modifier = Modifier
            .size(artworkSize)
            .graphicsLayer {
                translationX = animatedDragX
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(track.id) {
                detectHorizontalDragGestures(
                    onDragStart = { dragOffsetX = 0f },
                    onDragEnd = {
                        if (dragOffsetX < -100f) {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onSkipNext()
                        } else if (dragOffsetX > 100f) {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onSkipPrevious()
                        }
                        dragOffsetX = 0f
                    },
                    onDragCancel = { dragOffsetX = 0f },
                    onHorizontalDrag = { _, delta ->
                        dragOffsetX = (dragOffsetX + delta * 0.6f).coerceIn(-160f, 160f)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        ElovaireArtworkImage(
            artworkUri = track.albumArtUri,
            title = track.title,
            artist = track.artist,
            trackId = track.id,
            cornerRadius = 16.dp,
            showGlow = true,
            glowTint = ElovaireColors.AzureGlow,
            borderColor = ElovaireColors.BorderStrong,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun NowPlayingMetadataRow(
    track: Track,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onNavigateToAlbum: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                color = ElovaireColors.TextPrimary,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.basicMarquee(
                    iterations = Int.MAX_VALUE,
                    animationMode = MarqueeAnimationMode.Immediately,
                    repeatDelayMillis = 2400,
                    velocity = 26.dp
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${track.artist} · ${track.album}",
                color = ElovaireColors.TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onNavigateToAlbum
                )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Favorite Heart Button
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(44.dp).elovairePressScale(pressedScale = 0.85f)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) ElovaireColors.DestructiveRed else ElovaireColors.TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun AudioQualityBadge(track: Track) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(ElovaireRadii.pill))
            .background(ElovaireColors.Graphite)
            .border(1.dp, ElovaireColors.BorderSubtle, RoundedCornerShape(ElovaireRadii.pill))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            text = "${track.audioFormat.uppercase()} · ${track.bitrate} · 16-BIT PCM",
            color = ElovaireColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp
        )
    }
}

@Composable
private fun NowPlayingScrubber(
    progress: Float,
    currentPositionMs: Long,
    durationMs: Long,
    onSeekTo: (Long) -> Unit
) {
    val view = LocalView.current
    var isSeeking by remember { mutableStateOf(false) }
    var seekProgress by remember { mutableFloatStateOf(progress) }

    val effectiveProgress = if (isSeeking) seekProgress else progress

    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = effectiveProgress.coerceIn(0f, 1f),
            onValueChange = {
                isSeeking = true
                seekProgress = it
            },
            onValueChangeFinished = {
                val targetMs = (seekProgress * durationMs).toLong().coerceIn(0L, durationMs)
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onSeekTo(targetMs)
                isSeeking = false
            },
            colors = SliderDefaults.colors(
                thumbColor = ElovaireColors.Cloud,
                activeTrackColor = ElovaireColors.AzureAccent,
                inactiveTrackColor = ElovaireColors.BorderSubtle
            ),
            modifier = Modifier.fillMaxWidth().height(28.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val displayMs = if (isSeeking) (seekProgress * durationMs).toLong() else currentPositionMs
            Text(
                text = formatDuration(displayMs),
                color = ElovaireColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = formatDuration(durationMs),
                color = ElovaireColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun NowPlayingTransportControls(
    isPlaying: Boolean,
    isShuffle: Boolean,
    repeatMode: RepeatMode,
    heroSize: Dp,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit
) {
    val view = LocalView.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Shuffle Button
        IconButton(
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onToggleShuffle()
            },
            modifier = Modifier.size(46.dp).elovairePressScale(pressedScale = 0.88f)
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = "Shuffle",
                tint = if (isShuffle) ElovaireColors.AzureAccent else ElovaireColors.TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }

        // Previous
        IconButton(
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onSkipPrevious()
            },
            modifier = Modifier.size(50.dp).elovairePressScale(pressedScale = 0.88f)
        ) {
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = "Previous",
                tint = ElovaireColors.TextPrimary,
                modifier = Modifier.size(30.dp)
            )
        }

        // Hero Play/Pause Button
        Box(
            modifier = Modifier
                .size(heroSize)
                .clip(CircleShape)
                .background(ElovaireColors.AzureAccent)
                .elovairePressScale(pressedScale = 0.90f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onPlayPause()
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = isPlaying,
                transitionSpec = {
                    (fadeIn() + scaleIn(initialScale = 0.85f)) togetherWith
                            (fadeOut() + scaleOut(targetScale = 1.15f))
                },
                label = "now_playing_hero_icon"
            ) { playing ->
                Icon(
                    imageVector = if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (playing) "Pause" else "Play",
                    tint = ElovaireColors.Cloud,
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        // Next
        IconButton(
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onSkipNext()
            },
            modifier = Modifier.size(50.dp).elovairePressScale(pressedScale = 0.88f)
        ) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = "Next",
                tint = ElovaireColors.TextPrimary,
                modifier = Modifier.size(30.dp)
            )
        }

        // Repeat Button
        IconButton(
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onToggleRepeat()
            },
            modifier = Modifier.size(46.dp).elovairePressScale(pressedScale = 0.88f)
        ) {
            Icon(
                imageVector = when (repeatMode) {
                    RepeatMode.ONE -> Icons.Default.RepeatOne
                    else -> Icons.Default.Repeat
                },
                contentDescription = "Repeat",
                tint = if (repeatMode != RepeatMode.OFF) ElovaireColors.AzureAccent else ElovaireColors.TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun NowPlayingSecondaryActions(
    isLyricsActive: Boolean,
    onOpenLyrics: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenTimer: () -> Unit,
    onOpenEqualizer: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionPill(
            icon = Icons.Default.Lyrics,
            label = "Lyrics",
            isActive = isLyricsActive,
            onClick = onOpenLyrics
        )
        ActionPill(
            icon = Icons.AutoMirrored.Filled.QueueMusic,
            label = "Queue",
            isActive = false,
            onClick = onOpenQueue
        )
        ActionPill(
            icon = Icons.Default.Bedtime,
            label = "Timer",
            isActive = false,
            onClick = onOpenTimer
        )
        ActionPill(
            icon = Icons.Default.Equalizer,
            label = "Audio",
            isActive = false,
            onClick = onOpenEqualizer
        )
    }
}

@Composable
private fun ActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(34.dp)
            .clip(RoundedCornerShape(ElovaireRadii.pill))
            .background(if (isActive) ElovaireColors.AzureAccent else ElovaireColors.Graphite)
            .border(
                1.dp,
                if (isActive) ElovaireColors.AzureAccent else ElovaireColors.BorderSubtle,
                RoundedCornerShape(ElovaireRadii.pill)
            )
            .elovairePressScale(pressedScale = 0.90f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) ElovaireColors.Cloud else ElovaireColors.TextSecondary,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = label,
                color = if (isActive) ElovaireColors.Cloud else ElovaireColors.TextPrimary,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = (durationMs / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

private fun shareTrack(context: Context, track: Track) {
    try {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Listening to \"${track.title}\" by ${track.artist} on HyprMusic")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Track")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    } catch (_: Exception) {}
}
