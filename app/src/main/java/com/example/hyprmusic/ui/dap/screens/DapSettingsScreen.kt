package com.example.hyprmusic.ui.dap.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hyprmusic.core.updater.HyprUpdateManager
import com.example.hyprmusic.core.updater.UpdateDownloadState
import com.example.hyprmusic.ui.dap.state.DapHapticFeedback
import com.example.hyprmusic.ui.dap.theme.DapColors
import com.example.hyprmusic.ui.dap.theme.DapTypography
import kotlinx.coroutines.launch

/**
 * Retro LCD Settings, Storage Management & OTA Update Manager.
 * Integrates Goal Gradient Progress, Labor Illusion diagnostics, and Peak-End confirmation.
 */
@Composable
fun DapSettingsDisplay(
    trackCount: Int,
    isScanning: Boolean,
    onRescan: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val haptic = remember(view) { DapHapticFeedback(view) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // OTA Updater States
    val updateInfo by HyprUpdateManager.updateState.collectAsStateWithLifecycle()
    val isCheckingUpdate by HyprUpdateManager.isChecking.collectAsStateWithLifecycle()
    val diagnosticStep by HyprUpdateManager.diagnosticStep.collectAsStateWithLifecycle()
    val downloadState by HyprUpdateManager.downloadState.collectAsStateWithLifecycle()
    val currentInstalledBuild = remember(context) { HyprUpdateManager.getCurrentVersion(context) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .verticalScroll(scrollState)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = DapColors.TextPrimary,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Device Settings",
                style = DapTypography.MenuHeader,
                color = DapColors.TextPrimary
            )
        }

        HorizontalDivider(
            color = DapColors.ScreenCardBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // ==========================================
        // 1. Storage & Media Library Scanner (Labor Illusion + Goal Gradient)
        // ==========================================
        Text(
            text = "STORAGE & INDEXING",
            style = DapTypography.MenuSecondary,
            color = DapColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DapColors.ScreenCard)
                .border(1.dp, DapColors.ScreenCardBorder, RoundedCornerShape(10.dp))
                .clickable {
                    haptic.buttonClick()
                    onRescan()
                }
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isScanning) "Indexing Audio Library..." else "Rescan Storage",
                        style = DapTypography.MenuItem,
                        color = DapColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isScanning) "Reading metadata & extracting album art" else "$trackCount indexed local audio tracks",
                        style = DapTypography.MenuSecondary,
                        color = DapColors.TextSecondary
                    )
                }
                if (isScanning) {
                    CircularProgressIndicator(
                        color = DapColors.AccentIceBlue,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = DapColors.AccentIceBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            if (isScanning) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.5.dp),
                    color = DapColors.AccentIceBlue,
                    trackColor = DapColors.ProgressTrack
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 2. OTA Software Updates (GitHub Releases)
        // ==========================================
        Text(
            text = "SOFTWARE UPDATE // GITHUB RELEASES",
            style = DapTypography.MenuSecondary,
            color = DapColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DapColors.ScreenCard)
                .border(1.dp, DapColors.ScreenCardBorder, RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Current Build: $currentInstalledBuild",
                        style = DapTypography.MenuItem,
                        color = DapColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "GitHub Releases Production Channel",
                        style = DapTypography.MenuSecondary,
                        color = DapColors.TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DapColors.AccentIceBlue.copy(alpha = 0.15f))
                        .border(1.dp, DapColors.AccentIceBlue.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable(enabled = !isCheckingUpdate) {
                            haptic.buttonClick()
                            coroutineScope.launch {
                                HyprUpdateManager.checkForUpdates(context)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    if (isCheckingUpdate) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = DapColors.AccentIceBlue
                        )
                    } else {
                        Text(
                            text = "CHECK",
                            color = DapColors.AccentIceBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Diagnostic Step Output (Labor Illusion)
            if (diagnosticStep.isNotBlank() && diagnosticStep != "Idle") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• $diagnosticStep",
                    style = DapTypography.MenuSecondary,
                    color = DapColors.AccentIceBlue,
                    fontSize = 11.sp
                )
            }

            // Update Available or Up-to-Date Banner
            if (updateInfo != null) {
                Spacer(modifier = Modifier.height(12.dp))
                val info = updateInfo!!

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (info.isUpdateAvailable) DapColors.AccentIceBlue.copy(alpha = 0.12f)
                            else Color(0xFF10281E)
                        )
                        .border(
                            1.dp,
                            if (info.isUpdateAvailable) DapColors.AccentIceBlue.copy(alpha = 0.5f)
                            else Color(0xFF22C55E).copy(alpha = 0.4f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (info.isUpdateAvailable) Icons.Default.SystemUpdate else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (info.isUpdateAvailable) DapColors.AccentIceBlue else Color(0xFF22C55E),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (info.isUpdateAvailable) "NEW VERSION: ${info.tagName}" else "LATEST BUILD: ${info.tagName}",
                                    color = if (info.isUpdateAvailable) DapColors.AccentIceBlue else Color(0xFF22C55E),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (info.formattedSize.isNotEmpty()) {
                                Text(
                                    text = info.formattedSize,
                                    color = DapColors.TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (info.isUpdateAvailable) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = info.releaseNotes.take(160) + if (info.releaseNotes.length > 160) "..." else "",
                                color = DapColors.TextSecondary,
                                fontSize = 11.5.sp,
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Download / Progress Action Button (Goal Gradient Effect)
                            when (val state = downloadState) {
                                is UpdateDownloadState.Idle -> {
                                    Button(
                                        onClick = {
                                            haptic.heavyPulse()
                                            HyprUpdateManager.startApkDownload(context, info.downloadUrl)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = DapColors.AccentIceBlue,
                                            contentColor = Color.Black
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Download & Install Update",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp
                                        )
                                    }
                                }

                                is UpdateDownloadState.Downloading -> {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Downloading: ${state.percent}%",
                                                color = DapColors.AccentIceBlue,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${state.downloadedMb} / ${state.totalMb} MB (${state.speedText})",
                                                color = DapColors.TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        LinearProgressIndicator(
                                            progress = { state.progressFraction },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(5.dp),
                                            color = DapColors.AccentIceBlue,
                                            trackColor = DapColors.ProgressTrack
                                        )
                                    }
                                }

                                is UpdateDownloadState.ReadyToInstall -> {
                                    Button(
                                        onClick = {
                                            haptic.heavyPulse()
                                            HyprUpdateManager.installApk(context, state.apkFile)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF22C55E),
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Install APK Now", fontWeight = FontWeight.Bold)
                                    }
                                }

                                is UpdateDownloadState.Error -> {
                                    Text(
                                        text = "Download Error: ${state.message}",
                                        color = Color(0xFFEF4444),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 3. Audio Pipeline Telemetry
        // ==========================================
        Text(
            text = "HARDWARE AUDIO ENGINE",
            style = DapTypography.MenuSecondary,
            color = DapColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DapColors.ScreenCard)
                .border(1.dp, DapColors.ScreenCardBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = "DSP Pipeline Specifications",
                style = DapTypography.MenuItem,
                color = DapColors.TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "• AndroidX Media3 (ExoPlayer)\n• Bit-Perfect 32-bit Float Audio Sink\n• Zero-Telemetry Offline Perimeter\n• High-Definition Tactile Rotary Haptics",
                style = DapTypography.MenuSecondary,
                color = DapColors.TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
