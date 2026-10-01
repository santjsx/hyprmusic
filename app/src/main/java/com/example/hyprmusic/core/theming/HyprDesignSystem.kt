package com.example.hyprmusic.core.theming

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class IconPackType(val displayName: String) {
    PHOSPHOR_LINE("Phosphor Line"),
    NERD_FONTS_ASCII("Nerd Fonts ASCII"),
    ARCH_OUTLINE("Arch Outline")
}

enum class ProgressBarStyle(val displayName: String) {
    MINIMAL_WAYBAR("Minimal Waybar"),
    BLOCKS_SHELL("Blocks Shell [████░░]"),
    DYNAMIC_NEON("Dynamic Neon")
}

enum class GridLayoutStyle(val displayName: String) {
    ASYMMETRIC_TILES("Asymmetric Tiles (Bento)"),
    TIGHT_TERMINAL_ROWS("Tight Terminal Rows (CLI)")
}

enum class HyprFontType(val displayName: String, val fontFamily: FontFamily) {
    JETBRAINS_MONO("JetBrains Mono", FontFamily.Monospace),
    IBM_PLEX_MONO("IBM Plex Mono", FontFamily.Monospace),
    SYSTEM_MONOSPACE("System Monospace", FontFamily.Monospace),
    MINIMAL_SANS("Minimal Sans", FontFamily.SansSerif)
}

@Immutable
data class HyprDesignSpec(
    val themeName: String,
    val preset: ThemePreset,
    val isDark: Boolean = true,
    // Colors
    val bg: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val borderActive: Color,
    val borderInactive: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textAccent: Color,
    val activeBorderGradient: List<Color>,
    // Layout and Typography
    val fontType: HyprFontType,
    val fontFamily: FontFamily,
    val cornerRadius: Dp,
    val elementGap: Dp,
    val borderThickness: Dp,
    val blurRadiusDp: Int,
    val isOledMode: Boolean = false,
    // Dynamic Engine Components
    val iconPack: IconPackType,
    val progressStyle: ProgressBarStyle,
    val gridStyle: GridLayoutStyle
) {
    val gaps: Dp get() = elementGap

    fun toLegacyConfig(): HyprThemeConfig {
        return HyprThemeConfig(
            preset = preset,
            windowGapsDp = elementGap.value.toInt(),
            borderRadiusDp = cornerRadius.value.toInt(),
            borderThicknessDp = borderThickness.value.toInt(),
            blurRadiusDp = blurRadiusDp,
            activeBorderGradient = activeBorderGradient,
            inactiveBorderColor = borderInactive,
            backgroundColor = bg,
            surfaceColor = surface,
            surfaceVariantColor = surfaceVariant,
            textPrimaryColor = textPrimary,
            textSecondaryColor = textSecondary,
            accentColor = textAccent,
            isOledMode = isOledMode
        )
    }
}

object ThemeRegistry {
    val CatppuccinMocha = HyprDesignSpec(
        themeName = "catppuccin-mocha",
        preset = ThemePreset.CATPPUCCIN_MOCHA,
        isDark = true,
        bg = Color(0xFF1E1E2E),
        surface = Color(0xDD181825),
        surfaceVariant = Color(0xFF11111B),
        borderActive = Color(0xFFF5C2E7), // Flamingo Pink
        borderInactive = Color(0xFF313244),
        textPrimary = Color(0xFFCDD6F4),
        textSecondary = Color(0xFFA6ADC8),
        textAccent = Color(0xFFCBA6F7), // Mauve Purple
        activeBorderGradient = listOf(Color(0xFFCBA6F7), Color(0xFF89B4FA)),
        fontType = HyprFontType.JETBRAINS_MONO,
        fontFamily = HyprFontType.JETBRAINS_MONO.fontFamily,
        cornerRadius = 14.dp, // Cozy subtle rounding
        elementGap = 8.dp,
        borderThickness = 2.dp,
        blurRadiusDp = 18,
        isOledMode = false,
        iconPack = IconPackType.PHOSPHOR_LINE,
        progressStyle = ProgressBarStyle.MINIMAL_WAYBAR,
        gridStyle = GridLayoutStyle.ASYMMETRIC_TILES
    )

