package com.example.hyprmusic.ui.elovaire.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
object ElovaireRadii {
    val pill: Dp = 999.dp        // Action pills, search bar, scrub thumb, playback hero
    val dock: Dp = 24.dp         // Detached floating bottom navigation dock
    val card: Dp = 14.dp         // Grouped modules, album cards, dialog sheets
    val tile: Dp = 12.dp         // Individual track list item touch targets
    val artwork: Dp = 10.dp      // Full album cover presentation in headers & lists
    val artworkSmall: Dp = 6.dp  // Compact list & mini-player artwork thumbnails
    val button: Dp = 16.dp       // Standard action buttons
    val dialog: Dp = 20.dp       // Modal popups and confirmation dialogs
    val sheet: Dp = 28.dp        // Bottom sheet top corners
}

fun elovaireShapes(): Shapes {
    return Shapes(
        extraSmall = RoundedCornerShape(ElovaireRadii.artworkSmall),
        small = RoundedCornerShape(ElovaireRadii.tile),
        medium = RoundedCornerShape(ElovaireRadii.card),
        large = RoundedCornerShape(ElovaireRadii.dialog),
        extraLarge = RoundedCornerShape(ElovaireRadii.dock),
    )
}
