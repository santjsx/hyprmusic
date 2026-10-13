package com.example.hyprmusic.ui.dap.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * High-fidelity color tokens representing the retro-modern portable DAP
 * and high-contrast virtual LCD display.
 */
@Immutable
object DapColors {
    // Physical Chassis Bodies (supports customization / IKEA Effect)
    val ChassisGraphite = Color(0xFF1E232B)       // Iconic dark blue-gray graphite
    val ChassisTitanium = Color(0xFF262C36)       // Anodized dark titanium
    val ChassisObsidian = Color(0xFF13161C)       // Deep matte obsidian
    val ChassisBorder = Color(0xFF2F3744)         // Chamfered bevel edge highlight
    val ChassisGlow = Color(0x1A6495ED)           // Subtle blue atmospheric chassis bloom

    // Display Bezel Framing
    val BezelBackground = Color(0xFF090C12)       // Near-black recessed frame
    val BezelBorder = Color(0xFF151A24)           // Bezel inner groove border

    // Virtual LCD Screen (Deep Navy tonal palette)
    val ScreenBgTop = Color(0xFF0A1526)           // Midnight navy gradient top
    val ScreenBgBottom = Color(0xFF040A14)        // Deep navy/black gradient bottom
    val ScreenCard = Color(0x331F3252)            // Translucent rounded pill background
    val ScreenCardBorder = Color(0x4D4B6A97)      // Subtle gray-blue divider/border

    // Tactile Click Wheel
    val WheelSurface = Color(0xFF12151B)          // Matte charcoal black disc
    val WheelBorder = Color(0xFF222833)           // Outer disc boundary ring
    val WheelCenter = Color(0xFF191D25)           // Recessed center circular button
    val WheelCenterBorder = Color(0xFF2B3342)     // Center button recessed border
    val WheelIcon = Color(0xEEFFFFFF)             // High-contrast clean white icons
    val WheelPressed = Color(0xFF272E3B)          // Tactile press state feedback

    // Screen Typography & Indicator Tokens
    val TextPrimary = Color(0xFFF5F7FA)           // Crisp white for song titles & active states
    val TextSecondary = Color(0xFF8296B0)         // Muted cyan/blue-gray for artist/album
    val TextMuted = Color(0xFF4E6078)             // Dim labels & secondary metadata
    val TextTertiary = Color(0xFF4E6078)          // Tertiary subdued text & icons
    val AccentIceBlue = Color(0xFF6DA5FF)         // Active highlight, Dolby Atmos & star fill
    val ExplicitBadgeBg = Color(0x338296B0)       // [E] badge background
    val ExplicitBadgeText = Color(0xFFB0C4DE)     // [E] badge text

    // Scrubber / Seek Bar
    val ProgressTrack = Color(0xFF182436)         // Dark desaturated blue-gray background track
    val ProgressFill = Color(0xFFDCEBFA)          // Pale blue-white active progress fill
    val ProgressThumb = Color(0xFFFFFFFF)         // Micro scrub thumb indicator
}
