package com.example.hyprmusic.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
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
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hyprmusic.core.media.DolbyPresetProfile
import com.example.hyprmusic.core.media.EqualizerBand
import com.example.hyprmusic.core.media.HyprEqualizer
import com.example.hyprmusic.core.theming.HyprThemeConfig
import com.example.hyprmusic.core.theming.hyprBounceClick

/**
 * Operating mode tabs for the Dual-Tier Equalizer Suite.
 */
enum class EqualizerTab {
    SMART, // Smart Acoustic: 1-tap hardware targets & macro dynamics
    PRO    // Pro Console: 5-band vertical faders & frequency curve
}

/**
 * Production-Grade Studio Equalizer & DSP Mastering Dialog.
 * Features a dual-tier architecture that eliminates vertical scrolling,
 * prevents touch conflicts, avoids background bleed, and offers both
 * effortless smart profiles and precision audiophile console control.
 */
@Composable
fun EqualizerDialog(
    theme: HyprThemeConfig,
    onDismiss: () -> Unit
) {
    val isEnabled by HyprEqualizer.isEnabled.collectAsStateWithLifecycle()
    val bands by HyprEqualizer.bands.collectAsStateWithLifecycle()
    val currentPreset by HyprEqualizer.currentPreset.collectAsStateWithLifecycle()
    val bassBoost by HyprEqualizer.bassBoostStrength.collectAsStateWithLifecycle()
    val spatialStrength by HyprEqualizer.spatialStrength.collectAsStateWithLifecycle()
    val isDolbyEnabled by HyprEqualizer.isDolbyEnabled.collectAsStateWithLifecycle()
    val activeProfile by HyprEqualizer.currentDolbyProfile.collectAsStateWithLifecycle()
    val isLimiterEngaged by HyprEqualizer.isLimiterEngaged.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(EqualizerTab.SMART) }

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
            // Opaque, solid-backed dialog container to completely prevent background bleed
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(16).dp))
                    .background(theme.surfaceColor.copy(alpha = 1.0f))
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Unified Master Header Bar
                    MasterDspHeader(
                        isEnabled = isEnabled,
                        isDolbyEnabled = isDolbyEnabled,
                        activeProfile = activeProfile,
                        theme = theme,
                        onToggle = { HyprEqualizer.setEnabled(it) },
                        onDismiss = onDismiss
                    )

                    // 2. Segmented Mode Switcher (Smart Acoustic vs Pro Console)
                    SegmentedModeSwitcher(
                        currentTab = currentTab,
                        theme = theme,
                        onTabSelect = { currentTab = it }
                    )

                    // 3. Tab Content Area (Zero-Scroll bounded layout)
                    if (currentTab == EqualizerTab.SMART) {
                        SmartAcousticView(
                            activeProfile = activeProfile,
                            isEnabled = isEnabled,
                            bassBoost = bassBoost,
                            spatialStrength = spatialStrength,
                            theme = theme,
                            onSelectProfile = { profile ->
                                HyprEqualizer.setDolbyProfile(profile)
                            },
                            onBassBoostChange = { HyprEqualizer.setBassBoost(it) },
                            onSpatialChange = { HyprEqualizer.setSpatialStrength(it) },
                            onReapplyMatrix = {
                                HyprEqualizer.setDolbyProfile(activeProfile)
                            }
                        )
                    } else {
                        ProConsoleView(
                            bands = bands,
                            currentPreset = currentPreset,
                            isEnabled = isEnabled,
                            theme = theme,
                            onBandChange = { bandIndex, newLevel ->
                                HyprEqualizer.setBandLevel(bandIndex, newLevel)
                            },
                            onApplyPreset = { preset ->
                                if (isEnabled) {
                                    HyprEqualizer.applyPreset(preset)
                                }
                            },
                            onResetToFlat = {
                                if (isEnabled) {
                                    HyprEqualizer.applyPreset("Flat")
                                }
                            }
                        )
                    }

                    // 4. Unified Bottom Telemetry HUD
                    TelemetryHud(
                        activeProfile = activeProfile,
                        isLimiterEngaged = isLimiterEngaged,
                        isEnabled = isEnabled,
                        theme = theme
                    )
                }
            }
        }
    }
}

/**
 * Unified Master DSP Header with single power switch.
 */
