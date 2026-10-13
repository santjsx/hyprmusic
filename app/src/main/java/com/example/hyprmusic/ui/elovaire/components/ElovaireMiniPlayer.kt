package com.example.hyprmusic.ui.elovaire.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hyprmusic.core.model.PlaybackState
import com.example.hyprmusic.ui.components.MiniEqualizerBars
import com.example.hyprmusic.ui.elovaire.motion.ElovaireMotionSpecs
import com.example.hyprmusic.ui.elovaire.motion.elovairePressScale
import com.example.hyprmusic.ui.elovaire.theme.ElovaireColors
import com.example.hyprmusic.ui.elovaire.theme.ElovaireRadii
import com.example.hyprmusic.ui.elovaire.theme.ElovaireSpacing

/**
 * Floating Ambient Elovaire Mini-Player.
 * Features:
 * - 64dp floating glass capsule with subtle 1px border
 * - 44dp album artwork with dynamic drop shadow
 * - Infinite auto-marquee title and clean metadata subtitle
 * - Circular Canvas progress ring around play button
 * - Horizontal swipe gesture skip actions with spring settle
 */
@Composable
fun ElovaireMiniPlayer(
    playbackState: PlaybackState,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack ?: return
    val view = LocalView.current

    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    val animatedDragX by animateFloatAsState(
        targetValue = dragOffsetX,
        animationSpec = ElovaireMotionSpecs.settleSpring(),
        label = "mini_player_drag"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ElovaireSpacing.screenHorizontal, vertical = 2.dp)
            .graphicsLayer {
                translationX = animatedDragX
                rotationZ = animatedDragX * 0.015f
            }
            .clip(RoundedCornerShape(ElovaireRadii.card))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ElovaireColors.Carbon.copy(alpha = 0.96f),
                        ElovaireColors.Carbon.copy(alpha = 0.99f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = if (playbackState.isPlaying) ElovaireColors.BorderStrong else ElovaireColors.BorderSubtle,
                shape = RoundedCornerShape(ElovaireRadii.card)
            )
            .pointerInput(track.id) {
                detectHorizontalDragGestures(
                    onDragStart = { dragOffsetX = 0f },
                    onDragEnd = {
                        if (dragOffsetX < -80f) {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onSkipNext()
                        } else if (dragOffsetX > 80f) {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onSkipPrevious()
                        }
                        dragOffsetX = 0f
                    },
                    onDragCancel = { dragOffsetX = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffsetX = (dragOffsetX + dragAmount * 0.65f).coerceIn(-140f, 140f)
                    }
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 44dp Artwork Thumbnail with Palette Shadow
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(ElovaireRadii.artworkSmall))
            ) {
                ElovaireArtworkImage(
                    artworkUri = track.albumArtUri,
                    title = track.title,
                    artist = track.artist,
                    trackId = track.id,
                    cornerRadius = ElovaireRadii.artworkSmall,
                    showGlow = playbackState.isPlaying,
                    glowTint = ElovaireColors.AzureGlow,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title, Subtitle, and Equalizer indicator
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = track.title,
                        color = ElovaireColors.TextPrimary,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .basicMarquee(
                                iterations = Int.MAX_VALUE,
                                animationMode = MarqueeAnimationMode.Immediately,
                                repeatDelayMillis = 2400,
                                initialDelayMillis = 2000,
                                velocity = 26.dp
                            )
                    )

                    if (playbackState.isPlaying) {
                        MiniEqualizerBars(
                            theme = com.example.hyprmusic.core.theming.HyprThemeConfig(),
                            isPlaying = true,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${track.artist} · ${track.audioFormat}",
                    color = ElovaireColors.TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Circular Canvas Progress Play/Pause Button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .elovairePressScale(pressedScale = 0.88f)
                    .clip(CircleShape)
                    .background(ElovaireColors.Graphite)
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
                // Circular Progress Arc
                Canvas(modifier = Modifier.matchParentSize()) {
                    val strokeW = 2.4.dp.toPx()
                    val inset = strokeW / 2f + 1.5.dp.toPx()
                    val arcSize = Size(size.width - inset * 2, size.height - inset * 2)

                    // Track Background Arc
                    drawArc(
                        color = ElovaireColors.BorderSubtle,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = arcSize,
                        style = Stroke(width = strokeW)
                    )

                    // Active Progress Arc
                    val progress = playbackState.progress.coerceIn(0f, 1f)
                    drawArc(
                        color = ElovaireColors.AzureAccent,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = arcSize,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )
                }

                // Play / Pause Icon
                AnimatedContent(
                    targetState = playbackState.isPlaying,
                    transitionSpec = {
                        (fadeIn() + scaleIn(initialScale = 0.85f)) togetherWith
                                (fadeOut() + scaleOut(targetScale = 1.15f))
                    },
                    label = "mini_player_play_pause"
                ) { isPlaying ->
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = ElovaireColors.TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Skip Next Button
            IconButton(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onSkipNext()
                },
                modifier = Modifier
                    .size(38.dp)
                    .elovairePressScale(pressedScale = 0.88f)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Next",
                    tint = ElovaireColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
