package com.example.hyprmusic.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.hyprmusic.core.data.PlaylistRepository
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.core.theming.HyprThemeConfig
import com.example.hyprmusic.core.theming.hyprBounceClick

@Composable
fun AddToPlaylistDialog(
    theme: HyprThemeConfig,
    track: Track,
    playlistRepository: PlaylistRepository,
    onDismiss: () -> Unit
) {
    val view = LocalView.current
    val playlists by playlistRepository.playlists.collectAsState()
    var isCreatingNew by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

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
                            imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ADD TO PLAYLIST",
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

                Spacer(modifier = Modifier.height(4.dp))

                // Target Track Info Subtitle
                Text(
                    text = "${track.title} • ${track.artist}",
                    color = theme.textSecondaryColor,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Create New Playlist Row
                if (!isCreatingNew) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.surfaceVariantColor)
                            .hyprBounceClick {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                isCreatingNew = true
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = theme.accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NEW PLAYLIST",
                                color = theme.accentColor,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.surfaceVariantColor)
                            .border(1.dp, theme.accentColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = newPlaylistName,
                            onValueChange = { newPlaylistName = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = theme.textPrimaryColor,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            cursorBrush = SolidColor(theme.accentColor),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (newPlaylistName.isEmpty()) {
                                    Text(
                                        text = "Playlist name...",
                                        color = theme.textSecondaryColor.copy(alpha = 0.6f),
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                innerTextField()
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(theme.accentColor)
                                .hyprBounceClick {
                                    if (newPlaylistName.isNotBlank()) {
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        val created = playlistRepository.createPlaylist(newPlaylistName)
                                        if (created != null) {
                                            playlistRepository.toggleTrackInPlaylist(created.id, track.id)
                                        }
                                        newPlaylistName = ""
                                        isCreatingNew = false
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "CREATE",
                                color = theme.backgroundColor,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Playlists List
                if (playlists.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No custom playlists found.\nCreate your first playlist above!",
                            color = theme.textSecondaryColor,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 260.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(playlists, key = { it.id }) { pl ->
                            val containsTrack = pl.trackIds.contains(track.id)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (containsTrack) theme.accentColor.copy(alpha = 0.15f) else theme.surfaceVariantColor)
                                    .border(
                                        1.dp,
                                        if (containsTrack) theme.accentColor.copy(alpha = 0.4f) else theme.surfaceVariantColor,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        playlistRepository.toggleTrackInPlaylist(pl.id, track.id)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                                            contentDescription = null,
                                            tint = if (containsTrack) theme.accentColor else theme.textSecondaryColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = pl.name,
                                                color = theme.textPrimaryColor,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = FontFamily.Monospace,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${pl.trackIds.size} tracks",
                                                color = theme.textSecondaryColor,
                                                fontSize = 10.5.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }

                                    // Checkmark badge
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (containsTrack) theme.accentColor else theme.surfaceColor)
                                            .border(
                                                1.dp,
                                                if (containsTrack) theme.accentColor else theme.textSecondaryColor.copy(alpha = 0.4f),
                                                RoundedCornerShape(4.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (containsTrack) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Added",
                                                tint = theme.backgroundColor,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Done Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(theme.accentColor)
                        .hyprBounceClick {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onDismiss()
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "DONE",
                        color = theme.backgroundColor,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
