package com.example.hyprmusic.ui.elovaire.screens

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.RestartAlt
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hyprmusic.core.media.DolbyPresetProfile
import com.example.hyprmusic.core.media.EqualizerBand
import com.example.hyprmusic.core.media.HyprEqualizer
import com.example.hyprmusic.ui.elovaire.components.ElovaireCard
import com.example.hyprmusic.ui.elovaire.components.GroupedListRowContainer
import com.example.hyprmusic.ui.elovaire.motion.elovairePressScale
import com.example.hyprmusic.ui.elovaire.theme.ElovaireColors
import com.example.hyprmusic.ui.elovaire.theme.ElovaireRadii
import com.example.hyprmusic.ui.elovaire.theme.ElovaireSpacing
import kotlin.math.roundToInt

/**
 * Flagship Elovaire Audiophile Equalizer & DSP Bottom Sheet.
 * Features:
 * 1. Master power switch with dynamic glow (Bypass vs Active DSP)
 * 2. Frequency Response Curve Spline Canvas (31Hz - 16kHz Bezier interpolation)
 * 3. 5-Band Vertical Tactile Sliders (-12dB to +12dB) with zero-detent haptics
 * 4. Genre Preset Chips carousel
 * 5. One-Tap Hardware Acoustic Profile Cards (Buds, Studio Open-Back, Cinema, etc.)
 * 6. Macro DSP dials: Bass Boost & Spatial Width
 */
