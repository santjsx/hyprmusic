package com.example.hyprmusic.core.theming

import android.content.Context
import android.content.SharedPreferences
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ThemePreset(val displayName: String) {
    CATPPUCCIN_MOCHA("Catppuccin Mocha"),
    TOKYO_NIGHT("Tokyo Night"),
    NORDIC_FROST("Nordic Frost"),
    GRUVBOX_DARK("Gruvbox Dark"),
    OLED_CYBERPUNK("OLED Cyberpunk"),
    DRACULA_VOID("Dracula Void"),
    ROSE_PINE("Rosé Pine"),
    MONOKAI_PRO("Monokai Pro"),
    SOLARIZED_AMBER("Solarized Amber"),
    EMERALD_MATRIX("Emerald Matrix")
}

@Immutable
data class HyprThemeConfig(
    val preset: ThemePreset = ThemePreset.TOKYO_NIGHT,
    val windowGapsDp: Int = 8,
    val borderRadiusDp: Int = 12,
    val borderThicknessDp: Int = 2,
    val blurRadiusDp: Int = 18,
    val activeBorderGradient: List<Color> = listOf(Color(0xFF7AA2F7), Color(0xFFBB9AF7)),
    val inactiveBorderColor: Color = Color(0xFF24283B),
    val backgroundColor: Color = Color(0xFF1A1B26),
    val surfaceColor: Color = Color(0xDD16161E),
    val surfaceVariantColor: Color = Color(0xFF13141C),
    val textPrimaryColor: Color = Color(0xFFC0CAF5),
    val textSecondaryColor: Color = Color(0xFF787C99),
    val accentColor: Color = Color(0xFF7DCFFF),
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

    val DraculaVoid = HyprThemeConfig(
        preset = ThemePreset.DRACULA_VOID,
        windowGapsDp = 8,
        borderRadiusDp = 12,
        borderThicknessDp = 2,
        blurRadiusDp = 18,
        activeBorderGradient = listOf(Color(0xFFFF79C6), Color(0xFFBD93F9)),
        inactiveBorderColor = Color(0xFF44475A),
        backgroundColor = Color(0xFF21222C),
        surfaceColor = Color(0xDD282A36),
        surfaceVariantColor = Color(0xFF191A21),
        textPrimaryColor = Color(0xFFF8F8F2),
        textSecondaryColor = Color(0xFF6272A4),
        accentColor = Color(0xFF50FA7B)
    )

    val RosePine = HyprThemeConfig(
        preset = ThemePreset.ROSE_PINE,
        windowGapsDp = 8,
        borderRadiusDp = 14,
        borderThicknessDp = 2,
        blurRadiusDp = 18,
        activeBorderGradient = listOf(Color(0xFFEB6F92), Color(0xFFC4A7E7)),
        inactiveBorderColor = Color(0xFF26233A),
        backgroundColor = Color(0xFF191724),
        surfaceColor = Color(0xDD1F1D2E),
        surfaceVariantColor = Color(0xFF14121F),
        textPrimaryColor = Color(0xFFE0DEF4),
        textSecondaryColor = Color(0xFF908CAA),
        accentColor = Color(0xFFF6C177)
    )

    val MonokaiPro = HyprThemeConfig(
        preset = ThemePreset.MONOKAI_PRO,
        windowGapsDp = 8,
        borderRadiusDp = 12,
        borderThicknessDp = 2,
        blurRadiusDp = 18,
        activeBorderGradient = listOf(Color(0xFFA9DC76), Color(0xFFFF6188)),
        inactiveBorderColor = Color(0xFF403E41),
        backgroundColor = Color(0xFF2D2A2E),
        surfaceColor = Color(0xDD221F22),
        surfaceVariantColor = Color(0xFF19181A),
        textPrimaryColor = Color(0xFFFCFCFA),
        textSecondaryColor = Color(0xFF727072),
        accentColor = Color(0xFFFFD866)
    )

    val SolarizedAmber = HyprThemeConfig(
        preset = ThemePreset.SOLARIZED_AMBER,
        windowGapsDp = 8,
        borderRadiusDp = 10,
        borderThicknessDp = 2,
        blurRadiusDp = 14,
        activeBorderGradient = listOf(Color(0xFFB58900), Color(0xFFCB4B16)),
        inactiveBorderColor = Color(0xFF586E75),
        backgroundColor = Color(0xFF002B36),
        surfaceColor = Color(0xDD073642),
        surfaceVariantColor = Color(0xFF001F27),
        textPrimaryColor = Color(0xFFFDF6E3),
        textSecondaryColor = Color(0xFF93A1A1),
        accentColor = Color(0xFFB58900)
    )

    val EmeraldMatrix = HyprThemeConfig(
        preset = ThemePreset.EMERALD_MATRIX,
        windowGapsDp = 8,
        borderRadiusDp = 8,
        borderThicknessDp = 2,
        blurRadiusDp = 16,
        activeBorderGradient = listOf(Color(0xFF00FF66), Color(0xFF00CC44)),
        inactiveBorderColor = Color(0xFF1B3322),
        backgroundColor = Color(0xFF080F0A),
        surfaceColor = Color(0xDD0D1810),
        surfaceVariantColor = Color(0xFF050A06),
        textPrimaryColor = Color(0xFFE0FFE8),
        textSecondaryColor = Color(0xFF66AA77),
        accentColor = Color(0xFF00FF66)
    )

    fun getPreset(preset: ThemePreset): HyprThemeConfig = when (preset) {
        ThemePreset.CATPPUCCIN_MOCHA -> CatppuccinMocha
        ThemePreset.TOKYO_NIGHT -> TokyoNight
        ThemePreset.NORDIC_FROST -> NordicFrost
        ThemePreset.GRUVBOX_DARK -> GruvboxDark
        ThemePreset.OLED_CYBERPUNK -> OleCyberpunk
        ThemePreset.DRACULA_VOID -> DraculaVoid
        ThemePreset.ROSE_PINE -> RosePine
        ThemePreset.MONOKAI_PRO -> MonokaiPro
        ThemePreset.SOLARIZED_AMBER -> SolarizedAmber
        ThemePreset.EMERALD_MATRIX -> EmeraldMatrix
    }
}

