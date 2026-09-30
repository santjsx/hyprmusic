package com.example.hyprmusic.core.theming

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.composed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ThemePreset(val displayName: String) {
    CATPPUCCIN_MOCHA("Catppuccin Mocha"),
    TOKYO_NIGHT("Tokyo Night"),
    NORDIC_FROST("Nordic Frost"),
    GRUVBOX_DARK("Gruvbox Dark"),
    OLED_CYBERPUNK("OLED Cyberpunk")
}

data class HyprThemeConfig(
    val preset: ThemePreset = ThemePreset.CATPPUCCIN_MOCHA,
    val windowGapsDp: Int = 8,
    val borderRadiusDp: Int = 14,
    val borderThicknessDp: Int = 2,
    val blurRadiusDp: Int = 18,
    val activeBorderGradient: List<Color> = listOf(Color(0xFFCBA6F7), Color(0xFF89B4FA)),
    val inactiveBorderColor: Color = Color(0xFF313244),
    val backgroundColor: Color = Color(0xFF1E1E2E),
    val surfaceColor: Color = Color(0xDD181825),
    val surfaceVariantColor: Color = Color(0xFF11111B),
    val textPrimaryColor: Color = Color(0xFFCDD6F4),
    val textSecondaryColor: Color = Color(0xFFA6ADC8),
    val accentColor: Color = Color(0xFFF5C2E7),
    val isOledMode: Boolean = false
)

object ThemePresets {
    val CatppuccinMocha = HyprThemeConfig(
        preset = ThemePreset.CATPPUCCIN_MOCHA,
        windowGapsDp = 8,
        borderRadiusDp = 14,
        borderThicknessDp = 2,
        blurRadiusDp = 18,
        activeBorderGradient = listOf(Color(0xFFCBA6F7), Color(0xFF89B4FA)),
        inactiveBorderColor = Color(0xFF313244),
        backgroundColor = Color(0xFF1E1E2E),
        surfaceColor = Color(0xDD181825),
        surfaceVariantColor = Color(0xFF11111B),
        textPrimaryColor = Color(0xFFCDD6F4),
        textSecondaryColor = Color(0xFFA6ADC8),
        accentColor = Color(0xFFF5C2E7)
    )

    val TokyoNight = HyprThemeConfig(
        preset = ThemePreset.TOKYO_NIGHT,
        windowGapsDp = 8,
        borderRadiusDp = 12,
        borderThicknessDp = 2,
        blurRadiusDp = 20,
        activeBorderGradient = listOf(Color(0xFF7AA2F7), Color(0xFFBB9AF7)),
        inactiveBorderColor = Color(0xFF24283B),
        backgroundColor = Color(0xFF1A1B26),
        surfaceColor = Color(0xDD16161E),
        surfaceVariantColor = Color(0xFF13141C),
        textPrimaryColor = Color(0xFFC0CAF5),
        textSecondaryColor = Color(0xFF787C99),
        accentColor = Color(0xFF7DCFFF)
    )

    val NordicFrost = HyprThemeConfig(
        preset = ThemePreset.NORDIC_FROST,
        windowGapsDp = 8,
        borderRadiusDp = 16,
        borderThicknessDp = 2,
        blurRadiusDp = 16,
        activeBorderGradient = listOf(Color(0xFF88C0D0), Color(0xFF81A1C1)),
        inactiveBorderColor = Color(0xFF434C5E),
        backgroundColor = Color(0xFF2E3440),
        surfaceColor = Color(0xDD3B4252),
        surfaceVariantColor = Color(0xFF292E39),
        textPrimaryColor = Color(0xFFECEFF4),
        textSecondaryColor = Color(0xFFD8DEE9),
        accentColor = Color(0xFF8FBCBB)
    )

    val GruvboxDark = HyprThemeConfig(
        preset = ThemePreset.GRUVBOX_DARK,
        windowGapsDp = 8,
        borderRadiusDp = 10,
        borderThicknessDp = 2,
        blurRadiusDp = 14,
        activeBorderGradient = listOf(Color(0xFFFB4934), Color(0xFFFABD2F)),
        inactiveBorderColor = Color(0xFF3C3836),
        backgroundColor = Color(0xFF282828),
        surfaceColor = Color(0xDD1D2021),
        surfaceVariantColor = Color(0xFF141617),
        textPrimaryColor = Color(0xFFEBDBB2),
        textSecondaryColor = Color(0xFFA89984),
        accentColor = Color(0xFFB8BB26)
    )

    val OleCyberpunk = HyprThemeConfig(
        preset = ThemePreset.OLED_CYBERPUNK,
        windowGapsDp = 10,
        borderRadiusDp = 14,
        borderThicknessDp = 2,
        blurRadiusDp = 24,
        activeBorderGradient = listOf(Color(0xFFFF0055), Color(0xFF00FFEE)),
        inactiveBorderColor = Color(0xFF222222),
        backgroundColor = Color(0xFF000000),
        surfaceColor = Color(0xE60D0D0D),
        surfaceVariantColor = Color(0xFF050505),
        textPrimaryColor = Color(0xFFFFFFFF),
        textSecondaryColor = Color(0xFF888888),
        accentColor = Color(0xFFFFE600),
        isOledMode = true
    )

    fun getPreset(preset: ThemePreset): HyprThemeConfig = when (preset) {
        ThemePreset.CATPPUCCIN_MOCHA -> CatppuccinMocha
        ThemePreset.TOKYO_NIGHT -> TokyoNight
        ThemePreset.NORDIC_FROST -> NordicFrost
        ThemePreset.GRUVBOX_DARK -> GruvboxDark
        ThemePreset.OLED_CYBERPUNK -> OleCyberpunk
    }
}

