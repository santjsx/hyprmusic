package com.example.hyprmusic.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hyprmusic.core.media.EqualizerBand
import com.example.hyprmusic.core.media.HyprEqualizer
import com.example.hyprmusic.core.theming.HyprThemeConfig
import com.example.hyprmusic.core.theming.hyprBounceClick

@Composable
fun EqualizerDialog(
    theme: HyprThemeConfig,
    onDismiss: () -> Unit
) {
    val isEnabled by HyprEqualizer.isEnabled.collectAsStateWithLifecycle()
    val bands by HyprEqualizer.bands.collectAsStateWithLifecycle()
    val currentPreset by HyprEqualizer.currentPreset.collectAsStateWithLifecycle()
    val bassBoost by HyprEqualizer.bassBoostStrength.collectAsStateWithLifecycle()
    val virtualizer by HyprEqualizer.virtualizerStrength.collectAsStateWithLifecycle()
    val isDolbyEnabled by HyprEqualizer.isDolbyEnabled.collectAsStateWithLifecycle()
    val spatialStrength by HyprEqualizer.spatialStrength.collectAsStateWithLifecycle()
    val isLimiterEngaged by HyprEqualizer.isLimiterEngaged.collectAsStateWithLifecycle()

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
                    .heightIn(max = 680.dp)
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
                        onClick = {} // Intercept clicks inside dialog
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
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
                            Column {
                                Text(
                                    text = "EQUALIZER // CINEMA DSP",
                                    color = theme.textPrimaryColor,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = if (!isEnabled) {
                                        "BIT-PERFECT DIRECT"
                                    } else if (isDolbyEnabled) {
                                        "DOLBY CINEMA MULTI-BAND ENGINE"
                                    } else {
                                        "HARDWARE DSP ACTIVE"
                                    },
                                    color = if (!isEnabled) theme.textSecondaryColor else theme.accentColor,
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
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

                    // One-Tap Dolby Studio Remastering Hero Section
                    DolbyMasteringSelector(
                        isDolbyActive = isDolbyEnabled,
                        isEnabled = isEnabled,
                        isLimiterEngaged = isLimiterEngaged,
                        spatialStrength = spatialStrength,
                        theme = theme,
                        onToggleDolby = { HyprEqualizer.setDolbyEnabled(it) },
                        onApplyHybridCurve = { HyprEqualizer.applyDolbyCinemaTuning() },
                        onSpatialStrengthChange = { HyprEqualizer.setSpatialStrength(it) }
                    )

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
                                    .padding(horizontal = 9.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = preset.uppercase(),
                                    color = if (isSelected && isEnabled) theme.accentColor else theme.textSecondaryColor,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Frequency Response Curve Visualizer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.surfaceVariantColor.copy(alpha = 0.7f))
                            .border(0.5.dp, theme.accentColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height
                            val midY = height / 2

                            // Reference grid lines
                            drawLine(
                                color = Color.White.copy(alpha = 0.08f),
                                start = Offset(0f, midY - height * 0.25f),
                                end = Offset(width, midY - height * 0.25f),
                                strokeWidth = 1f
                            )
                            drawLine(
                                color = Color.White.copy(alpha = 0.16f),
                                start = Offset(0f, midY),
                                end = Offset(width, midY),
                                strokeWidth = 1.2f
                            )
                            drawLine(
                                color = Color.White.copy(alpha = 0.08f),
                                start = Offset(0f, midY + height * 0.25f),
                                end = Offset(width, midY + height * 0.25f),
                                strokeWidth = 1f
                            )

                            if (bands.isNotEmpty()) {
                                val strokePath = Path()
                                val fillPath = Path()
                                val step = width / (bands.size + 1)

                                strokePath.moveTo(0f, midY)
                                fillPath.moveTo(0f, midY)

                                bands.forEachIndexed { i, band ->
                                    val x = step * (i + 1)
                                    val normalizedDb = (band.levelMb / 1200f).coerceIn(-1f, 1f)
                                    val y = midY - (normalizedDb * (height * 0.40f))

                                    if (i == 0) {
                                        val cx = x / 2
                                        strokePath.cubicTo(cx, midY, cx, y, x, y)
                                        fillPath.cubicTo(cx, midY, cx, y, x, y)
                                    } else {
                                        val prevX = step * i
                                        val prevNorm = (bands[i - 1].levelMb / 1200f).coerceIn(-1f, 1f)
                                        val prevY = midY - (prevNorm * (height * 0.40f))
                                        val cx = (prevX + x) / 2
                                        strokePath.cubicTo(cx, prevY, cx, y, x, y)
                                        fillPath.cubicTo(cx, prevY, cx, y, x, y)
                                    }
                                }

                                val lastX = step * bands.size
                                val lastNorm = (bands.last().levelMb / 1200f).coerceIn(-1f, 1f)
                                val lastY = midY - (lastNorm * (height * 0.40f))
                                val finalCx = (lastX + width) / 2
                                strokePath.cubicTo(finalCx, lastY, finalCx, midY, width, midY)
                                fillPath.cubicTo(finalCx, lastY, finalCx, midY, width, midY)

                                fillPath.lineTo(width, height)
                                fillPath.lineTo(0f, height)
                                fillPath.close()

                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            if (isEnabled) theme.accentColor.copy(alpha = 0.22f) else Color.Transparent,
                                            Color.Transparent
                                        )
                                    )
                                )

                                drawPath(
                                    path = strokePath,
                                    color = if (isEnabled) theme.accentColor else Color.Gray.copy(alpha = 0.4f),
                                    style = Stroke(width = 2.2f, cap = StrokeCap.Round)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5-Band Vertical Studio Console Faders
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(145.dp),
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

                    Spacer(modifier = Modifier.height(12.dp))

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
                                .padding(9.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "BASS PUNCH",
                                    color = theme.textSecondaryColor,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${bassBoost / 10}%",
                                    color = if (isEnabled) theme.accentColor else theme.textSecondaryColor,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
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
                                    .height(28.dp)
                            )
                        }

                        // 3D Spatial Surround Card
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(theme.surfaceVariantColor)
                                .padding(9.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "3D SPATIAL",
                                    color = theme.textSecondaryColor,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${virtualizer / 10}%",
                                    color = if (isEnabled) theme.accentColor else theme.textSecondaryColor,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
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
                                    .height(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * One-Tap Dolby Studio Remastering Hero Selector.
 * Isolates complex audio configurations into clean, beautifully responsive states.
 */
@Composable
fun DolbyMasteringSelector(
    isDolbyActive: Boolean,
    isEnabled: Boolean,
    isLimiterEngaged: Boolean,
    spatialStrength: Int,
    theme: HyprThemeConfig,
    onToggleDolby: (Boolean) -> Unit,
    onApplyHybridCurve: () -> Unit,
    onSpatialStrengthChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (isDolbyActive && isEnabled) theme.accentColor.copy(alpha = 0.16f) else theme.surfaceVariantColor.copy(alpha = 0.70f),
        label = "DolbyBgAnimation"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isDolbyActive && isEnabled) theme.accentColor.copy(alpha = 0.75f) else theme.inactiveBorderColor.copy(alpha = 0.35f),
        label = "DolbyBorderAnimation"
    )
    val titleColor by animateColorAsState(
        targetValue = if (isDolbyActive && isEnabled) theme.accentColor else theme.textPrimaryColor,
        label = "DolbyTitleAnimation"
    )
    val textColor by animateColorAsState(
        targetValue = if (isDolbyActive && isEnabled) theme.textPrimaryColor else theme.textSecondaryColor,
        label = "DolbyTextAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .border(1.2.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(13.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .hyprBounceClick {
                        onToggleDolby(!(isDolbyActive && isEnabled))
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isDolbyActive && isEnabled) theme.accentColor else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Dolby Studio Remastering",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = titleColor
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (isDolbyActive && isEnabled)
                            "Adaptive 3D soundstage & active distortion prevention enabled."
                        else
                            "Standard flat playback. Tap to optimize acoustic balance.",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = textColor
                    )
                }

                Switch(
                    checked = isDolbyActive && isEnabled,
                    onCheckedChange = { onToggleDolby(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = theme.backgroundColor,
                        checkedTrackColor = theme.accentColor,
                        uncheckedThumbColor = theme.textSecondaryColor,
                        uncheckedTrackColor = theme.surfaceColor
                    ),
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Unbreakable Shield DRC & Headroom Telemetry
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (isDolbyActive && isEnabled) Color(0xFF00E676) else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "HEADROOM: -3.1dB (0.70x)",
                        color = if (isDolbyActive && isEnabled) Color(0xFF00E676) else Color.Gray,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (isLimiterEngaged) Color(0xFF00E5FF) else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isLimiterEngaged) "DRC LIMITER: ACTIVE" else "DRC: STANDBY",
                        color = if (isLimiterEngaged) Color(0xFF00E5FF) else Color.Gray,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (isDolbyActive && isEnabled) theme.accentColor else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val soundstageLabel = when {
                        !isEnabled || !isDolbyActive -> "DIRECT"
                        spatialStrength < 250 -> "DIRECT"
                        spatialStrength < 500 -> "WIDE"
                        spatialStrength < 750 -> "THEATER"
                        else -> "360 DOME"
                    }
                    Text(
                        text = "ACOUSTICS: $soundstageLabel",
                        color = if (isDolbyActive && isEnabled) theme.accentColor else Color.Gray,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (isDolbyActive && isEnabled) {
                Spacer(modifier = Modifier.height(10.dp))

                // Soundstage Width Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SOUNDSTAGE EXPANSION",
                        color = theme.textSecondaryColor,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${spatialStrength / 10}%",
                        color = theme.accentColor,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = spatialStrength.toFloat(),
                    onValueChange = { onSpatialStrengthChange(it.toInt()) },
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
                        .height(28.dp)
                )

                // Harmon/Dolby Hybrid Curve Action Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(theme.surfaceColor)
                        .border(0.8.dp, theme.accentColor.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                        .hyprBounceClick {
                            onApplyHybridCurve()
                        }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RE-APPLY HARMON/DOLBY HYBRID CURVE (-2dB MUD SCOOP / +6dB SUB)",
                        color = theme.accentColor,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
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
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (currentLevel != 0f) FontWeight.Bold else FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Vertical Track & Thumb Box
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .width(40.dp)
                .pointerInput(isEnabled) {
                    if (!isEnabled) return@pointerInput
                    detectDragGestures { change, _ ->
                        change.consume()
                        val h = size.height.toFloat()
                        val y = change.position.y.coerceIn(0f, h)
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
            val thumbWidthDp = 26.dp

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
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}
