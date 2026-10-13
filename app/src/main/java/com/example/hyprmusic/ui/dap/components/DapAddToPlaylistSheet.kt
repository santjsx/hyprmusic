package com.example.hyprmusic.ui.dap.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hyprmusic.core.model.Playlist
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.ui.dap.state.DapHapticFeedback
import com.example.hyprmusic.ui.dap.theme.DapColors
import com.example.hyprmusic.ui.dap.theme.DapTypography

/**
 * High-End Audiophile Add-to-Playlist Bottom Sheet.
 * Allows 1-tap addition to existing playlists and instant inline creation of new playlists.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DapAddToPlaylistSheet(
    track: Track,
    playlists: List<Playlist>,
    onAddToPlaylist: (playlistId: String, trackId: String) -> Unit,
    onCreatePlaylist: (name: String, trackId: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val haptic = remember(view) { DapHapticFeedback(view) }
    var isCreatingNew by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DapColors.ChassisTitanium,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = DapColors.ScreenCardBorder)
        },
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Add to Playlist",
                        style = DapTypography.MenuHeader,
                        color = DapColors.TextPrimary
                    )
                    Text(
                        text = "${track.title} • ${track.artist}",
                        style = DapTypography.MenuSecondary,
                        color = DapColors.TextSecondary,
                        maxLines = 1
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = DapColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Create New Playlist Card
            if (!isCreatingNew) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DapColors.ScreenCard)
                        .border(1.dp, DapColors.ScreenCardBorder, RoundedCornerShape(10.dp))
                        .clickable {
                            haptic.buttonClick()
                            isCreatingNew = true
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = DapColors.AccentIceBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "New Playlist",
                        style = DapTypography.MenuItem,
                        color = DapColors.AccentIceBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DapColors.ScreenCard)
                        .border(1.dp, DapColors.AccentIceBlue.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        placeholder = { Text("Playlist Name (e.g., Night Drive)", color = DapColors.TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DapColors.TextPrimary,
                            unfocusedTextColor = DapColors.TextPrimary,
                            focusedBorderColor = DapColors.AccentIceBlue,
                            unfocusedBorderColor = DapColors.ScreenCardBorder,
                            cursorColor = DapColors.AccentIceBlue
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { isCreatingNew = false }) {
                            Text("Cancel", color = DapColors.TextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newPlaylistName.isNotBlank()) {
                                    haptic.heavyPulse()
                                    onCreatePlaylist(newPlaylistName.trim(), track.id)
                                    onDismiss()
                                }
                            },
                            enabled = newPlaylistName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DapColors.AccentIceBlue,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Create & Add", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Playlists List
            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No playlists created yet",
                        style = DapTypography.MenuSecondary,
                        color = DapColors.TextSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(playlists) { playlist ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DapColors.ScreenCard)
                                .border(
                                    1.dp,
                                    DapColors.ScreenCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    haptic.buttonClick()
                                    onAddToPlaylist(playlist.id, track.id)
                                    onDismiss()
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                                    contentDescription = null,
                                    tint = DapColors.AccentIceBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = playlist.name,
                                        style = DapTypography.MenuItem,
                                        color = DapColors.TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${playlist.trackCount} tracks",
                                        style = DapTypography.MenuSecondary,
                                        color = DapColors.TextSecondary
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add to playlist",
                                tint = DapColors.TextTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
