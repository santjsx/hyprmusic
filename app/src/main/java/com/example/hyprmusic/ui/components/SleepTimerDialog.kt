package com.example.hyprmusic.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.hyprmusic.core.media.HyprAudioPlayer
import com.example.hyprmusic.core.media.HyprSleepTimer
import com.example.hyprmusic.core.theming.HyprThemeConfig
import com.example.hyprmusic.core.theming.hyprAnimatedGlow
import com.example.hyprmusic.core.theming.hyprBounceClick
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SleepTimerDialog(
    theme: HyprThemeConfig,
    audioPlayer: HyprAudioPlayer,
    onDismiss: () -> Unit
) {
    val view = LocalView.current
    val timerState by HyprSleepTimer.timerState.collectAsState()
    val currentTrack = audioPlayer.playbackState.collectAsState().value.currentTrack
    var customMinutes by remember { mutableFloatStateOf(20f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(theme.borderRadiusDp.dp))
                .background(theme.surfaceColor)
                .border(1.dp, theme.accentColor.copy(alpha = 0.35f), RoundedCornerShape(theme.borderRadiusDp.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SLEEP TIMER",
                            color = theme.textPrimaryColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = theme.textSecondaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Countdown Banner (if running)
                if (timerState.isActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.surfaceVariantColor)
                            .border(1.dp, theme.accentColor, RoundedCornerShape(8.dp))
                            .hyprAnimatedGlow(theme)
                            .padding(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ACTIVE TIMER REMAINING",
                                color = theme.textSecondaryColor,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = timerState.formattedRemaining,
                                color = theme.accentColor,
                                fontSize = 28.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                            ) {
                                if (!timerState.isEndOfTrack) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(theme.surfaceColor)
                                            .border(1.dp, theme.accentColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                            .hyprBounceClick {
                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                HyprSleepTimer.addMinutes(5)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "+5 MIN",
                                            color = theme.accentColor,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(theme.surfaceColor)
                                            .border(1.dp, theme.accentColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                            .hyprBounceClick {
                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                HyprSleepTimer.addMinutes(15)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "+15 MIN",
                                            color = theme.accentColor,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(theme.surfaceColor)
                                        .border(1.dp, theme.textSecondaryColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                        .hyprBounceClick {
                                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                            HyprSleepTimer.cancelTimer()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.TimerOff,
                                            contentDescription = null,
                                            tint = theme.textSecondaryColor,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "CANCEL",
                                            color = theme.textSecondaryColor,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Presets Title
                Text(
                    text = "QUICK PRESETS",
                    color = theme.textSecondaryColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Preset Buttons
                val presets = listOf(15, 30, 45, 60)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { mins ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(theme.surfaceVariantColor)
                                .border(1.dp, theme.surfaceVariantColor, RoundedCornerShape(6.dp))
                                .hyprBounceClick {
                                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    HyprSleepTimer.startTimer(mins, audioPlayer)
                                    onDismiss()
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$mins min",
                                color = theme.textPrimaryColor,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // End of Track Preset
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (timerState.isEndOfTrack) theme.accentColor.copy(alpha = 0.2f) else theme.surfaceVariantColor)
                        .border(
                            1.dp,
                            if (timerState.isEndOfTrack) theme.accentColor else theme.surfaceVariantColor,
                            RoundedCornerShape(6.dp)
                        )
                        .hyprBounceClick {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            if (currentTrack != null) {
                                HyprSleepTimer.startEndOfTrackTimer(currentTrack.id, audioPlayer)
                                onDismiss()
                            }
                        }
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = if (timerState.isEndOfTrack) theme.accentColor else theme.textPrimaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "END OF CURRENT TRACK",
                            color = if (timerState.isEndOfTrack) theme.accentColor else theme.textPrimaryColor,
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Duration Slider
                Text(
                    text = "CUSTOM DURATION: ${customMinutes.roundToInt()} MIN",
                    color = theme.textSecondaryColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Slider(
                    value = customMinutes,
                    onValueChange = { customMinutes = it },
                    valueRange = 5f..120f,
                    steps = 22,
                    colors = SliderDefaults.colors(
                        thumbColor = theme.accentColor,
                        activeTrackColor = theme.accentColor,
                        inactiveTrackColor = theme.surfaceVariantColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(theme.accentColor)
                        .hyprBounceClick {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            HyprSleepTimer.startTimer(customMinutes.roundToInt(), audioPlayer)
                            onDismiss()
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "START ${customMinutes.roundToInt()} MIN TIMER",
                        color = theme.backgroundColor,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Audiophile reassurance notice
                Text(
                    text = "Includes a gentle 15-second audiophile volume fade-out prior to pause.",
                    color = theme.textSecondaryColor.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
