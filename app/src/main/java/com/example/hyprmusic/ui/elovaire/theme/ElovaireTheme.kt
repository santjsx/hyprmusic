package com.example.hyprmusic.ui.elovaire.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun ElovaireTheme(
    darkTheme: Boolean = true, // Default to deep OLED Dark Mode
    textScaleFactor: Float = 1f,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ElovaireDarkColorScheme else ElovaireLightColorScheme
    val typography = elovaireTypography(textScaleFactor)
    val shapes = elovaireShapes()

    CompositionLocalProvider(LocalTextScale provides textScaleFactor) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}