@Composable
private fun MasterDspHeader(
    isEnabled: Boolean,
    isDolbyEnabled: Boolean,
    activeProfile: DolbyPresetProfile,
    theme: HyprThemeConfig,
    onToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isEnabled) theme.accentColor.copy(alpha = 0.18f) else theme.surfaceVariantColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = if (isEnabled) theme.accentColor else theme.textSecondaryColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "HYPR AUDIO DSP // MASTER",
                    color = theme.textPrimaryColor,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = if (!isEnabled) {
                        "BIT-PERFECT DIRECT PASSTHROUGH"
                    } else if (isDolbyEnabled) {
                        "${activeProfile.displayName.uppercase()} // DOLBY 3D ACTIVE"
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
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = theme.backgroundColor,
                    checkedTrackColor = theme.accentColor,
                    uncheckedThumbColor = theme.textSecondaryColor,
                    uncheckedTrackColor = theme.surfaceVariantColor
                ),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
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
}

/**
 * Top Segmented View Switcher between Smart Acoustic and Pro Console.
 */
@Composable
private fun SegmentedModeSwitcher(
    currentTab: EqualizerTab,
    theme: HyprThemeConfig,
    onTabSelect: (EqualizerTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(theme.surfaceVariantColor.copy(alpha = 0.65f))
            .border(0.8.dp, theme.inactiveBorderColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(2.dp)
    ) {
        // Segment 1: Smart Acoustic
        val smartBg by animateColorAsState(
            targetValue = if (currentTab == EqualizerTab.SMART) theme.accentColor.copy(alpha = 0.22f) else Color.Transparent,
            label = "SmartTabBg"
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(6.dp))
                .background(smartBg)
                .border(
                    width = if (currentTab == EqualizerTab.SMART) 1.dp else 0.dp,
                    color = if (currentTab == EqualizerTab.SMART) theme.accentColor.copy(alpha = 0.7f) else Color.Transparent,
                    shape = RoundedCornerShape(6.dp)
                )
                .clickable { onTabSelect(EqualizerTab.SMART) },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = if (currentTab == EqualizerTab.SMART) theme.accentColor else theme.textSecondaryColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "SMART ACOUSTIC",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (currentTab == EqualizerTab.SMART) theme.accentColor else theme.textSecondaryColor
                )
            }
        }

        // Segment 2: Pro Console
        val proBg by animateColorAsState(
            targetValue = if (currentTab == EqualizerTab.PRO) theme.accentColor.copy(alpha = 0.22f) else Color.Transparent,
            label = "ProTabBg"
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(6.dp))
                .background(proBg)
                .border(
                    width = if (currentTab == EqualizerTab.PRO) 1.dp else 0.dp,
                    color = if (currentTab == EqualizerTab.PRO) theme.accentColor.copy(alpha = 0.7f) else Color.Transparent,
                    shape = RoundedCornerShape(6.dp)
                )
                .clickable { onTabSelect(EqualizerTab.PRO) },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = if (currentTab == EqualizerTab.PRO) theme.accentColor else theme.textSecondaryColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "PRO CONSOLE",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (currentTab == EqualizerTab.PRO) theme.accentColor else theme.textSecondaryColor
                )
            }
        }
    }
}

/**
 * Smart Acoustic Tab: Instant 1-tap hardware optimization and macro sonic controls.
 */
