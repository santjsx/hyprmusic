package com.example.hyprmusic.ui.dap.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hyprmusic.core.media.HyprEqualizer
import com.example.hyprmusic.ui.dap.theme.DapColors
import com.example.hyprmusic.ui.dap.theme.DapTypography

/**
 * Retro LCD 10-Band Graphic Equalizer and DSP tuning display.
 */
@Composable
fun DapEqualizerDisplay(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEnabled by HyprEqualizer.isEnabled.collectAsStateWithLifecycle()
    val bands by HyprEqualizer.bands.collectAsStateWithLifecycle()

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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    text = "DSP Equalizer",
                    style = DapTypography.MenuHeader,
                    color = DapColors.TextPrimary
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = { HyprEqualizer.setEnabled(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = DapColors.TextPrimary,
                    checkedTrackColor = DapColors.AccentIceBlue,
                    uncheckedThumbColor = DapColors.TextSecondary,
                    uncheckedTrackColor = DapColors.ScreenCard
                ),
                modifier = Modifier.scale(0.8f)
            )
        }

        HorizontalDivider(
            color = DapColors.ScreenCardBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Visual Frequency Spectrum Bands
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(bands) { band ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(DapColors.ScreenCard)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = band.formattedFreq,
                        style = DapTypography.MenuItem,
                        color = DapColors.TextPrimary,
                        modifier = Modifier.width(60.dp)
                    )

                    // Graphical Level Bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .padding(horizontal = 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(DapColors.ProgressTrack)
                    ) {
                        val fraction = ((band.levelDb + 12f) / 24f).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fraction)
                                .background(DapColors.AccentIceBlue)
                        )
                    }

                    Text(
                        text = "${if (band.levelDb > 0) "+" else ""}%.1f dB".format(band.levelDb),
                        style = DapTypography.TimeCounter,
                        color = DapColors.TextSecondary,
                        modifier = Modifier.width(55.dp)
                    )
                }
            }
        }
    }
}

private fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.size((52 * scale).dp, (32 * scale).dp)
)
