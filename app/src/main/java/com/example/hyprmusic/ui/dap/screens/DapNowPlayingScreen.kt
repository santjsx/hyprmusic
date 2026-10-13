package com.example.hyprmusic.ui.dap.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
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
 * Phase 3 Flagship Reference-Accurate Now Playing LCD Display.
 * Matches all 13 callout elements from the reference design with pixel accuracy,
 * Level 4 sub-animation micro-interactions, and high-fidelity audio telemetry.
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

    // Level 4 Favorite Star Scale Pop
    var favoriteScaleTarget by remember { mutableFloatStateOf(1f) }
    val favoriteScale by animateFloatAsState(
        targetValue = favoriteScaleTarget,
        animationSpec = DapMotion.iconMorphSpring(),
        label = "FavoriteStarScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // ==========================================
        // UPPER HALF: Artwork (1) + Track Info (2,3,4) + Buttons (6,7)
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // [1] Square Album Art with Multi-Layer Neon Glow Border (Callout 1)
            Box(
                modifier = Modifier
                    .size(114.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DapColors.ScreenCard)
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFFC084FC), // Neon lavender highlight
                                DapColors.AccentIceBlue, // Ice-blue cyan
                                Color(0xFF818CF8)  // Indigo
                            )
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ),
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
                        tint = DapColors.TextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Metadata Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(114.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // [2] Song Title with Explicit [E] Badge (Callout 2)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = track.title,
                            style = DapTypography.TrackTitle,
                            color = DapColors.TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        // Explicit badge
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(DapColors.AccentIceBlue.copy(alpha = 0.25f))
                                .border(0.8.dp, DapColors.AccentIceBlue.copy(alpha = 0.6f), RoundedCornerShape(3.dp))
                                .padding(horizontal = 4.dp, vertical = 0.5.dp)
                        ) {
                            Text(
                                text = "E",
                                style = DapTypography.ExplicitBadge,
                                color = DapColors.AccentIceBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // [3] Artist Name (Callout 3)
                    Text(
                        text = track.artist.ifBlank { "Unknown Artist" },
                        style = DapTypography.TrackArtist,
                        color = DapColors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // [4] Album Name (Callout 4)
                    Text(
                        text = track.album.ifBlank { "Unknown Album" },
                        style = DapTypography.TrackAlbum,
                        color = DapColors.TextSecondary.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // [6] Favorite Star & [7] More Options Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // [6] Favorite / Star Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DapColors.ScreenCard)
                            .border(1.dp, DapColors.ScreenCardBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                haptic.buttonClick()
                                favoriteScaleTarget = 1.25f
                                onToggleFavorite(track.id)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        LaunchedEffect(favoriteScaleTarget) {
                            if (favoriteScaleTarget > 1f) {
                                kotlinx.coroutines.delay(100)
                                favoriteScaleTarget = 1f
                            }
                        }
                        Icon(
                            imageVector = if (track.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (track.isFavorite) "Remove Favorite" else "Add Favorite",
                            tint = if (track.isFavorite) DapColors.AccentIceBlue else DapColors.TextPrimary,
                            modifier = Modifier
                                .size(18.dp)
                                .graphicsLayer(scaleX = favoriteScale, scaleY = favoriteScale)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // [7] More Options Button with Dropdown
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DapColors.ScreenCard)
                            .border(1.dp, DapColors.ScreenCardBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                haptic.buttonClick()
                                showMoreMenu = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "More Options",
                            tint = DapColors.TextPrimary,
                            modifier = Modifier.size(18.dp)
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
        }

        // ==========================================
        // [5] Audio Quality Indicator with Dolby Double-D Glyph (Callout 5)
        // Positioned directly beneath artwork
        // ==========================================
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(DapColors.ScreenCard)
                .border(1.dp, DapColors.ScreenCardBorder, RoundedCornerShape(6.dp))
                .clickable {
                    haptic.buttonClick()
                    showAudioSpecsDialog = true
                }
                .padding(horizontal = 9.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DolbyDoubleDGlyph(tint = DapColors.TextPrimary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Dolby Atmos",
                style = DapTypography.AudioBadge,
                color = DapColors.TextPrimary
            )
        }

        // ==========================================
        // TIMELINE: Elapsed (8), Seek Bar (9), Remaining (10)
        // ==========================================
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // [8] Elapsed Time
                Text(
                    text = formatDuration(playbackState.currentPositionMs),
                    style = DapTypography.TimeCounter,
                    color = DapColors.TextPrimary
                )

                // [10] Remaining Time in Rounded Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DapColors.ScreenCard)
                        .border(1.dp, DapColors.ScreenCardBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 2.5.dp)
                ) {
                    val remainingMs = (playbackState.durationMs - playbackState.currentPositionMs).coerceAtLeast(0L)
                    Text(
                        text = "-${formatDuration(remainingMs)}",
                        style = DapTypography.TimeCounter,
                        color = DapColors.TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // [9] Playback Progress Scrubber
            DapSeekBar(
                progress = playbackState.progress,
                durationMs = playbackState.durationMs,
                onSeekTo = onSeekTo
            )
        }

        // ==========================================
        // BOTTOM ROW: Connected Device (11), Lyrics (12), Queue (13)
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // [11] Connected Device Pill with Earbuds Glyph
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DapColors.ScreenCard)
                    .border(1.dp, DapColors.ScreenCardBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EarbudsDoubleGlyph(tint = DapColors.TextPrimary)
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = connectedDevice,
                    style = DapTypography.DeviceLabel,
                    color = DapColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Action Buttons: [12] Lyrics & [13] Queue
            Row(verticalAlignment = Alignment.CenterVertically) {
                // [12] Lyrics Button with Quote Bubble Glyph
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DapColors.ScreenCard)
                        .border(1.dp, DapColors.ScreenCardBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            haptic.buttonClick()
                            onOpenLyrics()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    QuoteChatBubbleGlyph(tint = DapColors.TextPrimary)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // [13] Queue / Playlist Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DapColors.ScreenCard)
                        .border(1.dp, DapColors.ScreenCardBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            haptic.buttonClick()
                            onOpenQueue()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                        contentDescription = "Queue",
                        tint = DapColors.TextPrimary,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }

    // Audiophile Audio Specs Dialog (Behavioral Contrast Effect)
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
 * Authentic Dolby Atmos double-D vector glyph (Callout 5).
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
 * Two Earbuds / AirPods glyph matching Callout 11.
 */
@Composable
fun EarbudsDoubleGlyph(
    tint: Color = DapColors.TextPrimary,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(14.dp, 14.dp)) {
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
 * Quote chat bubble glyph matching Callout 12.
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