@Composable
private fun SmartAcousticView(
    activeProfile: DolbyPresetProfile,
    isEnabled: Boolean,
    bassBoost: Int,
    spatialStrength: Int,
    theme: HyprThemeConfig,
    onSelectProfile: (DolbyPresetProfile) -> Unit,
    onBassBoostChange: (Int) -> Unit,
    onSpatialChange: (Int) -> Unit,
    onReapplyMatrix: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "HARDWARE TARGET ACOUSTICS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = theme.textSecondaryColor
            )
            Text(
                text = "ONE-TAP MATRIX",
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                color = theme.accentColor
            )
        }

        // Hardware Profile Selector Grid (5 profiles without scrolling)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompactAcousticCard(
                profile = DolbyPresetProfile.IN_EAR_BUDS,
                icon = Icons.Default.Headphones,
                benefit = "Skull Resonance Scoop",
                isSelected = activeProfile == DolbyPresetProfile.IN_EAR_BUDS,
                isEnabled = isEnabled,
                theme = theme,
                onSelect = { onSelectProfile(DolbyPresetProfile.IN_EAR_BUDS) },
                modifier = Modifier.weight(1f)
            )
            CompactAcousticCard(
                profile = DolbyPresetProfile.OVER_EAR_HEADPHONES,
                icon = Icons.Default.Headphones,
                benefit = "Open-Back Air & Flat Mids",
                isSelected = activeProfile == DolbyPresetProfile.OVER_EAR_HEADPHONES,
                isEnabled = isEnabled,
                theme = theme,
                onSelect = { onSelectProfile(DolbyPresetProfile.OVER_EAR_HEADPHONES) },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompactAcousticCard(
                profile = DolbyPresetProfile.CAR_AUDIO,
                icon = Icons.Default.Tune,
                benefit = "Road Noise Sub Punch",
                isSelected = activeProfile == DolbyPresetProfile.CAR_AUDIO,
                isEnabled = isEnabled,
                theme = theme,
                onSelect = { onSelectProfile(DolbyPresetProfile.CAR_AUDIO) },
                modifier = Modifier.weight(1f)
            )
            CompactAcousticCard(
                profile = DolbyPresetProfile.HOME_THEATER,
                icon = Icons.Default.GraphicEq,
                benefit = "3D Cinematic Room",
                isSelected = activeProfile == DolbyPresetProfile.HOME_THEATER,
                isEnabled = isEnabled,
                theme = theme,
                onSelect = { onSelectProfile(DolbyPresetProfile.HOME_THEATER) },
                modifier = Modifier.weight(1f)
            )
        }

        // 5th Profile: Midnight Cinema (Full width compact card)
        CompactAcousticCard(
            profile = DolbyPresetProfile.NIGHT_LOUDNESS,
            icon = Icons.Default.Bedtime,
            benefit = "Dialogue Clarity & Night Leveling",
            isSelected = activeProfile == DolbyPresetProfile.NIGHT_LOUDNESS,
            isEnabled = isEnabled,
            theme = theme,
            onSelect = { onSelectProfile(DolbyPresetProfile.NIGHT_LOUDNESS) },
            modifier = Modifier.fillMaxWidth()
        )

        // Dual Macro Dynamics Sliders (Sub-Bass Punch & Spatial Room)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Bass Punch Macro
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.surfaceVariantColor.copy(alpha = 0.70f))
                    .border(0.8.dp, theme.inactiveBorderColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 9.dp, vertical = 7.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SUB-BASS PUNCH",
                        color = theme.textSecondaryColor,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${bassBoost / 10}%",
                        color = if (isEnabled) theme.accentColor else theme.textSecondaryColor,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Slider(
                    value = bassBoost.toFloat(),
                    onValueChange = { onBassBoostChange(it.toInt()) },
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
                        .height(24.dp)
                )
            }

            // Spatial Room Macro
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.surfaceVariantColor.copy(alpha = 0.70f))
                    .border(0.8.dp, theme.inactiveBorderColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 9.dp, vertical = 7.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SPATIAL ROOM",
                        color = theme.textSecondaryColor,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${spatialStrength / 10}%",
                        color = if (isEnabled) theme.accentColor else theme.textSecondaryColor,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Slider(
                    value = spatialStrength.toFloat(),
                    onValueChange = { onSpatialChange(it.toInt()) },
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
                        .height(24.dp)
                )
            }
        }

        // Re-Apply Calibrated Matrix Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(theme.surfaceVariantColor.copy(alpha = 0.60f))
                .border(0.8.dp, theme.accentColor.copy(alpha = 0.40f), RoundedCornerShape(6.dp))
                .hyprBounceClick {
                    if (isEnabled) {
                        onReapplyMatrix()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "RE-APPLY ${activeProfile.displayName.uppercase()} MATRIX",
                color = if (isEnabled) theme.accentColor else theme.textSecondaryColor,
                fontSize = 8.5.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Compact Acoustic Card with zero text clipping.
 */
@Composable
private fun CompactAcousticCard(
    profile: DolbyPresetProfile,
    icon: ImageVector,
    benefit: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    theme: HyprThemeConfig,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val active = isSelected && isEnabled
    val bg by animateColorAsState(
        targetValue = if (active) theme.accentColor.copy(alpha = 0.18f) else theme.surfaceVariantColor.copy(alpha = 0.65f),
        label = "CompactCardBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (active) theme.accentColor else theme.inactiveBorderColor.copy(alpha = 0.35f),
        label = "CompactCardBorder"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(width = if (active) 1.2.dp else 0.8.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
            .clickable { onSelect() }
            .padding(horizontal = 9.dp, vertical = 7.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (active) theme.accentColor else theme.textSecondaryColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = profile.displayName,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (active) theme.accentColor else theme.textPrimaryColor,
                        maxLines = 1
                    )
                    Text(
                        text = benefit,
                        fontSize = 7.5.sp,
                        fontFamily = FontFamily.Monospace,
                        color = theme.textSecondaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (active) theme.accentColor.copy(alpha = 0.25f) else Color.Transparent)
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "${profile.preCutGain}x",
                    fontSize = 7.5.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (active) theme.accentColor else theme.textSecondaryColor
                )
            }
        }
    }
}