val LocalHyprTheme = compositionLocalOf { ThemePresets.TokyoNight }

object ThemeManager {
    private var prefs: SharedPreferences? = null
    private val _designSpec = MutableStateFlow(ThemeRegistry.TokyoNight)
    val designSpec: StateFlow<HyprDesignSpec> = _designSpec.asStateFlow()

    private val _themeConfig = MutableStateFlow(ThemeRegistry.TokyoNight.toLegacyConfig())
    val themeConfig: StateFlow<HyprThemeConfig> = _themeConfig.asStateFlow()

    fun init(context: Context) {
        val p = context.getSharedPreferences("hypr_theme_prefs", Context.MODE_PRIVATE)
        prefs = p

        val savedPresetName = p.getString("theme_preset", ThemePreset.TOKYO_NIGHT.name)
        val preset = try {
            ThemePreset.valueOf(savedPresetName ?: ThemePreset.TOKYO_NIGHT.name)
        } catch (_: Exception) {
            ThemePreset.TOKYO_NIGHT
        }

        val baseSpec = ThemeRegistry.getSpec(preset)
        val gaps = p.getInt("window_gaps", baseSpec.elementGap.value.toInt())
        val radius = p.getInt("border_radius", baseSpec.cornerRadius.value.toInt())
        val thickness = p.getInt("border_thickness", baseSpec.borderThickness.value.toInt())
        val blur = p.getInt("blur_radius", baseSpec.blurRadiusDp)
        val isOled = p.getBoolean("is_oled_mode", baseSpec.isOledMode)

        val savedIconPack = p.getString("icon_pack", baseSpec.iconPack.name)?.let {
            try { IconPackType.valueOf(it) } catch (_: Exception) { baseSpec.iconPack }
        } ?: baseSpec.iconPack

        val savedProgressStyle = p.getString("progress_style", baseSpec.progressStyle.name)?.let {
            try { ProgressBarStyle.valueOf(it) } catch (_: Exception) { baseSpec.progressStyle }
        } ?: baseSpec.progressStyle

        val savedGridStyle = p.getString("grid_style", baseSpec.gridStyle.name)?.let {
            try { GridLayoutStyle.valueOf(it) } catch (_: Exception) { baseSpec.gridStyle }
        } ?: baseSpec.gridStyle

        val savedFontType = p.getString("font_type", baseSpec.fontType.name)?.let {
            try { HyprFontType.valueOf(it) } catch (_: Exception) { baseSpec.fontType }
        } ?: baseSpec.fontType

        val savedControlStyle = p.getString("control_style", baseSpec.controlStyle.name)?.let {
            try { PlayControlStyle.valueOf(it) } catch (_: Exception) { baseSpec.controlStyle }
        } ?: baseSpec.controlStyle

        val savedGlow = p.getFloat("glow_intensity", baseSpec.glowIntensity)

        val resolvedSpec = baseSpec.copy(
            elementGap = gaps.dp,
            cornerRadius = radius.dp,
            borderThickness = thickness.dp,
            blurRadiusDp = blur,
            glowIntensity = savedGlow,
            isOledMode = isOled,
            bg = if (isOled) Color(0xFF000000) else baseSpec.bg,
            surface = if (isOled) Color(0xEE0A0A0A) else baseSpec.surface,
            iconPack = savedIconPack,
            progressStyle = savedProgressStyle,
            controlStyle = savedControlStyle,
            gridStyle = savedGridStyle,
            fontType = savedFontType,
            fontFamily = savedFontType.fontFamily
        )

        _designSpec.value = resolvedSpec
        _themeConfig.value = resolvedSpec.toLegacyConfig()
    }

