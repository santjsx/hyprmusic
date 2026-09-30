package com.example.hyprmusic.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.hyprmusic.core.media.HyprEqualizer
import com.example.hyprmusic.core.theming.HyprThemeConfig
import com.example.hyprmusic.core.theming.ThemeManager
import com.example.hyprmusic.core.theming.ThemePreset
import com.example.hyprmusic.core.theming.ThemePresets
import com.example.hyprmusic.core.theming.hyprAnimatedGlow
import com.example.hyprmusic.core.theming.hyprBounceClick
import com.example.hyprmusic.core.theming.hyprTile
import com.example.hyprmusic.core.updater.HyprUpdateManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    theme: HyprThemeConfig,
    isScanning: Boolean,
    onRescanMedia: () -> Unit,
    onOpenEqualizer: () -> Unit,
    telegramRepository: com.example.hyprmusic.core.cloud.telegram.TelegramMusicRepository? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showPrivacyPolicy by remember { mutableStateOf(false) }

    val updateInfo by HyprUpdateManager.updateState.collectAsState()
    val isCheckingUpdate by HyprUpdateManager.isChecking.collectAsState()
    val downloadProgress by HyprUpdateManager.downloadProgress.collectAsState()
    val eqEnabled by HyprEqualizer.isEnabled.collectAsState()
    val eqPreset by HyprEqualizer.currentPreset.collectAsState()

    val cloudSettings = telegramRepository?.config?.settings?.collectAsState()?.value
    val isSyncingCloud = telegramRepository?.isSyncing?.collectAsState()?.value ?: false
    val isWakingServer = telegramRepository?.isWakingServer?.collectAsState()?.value ?: false
    val serverHealth = telegramRepository?.serverHealth?.collectAsState()?.value
    val syncError = telegramRepository?.syncError?.collectAsState()?.value

    var serverHostInput by remember(cloudSettings?.serverUrl) { mutableStateOf(cloudSettings?.serverUrl ?: "http://10.0.2.2:8080") }
    var userIdInput by remember(cloudSettings?.userId) { mutableStateOf(if ((cloudSettings?.userId ?: 0L) > 0L) cloudSettings!!.userId.toString() else "") }
    var apiKeyInput by remember(cloudSettings?.apiSecretKey) { mutableStateOf(cloudSettings?.apiSecretKey ?: "") }
    var testResultText by remember { mutableStateOf<String?>(null) }
    var isTestingConnection by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = theme.windowGapsDp.dp),
        verticalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
    ) {
        // Section: System Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .hyprTile(theme = theme)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(theme.surfaceVariantColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HYPRMUSIC CONFIG",
                            color = theme.textPrimaryColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Hyprland Tiling Ricing & Audio Suite",
                            color = theme.textSecondaryColor,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }

        // Section: Audio DSP & Equalizer
        item {
            Column {
                Text(
                    text = "AUDIO ENGINE // DSP",
                    color = theme.textSecondaryColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .hyprTile(theme = theme)
                        .clickable { onOpenEqualizer() }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(theme.surfaceVariantColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = theme.accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Parametric Equalizer",
                                color = theme.textPrimaryColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (eqEnabled) "Active ($eqPreset) • 5-Band Hardware DSP" else "Disabled • Tap to configure",
                                color = if (eqEnabled) theme.accentColor else theme.textSecondaryColor,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(theme.surfaceVariantColor)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "[ CONFIGURE ]",
                                color = theme.accentColor,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section: Theme Presets
        item {
            Column {
                Text(
                    text = "DESKTOP THEMES // PALETTE",
                    color = theme.textSecondaryColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(theme.windowGapsDp.dp)
                ) {
                    items(ThemePreset.values()) { preset ->
                        val isSelected = theme.preset == preset
                        val presetTheme = remember(preset) { ThemePresets.getPreset(preset) }
                        Box(
                            modifier = Modifier
                                .width(135.dp)
                                .hyprTile(theme = theme, isActive = isSelected)
                                .hyprBounceClick { ThemeManager.setPreset(preset) }
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = preset.displayName.split(" ").first().uppercase(),
                                        color = if (isSelected) theme.accentColor else theme.textPrimaryColor,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = theme.accentColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Unique color swatches for each distinct theme
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(presetTheme.activeBorderGradient.first())
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(presetTheme.activeBorderGradient.last())
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(presetTheme.accentColor)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Live Rice Metrics
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .hyprTile(theme = theme)
                    .padding(14.dp)
            ) {
                Text(
                    text = "WINDOW MANAGER METRICS",
                    color = theme.textSecondaryColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Gaps Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Window Gaps", color = theme.textPrimaryColor, fontSize = 12.sp)
                    Text(text = "${theme.windowGapsDp}px", color = theme.accentColor, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                }
                Slider(
                    value = theme.windowGapsDp.toFloat(),
                    onValueChange = { ThemeManager.updateGaps(it.toInt()) },
                    valueRange = 4f..16f,
                    colors = SliderDefaults.colors(thumbColor = theme.accentColor, activeTrackColor = theme.accentColor),
                    modifier = Modifier.height(28.dp)
                )

                // Corner Radius Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Border Radius", color = theme.textPrimaryColor, fontSize = 12.sp)
                    Text(text = "${theme.borderRadiusDp}px", color = theme.accentColor, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                }
                Slider(
                    value = theme.borderRadiusDp.toFloat(),
                    onValueChange = { ThemeManager.updateRadius(it.toInt()) },
                    valueRange = 0f..24f,
                    colors = SliderDefaults.colors(thumbColor = theme.accentColor, activeTrackColor = theme.accentColor),
                    modifier = Modifier.height(28.dp)
                )

                // Border Thickness Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Border Thickness", color = theme.textPrimaryColor, fontSize = 12.sp)
                    Text(text = "${theme.borderThicknessDp}px", color = theme.accentColor, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                }
                Slider(
                    value = theme.borderThicknessDp.toFloat(),
                    onValueChange = { ThemeManager.updateBorderThickness(it.toInt()) },
                    valueRange = 1f..4f,
                    colors = SliderDefaults.colors(thumbColor = theme.accentColor, activeTrackColor = theme.accentColor),
                    modifier = Modifier.height(28.dp)
                )

                // OLED Pitch Black Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "OLED Pure Black Mode", color = theme.textPrimaryColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text(text = "0% energy black backdrop", color = theme.textSecondaryColor, fontSize = 11.sp)
                    }
                    Switch(
                        checked = theme.isOledMode,
                        onCheckedChange = { ThemeManager.toggleOled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = theme.backgroundColor,
                            checkedTrackColor = theme.accentColor
                        )
                    )
                }
            }
        }

        // Section: Telegram Personal Music Cloud (TPMC)
        item {
            Column {
                Text(
                    text = "TELEGRAM CLOUD STORAGE // TPMC",
                    color = theme.textSecondaryColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .hyprTile(theme = theme)
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Telegram Music Gateway",
                                    color = theme.textPrimaryColor,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Stream & download from your private channel",
                                    color = theme.textSecondaryColor,
                                    fontSize = 11.sp
                                )
                            }

                            Switch(
                                checked = cloudSettings?.isEnabled ?: false,
                                onCheckedChange = { enabled ->
                                    telegramRepository?.config?.updateSettings(
                                        serverUrl = serverHostInput,
                                        userId = userIdInput.toLongOrNull() ?: 0L,
                                        apiSecretKey = apiKeyInput,
                                        isEnabled = enabled
                                    )
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = theme.backgroundColor,
                                    checkedTrackColor = theme.accentColor
                                )
                            )
                        }

                        // Server Host Input
                        Column {
                            Text(
                                text = "SERVER HOST (URL / IP:PORT)",
                                color = theme.textSecondaryColor,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(theme.surfaceVariantColor)
                                    .border(1.dp, theme.inactiveBorderColor, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                BasicTextField(
                                    value = serverHostInput,
                                    onValueChange = { serverHostInput = it },
                                    textStyle = TextStyle(
                                        color = theme.textPrimaryColor,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.5.sp
                                    ),
                                    cursorBrush = SolidColor(theme.accentColor),
                                    modifier = Modifier.fillMaxWidth(),
                                    decorationBox = { innerTextField ->
                                        if (serverHostInput.isEmpty()) {
                                            Text(
                                                text = "https://tpmc-music-cloud.onrender.com",
                                                color = theme.textSecondaryColor.copy(alpha = 0.5f),
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 12.5.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                        }

                        // User ID & API Secret Inputs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "USER ID (TELEGRAM)",
                                    color = theme.textSecondaryColor,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(theme.surfaceVariantColor)
                                        .border(1.dp, theme.inactiveBorderColor, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    BasicTextField(
                                        value = userIdInput,
                                        onValueChange = { userIdInput = it.filter { ch -> ch.isDigit() } },
                                        textStyle = TextStyle(
                                            color = theme.textPrimaryColor,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.5.sp
                                        ),
                                        cursorBrush = SolidColor(theme.accentColor),
                                        modifier = Modifier.fillMaxWidth(),
                                        decorationBox = { innerTextField ->
                                            if (userIdInput.isEmpty()) {
                                                Text(
                                                    text = "123456789",
                                                    color = theme.textSecondaryColor.copy(alpha = 0.5f),
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 12.5.sp
                                                )
                                            }
                                            innerTextField()
                                        }
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "API SECRET (OPTIONAL)",
                                    color = theme.textSecondaryColor,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(theme.surfaceVariantColor)
                                        .border(1.dp, theme.inactiveBorderColor, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    BasicTextField(
                                        value = apiKeyInput,
                                        onValueChange = { apiKeyInput = it },
                                        textStyle = TextStyle(
                                            color = theme.textPrimaryColor,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.5.sp
                                        ),
                                        cursorBrush = SolidColor(theme.accentColor),
                                        modifier = Modifier.fillMaxWidth(),
                                        decorationBox = { innerTextField ->
                                            if (apiKeyInput.isEmpty()) {
                                                Text(
                                                    text = "secret_token",
                                                    color = theme.textSecondaryColor.copy(alpha = 0.5f),
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 12.5.sp
                                                )
                                            }
                                            innerTextField()
                                        }
                                    )
                                }
                            }
                        }

                        // Status banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(theme.backgroundColor)
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (isWakingServer) {
                                    Text(
                                        text = "⏳ [TPMC WAKING UP... Render cold boot in progress]",
                                        color = Color(0xFFFFB74D),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else if (serverHealth != null) {
                                    Text(
                                        text = "● CONNECTED (Status: ${serverHealth.status} | MTProto: ${serverHealth.telegram} | Bot: ${serverHealth.bot})",
                                        color = Color(0xFF4CAF50),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Catalog: ${serverHealth.totalTracks} tracks • Server RAM: ${"%.1f".format(serverHealth.memoryMb)}MB",
                                        color = theme.textSecondaryColor,
                                        fontSize = 10.5.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                } else if (testResultText != null) {
                                    Text(
                                        text = testResultText ?: "",
                                        color = if (testResultText?.startsWith("FAIL") == true) Color(0xFFEF5350) else Color(0xFF4CAF50),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                } else if (syncError != null) {
                                    Text(
                                        text = "▲ $syncError",
                                        color = Color(0xFFEF5350),
                                        fontSize = 10.5.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                } else {
                                    Text(
                                        text = "○ Cloud Status: ${if (cloudSettings?.isEnabled == true) "Active (${cloudSettings.cachedTrackCount} cached)" else "Inactive"}",
                                        color = theme.textSecondaryColor,
                                        fontSize = 10.5.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        // Action Buttons: Save & Test, Sync
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(theme.surfaceVariantColor)
                                    .clickable(enabled = !isTestingConnection && !isWakingServer) {
                                        coroutineScope.launch {
                                            isTestingConnection = true
                                            testResultText = "Pinging..."
                                            telegramRepository?.config?.updateSettings(
                                                serverUrl = serverHostInput,
                                                userId = userIdInput.toLongOrNull() ?: 0L,
                                                apiSecretKey = apiKeyInput,
                                                isEnabled = true
                                            )
                                            val result = telegramRepository?.testConnection()
                                            testResultText = if (result?.isSuccess == true) {
                                                "OK: Connected to ${result.getOrNull()?.status} gateway"
                                            } else {
                                                "FAIL: ${result?.exceptionOrNull()?.localizedMessage ?: "Unreachable"}"
                                            }
                                            isTestingConnection = false
                                        }
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isTestingConnection || isWakingServer) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = theme.accentColor)
                                } else {
                                    Text(
                                        text = "[ TEST CONNECTION ]",
                                        color = theme.accentColor,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(theme.accentColor.copy(alpha = 0.15f))
                                    .border(1.dp, theme.accentColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable(enabled = !isSyncingCloud) {
                                        coroutineScope.launch {
                                            telegramRepository?.config?.updateSettings(
                                                serverUrl = serverHostInput,
                                                userId = userIdInput.toLongOrNull() ?: 0L,
                                                apiSecretKey = apiKeyInput,
                                                isEnabled = true
                                            )
                                            telegramRepository?.syncLibrary(force = true)
                                        }
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSyncingCloud) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = theme.accentColor)
                                } else {
                                    Text(
                                        text = "[ SYNC NOW ]",
                                        color = theme.accentColor,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: GitHub Releases OTA Updater
        item {
            Column {
                Text(
                    text = "OTA UPDATES // GITHUB RELEASES",
                    color = theme.textSecondaryColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .hyprTile(theme = theme)
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Current Build: ${HyprUpdateManager.CURRENT_VERSION}",
                                    color = theme.textPrimaryColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Automated GitHub Releases Channel",
                                    color = theme.textSecondaryColor,
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(theme.surfaceVariantColor)
                                    .clickable(enabled = !isCheckingUpdate) {
                                        coroutineScope.launch {
                                            HyprUpdateManager.checkForUpdates()
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                if (isCheckingUpdate) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = theme.accentColor
                                    )
                                } else {
                                    Text(
                                        text = "[ CHECK ]",
                                        color = theme.accentColor,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (updateInfo != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            val info = updateInfo!!
                            if (info.isUpdateAvailable) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(theme.accentColor.copy(alpha = 0.15f))
                                        .border(1.dp, theme.accentColor, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "★ NEW VERSION AVAILABLE: ${info.tagName}",
                                            color = theme.accentColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = info.releaseNotes,
                                            color = theme.textPrimaryColor,
                                            fontSize = 11.sp,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(theme.accentColor)
                                                .clickable {
                                                    HyprUpdateManager.downloadAndInstallApk(context, info.downloadUrl)
                                                }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "DOWNLOAD & UPDATE",
                                                color = theme.backgroundColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(theme.surfaceVariantColor)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "✔ You are running the latest bit-perfect build (${info.tagName})",
                                        color = theme.accentColor,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        if (downloadProgress != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = downloadProgress!!,
                                color = theme.accentColor,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Section: About & Developer Santhosh Reddy
        item {
            Column {
                Text(
                    text = "ABOUT // DEVELOPER",
                    color = theme.textSecondaryColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .hyprTile(theme = theme)
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(theme.surfaceVariantColor)
                                    .border(1.dp, theme.accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = theme.accentColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = HyprUpdateManager.DEVELOPER_NAME,
                                        color = theme.textPrimaryColor,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified Creator",
                                        tint = theme.accentColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    text = "Lead Architect & Developer",
                                    color = theme.textSecondaryColor,
                                    fontSize = 11.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "HyprMusic is an ultra-fast, offline-first audiophile music player built with Jetpack Compose, Media3 ExoPlayer, and inspired by the modern Hyprland Linux ricing aesthetic.",
                            color = theme.textSecondaryColor,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Privacy Policy Button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(theme.surfaceVariantColor)
                                    .clickable { showPrivacyPolicy = true }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = theme.accentColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "PRIVACY POLICY",
                                        color = theme.accentColor,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // GitHub Repo Button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(theme.surfaceVariantColor)
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(HyprUpdateManager.GITHUB_REPO_URL)).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(intent)
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "[ GITHUB REPO ]",
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
        }

        item { Spacer(modifier = Modifier.height(90.dp)) }
    }

    // In-App Privacy Policy Dialog
    if (showPrivacyPolicy) {
        Dialog(onDismissRequest = { showPrivacyPolicy = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(theme.surfaceColor)
                    .border(1.dp, theme.accentColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = theme.accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PRIVACY POLICY",
                                color = theme.textPrimaryColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        IconButton(onClick = { showPrivacyPolicy = false }, modifier = Modifier.size(26.dp)) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Close",
                                tint = theme.accentColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "1. 100% Offline-First Architecture:\n" +
                               "HyprMusic never collects, transmits, sells, or stores personal user data on external servers.\n\n" +
                               "2. Media Access:\n" +
                               "Read access to device audio storage is used exclusively to scan and play music files locally.\n\n" +
                               "3. Network Connectivity:\n" +
                               "Internet access is used solely for optional real-time LRCLIB synced lyrics search and GitHub Releases OTA version checking.\n\n" +
                               "4. Zero Analytics / Ads:\n" +
                               "No advertisement SDKs, third-party tracking cookies, or analytics libraries are bundled into HyprMusic.",
                        color = theme.textSecondaryColor,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.accentColor)
                            .clickable { showPrivacyPolicy = false }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ACKNOWLEDGED",
                            color = theme.backgroundColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