/**
 * Pro Console Tab: Surgical 5-band faders, frequency spectrum spline, and genre curves.
 */
@Composable
private fun ProConsoleView(
    bands: List<EqualizerBand>,
    currentPreset: String,
    isEnabled: Boolean,
    theme: HyprThemeConfig,
    onBandChange: (Short, Short) -> Unit,
    onApplyPreset: (String) -> Unit,
    onResetToFlat: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Frequency Response Curve Visualizer (54dp height)
        FrequencyCurveCanvas(
            bands = bands,
            isEnabled = isEnabled,
            theme = theme,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        )

        // 2. 5-Band Vertical Studio Console Faders (152dp height)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(152.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bands.forEach { band ->
                VerticalEqualizerFader(
                    band = band,
                    isEnabled = isEnabled,
                    theme = theme,
                    onLevelChange = { newLevel ->
                        onBandChange(band.bandIndex, newLevel)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Preset Curves Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PRESET CURVES",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = theme.textSecondaryColor
            )
            Text(
                text = currentPreset.uppercase(),
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                color = theme.accentColor
            )
        }

        val presetScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(presetScrollState),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            HyprEqualizer.availablePresets.forEach { preset ->
                val isSelected = preset.equals(currentPreset, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            if (isSelected && isEnabled) theme.accentColor.copy(alpha = 0.25f)
                            else theme.surfaceVariantColor
                        )
                        .border(
                            width = 0.8.dp,
                            color = if (isSelected && isEnabled) theme.accentColor else Color.Transparent,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .clickable { onApplyPreset(preset) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = preset.uppercase(),
                        color = if (isSelected && isEnabled) theme.accentColor else theme.textSecondaryColor,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Reset to Flat Action Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(theme.surfaceVariantColor.copy(alpha = 0.50f))
                .border(0.8.dp, theme.inactiveBorderColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                .hyprBounceClick {
                    if (isEnabled) {
                        onResetToFlat()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "RESET ALL FADERS TO FLAT (0 dB)",
                color = theme.textSecondaryColor,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Frequency Response Curve Spline Canvas with reference grids and gradient fill.
 */
@Composable
private fun FrequencyCurveCanvas(
    bands: List<EqualizerBand>,
    isEnabled: Boolean,
    theme: HyprThemeConfig,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(theme.surfaceVariantColor.copy(alpha = 0.70f))
            .border(0.6.dp, theme.accentColor.copy(alpha = 0.30f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val midY = height / 2

            // Reference grid lines (+6dB, 0dB, -6dB)
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
                    style = Stroke(width = 2.0f, cap = StrokeCap.Round)
                )
            }
        }
    }
}

/**
 * Custom tactile vertical fader matching physical studio hardware consoles.
 * Free from parent scroll interference with instant touch tracking.
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

        Spacer(modifier = Modifier.height(3.dp))

        // Vertical Track & Thumb Box
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .width(38.dp)
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

        Spacer(modifier = Modifier.height(3.dp))

        // Center Frequency
        Text(
            text = band.formattedFreq,
            color = theme.textSecondaryColor,
            fontSize = 8.5.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Unified Bottom Telemetry HUD: clean, non-wrapping status readouts.
 */
@Composable
private fun TelemetryHud(
    activeProfile: DolbyPresetProfile,
    isLimiterEngaged: Boolean,
    isEnabled: Boolean,
    theme: HyprThemeConfig
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(theme.surfaceVariantColor.copy(alpha = 0.50f))
            .border(0.6.dp, theme.inactiveBorderColor.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Headroom Safety
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(if (isEnabled) Color(0xFF00E676) else Color.Gray)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "HEADROOM: ${activeProfile.preCutGain}x SAFE",
                color = if (isEnabled) Color(0xFF00E676) else Color.Gray,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        // Limiter Status
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(if (isLimiterEngaged && isEnabled) Color(0xFF00E5FF) else Color.Gray)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isLimiterEngaged && isEnabled) "LIMITER: ARMED" else "LIMITER: PASS",
                color = if (isLimiterEngaged && isEnabled) Color(0xFF00E5FF) else Color.Gray,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        // Active Profile
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(if (isEnabled) theme.accentColor else Color.Gray)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = activeProfile.displayName.uppercase(),
                color = if (isEnabled) theme.accentColor else theme.textSecondaryColor,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
