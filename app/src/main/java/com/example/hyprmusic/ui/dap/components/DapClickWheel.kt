package com.example.hyprmusic.ui.dap.components

import android.view.SoundEffectConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.hyprmusic.ui.dap.theme.DapTypography
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.hyprmusic.ui.dap.state.DapHapticFeedback
import com.example.hyprmusic.ui.dap.theme.DapColors
import com.example.hyprmusic.ui.dap.theme.DapMotion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Advanced Phase 2 Tactile Rotary Click-Wheel Engine.
 *
 * Simulates physical click-wheel hardware via:
 * - Continuous angular drag detection: atan2(dy, dx)
 * - Rotary velocity inertia (fling glide with decay friction)
 * - 18° step quantization with Tier 1 micro-haptics & subtle mechanical click sound
 * - Quadrant hit-testing (Menu, Prev, Next, Play/Pause) with long-press fast seek support
 * - Dynamic rotational guide arc reflecting active drag angle
 * - Recessed center button with spring-backed compression animation
 */
@Composable
fun DapClickWheel(
    isPlaying: Boolean,
    onMenuClick: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onCenterClick: () -> Unit,
    onRotateStep: (isClockwise: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onPrevLongPress: (() -> Unit)? = null,
    onNextLongPress: (() -> Unit)? = null,
    onCenterLongPress: (() -> Unit)? = null,
    enableAuditoryClick: Boolean = true,
    wheelDiameter: Dp = 270.dp
) {
    val view = LocalView.current
    val haptic = remember(view) { DapHapticFeedback(view) }
    val coroutineScope = rememberCoroutineScope()

    var lastAngle by remember { mutableFloatStateOf(0f) }
    var accumulatedDelta by remember { mutableFloatStateOf(0f) }
    val stepAngleThreshold = 18f // 20 discrete steps per complete 360° circle

    // Inertial fling velocity tracking
    var lastDragTimeMs by remember { mutableLongStateOf(0L) }
    var currentAngularVelocity by remember { mutableFloatStateOf(0f) }
    var inertiaJob by remember { mutableStateOf<Job?>(null) }

    // Visual drag tracking arc state
    var isDraggingWheel by remember { mutableStateOf(false) }
    var touchAngleDegrees by remember { mutableFloatStateOf(0f) }
    val trackingArcAlpha = remember { Animatable(0f) }

    var isCenterPressed by remember { mutableStateOf(false) }
    var pressedQuadrant by remember { mutableStateOf<String?>(null) }

    val centerScale by animateFloatAsState(
        targetValue = if (isCenterPressed) 0.93f else 1.0f,
        animationSpec = DapMotion.wheelCenterSpring(),
        label = "DapCenterScale"
    )

    fun emitStep(isClockwise: Boolean) {
        haptic.tick()
        if (enableAuditoryClick) {
            try {
                view.playSoundEffect(SoundEffectConstants.CLICK)
            } catch (_: Exception) {}
        }
        onRotateStep(isClockwise)
    }

    Box(
        modifier = modifier
            .size(wheelDiameter)
            .semantics {
                role = Role.Button
                contentDescription = "Physical-style Rotary Click Wheel"
            }
            // 1. Quadrant & Center Tap / Long Press Processing
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        inertiaJob?.cancel()
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val dx = offset.x - centerX
                        val dy = offset.y - centerY
                        val distance = sqrt(dx * dx + dy * dy)
                        val outerRadius = size.width / 2f
                        val centerRadius = outerRadius * 0.38f

                        if (distance <= centerRadius) {
                            isCenterPressed = true
                            tryAwaitRelease()
                            isCenterPressed = false
                        } else if (distance <= outerRadius) {
                            val angle = (Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 360f) % 360f
                            pressedQuadrant = when (angle) {
                                in 45f..135f -> "BOTTOM"
                                in 135f..225f -> "LEFT"
                                in 225f..315f -> "TOP"
                                else -> "RIGHT"
                            }
                            tryAwaitRelease()
                            pressedQuadrant = null
                        }
                    },
                    onTap = { offset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val dx = offset.x - centerX
                        val dy = offset.y - centerY
                        val distance = sqrt(dx * dx + dy * dy)
                        val outerRadius = size.width / 2f
                        val centerRadius = outerRadius * 0.38f

                        if (distance <= centerRadius) {
                            haptic.buttonClick()
                            onCenterClick()
                        } else if (distance <= outerRadius) {
                            val angle = (Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 360f) % 360f
                            haptic.buttonClick()
                            when (angle) {
                                in 45f..135f -> onPlayPauseClick()
                                in 135f..225f -> onPrevClick()
                                in 225f..315f -> onMenuClick()
                                else -> onNextClick()
                            }
                        }
                    },
                    onLongPress = { offset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val dx = offset.x - centerX
                        val dy = offset.y - centerY
                        val distance = sqrt(dx * dx + dy * dy)
                        val outerRadius = size.width / 2f
                        val centerRadius = outerRadius * 0.38f

                        haptic.heavyPulse()
                        if (distance <= centerRadius) {
                            onCenterLongPress?.invoke() ?: onCenterClick()
                        } else if (distance <= outerRadius) {
                            val angle = (Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 360f) % 360f
                            when (angle) {
                                in 135f..225f -> onPrevLongPress?.invoke() ?: onPrevClick()
                                in 225f..315f -> onMenuClick()
                                in 45f..135f -> onPlayPauseClick()
                                else -> onNextLongPress?.invoke() ?: onNextClick()
                            }
                        }
                    }
                )
            }
            // 2. Rotary Angular Drag Processing with Inertia Physics
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        inertiaJob?.cancel()
                        isDraggingWheel = true
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        lastAngle = (Math.toDegrees(atan2((offset.y - centerY).toDouble(), (offset.x - centerX).toDouble())).toFloat() + 360f) % 360f
                        touchAngleDegrees = lastAngle
                        accumulatedDelta = 0f
                        lastDragTimeMs = System.currentTimeMillis()
                        currentAngularVelocity = 0f
                        coroutineScope.launch { trackingArcAlpha.animateTo(0.85f, tween(120)) }
                    },
                    onDragEnd = {
                        isDraggingWheel = false
                        coroutineScope.launch { trackingArcAlpha.animateTo(0f, tween(280)) }

                        // Trigger inertial rotation if released with high angular velocity
                        val absVel = abs(currentAngularVelocity)
                        if (absVel > 140f) {
                            inertiaJob = coroutineScope.launch {
                                var vel = currentAngularVelocity
                                val isClockwise = vel > 0
                                var inertiaAccumulator = 0f
                                while (isActive && abs(vel) > 30f) {
                                    val dtSeconds = 0.016f // approx 60fps step
                                    val frameDelta = vel * dtSeconds
                                    inertiaAccumulator += frameDelta
                                    if (abs(inertiaAccumulator) >= stepAngleThreshold) {
                                        emitStep(isClockwise)
                                        inertiaAccumulator %= stepAngleThreshold
                                    }
                                    vel *= 0.90f // 10% deceleration per frame
                                    delay(16)
                                }
                            }
                        }
                    },
                    onDragCancel = {
                        isDraggingWheel = false
                        coroutineScope.launch { trackingArcAlpha.animateTo(0f, tween(200)) }
                    },
                    onDrag = { change, _ ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val currentAngle = (Math.toDegrees(atan2((change.position.y - centerY).toDouble(), (change.position.x - centerX).toDouble())).toFloat() + 360f) % 360f
                        touchAngleDegrees = currentAngle

                        var delta = currentAngle - lastAngle
                        if (delta > 180f) delta -= 360f
                        if (delta < -180f) delta += 360f

                        val now = System.currentTimeMillis()
                        val dtMs = (now - lastDragTimeMs).coerceAtLeast(1L)
                        currentAngularVelocity = (delta / (dtMs / 1000f)) // deg/sec
                        lastDragTimeMs = now

                        accumulatedDelta += delta
                        lastAngle = currentAngle

                        if (abs(accumulatedDelta) >= stepAngleThreshold) {
                            val isClockwise = accumulatedDelta > 0
                            emitStep(isClockwise)
                            accumulatedDelta %= stepAngleThreshold
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Base Wheel Geometry Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.width / 2f
            val centerRadius = outerRadius * 0.38f

            // Outer Wheel Disc with subtle radial depth gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DapColors.WheelSurface.copy(alpha = 0.95f),
                        DapColors.WheelSurface,
                        Color(0xFF0F1217)
                    ),
                    center = center,
                    radius = outerRadius
                ),
                radius = outerRadius,
                center = center
            )

            // Outer Bevel Highlight Border
            drawCircle(
                color = DapColors.WheelBorder,
                radius = outerRadius,
                center = center,
                style = Stroke(width = 1.6f)
            )

            // Mid Hairline Concentric Guide Ring
            val midRadius = (outerRadius + centerRadius) / 2f
            drawCircle(
                color = DapColors.WheelBorder.copy(alpha = 0.45f),
                radius = midRadius,
                center = center,
                style = Stroke(
                    width = 1.0f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )
            )

            // Dynamic Rotational Drag Indicator Arc (Callout 19 style)
            if (trackingArcAlpha.value > 0.01f) {
                val arcRadius = midRadius
                val sweep = 42f
                val start = touchAngleDegrees - (sweep / 2f)
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(
                            DapColors.AccentIceBlue.copy(alpha = 0f),
                            DapColors.AccentIceBlue.copy(alpha = trackingArcAlpha.value),
                            DapColors.AccentIceBlue.copy(alpha = 0f)
                        ),
                        center = center
                    ),
                    startAngle = start,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                    size = Size(arcRadius * 2, arcRadius * 2),
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                )
            }

            // Center Recessed Circular Button with Depth Highlight
            val animatedRadius = centerRadius * centerScale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (isCenterPressed) DapColors.WheelPressed else DapColors.WheelCenter,
                        if (isCenterPressed) Color(0xFF1E232C) else Color(0xFF14171E)
                    ),
                    center = center,
                    radius = animatedRadius
                ),
                radius = animatedRadius,
                center = center
            )
            drawCircle(
                color = DapColors.WheelCenterBorder,
                radius = animatedRadius,
                center = center,
                style = Stroke(width = 1.4f)
            )
        }

        // Top Quadrant — Iconic iPod MENU Text
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "MENU",
                style = DapTypography.WheelLabel,
                color = if (pressedQuadrant == "TOP") DapColors.AccentIceBlue else DapColors.WheelIcon,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 18.dp)
            )
        }

        // Left Quadrant — Previous Track Icon
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Previous Track",
                tint = if (pressedQuadrant == "LEFT") DapColors.AccentIceBlue else DapColors.WheelIcon,
                modifier = Modifier
                    .size(46.dp)
                    .padding(start = 16.dp)
            )
        }

        // Right Quadrant — Next Track Icon
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next Track",
                tint = if (pressedQuadrant == "RIGHT") DapColors.AccentIceBlue else DapColors.WheelIcon,
                modifier = Modifier
                    .size(46.dp)
                    .padding(end = 16.dp)
            )
        }

        // Bottom Quadrant — Play / Pause Icon
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = if (pressedQuadrant == "BOTTOM") DapColors.AccentIceBlue else DapColors.WheelIcon,
                modifier = Modifier
                    .size(46.dp)
                    .padding(bottom = 16.dp)
            )
        }
    }
}
