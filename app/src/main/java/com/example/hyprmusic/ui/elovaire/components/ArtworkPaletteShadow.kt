package com.example.hyprmusic.ui.elovaire.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Renders an atmospheric, color-matched ambient drop shadow behind album artwork.
 * Uses hardware-accelerated ambientShadowColor and spotShadowColor with calibrated alpha.
 */
@Composable
fun BoxScope.ArtworkPaletteShadow(
    cornerRadius: Dp,
    tint: Color?,
    modifier: Modifier = Modifier,
    blurRadius: Dp = 18.dp,
    verticalOffset: Dp = 6.dp,
    fillFraction: Float = 0.88f
) {
    if (tint == null || tint == Color.Transparent) return

    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val shadowElevation = with(LocalDensity.current) { (blurRadius * 0.42f).toPx() }

    Box(
        modifier = modifier
            .align(Alignment.Center)
            .fillMaxSize(fillFraction)
            .offset(y = verticalOffset)
            .graphicsLayer {
                this.shadowElevation = shadowElevation
                this.shape = shape
                clip = false
                ambientShadowColor = tint.copy(alpha = 0.46f)
                spotShadowColor = tint.copy(alpha = 0.46f)
            }
            .background(tint.copy(alpha = 0.14f), shape)
    )
}
