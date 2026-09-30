package com.example.hyprmusic.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.hyprmusic.core.media.EqualizerBand
import com.example.hyprmusic.core.media.HyprEqualizer
import com.example.hyprmusic.core.theming.HyprThemeConfig
import com.example.hyprmusic.core.theming.hyprBounceClick

@Composable
fun EqualizerDialog(
    theme: HyprThemeConfig,
    onDismiss: () -> Unit
) {
    val isEnabled by HyprEqualizer.isEnabled.collectAsState()
    val bands by HyprEqualizer.bands.collectAsState()
    val currentPreset by HyprEqualizer.currentPreset.collectAsState()
    val bassBoost by HyprEqualizer.bassBoostStrength.collectAsState()
    val virtualizer by HyprEqualizer.virtualizerStrength.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(16).dp))
                    .background(theme.surfaceColor)
                    .border(
                        width = theme.borderThicknessDp.dp,
                        brush = Brush.linearGradient(theme.activeBorderGradient),
                        shape = RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(16).dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Intercept and consume clicks inside the dialog so it doesn't dismiss
                    )
                    .padding(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = theme.accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "EQUALIZER // DSP",
                                color = theme.textPrimaryColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = isEnabled,
                                onCheckedChange = { HyprEqualizer.setEnabled(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = theme.backgroundColor,
                                    checkedTrackColor = theme.accentColor,
                                    uncheckedThumbColor = theme.textSecondaryColor,
                                    uncheckedTrackColor = theme.surfaceVariantColor
                                ),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = theme.textSecondaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Presets Carousel
                    val presetScrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(presetScrollState),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HyprEqualizer.availablePresets.forEach { preset ->
                            val isSelected = preset.equals(currentPreset, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isSelected && isEnabled) theme.accentColor.copy(alpha = 0.25f)
                                        else theme.surfaceVariantColor
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected && isEnabled) theme.accentColor else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        if (isEnabled) {
                                            HyprEqualizer.applyPreset(preset)
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = preset.uppercase(),
                                    color = if (isSelected && isEnabled) theme.accentColor else theme.textSecondaryColor,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                Spacer(modifier = Modifier.height(14.dp))

                // Frequency Response Curve Visualizer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.surfaceVariantColor.copy(alpha = 0.7f))
                        .border(0.5.dp, theme.accentColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val midY = height / 2

                        // Zero reference line
                        drawLine(
                            color = Color.White.copy(alpha = 0.15f),
                            start = Offset(0f, midY),
                            end = Offset(width, midY),
                            strokeWidth = 1f
                        )

                        if (bands.isNotEmpty()) {
                            val path = Path()
                            val step = width / (bands.size + 1)

                            path.moveTo(0f, midY)
                            bands.forEachIndexed { i, band ->
                                val x = step * (i + 1)
                                val normalizedDb = (band.levelMb / 1200f).coerceIn(-1f, 1f)
                                val y = midY - (normalizedDb * (height * 0.42f))

                                if (i == 0) {
                                    val cx = x / 2
                                    path.cubicTo(cx, midY, cx, y, x, y)
                                } else {
                                    val prevX = step * i
                                    val prevNorm = (bands[i - 1].levelMb / 1200f).coerceIn(-1f, 1f)
                                    val prevY = midY - (prevNorm * (height * 0.42f))
                                    val cx = (prevX + x) / 2
                                    path.cubicTo(cx, prevY, cx, y, x, y)
                                }
                            }
                            val lastX = step * bands.size
                            val lastNorm = (bands.last().levelMb / 1200f).coerceIn(-1f, 1f)
                            val lastY = midY - (lastNorm * (height * 0.42f))
                            val finalCx = (lastX + width) / 2
                            path.cubicTo(finalCx, lastY, finalCx, midY, width, midY)

                            drawPath(
                                path = path,
                                color = if (isEnabled) theme.accentColor else Color.Gray.copy(alpha = 0.4f),
                                style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5-Band Vertical Studio Console Faders
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(155.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    bands.forEach { band ->
                        VerticalEqualizerFader(
                            band = band,
                            isEnabled = isEnabled,
                            theme = theme,
                            onLevelChange = { newLevel ->
                                HyprEqualizer.setBandLevel(band.bandIndex, newLevel)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Acoustic Enhancement Sliders: Bass Boost & 3D Surround
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Bass Boost Card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.surfaceVariantColor)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "BASS BOOST",
                                color = theme.textSecondaryColor,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${bassBoost / 10}%",
                                color = if (isEnabled) theme.accentColor else theme.textSecondaryColor,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Slider(
                            value = bassBoost.toFloat(),
                            onValueChange = { HyprEqualizer.setBassBoost(it.toInt()) },
                            valueRange = 0f..1000f,
                            enabled = isEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = theme.accentColor,
                                activeTrackColor = theme.accentColor,
                                inactiveTrackColor = theme.surfaceColor,
                                disabledThumbColor = Color.Gray,
                                disabledActiveTrackColor = Color.DarkGray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                        )
                    }

                    // 3D Surround Card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.surfaceVariantColor)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "3D SURROUND",
                                color = theme.textSecondaryColor,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${virtualizer / 10}%",
                                color = if (isEnabled) theme.accentColor else theme.textSecondaryColor,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Slider(
                            value = virtualizer.toFloat(),
                            onValueChange = { HyprEqualizer.setVirtualizer(it.toInt()) },
                            valueRange = 0f..1000f,
                            enabled = isEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = theme.accentColor,
                                activeTrackColor = theme.accentColor,
                                inactiveTrackColor = theme.surfaceColor,
                                disabledThumbColor = Color.Gray,
                                disabledActiveTrackColor = Color.DarkGray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                        )
                    }
                }
            }
        }
    }
}
}

/**
 * Custom tactile vertical fader matching physical studio hardware consoles.
 * Supports smooth vertical drag gestures and tap-to-level.
 */
@Composable
private fun VerticalEqualizerFader(
    band: EqualizerBand,
    isEnabled: Boolean,
    theme: HyprThemeConfig,
    onLevelChange: (Short) -> Unit,
    modifier: Modifier = Modifier
) {
    val minDb = -1200f
    val maxDb = 1200f
    val currentLevel = band.levelMb.toFloat().coerceIn(minDb, maxDb)

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Gain dB readout
        val dbValue = (currentLevel / 100).toInt()
        Text(
            text = "${if (dbValue > 0) "+" else ""}${dbValue}dB",
            color = if (isEnabled && currentLevel != 0f) theme.accentColor else theme.textSecondaryColor,
            fontSize = 9.5.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (currentLevel != 0f) FontWeight.Bold else FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Vertical Track & Thumb Box
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .width(42.dp)
                .pointerInput(isEnabled) {
                    if (!isEnabled) return@pointerInput
                    detectDragGestures { change, _ ->
                        change.consume()
                        val h = size.height.toFloat()
                        val y = change.position.y.coerceIn(0f, h)
                        // Top is +12dB, Bottom is -12dB
                        val fraction = if (h > 0f) 1f - (y / h) else 0.5f
                        val newDb = minDb + (fraction * (maxDb - minDb))
                        onLevelChange(newDb.toInt().toShort())
                    }
                }
                .pointerInput(isEnabled) {
                    if (!isEnabled) return@pointerInput
                    detectTapGestures { offset ->
                        val h = size.height.toFloat()
                        val y = offset.y.coerceIn(0f, h)
                        val fraction = if (h > 0f) 1f - (y / h) else 0.5f
                        val newDb = minDb + (fraction * (maxDb - minDb))
                        onLevelChange(newDb.toInt().toShort())
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            val totalHeight = maxHeight
            val density = LocalDensity.current

            // Vertical Track Slot
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(theme.surfaceVariantColor)
            )

            // Center 0 dB Reference Tick
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(1.5.dp)
                    .align(Alignment.Center)
                    .background(Color.White.copy(alpha = 0.2f))
            )

            // Dynamic Fader Knob Thumb
            val thumbHeightDp = 18.dp
            val thumbWidthDp = 28.dp

            val normalizedFraction = ((currentLevel - minDb) / (maxDb - minDb)).coerceIn(0f, 1f)
            val thumbOffsetYDp = with(density) {
                val availableTravel = (totalHeight - thumbHeightDp).toPx()
                val offsetPx = (1f - normalizedFraction) * availableTravel
                offsetPx.toDp()
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset {
                        IntOffset(x = 0, y = with(density) { thumbOffsetYDp.roundToPx() })
                    }
                    .size(width = thumbWidthDp, height = thumbHeightDp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isEnabled) theme.surfaceColor else Color.DarkGray)
                    .border(
                        width = 1.dp,
                        color = if (isEnabled) theme.accentColor else Color.Gray,
                        shape = RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Grip Line on Knob
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .height(2.dp)
                        .background(if (isEnabled) theme.accentColor else Color.Gray)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Center Frequency
        Text(
            text = band.formattedFreq,
            color = theme.textSecondaryColor,
            fontSize = 9.5.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}
