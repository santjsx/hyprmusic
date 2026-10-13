package com.example.hyprmusic.ui.dap.screens

import android.content.Intent
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hyprmusic.core.model.PlaybackState
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.ui.dap.components.DapEmptyState
import com.example.hyprmusic.ui.dap.components.DapSeekBar
import com.example.hyprmusic.ui.dap.state.DapHapticFeedback
import com.example.hyprmusic.ui.dap.theme.DapColors
import com.example.hyprmusic.ui.dap.theme.DapMotion
import com.example.hyprmusic.ui.dap.theme.DapTypography

/**
 * Pixel-Accurate iPod-Inspired Now Playing Card (Matching Reference Design).
 * - Full-height vertical distribution (eliminates bottom empty void).
 * - Prominent 136dp square album art with ambient glow backdrop.
 * - Naked unboxed controls: Dolby Atmos, outline star, circular more options,
 *   tabular remaining timer (-3:40), precision scrubber, live AirPods glyph,
 *   quote chat-bubble lyrics, and bulleted queue list.
 */
@Composable
fun DapNowPlayingDisplay(
    playbackState: PlaybackState,
    connectedDevice: String,
    onToggleFavorite: (String) -> Unit,
    onSeekTo: (Long) -> Unit,
    onOpenLyrics: () -> Unit,
    onOpenQueue: () -> Unit,
    onAddToPlaylist: ((Track) -> Unit)? = null,
    onBrowseLibrary: (() -> Unit)? = null,
    onShuffleAll: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack
    if (track == null) {
        DapEmptyState(
            title = "No Track Playing",
            message = "Select a song from your library or queue to start listening.",
            actionText = "Shuffle Entire Library",
            onActionClick = onShuffleAll,
            secondaryActionText = "Browse Songs",
            onSecondaryActionClick = onBrowseLibrary,
            modifier = modifier
        )
        return
    }

    val context = LocalContext.current
    val view = LocalView.current
    val haptic = remember(view) { DapHapticFeedback(view) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showAudioSpecsDialog by remember { mutableStateOf(false) }
    var showDeviceRouteDialog by remember { mutableStateOf(false) }

    // Favorite Star Scale Pop
    var favoriteScaleTarget by remember { mutableFloatStateOf(1f) }
    val favoriteScale by animateFloatAsState(
        targetValue = favoriteScaleTarget,
        animationSpec = DapMotion.iconMorphSpring(),
        label = "FavoriteStarScale"
    )

    // Atmospheric Ambient Deep Ocean / Navy Canvas
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E355B), // Soft ambient glow radiating from artwork
                        Color(0xFF0F1B2E),
                        Color(0xFF070E1A)
                    ),
                    center = Offset(140f, 120f),
                    radius = 850f
                )
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // =========================================================================
            // ROW 1: ALBUM ARTWORK + TITLE + ARTIST + ALBUM
            // =========================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Square Album Art with Soft Corners & Ambient Depth (NO cartoon neon border)
                Box(
                    modifier = Modifier
                        .size(136.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0B1424))
                        .border(1.dp, Color(0xFF243B61).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!track.albumArtUri.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(track.albumArtUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Cover for ${track.title}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Color(0xFF8EA2C6).copy(alpha = 0.5f),
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Title, Explicit Pill, Artist, Album Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 2.dp)
                ) {
                    // Song Title with [E] Explicit Pill Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = track.title,
                            style = DapTypography.TrackTitle,
                            color = Color.White,
                            fontSize = 19.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Explicit [E] Pill Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF243552))
                                .border(0.8.dp, Color(0xFF3B5685), RoundedCornerShape(3.dp))
                                .padding(horizontal = 4.5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "E",
                                style = DapTypography.ExplicitBadge,
                                color = Color(0xFFA1B7DE),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Artist Name
                    Text(
                        text = track.artist.ifBlank { "Unknown Artist" },
                        style = DapTypography.TrackArtist,
                        color = Color(0xFF90A5C9),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // Album Name
                    Text(
                        text = track.album.ifBlank { "Unknown Album" },
                        style = DapTypography.TrackAlbum,
                        color = Color(0xFF677F9F),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // =========================================================================
            // ROW 2: DOLBY ATMOS (LEFT) + STAR & MORE (RIGHT)
            // Naked Unboxed Controls
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dolby Atmos Naked Vector Glyph & Text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        haptic.buttonClick()
                        showAudioSpecsDialog = true
                    }
                ) {
                    DolbyDoubleDGlyph(tint = Color(0xFFA1B7DE))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Dolby Atmos",
                        style = DapTypography.AudioBadge,
                        color = Color(0xFFA1B7DE),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Favorite Star & More Options (Naked Icons)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Naked Favorite Star
                    LaunchedEffect(favoriteScaleTarget) {
                        if (favoriteScaleTarget > 1f) {
                            kotlinx.coroutines.delay(120)
                            favoriteScaleTarget = 1f
                        }
                    }

                    Icon(
                        imageVector = if (track.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = if (track.isFavorite) "Remove Favorite" else "Add Favorite",
                        tint = if (track.isFavorite) Color(0xFFFFC107) else Color(0xFF8EA2C6),
                        modifier = Modifier
                            .size(22.dp)
                            .graphicsLayer(scaleX = favoriteScale, scaleY = favoriteScale)
                            .clickable {
                                haptic.buttonClick()
                                favoriteScaleTarget = 1.35f
                                onToggleFavorite(track.id)
                            }
                    )

                    Spacer(modifier = Modifier.width(18.dp))

                    // Naked More Options Icon with Dropdown
                    Box {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "More Options",
                            tint = Color(0xFF8EA2C6),
                            modifier = Modifier
                                .size(22.dp)
                                .clickable {
                                    haptic.buttonClick()
                                    showMoreMenu = true
                                }
                        )

                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false },
                            modifier = Modifier.background(DapColors.ChassisTitanium)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Add to Playlist", color = DapColors.TextPrimary) },
                                onClick = {
                                    showMoreMenu = false
                                    onAddToPlaylist?.invoke(track)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Track Details", color = DapColors.TextPrimary) },
                                onClick = {
                                    showMoreMenu = false
                                    showAudioSpecsDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Share Track", color = DapColors.TextPrimary) },
                                onClick = {
                                    showMoreMenu = false
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, "Listening to ${track.title} by ${track.artist}")
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Track"))
                                }
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // ROW 3: TIMELINE (ELAPSED TIME, REMAINING TIME & TACTILE SCRUBBER)
            // =========================================================================
            Column(modifier = Modifier.fillMaxWidth()) {
                val remainingMs = (playbackState.durationMs - playbackState.currentPositionMs).coerceAtLeast(0L)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Elapsed Time (Left)
                    Text(
                        text = formatDuration(playbackState.currentPositionMs),
                        style = DapTypography.TimeCounter,
                        color = Color(0xFF7C94BA),
                        fontSize = 12.sp
                    )

                    // Remaining Time (Right) - e.g. -3:40
                    Text(
                        text = "-${formatDuration(remainingMs)}",
                        style = DapTypography.TimeCounter,
                        color = Color(0xFF7C94BA),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Playback Scrubber Bar
                DapSeekBar(
                    progress = playbackState.progress,
                    durationMs = playbackState.durationMs,
                    onSeekTo = onSeekTo
                )
            }

            // =========================================================================
            // ROW 4: CONNECTED AUDIO DEVICE (LEFT) + LYRICS & QUEUE (RIGHT)
            // Completely NAKED / Unboxed Controls sitting right above the bottom bezel
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Connected Audio Device with Authentic AirPods Dual-Bud Glyph
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clickable {
                            haptic.buttonClick()
                            showDeviceRouteDialog = true
                        }
                ) {
                    EarbudsDoubleGlyph(tint = Color(0xFFA1B7DE))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = connectedDevice,
                        style = DapTypography.DeviceLabel,
                        color = Color(0xFFA1B7DE),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Naked Action Buttons: Lyrics & Queue
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Naked Lyrics Button (Quote Chat Bubble Glyph)
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                haptic.buttonClick()
                                onOpenLyrics()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        QuoteChatBubbleGlyph(tint = Color(0xFF8EA2C6))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Naked Queue Button (Bulleted List Glyph)
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                haptic.buttonClick()
                                onOpenQueue()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                            contentDescription = "Queue",
                            tint = Color(0xFF8EA2C6),
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }
            }
        }
    }

    // =========================================================================
    // AUDIO SPECS MODAL
    // =========================================================================
    if (showAudioSpecsDialog) {
        Dialog(onDismissRequest = { showAudioSpecsDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DapColors.ChassisTitanium)
                    .border(1.dp, DapColors.ChassisBorder, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Audio Telemetry & Specs",
                            style = DapTypography.MenuHeader,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { showAudioSpecsDialog = false }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TelemetrySpecRow(label = "Format", value = "Dolby Atmos / FLAC Lossless")
                    TelemetrySpecRow(label = "Resolution", value = "24-bit / 96 kHz Studio Master")
                    TelemetrySpecRow(label = "Audio Route", value = connectedDevice)
                    TelemetrySpecRow(label = "DRC Limiter", value = "Active 10:1 DynamicsProcessing")
                    TelemetrySpecRow(label = "DSP Target", value = "AutoEq Harman Reference Curve")

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showAudioSpecsDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = DapColors.AccentIceBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // =========================================================================
    // CONNECTED AUDIO DEVICE INFO / ROUTE DIALOG
    // =========================================================================
    if (showDeviceRouteDialog) {
        Dialog(onDismissRequest = { showDeviceRouteDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DapColors.ChassisTitanium)
                    .border(1.dp, DapColors.ChassisBorder, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Headphones, contentDescription = null, tint = DapColors.AccentIceBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Active Audio Route",
                                style = DapTypography.MenuHeader,
                                color = Color.White
                            )
                        }
                        IconButton(onClick = { showDeviceRouteDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = connectedDevice,
                        style = DapTypography.MenuItem,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Audio stream is actively routed to this endpoint with synchronized low-latency buffer.",
                        style = DapTypography.MenuSecondary,
                        color = DapColors.TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                showDeviceRouteDialog = false
                                try {
                                    val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DapColors.AccentIceBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Bluetooth Settings", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        }

                        OutlinedButton(
                            onClick = { showDeviceRouteDialog = false },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Dismiss", color = Color.White, fontSize = 11.5.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetrySpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = DapTypography.MenuSecondary, color = DapColors.TextSecondary)
        Text(text = value, style = DapTypography.MenuItem, color = DapColors.AccentIceBlue, fontWeight = FontWeight.Bold)
    }
}

/**
 * Authentic Dolby Atmos double-D vector glyph matching reference.
 */
@Composable
fun DolbyDoubleDGlyph(
    tint: Color = DapColors.TextPrimary,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(17.dp, 11.dp)) {
        val w = size.width
        val h = size.height
        val rectW = w * 0.44f

        // Left Mirrored D
        val leftPath = Path().apply {
            moveTo(rectW, 0f)
            lineTo(0f, 0f)
            lineTo(0f, h)
            lineTo(rectW, h)
            close()
        }
        drawPath(leftPath, color = tint)
        drawCircle(
            color = Color.Black.copy(alpha = 0.9f),
            radius = h * 0.32f,
            center = Offset(rectW * 0.1f, h * 0.5f)
        )

        // Right D
        val rightPath = Path().apply {
            moveTo(w - rectW, 0f)
            lineTo(w, 0f)
            lineTo(w, h)
            lineTo(w - rectW, h)
            close()
        }
        drawPath(rightPath, color = tint)
        drawCircle(
            color = Color.Black.copy(alpha = 0.9f),
            radius = h * 0.32f,
            center = Offset(w - rectW * 0.1f, h * 0.5f)
        )
    }
}

/**
 * Authentic Two Earbuds / AirPods dual glyph matching reference image.
 * Uses proportional geometric head circles and stems for crisp rendering on high-DPI screens.
 */
@Composable
fun EarbudsDoubleGlyph(
    tint: Color = DapColors.TextPrimary,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(17.dp, 17.dp)) {
        val w = size.width
        val h = size.height
        val headRadius = w * 0.18f

        // Left Earbud Head & Stem
        drawCircle(
            color = tint,
            radius = headRadius,
            center = Offset(w * 0.28f, h * 0.30f)
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.28f, h * 0.30f),
            end = Offset(w * 0.20f, h * 0.85f),
            strokeWidth = w * 0.14f,
            cap = StrokeCap.Round
        )

        // Right Earbud Head & Stem
        drawCircle(
            color = tint,
            radius = headRadius,
            center = Offset(w * 0.72f, h * 0.30f)
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.72f, h * 0.30f),
            end = Offset(w * 0.80f, h * 0.85f),
            strokeWidth = w * 0.14f,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Authentic Quote chat bubble glyph matching reference image.
 */
@Composable
fun QuoteChatBubbleGlyph(
    tint: Color = DapColors.TextPrimary,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(18.dp), contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = "Lyrics",
            tint = tint,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = "”",
            color = tint,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = (durationMs / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%d:%02d".format(minutes, seconds)
    }
}