    fun setPreset(preset: ThemePreset) {
        val baseSpec = ThemeRegistry.getSpec(preset)
        val currentSpec = _designSpec.value
        val updatedSpec = baseSpec.copy(
            elementGap = currentSpec.elementGap,
            cornerRadius = currentSpec.cornerRadius,
            borderThickness = currentSpec.borderThickness,
            blurRadiusDp = currentSpec.blurRadiusDp,
            glowIntensity = baseSpec.glowIntensity,
            isOledMode = currentSpec.isOledMode,
            bg = if (currentSpec.isOledMode) Color(0xFF000000) else baseSpec.bg,
            surface = if (currentSpec.isOledMode) Color(0xEE0A0A0A) else baseSpec.surface,
            iconPack = baseSpec.iconPack,
            progressStyle = baseSpec.progressStyle,
            controlStyle = baseSpec.controlStyle,
            fontType = baseSpec.fontType,
            fontFamily = baseSpec.fontFamily
        )
        _designSpec.value = updatedSpec
        _themeConfig.value = updatedSpec.toLegacyConfig()
        prefs?.edit()?.putString("theme_preset", preset.name)
            ?.putString("icon_pack", baseSpec.iconPack.name)
            ?.putString("progress_style", baseSpec.progressStyle.name)
            ?.putString("control_style", baseSpec.controlStyle.name)
            ?.putString("font_type", baseSpec.fontType.name)
            ?.apply()
    }

    fun updateIconPack(pack: IconPackType) {
        _designSpec.update { it.copy(iconPack = pack) }
        prefs?.edit()?.putString("icon_pack", pack.name)?.apply()
    }

    fun updateProgressStyle(style: ProgressBarStyle) {
        _designSpec.update { it.copy(progressStyle = style) }
        prefs?.edit()?.putString("progress_style", style.name)?.apply()
    }

    fun updateControlStyle(style: PlayControlStyle) {
        _designSpec.update { it.copy(controlStyle = style) }
        prefs?.edit()?.putString("control_style", style.name)?.apply()
    }

    fun updateGlowIntensity(intensity: Float) {
        val safeGlow = intensity.coerceIn(0f, 1f)
        _designSpec.update { it.copy(glowIntensity = safeGlow) }
        prefs?.edit()?.putFloat("glow_intensity", safeGlow)?.apply()
    }

