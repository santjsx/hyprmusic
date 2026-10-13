package com.example.hyprmusic.ui.elovaire.screens

import android.view.HapticFeedbackConstants
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hyprmusic.core.model.PlaybackState
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.ui.elovaire.components.ElovaireArtworkImage
import com.example.hyprmusic.ui.elovaire.components.ElovaireCard
import com.example.hyprmusic.ui.elovaire.components.GroupedListRowContainer
import com.example.hyprmusic.ui.elovaire.components.GroupedRowDivider
import com.example.hyprmusic.ui.elovaire.motion.elovairePressScale
import com.example.hyprmusic.ui.elovaire.theme.ElovaireColors
import com.example.hyprmusic.ui.elovaire.theme.ElovaireRadii
import com.example.hyprmusic.ui.elovaire.theme.ElovaireSpacing

/**
 * Editorial Elovaire Home Screen.
 * Layout:
 * 1. Listening Overview Header & Quick Shuffle Hero Banner
 * 2. "Heavy Rotation" Carousel: Horizontal cards with glowing artwork
 * 3. "Recently Added" Section: Grouped inset rows with 1-touch play
 */
@Composable
fun ElovaireHomeScreen(
    playbackState: PlaybackState,
    tracks: List<Track>,
    heavyRotationTracks: List<Track>,
    isScanning: Boolean,
    hasInitialScanCompleted: Boolean,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onTogglePlayPause: () -> Unit,
    onRandomMix: () -> Unit,
    onRescan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    if (tracks.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize().padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        color = ElovaireColors.AzureAccent,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = "Indexing Your Local Audio Library...",
                        color = ElovaireColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = "No Audio Files Found",
                        color = ElovaireColors.TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ensure your music directory contains audio files (FLAC, MP3, M4A, WAV).",
                        color = ElovaireColors.TextSecondary,
                        fontSize = 13.5.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(ElovaireRadii.pill))
                            .background(ElovaireColors.AzureAccent)
                            .elovairePressScale()
                            .clickable(onClick = onRescan)
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Scan Device Storage",
                            color = ElovaireColors.Cloud,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                    }
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 10.dp,
            bottom = ElovaireSpacing.listContentBottomPad
        ),
        verticalArrangement = Arrangement.spacedBy(ElovaireSpacing.sectionVertical)
    ) {
        // Quick Shuffle Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ElovaireSpacing.screenHorizontal)
                    .clip(RoundedCornerShape(ElovaireRadii.card))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ElovaireColors.Carbon,
                                ElovaireColors.Graphite
                            )
                        )
                    )
                    .border(1.dp, ElovaireColors.BorderSubtle, RoundedCornerShape(ElovaireRadii.card))
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "YOUR LIBRARY",
                            color = ElovaireColors.AzureAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${tracks.size} Audio Tracks",
                            color = ElovaireColors.TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Ready for bit-perfect offline listening",
                            color = ElovaireColors.TextSecondary,
                            fontSize = 12.5.sp
                        )
                    }

                    // Tactile Shuffle Hero Action Button
                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .clip(RoundedCornerShape(ElovaireRadii.pill))
                            .background(ElovaireColors.AzureAccent)
                            .elovairePressScale(pressedScale = 0.90f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    onRandomMix()
                                }
                            )
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Shuffle",
                                tint = ElovaireColors.Cloud,
                                modifier = Modifier.size(17.dp)
                            )
                            Text(
                                text = "Shuffle Mix",
                                color = ElovaireColors.Cloud,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Heavy Rotation Carousel
        if (heavyRotationTracks.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ElovaireSpacing.screenHorizontal),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Heavy Rotation",
                            color = ElovaireColors.TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Most Played",
                            color = ElovaireColors.TextMuted,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = ElovaireSpacing.screenHorizontal),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(heavyRotationTracks, key = { "rotation_${it.id}" }) { track ->
                            val isPlaying = playbackState.currentTrack?.id == track.id && playbackState.isPlaying

                            Column(
                                modifier = Modifier
                                    .width(136.dp)
                                    .elovairePressScale(pressedScale = 0.94f)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                            onTrackSelected(track, heavyRotationTracks)
                                        }
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(136.dp)
                                        .clip(RoundedCornerShape(ElovaireRadii.card))
                                ) {
                                    ElovaireArtworkImage(
                                        artworkUri = track.albumArtUri,
                                        title = track.title,
                                        artist = track.artist,
                                        trackId = track.id,
                                        cornerRadius = ElovaireRadii.card,
                                        showGlow = isPlaying,
                                        glowTint = ElovaireColors.AzureGlow,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    if (isPlaying) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(6.dp)
                                                .size(26.dp)
                                                .clip(CircleShape)
                                                .background(ElovaireColors.AzureAccent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.GraphicEq,
                                                contentDescription = null,
                                                tint = ElovaireColors.Cloud,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = track.title,
                                    color = if (isPlaying) ElovaireColors.AzureAccent else ElovaireColors.TextPrimary,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = track.artist,
                                    color = ElovaireColors.TextSecondary,
                                    fontSize = 11.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recently Added Section (Inset Grouped Rows)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ElovaireSpacing.screenHorizontal)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recently Indexed",
                        color = ElovaireColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${tracks.take(15).size} Tracks",
                        color = ElovaireColors.TextMuted,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val recentList = tracks.take(15)
                recentList.forEachIndexed { index, track ->
                    val isCurrent = playbackState.currentTrack?.id == track.id
                    val isPlaying = isCurrent && playbackState.isPlaying

                    GroupedListRowContainer(
                        index = index,
                        count = recentList.size,
                        backgroundColor = if (isCurrent) ElovaireColors.Graphite else ElovaireColors.Carbon,
                        borderColor = if (isCurrent) ElovaireColors.BorderStrong else ElovaireColors.BorderSubtle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .elovairePressScale(pressedScale = 0.98f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    onTrackSelected(track, tracks)
                                }
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(ElovaireRadii.artworkSmall))
                            ) {
                                ElovaireArtworkImage(
                                    artworkUri = track.albumArtUri,
                                    title = track.title,
                                    artist = track.artist,
                                    trackId = track.id,
                                    cornerRadius = ElovaireRadii.artworkSmall,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.title,
                                    color = if (isCurrent) ElovaireColors.AzureAccent else ElovaireColors.TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${track.artist} · ${track.album}",
                                    color = ElovaireColors.TextSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (isPlaying) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = ElovaireColors.AzureAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    if (index < recentList.size - 1) {
                        GroupedRowDivider(insetStart = 66.dp)
                    }
                }
            }
        }
    }
}
