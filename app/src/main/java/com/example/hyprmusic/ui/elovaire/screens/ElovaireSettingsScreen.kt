package com.example.hyprmusic.ui.elovaire.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hyprmusic.core.cloud.telegram.TelegramMusicRepository
import com.example.hyprmusic.core.media.HyprEqualizer
import com.example.hyprmusic.core.updater.HyprUpdateManager
import com.example.hyprmusic.core.updater.UpdateDownloadState
import com.example.hyprmusic.ui.elovaire.components.ElovaireCard
import com.example.hyprmusic.ui.elovaire.components.GroupedListRowContainer
import com.example.hyprmusic.ui.elovaire.components.GroupedRowDivider
import com.example.hyprmusic.ui.elovaire.motion.elovairePressScale
import com.example.hyprmusic.ui.elovaire.theme.ElovaireColors
import com.example.hyprmusic.ui.elovaire.theme.ElovaireRadii
import com.example.hyprmusic.ui.elovaire.theme.ElovaireSpacing
import kotlinx.coroutines.launch

/**
 * Editorial Elovaire Settings Screen.
 * Elegant inset grouped cards for Audio Engine, Media Library, Telegram Cloud, and App Info.
 */
@Composable
fun ElovaireSettingsScreen(
    isScanning: Boolean,
    onRescanMedia: () -> Unit,
    onOpenEqualizer: () -> Unit,
    telegramRepository: TelegramMusicRepository?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val eqEnabled by HyprEqualizer.isEnabled.collectAsStateWithLifecycle()
    val eqPreset by HyprEqualizer.currentPreset.collectAsStateWithLifecycle()
    val isDolbyEnabled by HyprEqualizer.isDolbyEnabled.collectAsStateWithLifecycle()

    val updateInfo by HyprUpdateManager.updateState.collectAsStateWithLifecycle()
    val isCheckingUpdate by HyprUpdateManager.isChecking.collectAsStateWithLifecycle()
    val downloadProgress by HyprUpdateManager.downloadProgress.collectAsStateWithLifecycle()
    val downloadState by HyprUpdateManager.downloadState.collectAsStateWithLifecycle()

    val cloudSettings = telegramRepository?.config?.settings?.collectAsStateWithLifecycle()?.value
    val isSyncingCloud = telegramRepository?.isSyncing?.collectAsStateWithLifecycle()?.value ?: false
    val cloudTracks by (telegramRepository?.cloudTracks?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(emptyList()) })

    var serverUrlInput by remember(cloudSettings?.serverUrl) {
        val currentUrl = cloudSettings?.serverUrl
        mutableStateOf(
            if (currentUrl.isNullOrBlank() || currentUrl == "http://10.0.2.2:8080") {
                "https://tpmc-music-cloud.onrender.com"
            } else currentUrl
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = ElovaireSpacing.screenHorizontal,
            top = 10.dp,
            end = ElovaireSpacing.screenHorizontal,
            bottom = ElovaireSpacing.listContentBottomPad
        ),
        verticalArrangement = Arrangement.spacedBy(ElovaireSpacing.sectionVertical)
    ) {
        // Hero Overview Card
        item {
            ElovaireCard(
                backgroundColor = ElovaireColors.Carbon,
                borderColor = ElovaireColors.BorderSubtle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(ElovaireRadii.tile))
                            .background(ElovaireColors.AzureGlow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = ElovaireColors.AzureAccent,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HyprMusic // Elovaire",
                            color = ElovaireColors.TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Haute Audiophile Sound & Design System",
                            color = ElovaireColors.TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Section 1: Audio Engine & DSP Tuning
        item {
            SettingsSectionHeader(title = "AUDIO ENGINE & HARDWARE DSP")

            Column(modifier = Modifier.fillMaxWidth()) {
                // Row 1: Studio DSP Equalizer (Tappable drill-down)
                GroupedListRowContainer(
                    index = 0,
                    count = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .elovairePressScale()
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onOpenEqualizer()
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SettingsIconBox(icon = Icons.Default.Equalizer, tint = ElovaireColors.AzureAccent)
                            Column {
                                Text(
                                    text = "Studio DSP Equalizer",
                                    color = ElovaireColors.TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (eqEnabled) "Active · $eqPreset Profile" else "Bypassed (Bit-Perfect)",
                                    color = if (eqEnabled) ElovaireColors.AzureAccent else ElovaireColors.TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = ElovaireColors.TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                GroupedRowDivider(insetStart = 56.dp)

                // Row 2: Dolby Spatial Audio Engine
                GroupedListRowContainer(
                    index = 1,
                    count = 3,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SettingsIconBox(icon = Icons.Default.GraphicEq, tint = ElovaireColors.CyanAccent)
                            Column {
                                Text(
                                    text = "Dolby Cinema Soundstage",
                                    color = ElovaireColors.TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (isDolbyEnabled) "Hardware matrix active" else "Standard stereo field",
                                    color = ElovaireColors.TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Switch(
                            checked = isDolbyEnabled,
                            onCheckedChange = {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                HyprEqualizer.setDolbyEnabled(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ElovaireColors.Cloud,
                                checkedTrackColor = ElovaireColors.AzureAccent,
                                uncheckedThumbColor = ElovaireColors.TextMuted,
                                uncheckedTrackColor = ElovaireColors.Graphite
                            )
                        )
                    }
                }

                GroupedRowDivider(insetStart = 56.dp)

                // Row 3: PCM Buffer Specs
                GroupedListRowContainer(
                    index = 2,
                    count = 3,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SettingsIconBox(icon = Icons.Default.Security, tint = ElovaireColors.SuccessGreen)
                            Column {
                                Text(
                                    text = "Headroom Limiter & Buffer",
                                    color = ElovaireColors.TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "180s RAM Cache · Zero-clipping limiter",
                                    color = ElovaireColors.TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(ElovaireRadii.pill))
                                .background(ElovaireColors.Graphite)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = ElovaireColors.SuccessGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Library & Local Storage
        item {
            SettingsSectionHeader(title = "MEDIA STORAGE & SCANNER")

            Column(modifier = Modifier.fillMaxWidth()) {
                // Row 1: Re-scan device storage
                GroupedListRowContainer(
                    index = 0,
                    count = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .elovairePressScale()
                        .clickable {
                            if (!isScanning) {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                onRescanMedia()
                                Toast.makeText(context, "Indexing storage...", Toast.LENGTH_SHORT).show()
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SettingsIconBox(icon = Icons.Default.Refresh, tint = ElovaireColors.AzureAccent)
                            Column {
                                Text(
                                    text = "Rescan Device Media",
                                    color = ElovaireColors.TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (isScanning) "Scanning internal storage..." else "Re-index local FLAC, MP3, WAV tracks",
                                    color = ElovaireColors.TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = ElovaireColors.AzureAccent,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Scan",
                                color = ElovaireColors.AzureAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                GroupedRowDivider(insetStart = 56.dp)

                // Row 2: Storage Permissions
                GroupedListRowContainer(
                    index = 1,
                    count = 2,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SettingsIconBox(icon = Icons.Default.Folder, tint = ElovaireColors.PurpleAccent)
                            Column {
                                Text(
                                    text = "Storage Access Permission",
                                    color = ElovaireColors.TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Standard Android MediaStore Access",
                                    color = ElovaireColors.TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(ElovaireRadii.pill))
                                .background(ElovaireColors.Graphite)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "GRANTED",
                                color = ElovaireColors.SuccessGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Telegram Cloud Sync
        if (telegramRepository != null) {
            item {
                SettingsSectionHeader(title = "TELEGRAM CLOUD AUDIO")

                Column(modifier = Modifier.fillMaxWidth()) {
                    // Row 1: Cloud Tracks & Sync
                    GroupedListRowContainer(
                        index = 0,
                        count = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .elovairePressScale()
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                coroutineScope.launch {
                                    telegramRepository.syncLibrary(force = true)
                                    Toast.makeText(context, "Syncing Telegram audio...", Toast.LENGTH_SHORT).show()
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 13.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SettingsIconBox(icon = Icons.Default.CloudSync, tint = ElovaireColors.AzureAccent)
                                Column {
                                    Text(
                                        text = "Sync Cloud Tracks",
                                        color = ElovaireColors.TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${cloudTracks.size} Cloud tracks indexed",
                                        color = ElovaireColors.TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            if (isSyncingCloud) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = ElovaireColors.AzureAccent,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Sync Now",
                                    color = ElovaireColors.AzureAccent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    GroupedRowDivider(insetStart = 56.dp)

                    // Row 2: Server Host Config
                    GroupedListRowContainer(
                        index = 1,
                        count = 2,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Cloud Relay Endpoint",
                                color = ElovaireColors.TextPrimary,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(ElovaireRadii.tile))
                                        .background(ElovaireColors.Graphite)
                                        .border(1.dp, ElovaireColors.BorderSubtle, RoundedCornerShape(ElovaireRadii.tile))
                                        .padding(horizontal = 10.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    BasicTextField(
                                        value = serverUrlInput,
                                        onValueChange = { serverUrlInput = it },
                                        singleLine = true,
                                        textStyle = TextStyle(
                                            color = ElovaireColors.TextPrimary,
                                            fontSize = 12.sp
                                        ),
                                        cursorBrush = SolidColor(ElovaireColors.AzureAccent),
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                        keyboardActions = KeyboardActions(onDone = {
                                            keyboardController?.hide()
                                            val cur = telegramRepository.config.settings.value
                                            telegramRepository.config.updateSettings(
                                                serverUrl = serverUrlInput.trim(),
                                                userId = cur.userId,
                                                apiSecretKey = cur.apiSecretKey,
                                                isEnabled = cur.isEnabled,
                                                autoSyncOnStartup = cur.autoSyncOnStartup
                                            )
                                            Toast.makeText(context, "Saved server URL", Toast.LENGTH_SHORT).show()
                                        }),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(ElovaireRadii.tile))
                                        .background(ElovaireColors.AzureAccent)
                                        .elovairePressScale()
                                        .clickable {
                                            keyboardController?.hide()
                                            val cur = telegramRepository.config.settings.value
                                            telegramRepository.config.updateSettings(
                                                serverUrl = serverUrlInput.trim(),
                                                userId = cur.userId,
                                                apiSecretKey = cur.apiSecretKey,
                                                isEnabled = cur.isEnabled,
                                                autoSyncOnStartup = cur.autoSyncOnStartup
                                            )
                                            Toast.makeText(context, "Saved server URL", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Save",
                                        color = ElovaireColors.Cloud,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 4: App Information & Software Updates
        item {
            SettingsSectionHeader(title = "ABOUT & UPDATES")

            Column(modifier = Modifier.fillMaxWidth()) {
                // Row 1: Check for updates
                GroupedListRowContainer(
                    index = 0,
                    count = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .elovairePressScale()
                        .clickable {
                            if (!isCheckingUpdate) {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                coroutineScope.launch {
                                    HyprUpdateManager.checkForUpdates(context)
                                }
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SettingsIconBox(icon = Icons.Default.SystemUpdate, tint = ElovaireColors.AzureAccent)
                            Column {
                                Text(
                                    text = "Software Updates",
                                    color = ElovaireColors.TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (isCheckingUpdate) "Checking GitHub repository..." else "Version ${HyprUpdateManager.getCurrentVersion(context)}",
                                    color = ElovaireColors.TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (isCheckingUpdate) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = ElovaireColors.AzureAccent,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Check",
                                color = ElovaireColors.AzureAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                GroupedRowDivider(insetStart = 56.dp)

                // Row 2: Privacy & Source
                GroupedListRowContainer(
                    index = 1,
                    count = 2,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SettingsIconBox(icon = Icons.Default.Info, tint = ElovaireColors.TextSecondary)
                            Column {
                                Text(
                                    text = "Architecture & Engine",
                                    color = ElovaireColors.TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Kotlin 2.0+ · Jetpack Compose · ExoPlayer Media3",
                                    color = ElovaireColors.TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(ElovaireRadii.pill))
                                .background(ElovaireColors.Graphite)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "OFFLINE",
                                color = ElovaireColors.AzureAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = ElovaireColors.TextSecondary,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
    )
}

@Composable
private fun SettingsIconBox(icon: ImageVector, tint: Color) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ElovaireColors.Graphite),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(17.dp)
        )
    }
}
