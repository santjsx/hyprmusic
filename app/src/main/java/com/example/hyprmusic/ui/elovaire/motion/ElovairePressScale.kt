package com.example.hyprmusic.ui.elovaire.motion

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Tactile spring press scale modifier.
 * Scales down smoothly when pressed and springs back on release.
 * The transformation is draw-only via graphicsLayer, so layout and hit-targets remain unshifted.
 */
fun Modifier.elovairePressScale(
    enabled: Boolean = true,
    pressedScale: Float = 0.92f,
    interactionSource: MutableInteractionSource? = null,
    label: String = "press_scale"
): Modifier = composed {
    if (!enabled) return@composed this

    val resolvedSource = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by resolvedSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1.0f,
        animationSpec = ElovaireMotionSpecs.pressSpring(),
        label = label
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
