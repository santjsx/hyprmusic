package com.example.hyprmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hyprmusic.core.theming.GridLayoutStyle
import com.example.hyprmusic.core.theming.HyprFontType
import com.example.hyprmusic.core.theming.HyprTheme
import com.example.hyprmusic.core.theming.HyprThemeConfig
import com.example.hyprmusic.core.theming.IconPackType
import com.example.hyprmusic.core.theming.ProgressBarStyle
import com.example.hyprmusic.core.theming.ThemeManager
import com.example.hyprmusic.core.theming.ThemePreset
import com.example.hyprmusic.core.theming.hyprAnimatedGlow
import com.example.hyprmusic.core.theming.hyprTile
import com.example.hyprmusic.ui.components.AdaptivePlayButton
import com.example.hyprmusic.ui.components.AdaptiveProgressBar
import com.example.hyprmusic.ui.components.AdaptiveSkipButton

@Composable
fun ThemerScreen(
    theme: HyprThemeConfig,
    modifier: Modifier = Modifier
) {
    val spec = HyprTheme.spec
    var previewProgress by remember { mutableFloatStateOf(0.62f) }
    var isPreviewPlaying by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = theme.windowGapsDp.dp),
        verticalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
    ) {
        // Section: Live Interactive Ricing Preview Tile
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE RICE PREVIEW",
                        color = theme.textSecondaryColor,
                        fontSize = 12.sp,
                        fontFamily = spec.fontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "hyprland.conf",
                        color = theme.accentColor,
                        fontSize = 11.sp,
                        fontFamily = spec.fontFamily
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .hyprTile(theme = theme, isActive = true)
                        .hyprAnimatedGlow(theme = theme)
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Active Window Tile",
                                color = theme.textPrimaryColor,
                                fontFamily = spec.fontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(theme.surfaceVariantColor)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "GLOW_ENABLED",
                                    color = theme.accentColor,
                                    fontSize = 10.sp,
                                    fontFamily = spec.fontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Gaps: ${theme.windowGapsDp}px • Radius: ${theme.borderRadiusDp}px • Border: ${theme.borderThicknessDp}px • Font: ${spec.fontType.name}",
                            color = theme.textSecondaryColor,
                            fontSize = 11.5.sp,
                            fontFamily = spec.fontFamily
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Interactive Adaptive Progress Bar in Live Preview
                        AdaptiveProgressBar(
                            progressPercent = previewProgress,
                            elapsed = "02:14",
                            total = "03:45",
                            onSeekToPercent = { previewProgress = it }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Interactive Adaptive Controls in Live Preview
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AdaptiveSkipButton(
                                isNext = false,
                                onClick = {},
                                size = 36.dp
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            AdaptivePlayButton(
                                isPlaying = isPreviewPlaying,
                                onClick = { isPreviewPlaying = !isPreviewPlaying },
                                size = 46.dp
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            AdaptiveSkipButton(
                                isNext = true,
                                onClick = {},
                                size = 36.dp
                            )
                        }
                    }
                }
            }
        }

        // Section: Theme Presets Selector
        item {
            Column {
                Text(
                    text = "PALETTE PRESETS",
                    color = theme.textSecondaryColor,
                    fontSize = 12.sp,
                    fontFamily = spec.fontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = ThemePreset.entries,
                        key = { it.name }
                    ) { preset ->
                        val isSelected = theme.preset == preset
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(10).dp))
                                .background(if (isSelected) theme.accentColor.copy(alpha = 0.25f) else theme.surfaceColor)
                                .clickable { ThemeManager.setPreset(preset) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = theme.accentColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = preset.displayName,
                                    color = if (isSelected) theme.accentColor else theme.textPrimaryColor,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = spec.fontFamily
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Icon Pack Architecture
        item {
            Column {
                Text(
                    text = "ICON PACK ARCHITECTURE",
                    color = theme.textSecondaryColor,
                    fontSize = 12.sp,
                    fontFamily = spec.fontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = IconPackType.entries,
                        key = { it.name }
                    ) { pack ->
                        val isSelected = spec.iconPack == pack
                        val label = when (pack) {
                            IconPackType.PHOSPHOR_LINE -> "Phosphor (Vector)"
                            IconPackType.NERD_FONTS_ASCII -> "Nerd Font (ASCII)"
                            IconPackType.ARCH_OUTLINE -> "Arch (Geometric)"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(10).dp))
                                .background(if (isSelected) theme.accentColor.copy(alpha = 0.25f) else theme.surfaceColor)
                                .clickable { ThemeManager.updateIconPack(pack) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = theme.accentColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = label,
                                    color = if (isSelected) theme.accentColor else theme.textPrimaryColor,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = spec.fontFamily
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Progress Bar Visual Engine
        item {
            Column {
                Text(
                    text = "PROGRESS BAR ENGINE",
                    color = theme.textSecondaryColor,
                    fontSize = 12.sp,
                    fontFamily = spec.fontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = ProgressBarStyle.entries,
                        key = { it.name }
                    ) { style ->
                        val isSelected = spec.progressStyle == style
                        val label = when (style) {
                            ProgressBarStyle.BLOCKS_SHELL -> "Blocks Shell [███░]"
                            ProgressBarStyle.MINIMAL_WAYBAR -> "Minimal Waybar"
                            ProgressBarStyle.DYNAMIC_NEON -> "Dynamic Neon"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(10).dp))
                                .background(if (isSelected) theme.accentColor.copy(alpha = 0.25f) else theme.surfaceColor)
                                .clickable { ThemeManager.updateProgressStyle(style) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = theme.accentColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = label,
                                    color = if (isSelected) theme.accentColor else theme.textPrimaryColor,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = spec.fontFamily
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Home Grid Layout Architecture
        item {
            Column {
                Text(
                    text = "HOME GRID LAYOUT ARCHITECTURE",
                    color = theme.textSecondaryColor,
                    fontSize = 12.sp,
                    fontFamily = spec.fontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = GridLayoutStyle.entries,
                        key = { it.name }
                    ) { layout ->
                        val isSelected = spec.gridStyle == layout
                        val label = when (layout) {
                            GridLayoutStyle.ASYMMETRIC_TILES -> "Asymmetric Bento"
                            GridLayoutStyle.TIGHT_TERMINAL_ROWS -> "Tight Terminal CLI"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(10).dp))
                                .background(if (isSelected) theme.accentColor.copy(alpha = 0.25f) else theme.surfaceColor)
                                .clickable { ThemeManager.updateGridStyle(layout) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = theme.accentColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = label,
                                    color = if (isSelected) theme.accentColor else theme.textPrimaryColor,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = spec.fontFamily
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Typography Engine
        item {
            Column {
                Text(
                    text = "TYPOGRAPHY ENGINE",
                    color = theme.textSecondaryColor,
                    fontSize = 12.sp,
                    fontFamily = spec.fontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = HyprFontType.entries,
                        key = { it.name }
                    ) { fontType ->
                        val isSelected = spec.fontType == fontType
                        val label = when (fontType) {
                            HyprFontType.JETBRAINS_MONO -> "JetBrains Mono"
                            HyprFontType.IBM_PLEX_MONO -> "IBM Plex Mono"
                            HyprFontType.SYSTEM_MONOSPACE -> "System Mono"
                            HyprFontType.MINIMAL_SANS -> "Minimal Sans"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(10).dp))
                                .background(if (isSelected) theme.accentColor.copy(alpha = 0.25f) else theme.surfaceColor)
                                .clickable { ThemeManager.updateFontType(fontType) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = theme.accentColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = label,
                                    color = if (isSelected) theme.accentColor else theme.textPrimaryColor,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = spec.fontFamily
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Sliders Configuration Tile
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .hyprTile(theme = theme)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "WINDOW GEOMETRY & COMPOSITOR",
                        color = theme.textPrimaryColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    // Window Gaps
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Window Gaps",
                                color = theme.textSecondaryColor,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${theme.windowGapsDp} dp",
                                color = theme.accentColor,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = theme.windowGapsDp.toFloat(),
                            onValueChange = { ThemeManager.updateGaps(it.toInt()) },
                            valueRange = 0f..24f,
                            steps = 24,
                            colors = SliderDefaults.colors(
                                thumbColor = theme.accentColor,
                                activeTrackColor = theme.accentColor,
                                inactiveTrackColor = theme.surfaceVariantColor
                            )
                        )
                    }

                    // Border Radius
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Squircle Border Radius",
                                color = theme.textSecondaryColor,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${theme.borderRadiusDp} dp",
                                color = theme.accentColor,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = theme.borderRadiusDp.toFloat(),
                            onValueChange = { ThemeManager.updateRadius(it.toInt()) },
                            valueRange = 0f..28f,
                            steps = 28,
                            colors = SliderDefaults.colors(
                                thumbColor = theme.accentColor,
                                activeTrackColor = theme.accentColor,
                                inactiveTrackColor = theme.surfaceVariantColor
                            )
                        )
                    }

                    // Border Thickness
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Border Thickness",
                                color = theme.textSecondaryColor,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${theme.borderThicknessDp} dp",
                                color = theme.accentColor,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = theme.borderThicknessDp.toFloat(),
                            onValueChange = { ThemeManager.updateBorderThickness(it.toInt()) },
                            valueRange = 1f..6f,
                            steps = 5,
                            colors = SliderDefaults.colors(
                                thumbColor = theme.accentColor,
                                activeTrackColor = theme.accentColor,
                                inactiveTrackColor = theme.surfaceVariantColor
                            )
                        )
                    }

                    // Blur Radius
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Frosted Glass Blur (Android 12+)",
                                color = theme.textSecondaryColor,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${theme.blurRadiusDp} dp",
                                color = theme.accentColor,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = theme.blurRadiusDp.toFloat(),
                            onValueChange = { ThemeManager.updateBlur(it.toInt()) },
                            valueRange = 0f..40f,
                            steps = 40,
                            colors = SliderDefaults.colors(
                                thumbColor = theme.accentColor,
                                activeTrackColor = theme.accentColor,
                                inactiveTrackColor = theme.surfaceVariantColor
                            )
                        )
                    }
                }
            }
        }

        // Section: OLED & Reset Actions
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .hyprTile(theme = theme)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "OLED Pure Black Mode",
                            color = theme.textPrimaryColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Turns background #000000 to save battery",
                            color = theme.textSecondaryColor,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = theme.isOledMode,
                        onCheckedChange = { ThemeManager.toggleOled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = theme.accentColor,
                            checkedTrackColor = theme.accentColor.copy(alpha = 0.4f),
                            uncheckedThumbColor = theme.textSecondaryColor,
                            uncheckedTrackColor = theme.surfaceVariantColor
                        )
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(90.dp)) }
    }
}