@Composable
fun ElovaireEqualizerSheet(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEnabled by HyprEqualizer.isEnabled.collectAsStateWithLifecycle()
    val bands by HyprEqualizer.bands.collectAsStateWithLifecycle()
    val currentPreset by HyprEqualizer.currentPreset.collectAsStateWithLifecycle()
    val bassBoost by HyprEqualizer.bassBoostStrength.collectAsStateWithLifecycle()
    val spatialStrength by HyprEqualizer.spatialStrength.collectAsStateWithLifecycle()
    val isDolbyEnabled by HyprEqualizer.isDolbyEnabled.collectAsStateWithLifecycle()
    val currentDolbyProfile by HyprEqualizer.currentDolbyProfile.collectAsStateWithLifecycle()
    val isLimiterEngaged by HyprEqualizer.isLimiterEngaged.collectAsStateWithLifecycle()
    val view = LocalView.current

    BackHandler(enabled = true) {
        onClose()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = ElovaireRadii.sheet, topEnd = ElovaireRadii.sheet))
            .background(ElovaireColors.Night)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Drag Handle & Master Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ElovaireColors.Carbon)
                    .padding(top = 10.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(ElovaireColors.BorderStrong)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ElovaireSpacing.screenHorizontal),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(ElovaireRadii.tile))
                                .background(if (isEnabled) ElovaireColors.AzureGlow else ElovaireColors.Graphite),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = if (isEnabled) ElovaireColors.AzureAccent else ElovaireColors.TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Studio DSP Equalizer",
                                    color = ElovaireColors.TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                if (isLimiterEngaged) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(ElovaireColors.DestructiveRed.copy(alpha = 0.2f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "LIMITER",
                                            color = ElovaireColors.DestructiveRed,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Text(
                                text = if (isEnabled) "Hardware OpenSL ES Engine Active" else "Bypass (100% Bit-Perfect Audio)",
                                color = if (isEnabled) ElovaireColors.AzureAccent else ElovaireColors.TextMuted,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Power toggle switch
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                HyprEqualizer.setEnabled(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ElovaireColors.Cloud,
                                checkedTrackColor = ElovaireColors.AzureAccent,
                                uncheckedThumbColor = ElovaireColors.TextMuted,
                                uncheckedTrackColor = ElovaireColors.Graphite
                            )
                        )

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = ElovaireColors.TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Scrollable Equalizer Modules
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = ElovaireSpacing.screenHorizontal),
                contentPadding = PaddingValues(top = 14.dp, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Bezier Frequency Response Curve Spline
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FREQUENCY RESPONSE CURVE",
                                color = ElovaireColors.TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            Text(
                                text = currentPreset.uppercase(),
                                color = if (isEnabled) ElovaireColors.AzureAccent else ElovaireColors.TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        ElovaireFrequencyCurve(
                            bands = bands,
                            isEnabled = isEnabled,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                        )
                    }
                }

                // 2. 5-Band Vertical Tactile Sliders
                item {
                    ElovaireCard(
                        backgroundColor = ElovaireColors.Carbon,
                        borderColor = ElovaireColors.BorderSubtle,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                bands.forEach { band ->
                                    ElovaireVerticalFader(
                                        band = band,
                                        isEnabled = isEnabled,
                                        onLevelChange = { newLevel ->
                                            HyprEqualizer.setBandLevel(band.bandIndex, newLevel)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Reset to flat pill
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(ElovaireRadii.pill))
                                    .background(ElovaireColors.Graphite)
                                    .elovairePressScale()
                                    .clickable {
                                        if (isEnabled) {
                                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                            HyprEqualizer.applyPreset("Flat")
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RestartAlt,
                                        contentDescription = null,
                                        tint = ElovaireColors.TextSecondary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "Reset All Bands to Flat (0 dB)",
                                        color = ElovaireColors.TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Preset Curves Horizontal Carousel
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "PRESET SOUND PROFILES",
                            color = ElovaireColors.TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        val presetScrollState = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(presetScrollState),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HyprEqualizer.availablePresets.forEach { preset ->
                                val isSelected = preset.equals(currentPreset, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(ElovaireRadii.pill))
                                        .background(
                                            if (isSelected && isEnabled) ElovaireColors.AzureAccent
                                            else ElovaireColors.Carbon
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected && isEnabled) ElovaireColors.AzureAccent else ElovaireColors.BorderSubtle,
                                            shape = RoundedCornerShape(ElovaireRadii.pill)
                                        )
                                        .elovairePressScale(pressedScale = 0.92f)
                                        .clickable {
                                            if (isEnabled) {
                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                HyprEqualizer.applyPreset(preset)
                                            }
                                        }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = preset,
                                        color = if (isSelected && isEnabled) ElovaireColors.Cloud else ElovaireColors.TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. One-Tap Hardware Acoustic Matrix
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "HARDWARE ACOUSTIC MATRIX",
                                color = ElovaireColors.TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "DOLBY SPATIAL",
                                color = ElovaireColors.AzureAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DolbyPresetProfile.entries.forEach { profile ->
                            val isSelected = currentDolbyProfile == profile && isDolbyEnabled
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(ElovaireRadii.card))
                                    .background(if (isSelected && isEnabled) ElovaireColors.Graphite else ElovaireColors.Carbon)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected && isEnabled) ElovaireColors.AzureAccent else ElovaireColors.BorderSubtle,
                                        shape = RoundedCornerShape(ElovaireRadii.card)
                                    )
                                    .elovairePressScale()
                                    .clickable {
                                        if (isEnabled) {
                                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                            HyprEqualizer.setDolbyEnabled(true)
                                            HyprEqualizer.setDolbyProfile(profile)
                                        }
                                    }
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected && isEnabled) ElovaireColors.AzureAccent else ElovaireColors.Graphite),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Headphones,
                                                contentDescription = null,
                                                tint = if (isSelected && isEnabled) ElovaireColors.Cloud else ElovaireColors.TextSecondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = profile.displayName,
                                                color = if (isSelected && isEnabled) ElovaireColors.AzureAccent else ElovaireColors.TextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = profile.subtitle,
                                                color = ElovaireColors.TextSecondary,
                                                fontSize = 11.5.sp
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ElovaireColors.Graphite)
                                            .padding(horizontal = 7.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "${(profile.preCutGain * 100).toInt()}% GAIN",
                                            color = ElovaireColors.TextMuted,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }

                // 5. Macro Sonic Dials (Bass Boost & Spatial Width)
                item {
                    ElovaireCard(
                        backgroundColor = ElovaireColors.Carbon,
                        borderColor = ElovaireColors.BorderSubtle,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "ACOUSTIC ENHANCEMENT CONTROLS",
                                color = ElovaireColors.TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            // Bass Boost Slider
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Sub-Bass Punch",
                                        color = ElovaireColors.TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${bassBoost / 10}%",
                                        color = ElovaireColors.AzureAccent,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Slider(
                                    value = bassBoost.toFloat(),
                                    onValueChange = {
                                        if (isEnabled) {
                                            HyprEqualizer.setBassBoost(it.roundToInt())
                                        }
                                    },
                                    valueRange = 0f..1000f,
                                    enabled = isEnabled,
                                    colors = SliderDefaults.colors(
                                        thumbColor = ElovaireColors.Cloud,
                                        activeTrackColor = ElovaireColors.AzureAccent,
                                        inactiveTrackColor = ElovaireColors.Graphite
                                    )
                                )
                            }

                            // Spatial Stage Slider
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Spatial Soundstage Width",
                                        color = ElovaireColors.TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${spatialStrength / 10}%",
                                        color = ElovaireColors.AzureAccent,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Slider(
                                    value = spatialStrength.toFloat(),
                                    onValueChange = {
                                        if (isEnabled) {
                                            HyprEqualizer.setSpatialStrength(it.roundToInt())
                                        }
                                    },
                                    valueRange = 0f..1000f,
                                    enabled = isEnabled,
                                    colors = SliderDefaults.colors(
                                        thumbColor = ElovaireColors.Cloud,
                                        activeTrackColor = ElovaireColors.AzureAccent,
                                        inactiveTrackColor = ElovaireColors.Graphite
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Curved Frequency Response Graph using Bezier Spline.
 */
@Composable
private fun ElovaireFrequencyCurve(
    bands: List<EqualizerBand>,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(ElovaireRadii.card))
            .background(ElovaireColors.Carbon)
            .border(1.dp, ElovaireColors.BorderSubtle, RoundedCornerShape(ElovaireRadii.card))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val midY = height / 2

            // Reference zero lines
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(0f, midY - height * 0.28f),
                end = Offset(width, midY - height * 0.28f),
                strokeWidth = 1f
            )
            drawLine(
                color = Color.White.copy(alpha = 0.12f),
                start = Offset(0f, midY),
                end = Offset(width, midY),
                strokeWidth = 1f
            )
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(0f, midY + height * 0.28f),
                end = Offset(width, midY + height * 0.28f),
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
                    val y = midY - (normalizedDb * (height * 0.42f))

                    if (i == 0) {
                        val cx = x / 2
                        strokePath.cubicTo(cx, midY, cx, y, x, y)
                        fillPath.cubicTo(cx, midY, cx, y, x, y)
                    } else {
                        val prevX = step * i
                        val prevNorm = (bands[i - 1].levelMb / 1200f).coerceIn(-1f, 1f)
                        val prevY = midY - (prevNorm * (height * 0.42f))
                        val cx = (prevX + x) / 2
                        strokePath.cubicTo(cx, prevY, cx, y, x, y)
                        fillPath.cubicTo(cx, prevY, cx, y, x, y)
                    }
                }

                val lastX = step * bands.size
                val lastNorm = (bands.last().levelMb / 1200f).coerceIn(-1f, 1f)
                val lastY = midY - (lastNorm * (height * 0.42f))
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
                            if (isEnabled) ElovaireColors.AzureAccent.copy(alpha = 0.28f) else Color.Transparent,
                            Color.Transparent
                        )
                    )
                )

                drawPath(
                    path = strokePath,
                    color = if (isEnabled) ElovaireColors.AzureAccent else ElovaireColors.TextMuted,
                    style = Stroke(width = 2.4f, cap = StrokeCap.Round)
                )
            }
        }
    }
}

