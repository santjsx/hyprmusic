package com.example.hyprmusic.ui.elovaire.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hyprmusic.core.media.HyprAudioPlayer
import com.example.hyprmusic.core.media.HyprSleepTimer
import com.example.hyprmusic.ui.elovaire.motion.elovairePressScale
import com.example.hyprmusic.ui.elovaire.theme.ElovaireColors
import com.example.hyprmusic.ui.elovaire.theme.ElovaireRadii
import kotlin.math.roundToInt

@Composable
fun ElovaireSleepTimerDialog(
    audioPlayer: HyprAudioPlayer,
    onDismiss: () -> Unit
) {
    val view = LocalView.current
    val timerState by HyprSleepTimer.timerState.collectAsStateWithLifecycle()
    val playbackState by audioPlayer.playbackState.collectAsStateWithLifecycle()
    val currentTrack = playbackState.currentTrack
    var customMinutes by remember { mutableFloatStateOf(20f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(ElovaireRadii.dialog))
                .background(ElovaireColors.Carbon)
                .border(1.dp, ElovaireColors.BorderSubtle, RoundedCornerShape(ElovaireRadii.dialog))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(ElovaireColors.Graphite),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bedtime,
                                contentDescription = null,
                                tint = ElovaireColors.AzureAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "Sleep Timer",
                            color = ElovaireColors.TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ElovaireColors.TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Active Timer Display
                if (timerState.isActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(ElovaireRadii.card))
                            .background(ElovaireColors.Graphite)
                            .border(1.dp, ElovaireColors.BorderStrong, RoundedCornerShape(ElovaireRadii.card))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "PLAYBACK WILL PAUSE IN",
                                color = ElovaireColors.TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = timerState.formattedRemaining,
                                color = ElovaireColors.AzureAccent,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                            ) {
                                if (!timerState.isEndOfTrack) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(ElovaireRadii.pill))
                                            .background(ElovaireColors.Carbon)
                                            .border(1.dp, ElovaireColors.BorderSubtle, RoundedCornerShape(ElovaireRadii.pill))
                                            .elovairePressScale()
                                            .clickable {
                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                HyprSleepTimer.addMinutes(5)
                                            }
                                            .padding(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "+5m",
                                            color = ElovaireColors.TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(ElovaireRadii.pill))
                                            .background(ElovaireColors.Carbon)
                                            .border(1.dp, ElovaireColors.BorderSubtle, RoundedCornerShape(ElovaireRadii.pill))
                                            .elovairePressScale()
                                            .clickable {
                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                HyprSleepTimer.addMinutes(15)
                                            }
                                            .padding(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "+15m",
                                            color = ElovaireColors.TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(ElovaireRadii.pill))
                                        .background(ElovaireColors.DestructiveRed.copy(alpha = 0.15f))
                                        .border(1.dp, ElovaireColors.DestructiveRed.copy(alpha = 0.3f), RoundedCornerShape(ElovaireRadii.pill))
                                        .elovairePressScale()
                                        .clickable {
                                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                            HyprSleepTimer.cancelTimer()
                                        }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TimerOff,
                                            contentDescription = null,
                                            tint = ElovaireColors.DestructiveRed,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "Cancel",
                                            color = ElovaireColors.DestructiveRed,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Quick Presets
                Text(
                    text = "QUICK PRESETS",
                    color = ElovaireColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(15, 30, 45, 60).forEach { mins ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(ElovaireRadii.card))
                                .background(ElovaireColors.Graphite)
                                .border(1.dp, ElovaireColors.BorderSubtle, RoundedCornerShape(ElovaireRadii.card))
                                .elovairePressScale()
                                .clickable {
                                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    HyprSleepTimer.startTimer(mins, audioPlayer)
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${mins}m",
                                color = ElovaireColors.TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // End of current track preset
                if (currentTrack != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(ElovaireRadii.card))
                            .background(ElovaireColors.Graphite)
                            .border(1.dp, ElovaireColors.BorderSubtle, RoundedCornerShape(ElovaireRadii.card))
                            .elovairePressScale()
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                HyprSleepTimer.startEndOfTrackTimer(currentTrack.id, audioPlayer)
                                onDismiss()
                            }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = ElovaireColors.AzureAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "End of Current Song",
                                color = ElovaireColors.TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Custom Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CUSTOM DURATION",
                        color = ElovaireColors.TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${customMinutes.roundToInt()} minutes",
                        color = ElovaireColors.AzureAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = customMinutes,
                    onValueChange = { customMinutes = it },
                    valueRange = 5f..120f,
                    steps = 22,
                    colors = SliderDefaults.colors(
                        thumbColor = ElovaireColors.Cloud,
                        activeTrackColor = ElovaireColors.AzureAccent,
                        inactiveTrackColor = ElovaireColors.Graphite
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Start Timer Action Pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(ElovaireRadii.pill))
                        .background(ElovaireColors.AzureAccent)
                        .elovairePressScale()
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            HyprSleepTimer.startTimer(customMinutes.roundToInt(), audioPlayer)
                            onDismiss()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Start ${customMinutes.roundToInt()} min Sleep Timer",
                        color = ElovaireColors.Cloud,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