    fun updateGridStyle(gridStyle: GridLayoutStyle) {
        _designSpec.update { it.copy(gridStyle = gridStyle) }
        prefs?.edit()?.putString("grid_style", gridStyle.name)?.apply()
    }

    fun updateFontType(fontType: HyprFontType) {
        _designSpec.update { it.copy(fontType = fontType, fontFamily = fontType.fontFamily) }
        prefs?.edit()?.putString("font_type", fontType.name)?.apply()
    }

    fun updateGaps(gapsDp: Int) {
        val safeGaps = gapsDp.coerceIn(0, 24)
        _designSpec.update { it.copy(elementGap = safeGaps.dp) }
        _themeConfig.update { it.copy(windowGapsDp = safeGaps) }
        prefs?.edit()?.putInt("window_gaps", safeGaps)?.apply()
    }

    fun updateWindowGaps(gapsDp: Int) = updateGaps(gapsDp)

    fun updateRadius(radiusDp: Int) {
        val safeRadius = radiusDp.coerceIn(0, 28)
        _designSpec.update { it.copy(cornerRadius = safeRadius.dp) }
        _themeConfig.update { it.copy(borderRadiusDp = safeRadius) }
        prefs?.edit()?.putInt("border_radius", safeRadius)?.apply()
    }

    fun updateBorderRadius(radiusDp: Int) = updateRadius(radiusDp)

    fun updateBorderThickness(thicknessDp: Int) {
        val safeThickness = thicknessDp.coerceIn(1, 6)
        _designSpec.update { it.copy(borderThickness = safeThickness.dp) }
        _themeConfig.update { it.copy(borderThicknessDp = safeThickness) }
        prefs?.edit()?.putInt("border_thickness", safeThickness)?.apply()
    }

    fun updateBlur(blurDp: Int) {
        val safeBlur = blurDp.coerceIn(0, 40)
        _designSpec.update { it.copy(blurRadiusDp = safeBlur) }
        _themeConfig.update { it.copy(blurRadiusDp = safeBlur) }
        prefs?.edit()?.putInt("blur_radius", safeBlur)?.apply()
    }

    fun toggleOled(enabled: Boolean) {
        _designSpec.update {
            val base = ThemeRegistry.getSpec(it.preset)
            if (enabled) {
                it.copy(
                    isOledMode = true,
                    bg = Color(0xFF000000),
                    surface = Color(0xEE0A0A0A)
                )
            } else {
                it.copy(
                    isOledMode = false,
                    bg = base.bg,
                    surface = base.surface
                )
            }
        }
        _themeConfig.value = _designSpec.value.toLegacyConfig()
        prefs?.edit()?.putBoolean("is_oled_mode", enabled)?.apply()
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
    val offsetProgress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glow_offset"
    )

    val density = LocalDensity.current
    val strokeWidthPx = remember(density, theme.borderThicknessDp) {
        with(density) { (theme.borderThicknessDp + 1).dp.toPx() }
    }
    val cornerRadiusPx = remember(density, theme.borderRadiusDp) {
        with(density) { theme.borderRadiusDp.dp.toPx() }
    }
    val gradientColors = remember(theme.activeBorderGradient) {
        theme.activeBorderGradient + theme.activeBorderGradient.first()
    }

    return this.drawWithContent {
        drawContent()
        val currentOffset = offsetProgress.value
        val animatedBrush = Brush.linearGradient(
            colors = gradientColors,
            start = Offset(currentOffset, currentOffset),
            end = Offset(currentOffset + 500f, currentOffset + 500f)
        )
        val halfStroke = strokeWidthPx / 2f
        drawRoundRect(
            brush = animatedBrush,
            topLeft = Offset(halfStroke, halfStroke),
            size = Size(size.width - strokeWidthPx, size.height - strokeWidthPx),
            cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
            style = Stroke(width = strokeWidthPx)
        )
    }
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
    designSpec: HyprDesignSpec = ThemeManager.designSpec.value,
    themeConfig: HyprThemeConfig = ThemeManager.themeConfig.value,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalHyprDesign provides designSpec,
        LocalHyprTheme provides themeConfig
    ) {
        content()
    }
}
