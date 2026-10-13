package com.example.hyprmusic.ui.dap.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.example.hyprmusic.ui.dap.state.DapHapticFeedback
import com.example.hyprmusic.ui.dap.theme.DapColors
import com.example.hyprmusic.ui.dap.theme.DapMotion

/**
 * Slim, tactile progress scrubber designed to match the reference DAP display.
 * Expands gently on touch and supports smooth real-time seeking with light haptic ticks.
 */
@Composable
fun DapSeekBar(
    progress: Float,
    durationMs: Long,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val haptic = remember(view) { DapHapticFeedback(view) }

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    val trackHeight by animateDpAsState(
        targetValue = if (isDragging) 8.dp else 4.5.dp,
        animationSpec = DapMotion.scrubExpandSpring(),
        label = "DapSeekBarHeight"
    )

    val currentProgress = if (isDragging) dragProgress else progress.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp) // Generous touch target
            .pointerInput(durationMs) {
                detectTapGestures { offset ->
                    if (size.width > 0 && durationMs > 0) {
                        val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                        haptic.lightTap()
                        onSeekTo((fraction * durationMs).toLong())
                    }
                }
            }
            .pointerInput(durationMs) {
                detectDragGestures(
                    onDragStart = { offset ->
                        if (size.width > 0) {
                            isDragging = true
                            dragProgress = (offset.x / size.width).coerceIn(0f, 1f)
                            haptic.lightTap()
                        }
                    },
                    onDragEnd = {
                        if (durationMs > 0) {
                            onSeekTo((dragProgress * durationMs).toLong())
                            haptic.buttonClick()
                        }
                        isDragging = false
                    },
                    onDragCancel = {
                        isDragging = false
                    },
                    onDrag = { change, _ ->
                        if (size.width > 0) {
                            val newFraction = (change.position.x / size.width).coerceIn(0f, 1f)
                            if (kotlin.math.abs(newFraction - dragProgress) > 0.05f) {
                                haptic.tick()
                            }
                            dragProgress = newFraction
                        }
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        // Background Inactive Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(RoundedCornerShape(trackHeight / 2))
                .background(DapColors.ProgressTrack)
        ) {
            // Foreground Active Fill
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(currentProgress)
                    .clip(RoundedCornerShape(trackHeight / 2))
                    .background(DapColors.ProgressFill)
            )
        }
    }
}
