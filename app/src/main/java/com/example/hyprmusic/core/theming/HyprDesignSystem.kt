package com.example.hyprmusic.core.theming

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Visual geometry and tactile feedback styling for playback control buttons.
 */
enum class PlayControlStyle(val displayName: String, val description: String) {
    FLOATING_SQUIRCLE("Floating Squircle", "Soft rounded squircle with subtle elevation and active border"),
    NEON_GLOW_PILL("Neon Glow Pill", "Stadium pill shape with radiant outer glow aura"),
    TACTILE_BEVEL("Tactile Hi-Fi Bevel", "Stereo hardware physical button with 3D drop-shadow depth"),
    MINIMAL_GLASS_HALO("Minimal Glass Halo", "Translucent frosted circular ring with hairline vector stroke"),
    CYBER_CHAMFER("Cyber Chamfer", "Futuristic 45-degree technical polygon with angular corner cuts"),
    BRACKET_CONSOLE("Bracket Console", "Retro terminal brackets with crisp vector bounding frames")
}

/**
 * Specialized progress bar engines with distinctive visual mechanics and seeking physics.
 */
enum class ProgressBarStyle(val displayName: String, val description: String) {
    CAPSULE_SEEKER("Capsule Seeker", "5dp rounded track with a 12dp tactile scrubber thumb and fluid drag gestures"),
    WAVEFORM_SCRUBBER("Waveform EQ Scrubber", "Simulated multi-band audio waveform where elapsed bars illuminate in accent"),
    SEGMENTED_LED_VU("Segmented LED VU", "Discrete studio audio VU meter blocks with green/yellow/accent thresholds"),
    MINIMAL_WAYBAR("Minimal Waybar Line", "Ultra-thin 2.5dp low-profile line with micro scrub head"),
    DYNAMIC_NEON("Dynamic Neon Glow", "Luminous accent slider with responsive glowing borders and radiant halo"),
    ANALOG_TAPE_GAUGE("Analog Tape Gauge", "Vintage reel-to-reel style dual-rail track with precision tick marks")
}

/**
 * Icon visual language and stroke weight architecture.
 */
enum class IconPackType(val displayName: String, val strokeWidthDp: Float) {
    PHOSPHOR("Phosphor Rounded", 2.0f),
    LUCIDE("Lucide Geometric", 1.8f),
    REMIX("Remix Solid", 0.0f),
    TABLER("Tabler Minimal", 1.5f),
    RETRO_CONSOLE("Retro Console", 2.2f)
}

/**
 * Grid layout architecture for the home screen.
 */
enum class GridLayoutStyle(val displayName: String) {
    ASYMMETRIC_TILES("Asymmetric Bento Tiles"),
    TIGHT_TERMINAL_ROWS("High Density Track Table")
}

/**
 * Font families calibrated across monospace and contemporary sans typefaces.
 */
