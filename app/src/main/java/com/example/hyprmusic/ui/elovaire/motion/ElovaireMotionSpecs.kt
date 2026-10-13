package com.example.hyprmusic.ui.elovaire.motion

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable

@Immutable
object ElovaireMotionDuration {
    const val Instant: Int = 120
    const val Quick: Int = 200
    const val Standard: Int = 300
    const val Emphasized: Int = 420
    const val Long: Int = 600
}

@Immutable
object ElovaireEasing {
    val RefinedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    val RefinedAccelerate = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
    val Standard = FastOutSlowInEasing
    val Decelerate = LinearOutSlowInEasing
}

@Immutable
object ElovaireMotionSpecs {
    // Tactile press & release spring: highly responsive with natural bounce
    fun <T> pressSpring(): AnimationSpec<T> = spring(
        dampingRatio = 0.82f,
        stiffness = 560f
    )

    // Smooth settling spring for drag gesture snaps and transitions
    fun <T> settleSpring(): AnimationSpec<T> = spring(
        dampingRatio = 0.86f,
        stiffness = Spring.StiffnessMediumLow
    )

    // Screen and overlay transition specs
    fun <T> overlayEnter(): FiniteAnimationSpec<T> = tween(
        durationMillis = ElovaireMotionDuration.Standard,
        easing = ElovaireEasing.RefinedDecelerate
    )

    fun <T> overlayExit(): FiniteAnimationSpec<T> = tween(
        durationMillis = ElovaireMotionDuration.Quick,
        easing = ElovaireEasing.RefinedAccelerate
    )
}
