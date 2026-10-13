package com.example.hyprmusic.ui.dap.screens

import android.content.Intent
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
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
 * Pixel-Accurate iPod-Inspired Now Playing Card.
 * Matches reference design with naked unboxed controls, atmospheric ambient glow,
 * clean typography, tabular lining timers, and live audio telemetry.
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
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack
    if (track == null) {
        DapEmptyState(
            title = "No Track Playing",
            message = "Select a song from your library or queue to start listening.",
            modifier = modifier
        )
        return
    }

    val context = LocalContext.current
    val view = LocalView.current
    val haptic = remember(view) { DapHapticFeedback(view) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showAudioSpecsDialog by remember { mutableStateOf(false) }

    // Favorite Star Scale Pop
    var favoriteScaleTarget by remember { mutableFloatStateOf(1f) }
    val favoriteScale by animateFloatAsState(
        targetValue = favoriteScaleTarget,
        animationSpec = DapMotion.iconMorphSpring(),
        label = "FavoriteStarScale"
    )

    // Atmospheric Deep Ocean / Midnight Gradient Card
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF14243D), // Atmospheric ambient glow
                        Color(0xFF0C1628),
                        Color(0xFF060B14)
                    ),
                    center = Offset(200f, 150f),
                    radius = 900f
                )
            )
            .border(
                width = 1.dp,
                color = Color(0xFF1E2D4A).copy(alpha = 0.5f),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ==========================================
            // TOP ROW: Artwork + Title + Artist + Album
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Square Album Art with Soft Corners & Subtle Shadow (NO cartoon neon border)
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0E1726))
                        .border(1.dp, Color(0xFF1E2D4A).copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
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
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Title, Artist, Album Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 2.dp)
                ) {
                    // Song Title with [E] Explicit Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = track.title,
                            style = DapTypography.TrackTitle,
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        // Explicit [E] Pill Badge
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF24334E))
                                .border(0.8.dp, Color(0xFF384F75), RoundedCornerShape(3.dp))
                                .padding(horizontal = 4.5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "E",
                                style = DapTypography.ExplicitBadge,
                                color = Color(0xFF94A9CF),
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
                        color = Color(0xFF8EA2C6),
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Album Name
                    Text(
                        text = track.album.ifBlank { "Unknown Album" },
                        style = DapTypography.TrackAlbum,
                        color = Color(0xFF62789E),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // ==========================================
            // MIDDLE ROW: Dolby Atmos (Left) + Star + More (Right)
            // Completely NAKED / Unboxed Controls
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
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
                    DolbyDoubleDGlyph(tint = Color(0xFF9CB1D6))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Dolby Atmos",
                        style = DapTypography.AudioBadge,
                        color = Color(0xFF9CB1D6),
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
                            .size(21.dp)
                            .graphicsLayer(scaleX = favoriteScale, scaleY = favoriteScale)
                            .clickable {
                                haptic.buttonClick()
                                favoriteScaleTarget = 1.35f
                                onToggleFavorite(track.id)
                            }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Naked More Options Icon with Dropdown
                    Box {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "More Options",
                            tint = Color(0xFF8EA2C6),
                            modifier = Modifier
                                .size(21.dp)
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

            // ==========================================
            // TIMELINE ROW: Naked Elapsed (Left), Naked Remaining (Right), Scrubber
            // ==========================================
            Column(modifier = Modifier.fillMaxWidth()) {
                val remainingMs = (playbackState.durationMs - playbackState.currentPositionMs).coerceAtLeast(0L)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Naked Elapsed Time
                    Text(
                        text = formatDuration(playbackState.currentPositionMs),
                        style = DapTypography.TimeCounter,
                        color = Color(0xFF62789E),
                        fontSize = 12.sp
                    )

                    // Naked Remaining Time (NO outlined box)
                    Text(
                        text = "-${formatDuration(remainingMs)}",
                        style = DapTypography.TimeCounter,
                        color = Color(0xFF62789E),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Playback Scrubber Bar
                DapSeekBar(
                    progress = playbackState.progress,
                    durationMs = playbackState.durationMs,
                    onSeekTo = onSeekTo
                )
            }

            // ==========================================
            // BOTTOM ROW: Connected Device (Left) + Lyrics & Queue (Right)
            // Completely NAKED / Unboxed Controls
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Naked Connected Device with Earbuds Glyph
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    EarbudsDoubleGlyph(tint = Color(0xFF8EA2C6))
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = connectedDevice,
                        style = DapTypography.DeviceLabel,
                        color = Color(0xFF8EA2C6),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Naked Action Buttons: Lyrics & Queue
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Naked Lyrics Button
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                haptic.buttonClick()
                                onOpenLyrics()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        QuoteChatBubbleGlyph(tint = Color(0xFF8EA2C6))
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Naked Queue Button
                    Box(
                        modifier = Modifier
                            .size(28.dp)
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
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }
    }

    // Audio Telemetry Modal
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
                            text = "Audio Telemetry",
                            style = DapTypography.MenuHeader,
                            color = DapColors.TextPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = DapColors.TextSecondary,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { showAudioSpecsDialog = false }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = DapColors.ScreenCardBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    AudioSpecRow(label = "Codec Format", value = track.audioFormat)
                    AudioSpecRow(label = "Resolution", value = if (track.isLossless) "24-bit / 96kHz Lossless" else "${track.bitrate} kbps Hi-Fi")
                    AudioSpecRow(label = "Fidelity Profile", value = if (track.isLossless) "Studio Master" else "Direct Stream")
                    AudioSpecRow(label = "Output Sink", value = connectedDevice)
                    if (track.formattedFileSize.isNotEmpty()) {
                        AudioSpecRow(label = "File Size", value = track.formattedFileSize)
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioSpecRow(label: String, value: String) {
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
    Canvas(modifier = modifier.size(16.dp, 10.dp)) {
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
 * Two Earbuds / AirPods glyph matching reference.
 */
@Composable
fun EarbudsDoubleGlyph(
    tint: Color = DapColors.TextPrimary,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(15.dp, 15.dp)) {
        val w = size.width
        val h = size.height

        // Left earbud
        drawCircle(color = tint, radius = 2.8f, center = Offset(w * 0.3f, h * 0.35f))
        drawLine(
            color = tint,
            start = Offset(w * 0.3f, h * 0.35f),
            end = Offset(w * 0.24f, h * 0.85f),
            strokeWidth = 1.8f,
            cap = StrokeCap.Round
        )

        // Right earbud
        drawCircle(color = tint, radius = 2.8f, center = Offset(w * 0.7f, h * 0.35f))
        drawLine(
            color = tint,
            start = Offset(w * 0.7f, h * 0.35f),
            end = Offset(w * 0.76f, h * 0.85f),
            strokeWidth = 1.8f,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Quote chat bubble glyph matching reference.
 */
@Composable
fun QuoteChatBubbleGlyph(
    tint: Color = DapColors.TextPrimary,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(17.dp), contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = "Lyrics",
            tint = tint,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = "”",
            color = tint,
            fontSize = 9.sp,
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
