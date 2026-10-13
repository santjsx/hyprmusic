package com.example.hyprmusic.ui.dap.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hyprmusic.core.autoeq.AutoEqDatabase
import com.example.hyprmusic.core.autoeq.AutoEqFrequencies
import com.example.hyprmusic.core.autoeq.AutoEqMatcher
import com.example.hyprmusic.core.autoeq.AutoEqProfile
import com.example.hyprmusic.core.media.HyprEqualizer
import com.example.hyprmusic.ui.dap.state.DapHapticFeedback
import com.example.hyprmusic.ui.dap.state.rememberConnectedAudioDevice
import com.example.hyprmusic.ui.dap.theme.DapColors
import com.example.hyprmusic.ui.dap.theme.DapTypography

/**
 * Flagship Audiophile Equalizer Screen powered by Jaakko Pasanen's AutoEq project.
 * Features:
 * - Smart Bluetooth Auto-Detection & 1-tap matching (e.g. boAt Rockerz 413, AirPods, Sony).
 * - 300+ Pre-bundled Headphone compensation profiles calibrated to the Harman Target.
 * - Interactive Spline Frequency Response Curve Visualizer.
 * - Precision 10-Band Graphic Equalizer with Preamp headroom protection.
 */
@Composable
fun DapEqualizerDisplay(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val haptic = remember(view) { DapHapticFeedback(view) }

    val isEnabled by HyprEqualizer.isEnabled.collectAsStateWithLifecycle()
    val bands by HyprEqualizer.bands.collectAsStateWithLifecycle()
    val currentPreset by HyprEqualizer.currentPreset.collectAsStateWithLifecycle()
    val activeAutoEq by HyprEqualizer.activeAutoEqProfile.collectAsStateWithLifecycle()
    val connectedDevice = rememberConnectedAudioDevice()

    // Automatic hardware match suggestion
    val suggestedProfile = remember(connectedDevice) {
        AutoEqMatcher.findBestMatch(connectedDevice)
    }

    var showHeadphoneSearchDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // =========================================================================
        // TOP HEADER: Back + Title + Master Switch
        // =========================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DapColors.TextPrimary,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable {
                            haptic.buttonClick()
                            onBack()
                        }
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AutoEq & DSP Engine",
                        style = DapTypography.MenuHeader,
                        color = DapColors.TextPrimary,
                        fontSize = 16.sp
                    )
                    Text(
                        text = if (isEnabled) "Active Headroom DRC" else "Bit-Perfect Direct Bypass",
                        style = DapTypography.MenuSecondary,
                        color = if (isEnabled) DapColors.AccentIceBlue else DapColors.TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = {
                    haptic.buttonClick()
                    HyprEqualizer.setEnabled(it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = DapColors.TextPrimary,
                    checkedTrackColor = DapColors.AccentIceBlue,
                    uncheckedThumbColor = DapColors.TextSecondary,
                    uncheckedTrackColor = DapColors.ScreenCard
                )
            )
        }

        HorizontalDivider(
            color = DapColors.ScreenCardBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // =========================================================================
            // AUTOEQ HEADPHONE PROFILE CARD
            // =========================================================================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (activeAutoEq != null) Color(0xFF14243D) else DapColors.ScreenCard
                        )
                        .border(
                            1.dp,
                            if (activeAutoEq != null) DapColors.AccentIceBlue.copy(alpha = 0.5f) else DapColors.ScreenCardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Headphones,
                                    contentDescription = null,
                                    tint = if (activeAutoEq != null) DapColors.AccentIceBlue else DapColors.TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AutoEq Headphone Correction",
                                    style = DapTypography.MenuItem,
                                    color = DapColors.TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                            }

                            if (activeAutoEq != null) {
                                Text(
                                    text = "Active",
                                    color = DapColors.AccentIceBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (activeAutoEq != null) {
                            val profile = activeAutoEq!!
                            Text(
                                text = "${profile.brand} ${profile.model}",
                                style = DapTypography.MenuHeader,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${profile.measurementSource} • Harman Target • Preamp ${profile.preampDb} dB",
                                style = DapTypography.MenuSecondary,
                                color = DapColors.TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = profile.description,
                                style = DapTypography.MenuSecondary,
                                color = DapColors.TextMuted,
                                fontSize = 10.5.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        haptic.buttonClick()
                                        showHeadphoneSearchDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DapColors.ChassisTitanium),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f).height(34.dp)
                                ) {
                                    Text("Change Model", fontSize = 11.5.sp, color = DapColors.TextPrimary)
                                }

                                OutlinedButton(
                                    onClick = {
                                        haptic.buttonClick()
                                        HyprEqualizer.clearAutoEqProfile()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f).height(34.dp)
                                ) {
                                    Text("Reset Flat", fontSize = 11.5.sp, color = DapColors.TextSecondary)
                                }
                            }
                        } else {
                            // Suggest detected headphone if available
                            if (suggestedProfile != null) {
                                Text(
                                    text = "Detected: ${suggestedProfile.brand} ${suggestedProfile.model}",
                                    style = DapTypography.MenuSecondary,
                                    color = DapColors.AccentIceBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = suggestedProfile.description,
                                    style = DapTypography.MenuSecondary,
                                    color = DapColors.TextSecondary,
                                    fontSize = 11.sp
                                )

                                Button(
                                    onClick = {
                                        haptic.heavyPulse()
                                        HyprEqualizer.applyAutoEqProfile(suggestedProfile)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DapColors.AccentIceBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().height(36.dp)
                                ) {
                                    Text(
                                        text = "Auto-Apply ${suggestedProfile.model} Profile ⭐",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            } else {
                                Text(
                                    text = "Connected device: $connectedDevice",
                                    style = DapTypography.MenuSecondary,
                                    color = DapColors.TextSecondary,
                                    fontSize = 11.5.sp
                                )
                                Text(
                                    text = "Compensate acoustic imperfections using 300+ measurement curves calibrated by Oratory1990 & Crinacle.",
                                    style = DapTypography.MenuSecondary,
                                    color = DapColors.TextMuted,
                                    fontSize = 11.sp
                                )

                                Button(
                                    onClick = {
                                        haptic.buttonClick()
                                        showHeadphoneSearchDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DapColors.ChassisTitanium),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().height(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = DapColors.AccentIceBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Browse 300+ AutoEq Headphone Profiles",
                                        color = DapColors.TextPrimary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // SPLINE FREQUENCY RESPONSE GRAPH
            // =========================================================================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF070D17))
                        .border(1.dp, DapColors.ScreenCardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Acoustic Target Curve",
                                style = DapTypography.MenuSecondary,
                                color = DapColors.TextSecondary,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "20 Hz — 20 kHz (Harman Target)",
                                style = DapTypography.MenuSecondary,
                                color = DapColors.AccentIceBlue,
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Dynamic Spline Curve Canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val midY = h * 0.5f

                            // Center 0 dB Reference Dotted Line
                            drawLine(
                                color = Color(0xFF223552),
                                start = Offset(0f, midY),
                                end = Offset(w, midY),
                                strokeWidth = 1f
                            )

                            val points = mutableListOf<Offset>()
                            val count = bands.size
                            if (count > 0) {
                                val stepX = w / (count - 1).coerceAtLeast(1)
                                bands.forEachIndexed { i, band ->
                                    val x = i * stepX
                                    // Scale: -12 dB to +12 dB
                                    val normalized = (band.levelDb / 12f).coerceIn(-1f, 1f)
                                    val y = midY - (normalized * (h * 0.42f))
                                    points.add(Offset(x, y))
                                }

                                if (points.size >= 2) {
                                    val path = Path().apply {
                                        moveTo(points[0].x, points[0].y)
                                        for (i in 0 until points.size - 1) {
                                            val p0 = points[i]
                                            val p1 = points[i + 1]
                                            val cx = (p0.x + p1.x) / 2f
                                            cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                                        }
                                    }

                                    // Fill Area Under Curve
                                    val fillPath = Path().apply {
                                        addPath(path)
                                        lineTo(w, h)
                                        lineTo(0f, h)
                                        close()
                                    }
                                    drawPath(
                                        path = fillPath,
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                DapColors.AccentIceBlue.copy(alpha = 0.25f),
                                                Color.Transparent
                                            ),
                                            startY = 0f,
                                            endY = h
                                        )
                                    )

                                    // Curve Stroke
                                    drawPath(
                                        path = path,
                                        color = DapColors.AccentIceBlue,
                                        style = Stroke(width = 2.2f, cap = StrokeCap.Round)
                                    )

                                    // Highlight band nodes
                                    points.forEach { pt ->
                                        drawCircle(color = Color.White, radius = 2.5f, center = pt)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // QUICK AUDIOPHILE PRESETS ROW
            // =========================================================================
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val presets = listOf("Flat", "Rock", "Pop", "Electronic", "Jazz", "Acoustic")
                    items(presets) { preset ->
                        val isSelected = currentPreset.equals(preset, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) DapColors.AccentIceBlue else DapColors.ScreenCard)
                                .clickable {
                                    haptic.lightTap()
                                    HyprEqualizer.setPreset(preset)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = preset,
                                color = if (isSelected) Color.Black else DapColors.TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // 10-BAND INTERACTIVE SLIDERS
            // =========================================================================
            item {
                Text(
                    text = "10-Band Graphic EQ Matrix",
                    style = DapTypography.MenuItem,
                    color = DapColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            items(bands) { band ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DapColors.ScreenCard)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = band.formattedFreq,
                        style = DapTypography.MenuItem,
                        color = DapColors.TextPrimary,
                        modifier = Modifier.width(55.dp),
                        fontSize = 13.sp
                    )

                    // Interactive Slider
                    Slider(
                        value = band.levelDb,
                        onValueChange = { newDb ->
                            val millibels = (newDb * 100).toInt().toShort()
                            HyprEqualizer.setBandLevel(band.bandIndex, millibels)
                        },
                        valueRange = -12f..12f,
                        steps = 23,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = DapColors.AccentIceBlue,
                            inactiveTrackColor = DapColors.ProgressTrack
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 6.dp)
                    )

                    Text(
                        text = "${if (band.levelDb > 0) "+" else ""}%.1f dB".format(band.levelDb),
                        style = DapTypography.TimeCounter,
                        color = if (band.levelDb != 0f) DapColors.AccentIceBlue else DapColors.TextSecondary,
                        modifier = Modifier.width(60.dp),
                        fontSize = 11.5.sp
                    )
                }
            }
        }
    }

    // =========================================================================
    // HEADPHONE SELECTOR BOTTOM SHEET / DIALOG
    // =========================================================================
    if (showHeadphoneSearchDialog) {
        var searchQuery by remember { mutableStateOf("") }
        val filteredProfiles = remember(searchQuery) {
            AutoEqDatabase.search(searchQuery)
        }

        Dialog(onDismissRequest = { showHeadphoneSearchDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(DapColors.ChassisTitanium)
                    .border(1.dp, DapColors.ChassisBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AutoEq Headphone Catalog",
                            style = DapTypography.MenuHeader,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        IconButton(onClick = { showHeadphoneSearchDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search brand or model (boAt, Sony, AirPods...)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DapColors.AccentIceBlue,
                            unfocusedBorderColor = DapColors.ScreenCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredProfiles) { profile ->
                            val isCurrent = activeAutoEq?.id == profile.id
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCurrent) Color(0xFF1B3152) else DapColors.ScreenCard)
                                    .border(
                                        1.dp,
                                        if (isCurrent) DapColors.AccentIceBlue else DapColors.ScreenCardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        haptic.heavyPulse()
                                        HyprEqualizer.applyAutoEqProfile(profile)
                                        showHeadphoneSearchDialog = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = profile.displayName,
                                            style = DapTypography.MenuItem,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${profile.formFactor} • ${profile.measurementSource} • Preamp ${profile.preampDb} dB",
                                            style = DapTypography.MenuSecondary,
                                            color = DapColors.TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }

                                    if (isCurrent) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = DapColors.AccentIceBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
