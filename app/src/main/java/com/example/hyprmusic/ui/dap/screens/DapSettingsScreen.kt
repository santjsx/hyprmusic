package com.example.hyprmusic.ui.dap.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hyprmusic.ui.dap.theme.DapColors
import com.example.hyprmusic.ui.dap.theme.DapTypography

/**
 * Retro LCD Settings and Device Diagnostics View.
 * Includes library management, UI shell switching, and audiophile engine telemetry.
 */
@Composable
fun DapSettingsDisplay(
    trackCount: Int,
    isScanning: Boolean,
    onRescan: () -> Unit,
    onBack: () -> Unit,
    onSwitchUi: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 12.dp)
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
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Rescan Local Media Action (Goal Gradient Effect on indexing)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(DapColors.ScreenCard)
                .clickable { onRescan() }
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isScanning) "Discovering Audio Tracks..." else "Scan Storage",
                        style = DapTypography.MenuItem,
                        color = DapColors.TextPrimary
                    )
                    Text(
                        text = "$trackCount indexed local audio tracks",
                        style = DapTypography.MenuSecondary,
                        color = DapColors.TextSecondary
                    )
                }
                if (isScanning) {
                    CircularProgressIndicator(
                        color = DapColors.AccentIceBlue,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = DapColors.AccentIceBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (isScanning) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(3.dp),
                    color = DapColors.AccentIceBlue,
                    trackColor = DapColors.ProgressTrack
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Switch to Modern Shell Toggle
        if (onSwitchUi != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DapColors.ScreenCard)
                    .clickable { onSwitchUi() }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UI Interface Shell",
                        style = DapTypography.MenuItem,
                        color = DapColors.TextPrimary
                    )
                    Text(
                        text = "Switch between Retro DAP & Modern View",
                        style = DapTypography.MenuSecondary,
                        color = DapColors.TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = DapColors.AccentIceBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Audio Engine Hardware Specs
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(DapColors.ScreenCard)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Audio Pipeline",
                style = DapTypography.MenuItem,
                color = DapColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• AndroidX Media3 (ExoPlayer)\n• Hardware Bit-Perfect PCM Sink\n• Zero-Telemetry Offline Perimeter",
                style = DapTypography.MenuSecondary,
                color = DapColors.TextSecondary
            )
        }
    }
}
