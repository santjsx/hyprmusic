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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
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
    MONOKAI_PRO("Monokai Pro")
}

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

    fun getPreset(preset: ThemePreset): HyprThemeConfig = when (preset) {
        ThemePreset.CATPPUCCIN_MOCHA -> CatppuccinMocha
        ThemePreset.TOKYO_NIGHT -> TokyoNight
        ThemePreset.NORDIC_FROST -> NordicFrost
        ThemePreset.GRUVBOX_DARK -> GruvboxDark
        ThemePreset.OLED_CYBERPUNK -> OleCyberpunk
        ThemePreset.DRACULA_VOID -> DraculaVoid
        ThemePreset.ROSE_PINE -> RosePine
        ThemePreset.MONOKAI_PRO -> MonokaiPro
    }
}

val LocalHyprTheme = compositionLocalOf { ThemePresets.TokyoNight }

object ThemeManager {
    private var prefs: SharedPreferences? = null
    private val _themeConfig = MutableStateFlow(ThemePresets.TokyoNight)
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

        val base = ThemePresets.getPreset(preset)
        val gaps = p.getInt("window_gaps", base.windowGapsDp)
        val radius = p.getInt("border_radius", base.borderRadiusDp)
        val thickness = p.getInt("border_thickness", base.borderThicknessDp)
        val blur = p.getInt("blur_radius", base.blurRadiusDp)
        val isOled = p.getBoolean("is_oled_mode", base.isOledMode)

        val resolved = base.copy(
            windowGapsDp = gaps,
            borderRadiusDp = radius,
            borderThicknessDp = thickness,
            blurRadiusDp = blur,
            isOledMode = isOled,
            backgroundColor = if (isOled) Color(0xFF000000) else base.backgroundColor,
            surfaceColor = if (isOled) Color(0xEE0A0A0A) else base.surfaceColor
        )

        _themeConfig.value = resolved
    }

    fun setPreset(preset: ThemePreset) {
        val base = ThemePresets.getPreset(preset)
        val current = _themeConfig.value
        val updated = base.copy(
            windowGapsDp = current.windowGapsDp,
            borderRadiusDp = current.borderRadiusDp,
            borderThicknessDp = current.borderThicknessDp,
            blurRadiusDp = current.blurRadiusDp,
            isOledMode = current.isOledMode,
            backgroundColor = if (current.isOledMode) Color(0xFF000000) else base.backgroundColor,
            surfaceColor = if (current.isOledMode) Color(0xEE0A0A0A) else base.surfaceColor
        )
        _themeConfig.value = updated
        prefs?.edit()?.putString("theme_preset", preset.name)?.apply()
    }

    fun updateGaps(gapsDp: Int) {
        val safeGaps = gapsDp.coerceIn(0, 24)
        _themeConfig.update { it.copy(windowGapsDp = safeGaps) }
        prefs?.edit()?.putInt("window_gaps", safeGaps)?.apply()
    }

    fun updateWindowGaps(gapsDp: Int) = updateGaps(gapsDp)

    fun updateRadius(radiusDp: Int) {
        val safeRadius = radiusDp.coerceIn(0, 28)
        _themeConfig.update { it.copy(borderRadiusDp = safeRadius) }
        prefs?.edit()?.putInt("border_radius", safeRadius)?.apply()
    }

    fun updateBorderRadius(radiusDp: Int) = updateRadius(radiusDp)

    fun updateBorderThickness(thicknessDp: Int) {
        val safeThickness = thicknessDp.coerceIn(1, 6)
        _themeConfig.update { it.copy(borderThicknessDp = safeThickness) }
        prefs?.edit()?.putInt("border_thickness", safeThickness)?.apply()
    }

    fun updateBlur(blurDp: Int) {
        val safeBlur = blurDp.coerceIn(0, 40)
        _themeConfig.update { it.copy(blurRadiusDp = safeBlur) }
        prefs?.edit()?.putInt("blur_radius", safeBlur)?.apply()
    }

    fun toggleOled(enabled: Boolean) {
        _themeConfig.update {
            val base = ThemePresets.getPreset(it.preset)
            if (enabled) {
                it.copy(
                    isOledMode = true,
                    backgroundColor = Color(0xFF000000),
                    surfaceColor = Color(0xEE0A0A0A)
                )
            } else {
                it.copy(
                    isOledMode = false,
                    backgroundColor = base.backgroundColor,
                    surfaceColor = base.surfaceColor
                )
            }
        }
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
