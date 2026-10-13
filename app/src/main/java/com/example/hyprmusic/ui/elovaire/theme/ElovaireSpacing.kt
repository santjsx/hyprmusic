package com.example.hyprmusic.ui.elovaire.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
object ElovaireSpacing {
    // Screen Layout Spacing
    val screenHorizontal: Dp = 18.dp
    val sectionVertical: Dp = 20.dp
    val itemVertical: Dp = 6.dp
    val textGap: Dp = 3.dp

    // Top Chrome
    val topBarHeight: Dp = 56.dp
    val topBarVerticalPad: Dp = 6.dp

    // Bottom Navigation Dock
    val bottomDockHeight: Dp = 68.dp
    val bottomDockOuterPad: Dp = 10.dp
    val bottomDockInnerPad: Dp = 6.dp

    // Mini Player
    val miniPlayerHeight: Dp = 64.dp
    val miniPlayerArtworkSize: Dp = 46.dp

    // Lists & Grids
    val listContentBottomPad: Dp = 150.dp  // Reserved clearance for floating MiniPlayer + Dock
    val gridSpacing: Dp = 14.dp
}