    val GruvboxRetro = HyprDesignSpec(
        themeName = "gruvbox-retro",
        preset = ThemePreset.GRUVBOX_DARK,
        isDark = true,
        bg = Color(0xFF282828),
        surface = Color(0xDD1D2021),
        surfaceVariant = Color(0xFF141617),
        borderActive = Color(0xFFFE8019), // Retro Orange
        borderInactive = Color(0xFF3C3836),
        textPrimary = Color(0xFFEBDBB2),
        textSecondary = Color(0xFFA89984),
        textAccent = Color(0xFFB8BB26), // Retro Green Badge
        activeBorderGradient = listOf(Color(0xFFFB4934), Color(0xFFFABD2F)),
        fontType = HyprFontType.IBM_PLEX_MONO,
        fontFamily = HyprFontType.IBM_PLEX_MONO.fontFamily,
        cornerRadius = 0.dp, // Zero rounding - pure hard corners!
        elementGap = 6.dp,   // Super compact tiling
        borderThickness = 2.dp,
        blurRadiusDp = 14,
        isOledMode = false,
        iconPack = IconPackType.NERD_FONTS_ASCII, // pure code symbols
        progressStyle = ProgressBarStyle.BLOCKS_SHELL, // [████░░]
        gridStyle = GridLayoutStyle.TIGHT_TERMINAL_ROWS
    )

    val TokyoNight = HyprDesignSpec(
        themeName = "tokyo-night",
        preset = ThemePreset.TOKYO_NIGHT,
        isDark = true,
        bg = Color(0xFF1A1B26),
        surface = Color(0xDD16161E),
        surfaceVariant = Color(0xFF13141C),
        borderActive = Color(0xFF7DCFFF), // Cyber Cyan
        borderInactive = Color(0xFF24283B),
        textPrimary = Color(0xFFC0CAF5),
        textSecondary = Color(0xFF787C99),
        textAccent = Color(0xFFBB9AF7), // Neon Violet
        activeBorderGradient = listOf(Color(0xFF7AA2F7), Color(0xFFBB9AF7)),
        fontType = HyprFontType.JETBRAINS_MONO,
        fontFamily = HyprFontType.JETBRAINS_MONO.fontFamily,
        cornerRadius = 12.dp,
        elementGap = 8.dp,
        borderThickness = 2.dp,
        blurRadiusDp = 20,
        isOledMode = false,
        iconPack = IconPackType.ARCH_OUTLINE,
        progressStyle = ProgressBarStyle.DYNAMIC_NEON,
        gridStyle = GridLayoutStyle.ASYMMETRIC_TILES
    )

    val NordicFrost = HyprDesignSpec(
        themeName = "nordic-frost",
        preset = ThemePreset.NORDIC_FROST,
        isDark = true,
        bg = Color(0xFF2E3440),
        surface = Color(0xDD3B4252),
        surfaceVariant = Color(0xFF292E39),
        borderActive = Color(0xFF88C0D0), // Polar Ice Blue
        borderInactive = Color(0xFF434C5E),
        textPrimary = Color(0xFFECEFF4),
        textSecondary = Color(0xFFD8DEE9),
        textAccent = Color(0xFF8FBCBB), // Frosted Mint
        activeBorderGradient = listOf(Color(0xFF88C0D0), Color(0xFF81A1C1)),
        fontType = HyprFontType.SYSTEM_MONOSPACE,
        fontFamily = HyprFontType.SYSTEM_MONOSPACE.fontFamily,
        cornerRadius = 16.dp,
        elementGap = 8.dp,
        borderThickness = 2.dp,
        blurRadiusDp = 16,
        isOledMode = false,
        iconPack = IconPackType.ARCH_OUTLINE,
        progressStyle = ProgressBarStyle.MINIMAL_WAYBAR,
        gridStyle = GridLayoutStyle.TIGHT_TERMINAL_ROWS
    )

