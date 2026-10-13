package com.example.hyprmusic.ui.elovaire.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import com.example.hyprmusic.ui.components.HyprArtworkImage
import com.example.hyprmusic.ui.elovaire.theme.ElovaireColors
import com.example.hyprmusic.ui.elovaire.theme.ElovaireRadii

/**
 * Elovaire Artwork Presentation Component.
 * Encapsulates reliable Coil resolution, deterministic fallback identicon,
 * calibrated corner curves, and optional ambient drop glow.
 */
@Composable
fun ElovaireArtworkImage(
    artworkUri: String?,
    title: String,
    artist: String,
    modifier: Modifier = Modifier,
    trackId: String = "",
    isAlbum: Boolean = false,
    cornerRadius: Dp = ElovaireRadii.artwork,
    showGlow: Boolean = false,
    glowTint: Color? = null,
    borderColor: Color? = ElovaireColors.BorderSubtle,
    contentScale: ContentScale = ContentScale.Crop
) {
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (showGlow) {
            val effectiveTint = glowTint ?: ElovaireColors.AzureGlow
            ArtworkPaletteShadow(
                cornerRadius = cornerRadius,
                tint = effectiveTint,
                blurRadius = cornerRadius * 1.8f
            )
        }

        HyprArtworkImage(
            artworkUri = artworkUri,
            title = title,
            artist = artist,
            trackId = trackId,
            isAlbum = isAlbum,
            shape = shape,
            borderColor = borderColor,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize()
        )
    }
}