/**
 * Tactile Studio Vertical Fader with real-time level readout and detent vibration.
 */
@Composable
private fun ElovaireVerticalFader(
    band: EqualizerBand,
    isEnabled: Boolean,
    onLevelChange: (Short) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val minDb = -1200f
    val maxDb = 1200f
    val currentLevel = band.levelMb.toFloat().coerceIn(minDb, maxDb)

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Gain readout badge
        val dbValue = (currentLevel / 100f)
        val dbText = if (dbValue > 0) "+%.1f".format(dbValue) else "%.1f".format(dbValue)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(ElovaireRadii.pill))
                .background(if (isEnabled && dbValue != 0f) ElovaireColors.AzureGlow else ElovaireColors.Graphite)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "${dbText}dB",
                color = if (isEnabled && dbValue != 0f) ElovaireColors.AzureAccent else ElovaireColors.TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Vertical Slider Track
        Box(
            modifier = Modifier
                .weight(1f)
                .width(36.dp)
                .pointerInput(band.bandIndex, isEnabled) {
                    if (!isEnabled) return@pointerInput
                    detectTapGestures { offset ->
                        val trackHeight = size.height
                        val fraction = 1f - (offset.y / trackHeight).coerceIn(0f, 1f)
                        val newDb = (minDb + fraction * (maxDb - minDb)).roundToInt().toShort()
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onLevelChange(newDb)
                    }
                }
                .pointerInput(band.bandIndex, isEnabled) {
                    if (!isEnabled) return@pointerInput
                    detectDragGestures { change, _ ->
                        change.consume()
                        val trackHeight = size.height
                        val fraction = 1f - (change.position.y / trackHeight).coerceIn(0f, 1f)
                        val newDb = (minDb + fraction * (maxDb - minDb)).roundToInt().toShort()
                        onLevelChange(newDb)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Track rail line
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(ElovaireColors.Graphite)
            )

            // Center zero detent notch
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(2.dp)
                    .background(ElovaireColors.BorderStrong)
            )

            // Floating Fader Thumb
            val normalizedPos = ((currentLevel - minDb) / (maxDb - minDb)).coerceIn(0f, 1f)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val thumbY = (1f - normalizedPos) * size.height
                val thumbWidth = 26.dp.toPx()
                val thumbHeight = 12.dp.toPx()

                drawRoundRect(
                    color = if (isEnabled) ElovaireColors.Cloud else ElovaireColors.TextMuted,
                    topLeft = Offset((size.width - thumbWidth) / 2f, thumbY - thumbHeight / 2f),
                    size = androidx.compose.ui.geometry.Size(thumbWidth, thumbHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )

                // Thumb center groove
                drawLine(
                    color = ElovaireColors.Night,
                    start = Offset((size.width - thumbWidth) / 2f + 4.dp.toPx(), thumbY),
                    end = Offset((size.width + thumbWidth) / 2f - 4.dp.toPx(), thumbY),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Band Frequency Label
        Text(
            text = band.formattedFreq,
            color = ElovaireColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