enum class HyprFontType(val displayName: String, val fontFamily: FontFamily, val letterSpacingSp: Float) {
    JETBRAINS_MONO("JetBrains Mono", FontFamily.Monospace, 0.5f),
    IBM_PLEX_MONO("IBM Plex Mono", FontFamily.Monospace, 0.8f),
    SPACE_GROTESK("Space Grotesk (Sans)", FontFamily.SansSerif, 0.2f),
    INTER_CLEAN("Inter Clean (Modern)", FontFamily.Default, 0.0f),
    RETRO_TERMINAL("Retro Terminal Monospace", FontFamily.Monospace, 1.2f)
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
    // Geometry & Effects
    val cornerRadius: Dp,
    val elementGap: Dp,
    val borderThickness: Dp,
    val blurRadiusDp: Int,
    val glowIntensity: Float = 0.5f,
    val isOledMode: Boolean = false,
    // Dynamic Engine Components
    val fontType: HyprFontType,
    val fontFamily: FontFamily = fontType.fontFamily,
    val iconPack: IconPackType,
    val progressStyle: ProgressBarStyle,
    val controlStyle: PlayControlStyle,
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
        cornerRadius = 14.dp,
        elementGap = 8.dp,
        borderThickness = 2.dp,
        blurRadiusDp = 18,
        glowIntensity = 0.45f,
        isOledMode = false,
        iconPack = IconPackType.PHOSPHOR,
        progressStyle = ProgressBarStyle.CAPSULE_SEEKER,
        controlStyle = PlayControlStyle.FLOATING_SQUIRCLE,
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
        cornerRadius = 10.dp,
        elementGap = 6.dp,
        borderThickness = 2.dp,
        blurRadiusDp = 14,
        glowIntensity = 0.3f,
        isOledMode = false,
        iconPack = IconPackType.RETRO_CONSOLE,
        progressStyle = ProgressBarStyle.SEGMENTED_LED_VU,
        controlStyle = PlayControlStyle.TACTILE_BEVEL,
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
        glowIntensity = 0.7f,
        isOledMode = false,
        iconPack = IconPackType.LUCIDE,
        progressStyle = ProgressBarStyle.DYNAMIC_NEON,
        controlStyle = PlayControlStyle.NEON_GLOW_PILL,
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
        fontType = HyprFontType.INTER_CLEAN,
        fontFamily = HyprFontType.INTER_CLEAN.fontFamily,
        cornerRadius = 16.dp,
        elementGap = 8.dp,
        borderThickness = 1.5.dp,
        blurRadiusDp = 16,
        glowIntensity = 0.35f,
        isOledMode = false,
        iconPack = IconPackType.TABLER,
        progressStyle = ProgressBarStyle.MINIMAL_WAYBAR,
        controlStyle = PlayControlStyle.MINIMAL_GLASS_HALO,
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
        fontType = HyprFontType.SPACE_GROTESK,
        fontFamily = HyprFontType.SPACE_GROTESK.fontFamily,
        cornerRadius = 14.dp,
        elementGap = 10.dp,
        borderThickness = 2.dp,
        blurRadiusDp = 24,
        glowIntensity = 0.85f,
        isOledMode = true,
        iconPack = IconPackType.REMIX,
        progressStyle = ProgressBarStyle.DYNAMIC_NEON,
        controlStyle = PlayControlStyle.CYBER_CHAMFER,
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
        glowIntensity = 0.6f,
        isOledMode = false,
        iconPack = IconPackType.PHOSPHOR,
        progressStyle = ProgressBarStyle.WAVEFORM_SCRUBBER,
        controlStyle = PlayControlStyle.FLOATING_SQUIRCLE,
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
        fontType = HyprFontType.INTER_CLEAN,
        fontFamily = HyprFontType.INTER_CLEAN.fontFamily,
        cornerRadius = 14.dp,
        elementGap = 8.dp,
        borderThickness = 1.5.dp,
        blurRadiusDp = 18,
        glowIntensity = 0.4f,
        isOledMode = false,
        iconPack = IconPackType.TABLER,
        progressStyle = ProgressBarStyle.CAPSULE_SEEKER,
        controlStyle = PlayControlStyle.MINIMAL_GLASS_HALO,
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
        cornerRadius = 10.dp,
        elementGap = 8.dp,
        borderThickness = 2.dp,
        blurRadiusDp = 18,
        glowIntensity = 0.5f,
        isOledMode = false,
        iconPack = IconPackType.LUCIDE,
        progressStyle = ProgressBarStyle.SEGMENTED_LED_VU,
        controlStyle = PlayControlStyle.BRACKET_CONSOLE,
        gridStyle = GridLayoutStyle.TIGHT_TERMINAL_ROWS
    )

    val SolarizedAmber = HyprDesignSpec(
        themeName = "solarized-amber",
        preset = ThemePreset.SOLARIZED_AMBER,
        isDark = true,
        bg = Color(0xFF002B36),
        surface = Color(0xDD073642),
        surfaceVariant = Color(0xFF001F27),
        borderActive = Color(0xFFB58900),
        borderInactive = Color(0xFF586E75),
        textPrimary = Color(0xFFFDF6E3),
        textSecondary = Color(0xFF93A1A1),
        textAccent = Color(0xFFCB4B16),
        activeBorderGradient = listOf(Color(0xFFB58900), Color(0xFFCB4B16)),
        fontType = HyprFontType.IBM_PLEX_MONO,
        fontFamily = HyprFontType.IBM_PLEX_MONO.fontFamily,
        cornerRadius = 8.dp,
        elementGap = 8.dp,
        borderThickness = 2.dp,
        blurRadiusDp = 14,
        glowIntensity = 0.55f,
        isOledMode = false,
        iconPack = IconPackType.RETRO_CONSOLE,
        progressStyle = ProgressBarStyle.ANALOG_TAPE_GAUGE,
        controlStyle = PlayControlStyle.TACTILE_BEVEL,
        gridStyle = GridLayoutStyle.TIGHT_TERMINAL_ROWS
    )

    val EmeraldMatrix = HyprDesignSpec(
        themeName = "emerald-matrix",
        preset = ThemePreset.EMERALD_MATRIX,
        isDark = true,
        bg = Color(0xFF080F0A),
        surface = Color(0xDD0D1810),
        surfaceVariant = Color(0xFF050A06),
        borderActive = Color(0xFF00FF66),
        borderInactive = Color(0xFF1B3322),
        textPrimary = Color(0xFFE0FFE8),
        textSecondary = Color(0xFF66AA77),
        textAccent = Color(0xFF00FF66),
        activeBorderGradient = listOf(Color(0xFF00FF66), Color(0xFF00CC44)),
        fontType = HyprFontType.RETRO_TERMINAL,
        fontFamily = HyprFontType.RETRO_TERMINAL.fontFamily,
        cornerRadius = 8.dp,
        elementGap = 8.dp,
        borderThickness = 2.dp,
        blurRadiusDp = 18,
        glowIntensity = 0.8f,
        isOledMode = false,
        iconPack = IconPackType.LUCIDE,
        progressStyle = ProgressBarStyle.MINIMAL_WAYBAR,
        controlStyle = PlayControlStyle.BRACKET_CONSOLE,
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
        ThemePreset.SOLARIZED_AMBER -> SolarizedAmber
        ThemePreset.EMERALD_MATRIX -> EmeraldMatrix
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
