package com.example.hyprmusic.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.hyprmusic.core.cloud.telegram.TelegramArtworkResolver
import kotlin.math.abs

/**
 * 4-Tier High Reliability Artwork Image Component:
 * Tier 1: TPMC Gateway Direct Artwork
 * Tier 2: Real-time Cloud Metadata (iTunes 600x600 High-Res)
 * Tier 3: Local ID3 Tag Extraction
 * Tier 4: Arch Hyprland Procedural Vinyl Identicon (Zero Broken States Guarantee)
 */
@Composable
fun HyprArtworkImage(
    artworkUri: String?,
    title: String,
    artist: String,
    modifier: Modifier = Modifier,
    trackId: String = "",
    isAlbum: Boolean = false,
    shape: Shape = RoundedCornerShape(8.dp),
    contentScale: ContentScale = ContentScale.Crop,
    borderColor: Color? = null
) {
    val context = LocalContext.current
    var effectiveUri by remember(artworkUri, title, artist) { mutableStateOf(artworkUri) }

    // Proactively resolve high-res cover if URI is absent or a TPMC endpoint
    LaunchedEffect(artworkUri, title, artist, trackId) {
        if (effectiveUri.isNullOrBlank() || effectiveUri?.contains("/api/artwork/") == true) {
            val resolved = if (isAlbum) {
                TelegramArtworkResolver.resolveAlbumArtwork(context, title, artist, artworkUri)
            } else {
                TelegramArtworkResolver.resolveTrackArtwork(context, trackId, title, artist, artworkUri)
            }
            if (!resolved.isNullOrBlank()) {
                effectiveUri = resolved
            }
        }
    }

    val request = remember(effectiveUri) {
        if (!effectiveUri.isNullOrBlank()) {
            ImageRequest.Builder(context)
                .data(effectiveUri)
                .crossfade(true)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build()
        } else null
    }

    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (borderColor != null) Modifier.border(1.dp, borderColor, shape)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (request != null) {
            SubcomposeAsyncImage(
                model = request,
                contentDescription = "$title - $artist artwork",
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale
            ) {
                val state = painter.state
                when (state) {
                    is AsyncImagePainter.State.Success -> {
                        SubcomposeAsyncImageContent()
                    }
                    else -> {
                        // While loading or on network error, render procedural vinyl identicon
                        HyprProceduralVinylArt(
                            title = title,
                            artist = artist,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        } else {
            // Tier 4 Procedural Vinyl Identicon (Offline / No Cover Available)
            HyprProceduralVinylArt(
                title = title,
                artist = artist,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Procedural Arch Hyprland Vinyl Identicon:
 * Deterministically generates an authentic, vibrant vinyl disc design with initials.
 * Guarantees zero blank, grey, or broken visuals.
 */
@Composable
fun HyprProceduralVinylArt(
    title: String,
    artist: String,
    modifier: Modifier = Modifier
) {
    val (colorA, colorB) = remember(title, artist) {
        getDeterministicPalette(title, artist)
    }

    val initials = remember(title, artist) {
        val t = title.trim().firstOrNull { it.isLetterOrDigit() }?.uppercaseChar() ?: 'H'
        val a = artist.trim().firstOrNull { it.isLetterOrDigit() }?.uppercaseChar() ?: 'M'
        "$t$a"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(colorA, colorB),
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Grooved vinyl record lines overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxR = size.minDimension / 2f

            // Outer dark vinyl ring
            drawCircle(
                color = Color.Black.copy(alpha = 0.45f),
                radius = maxR * 0.95f,
                center = center
            )

            // Concentric vinyl grooves
            val grooves = 3
            for (i in 1..grooves) {
                val radius = maxR * (0.35f + (i * 0.18f))
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.2f)
                )
            }

            // Center spindle hole
            drawCircle(
                color = Color.Black.copy(alpha = 0.75f),
                radius = maxR * 0.28f,
                center = center
            )
            drawCircle(
                color = colorA.copy(alpha = 0.9f),
                radius = maxR * 0.10f,
                center = center
            )
        }

        // Monogram Monospace Initials
        Text(
            text = initials,
            color = Color.White.copy(alpha = 0.95f),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            letterSpacing = 1.sp
        )
    }
}

/**
 * Curated Hyprland rice dual-tone gradients.
 */
private val HYPR_PALETTES = listOf(
    Color(0xFF1E1E2E) to Color(0xFF89B4FA), // Catppuccin Blue
    Color(0xFF1A1B26) to Color(0xFF7AA2F7), // Tokyo Night Storm
    Color(0xFF282828) to Color(0xFFFABD2F), // Gruvbox Gold
    Color(0xFF1E1E2E) to Color(0xFFF38BA8), // Catppuccin Flamingo
    Color(0xFF1A1B26) to Color(0xFFBB9AF7), // Tokyo Night Purple
    Color(0xFF24283B) to Color(0xFF7DCFFF), // Tokyo Night Cyan
    Color(0xFF181825) to Color(0xFFA6E3A1), // Catppuccin Green
    Color(0xFF2E3440) to Color(0xFF88C0D0), // Nord Frost
    Color(0xFF1E1E2E) to Color(0xFFCBA6F7), // Catppuccin Mauve
    Color(0xFF282C34) to Color(0xFFE5C07B)  // One Dark Amber
)

private fun getDeterministicPalette(title: String, artist: String): Pair<Color, Color> {
    val hash = abs(title.hashCode() * 31 + artist.hashCode())
    return HYPR_PALETTES[hash % HYPR_PALETTES.size]
}