    val OledCyberpunk = HyprDesignSpec(
        themeName = "oled-cyberpunk",
        preset = ThemePreset.OLED_CYBERPUNK,
        isDark = true,
        bg = Color(0xFF000000),
        surface = Color(0xE60D0D0D),
        surfaceVariant = Color(0xFF050505),
        borderActive = Color(0xFF00FFEE),
        borderInactive = Color(0xFF222222),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFF888888),
        textAccent = Color(0xFFFFE600),
        activeBorderGradient = listOf(Color(0xFFFF0055), Color(0xFF00FFEE)),
        fontType = HyprFontType.JETBRAINS_MONO,
        fontFamily = HyprFontType.JETBRAINS_MONO.fontFamily,
        cornerRadius = 14.dp,
        elementGap = 10.dp,
        borderThickness = 2.dp,
        blurRadiusDp = 24,
        isOledMode = true,
        iconPack = IconPackType.PHOSPHOR_LINE,
        progressStyle = ProgressBarStyle.DYNAMIC_NEON,
        gridStyle = GridLayoutStyle.ASYMMETRIC_TILES
    )

    val DraculaVoid = HyprDesignSpec(
        themeName = "dracula-void",
        preset = ThemePreset.DRACULA_VOID,
        isDark = true,
        bg = Color(0xFF21222C),
        surface = Color(0xDD282A36),
        surfaceVariant = Color(0xFF191A21),
        borderActive = Color(0xFFBD93F9),
        borderInactive = Color(0xFF44475A),
        textPrimary = Color(0xFFF8F8F2),
        textSecondary = Color(0xFF6272A4),
        textAccent = Color(0xFF50FA7B),
        activeBorderGradient = listOf(Color(0xFFFF79C6), Color(0xFFBD93F9)),
        fontType = HyprFontType.JETBRAINS_MONO,
        fontFamily = HyprFontType.JETBRAINS_MONO.fontFamily,
        cornerRadius = 12.dp,
        elementGap = 8.dp,
        borderThickness = 2.dp,
        blurRadiusDp = 18,
        isOledMode = false,
        iconPack = IconPackType.PHOSPHOR_LINE,
        progressStyle = ProgressBarStyle.MINIMAL_WAYBAR,
        gridStyle = GridLayoutStyle.ASYMMETRIC_TILES
    )

    val RosePine = HyprDesignSpec(
        themeName = "rose-pine",
        preset = ThemePreset.ROSE_PINE,
        isDark = true,
        bg = Color(0xFF191724),
        surface = Color(0xDD1F1D2E),
        surfaceVariant = Color(0xFF14121F),
        borderActive = Color(0xFFEB6F92),
        borderInactive = Color(0xFF26233A),
        textPrimary = Color(0xFFE0DEF4),
        textSecondary = Color(0xFF908CAA),
        textAccent = Color(0xFFF6C177),
        activeBorderGradient = listOf(Color(0xFFEB6F92), Color(0xFFC4A7E7)),
        fontType = HyprFontType.JETBRAINS_MONO,
        fontFamily = HyprFontType.JETBRAINS_MONO.fontFamily,
        cornerRadius = 14.dp,
        elementGap = 8.dp,
        borderThickness = 2.dp,
        blurRadiusDp = 18,
        isOledMode = false,
        iconPack = IconPackType.PHOSPHOR_LINE,
        progressStyle = ProgressBarStyle.MINIMAL_WAYBAR,
        gridStyle = GridLayoutStyle.ASYMMETRIC_TILES
    )

    val MonokaiPro = HyprDesignSpec(
        themeName = "monokai-pro",
        preset = ThemePreset.MONOKAI_PRO,
        isDark = true,
        bg = Color(0xFF2D2A2E),
        surface = Color(0xDD221F22),
        surfaceVariant = Color(0xFF19181A),
        borderActive = Color(0xFFFF6188),
        borderInactive = Color(0xFF403E41),
        textPrimary = Color(0xFFFCFCFA),
        textSecondary = Color(0xFF727072),
        textAccent = Color(0xFFFFD866),
        activeBorderGradient = listOf(Color(0xFFA9DC76), Color(0xFFFF6188)),
        fontType = HyprFontType.IBM_PLEX_MONO,
        fontFamily = HyprFontType.IBM_PLEX_MONO.fontFamily,
        cornerRadius = 12.dp,
        elementGap = 8.dp,
        borderThickness = 2.dp,
        blurRadiusDp = 18,
        isOledMode = false,
        iconPack = IconPackType.ARCH_OUTLINE,
        progressStyle = ProgressBarStyle.DYNAMIC_NEON,
        gridStyle = GridLayoutStyle.TIGHT_TERMINAL_ROWS
    )

    fun getSpec(preset: ThemePreset): HyprDesignSpec = when (preset) {
        ThemePreset.CATPPUCCIN_MOCHA -> CatppuccinMocha
        ThemePreset.GRUVBOX_DARK -> GruvboxRetro
        ThemePreset.TOKYO_NIGHT -> TokyoNight
        ThemePreset.NORDIC_FROST -> NordicFrost
        ThemePreset.OLED_CYBERPUNK -> OledCyberpunk
        ThemePreset.DRACULA_VOID -> DraculaVoid
        ThemePreset.ROSE_PINE -> RosePine
        ThemePreset.MONOKAI_PRO -> MonokaiPro
    }
}

val LocalHyprDesign = staticCompositionLocalOf { ThemeRegistry.TokyoNight }

@Composable
fun HyprMusicPlayerTheme(
    spec: HyprDesignSpec,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalHyprDesign provides spec,
        LocalHyprTheme provides spec.toLegacyConfig()
    ) {
        content()
    }
}

object HyprTheme {
    val spec: HyprDesignSpec
        @Composable
        get() = LocalHyprDesign.current
}
