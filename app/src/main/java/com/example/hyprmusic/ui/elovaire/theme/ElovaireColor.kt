package com.example.hyprmusic.ui.elovaire.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Elovaire Haute Audiophile Design Color Tokens.
 * Pure OLED night baseline, organic carbon/graphite surfaces, subtle perimeter borders,
 * and calibrated text contrasts with electric cobalt/azure accents.
 */
object ElovaireColors {
    // Pure Dark Mode (OLED)
    val Night = Color(0xFF000000)               // Primary screen background
    val Carbon = Color(0xFF0F0F12)              // Inset grouped card surface
    val Graphite = Color(0xFF18181D)            // Secondary elevated chips, pill buttons
    val SurfaceHighlight = Color(0xFF22222A)    // Active/pressed surface
    val BorderSubtle = Color(0x14FFFFFF)        // 8% white 1px perimeter border
    val BorderStrong = Color(0x28FFFFFF)        // 16% white border for active items
    val Divider = Color(0x0FFFFFFF)             // Grouped row inner separators

    // Pure Light Mode
    val Cloud = Color(0xFFFFFFFF)               // Primary screen background in light mode
    val Snow = Color(0xFFF7F7F9)                // Grouped card surface
    val Frost = Color(0xFFECECEF)               // Secondary elevated chips in light mode
    val BorderSubtleLight = Color(0x0F000000)   // 6% black perimeter border
    val DividerLight = Color(0x0A000000)

    // High Legibility Typography & Glyphs
    val TextPrimary = Color(0xFFF6F6F8)         // Primary crisp white text
    val TextSecondary = Color(0xFFA2A0AB)       // Subtitle / metadata text
    val TextMuted = Color(0xFF6B6976)           // Tertiary timestamp / track numbers
    val TextPrimaryLight = Color(0xFF0D0D11)
    val TextSecondaryLight = Color(0xFF615F6A)
    val TextMutedLight = Color(0xFF9694A0)

    // Vibrant Chromatic Accents
    val AzureAccent = Color(0xFF1162DB)         // Signature Elovaire electric azure
    val AzureGlow = Color(0x401162DB)
    val CyanAccent = Color(0xFF00C2FF)
    val PurpleAccent = Color(0xFF8A46FF)
    val DestructiveRed = Color(0xFFDE2C2C)
    val SuccessGreen = Color(0xFF13D079)
}

val ElovaireDarkColorScheme = darkColorScheme(
    primary = ElovaireColors.AzureAccent,
    onPrimary = ElovaireColors.Cloud,
    secondary = ElovaireColors.Graphite,
    onSecondary = ElovaireColors.TextPrimary,
    background = ElovaireColors.Night,
    onBackground = ElovaireColors.TextPrimary,
    surface = ElovaireColors.Carbon,
    onSurface = ElovaireColors.TextPrimary,
    surfaceVariant = ElovaireColors.Graphite,
    onSurfaceVariant = ElovaireColors.TextSecondary,
    outline = ElovaireColors.BorderSubtle,
    outlineVariant = ElovaireColors.Divider,
)

val ElovaireLightColorScheme = lightColorScheme(
    primary = ElovaireColors.AzureAccent,
    onPrimary = ElovaireColors.Cloud,
    secondary = ElovaireColors.Frost,
    onSecondary = ElovaireColors.TextPrimaryLight,
    background = ElovaireColors.Cloud,
    onBackground = ElovaireColors.TextPrimaryLight,
    surface = ElovaireColors.Snow,
    onSurface = ElovaireColors.TextPrimaryLight,
    surfaceVariant = ElovaireColors.Frost,
    onSurfaceVariant = ElovaireColors.TextSecondaryLight,
    outline = ElovaireColors.BorderSubtleLight,
    outlineVariant = ElovaireColors.DividerLight,
)
