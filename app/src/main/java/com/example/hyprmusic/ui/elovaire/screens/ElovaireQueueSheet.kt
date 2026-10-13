package com.example.hyprmusic.ui.elovaire.screens

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.ui.components.MiniEqualizerBars
import com.example.hyprmusic.ui.elovaire.components.ElovaireArtworkImage
import com.example.hyprmusic.ui.elovaire.components.GroupedListRowContainer
import com.example.hyprmusic.ui.elovaire.components.GroupedRowDivider
import com.example.hyprmusic.ui.elovaire.motion.elovairePressScale
import com.example.hyprmusic.ui.elovaire.theme.ElovaireColors
import com.example.hyprmusic.ui.elovaire.theme.ElovaireRadii

/**
 * Modern Elovaire Queue Bottom Sheet.
 * Displays current and upcoming tracks in the playback queue with one-touch switching.
 */
@Composable
fun ElovaireQueueSheet(
    currentTrack: Track,
    queue: List<Track>,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = ElovaireRadii.sheet, topEnd = ElovaireRadii.sheet))
            .background(ElovaireColors.Carbon)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Drag handle & Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(ElovaireColors.BorderStrong)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PLAYING QUEUE",
                            color = ElovaireColors.AzureAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "${queue.size} Tracks in Queue",
                            color = ElovaireColors.TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(36.dp).elovairePressScale(pressedScale = 0.88f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ElovaireColors.TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Queue List
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                itemsIndexed(
                    items = queue,
                    key = { index, track -> "queue_${track.id}_$index" }
                ) { index, track ->
                    val isCurrent = track.id == currentTrack.id

                    GroupedListRowContainer(
                        index = index,
                        count = queue.size,
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
                                    onTrackSelected(track, queue)
                                }
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Track Artwork Thumbnail
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

                            // Title & Subtitle
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

                            if (isCurrent) {
                                MiniEqualizerBars(
                                    theme = com.example.hyprmusic.core.theming.HyprThemeConfig(),
                                    isPlaying = true,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (index < queue.size - 1) {
                        GroupedRowDivider(insetStart = 64.dp)
                    }
                }
            }
        }
    }
}