val LocalHyprTheme = compositionLocalOf { ThemePresets.CatppuccinMocha }

object ThemeManager {
    private val _themeConfig = MutableStateFlow(ThemePresets.CatppuccinMocha)
    val themeConfig: StateFlow<HyprThemeConfig> = _themeConfig.asStateFlow()

    fun setPreset(preset: ThemePreset) {
        val base = when (preset) {
            ThemePreset.CATPPUCCIN_MOCHA -> ThemePresets.CatppuccinMocha
            ThemePreset.TOKYO_NIGHT -> ThemePresets.TokyoNight
            ThemePreset.NORDIC_FROST -> ThemePresets.NordicFrost
            ThemePreset.GRUVBOX_DARK -> ThemePresets.GruvboxDark
            ThemePreset.OLED_CYBERPUNK -> ThemePresets.OleCyberpunk
        }
        val current = _themeConfig.value
        _themeConfig.value = base.copy(
            windowGapsDp = current.windowGapsDp,
            borderRadiusDp = current.borderRadiusDp,
            borderThicknessDp = current.borderThicknessDp,
            blurRadiusDp = current.blurRadiusDp
        )
    }

    fun updateGaps(gapsDp: Int) {
        _themeConfig.update { it.copy(windowGapsDp = gapsDp.coerceIn(0, 24)) }
    }

    fun updateWindowGaps(gapsDp: Int) = updateGaps(gapsDp)

    fun updateRadius(radiusDp: Int) {
        _themeConfig.update { it.copy(borderRadiusDp = radiusDp.coerceIn(0, 28)) }
    }

    fun updateBorderRadius(radiusDp: Int) = updateRadius(radiusDp)

    fun updateBorderThickness(thicknessDp: Int) {
        _themeConfig.update { it.copy(borderThicknessDp = thicknessDp.coerceIn(1, 6)) }
    }

    fun updateBlur(blurDp: Int) {
        _themeConfig.update { it.copy(blurRadiusDp = blurDp.coerceIn(0, 40)) }
    }

    fun toggleOled(enabled: Boolean) {
        _themeConfig.update {
            if (enabled) {
                it.copy(
                    isOledMode = true,
                    backgroundColor = Color(0xFF000000),
                    surfaceColor = Color(0xEE0A0A0A)
                )
            } else {
                val defaultBg = when (it.preset) {
                    ThemePreset.CATPPUCCIN_MOCHA -> ThemePresets.CatppuccinMocha.backgroundColor
                    ThemePreset.TOKYO_NIGHT -> ThemePresets.TokyoNight.backgroundColor
                    ThemePreset.NORDIC_FROST -> ThemePresets.NordicFrost.backgroundColor
                    ThemePreset.GRUVBOX_DARK -> ThemePresets.GruvboxDark.backgroundColor
                    ThemePreset.OLED_CYBERPUNK -> Color(0xFF000000)
                }
                it.copy(
                    isOledMode = false,
                    backgroundColor = defaultBg,
                    surfaceColor = when (it.preset) {
                        ThemePreset.CATPPUCCIN_MOCHA -> ThemePresets.CatppuccinMocha.surfaceColor
                        ThemePreset.TOKYO_NIGHT -> ThemePresets.TokyoNight.surfaceColor
                        ThemePreset.NORDIC_FROST -> ThemePresets.NordicFrost.surfaceColor
                        ThemePreset.GRUVBOX_DARK -> ThemePresets.GruvboxDark.surfaceColor
                        ThemePreset.OLED_CYBERPUNK -> ThemePresets.OleCyberpunk.surfaceColor
                    }
                )
            }
        }
    }
}

/**
 * Hyprland-inspired custom tile modifier with dynamic gaps, borders, squircle clipping, and blur.
 */
fun Modifier.hyprTile(
    theme: HyprThemeConfig,
    isActive: Boolean = false,
    customCornerRadius: Int? = null,
    customPadding: Int? = null
): Modifier {
    val radius = (customCornerRadius ?: theme.borderRadiusDp).dp
    val padding = (customPadding ?: (theme.windowGapsDp / 2)).dp
    val shape = RoundedCornerShape(radius)

    return this
        .padding(padding)
        .clip(shape)
        .background(theme.surfaceColor)
        .border(
            width = theme.borderThicknessDp.dp,
            brush = if (isActive) {
                Brush.linearGradient(theme.activeBorderGradient)
            } else {
                SolidColor(theme.inactiveBorderColor)
            },
            shape = shape
        )
}

/**
 * Animated gradient border modifier for active/playing track tiles.
 */
@Composable
fun Modifier.hyprAnimatedGlow(
    theme: HyprThemeConfig,
    enabled: Boolean = true
): Modifier {
    if (!enabled) return this

    val infiniteTransition = rememberInfiniteTransition(label = "hypr_glow")
    val offsetProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glow_offset"
    )

    val shape = RoundedCornerShape(theme.borderRadiusDp.dp)
    val animatedBrush = Brush.linearGradient(
        colors = theme.activeBorderGradient + theme.activeBorderGradient.first(),
        start = Offset(offsetProgress, offsetProgress),
        end = Offset(offsetProgress + 500f, offsetProgress + 500f)
    )

    return this.border(
        width = (theme.borderThicknessDp + 1).dp,
        brush = animatedBrush,
        shape = shape
    )
}

/**
 * Tactile micro-interaction modifier providing smooth spring bounce compression on press/tap.
 */
fun Modifier.hyprBounceClick(
    targetScale: Float = 0.95f,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) targetScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "hypr_bounce_scale"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
}

@Composable
fun HyprThemeProvider(
    themeConfig: HyprThemeConfig = ThemeManager.themeConfig.value,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalHyprTheme provides themeConfig) {
        content()
    }
}

