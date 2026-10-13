package com.example.hyprmusic.ui.dap.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.IntOffset

/**
 * Emil Kowalski Level 4 Motion Framework.
 * Breaks interactions into micro-durations and spring physics for a tactile feel.
 */
@Immutable
object DapMotion {
    // Easing curves
    val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
    val EaseInOutCubic = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)

    // Tactile button press & release spring (Level 4: instant tactile compression, gentle return)
    fun <T> buttonPressSpring(): AnimationSpec<T> = spring(
        dampingRatio = 0.72f,
        stiffness = 650f
    )

    // Center click-wheel compression spring
    fun <T> wheelCenterSpring(): AnimationSpec<T> = spring(
        dampingRatio = 0.68f,
        stiffness = 580f
    )

    // Icon state morphing spring (Favorite star pop, play/pause swap)
    fun <T> iconMorphSpring(): AnimationSpec<T> = spring(
        dampingRatio = 0.60f,
        stiffness = Spring.StiffnessMediumLow
    )

    // Seekbar expansion on drag (3dp -> 6dp smooth expansion)
    fun <T> scrubExpandSpring(): AnimationSpec<T> = spring(
        dampingRatio = 0.85f,
        stiffness = Spring.StiffnessMedium
    )

    // Virtual LCD Screen drill-down transition (fast entering slide, zero latency)
    fun lcdSlideEnter(): FiniteAnimationSpec<IntOffset> = tween(
        durationMillis = 240,
        easing = EaseOutQuart
    )

    fun lcdSlideExit(): FiniteAnimationSpec<IntOffset> = tween(
        durationMillis = 180,
        easing = EaseInOutCubic
    )
}
