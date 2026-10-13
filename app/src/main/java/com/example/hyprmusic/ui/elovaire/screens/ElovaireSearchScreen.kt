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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hyprmusic.core.cloud.telegram.TelegramMusicRepository
import com.example.hyprmusic.core.data.MusicRepository
import com.example.hyprmusic.core.model.PlaybackState
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.ui.elovaire.components.ElovaireArtworkImage
import com.example.hyprmusic.ui.elovaire.components.GroupedListRowContainer
import com.example.hyprmusic.ui.elovaire.components.GroupedRowDivider
import com.example.hyprmusic.ui.elovaire.motion.elovairePressScale
import com.example.hyprmusic.ui.elovaire.theme.ElovaireColors
import com.example.hyprmusic.ui.elovaire.theme.ElovaireRadii
import com.example.hyprmusic.ui.elovaire.theme.ElovaireSpacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Spotlight Elovaire Search Screen.
 * Features:
 * - Clean frosted pill search input
 * - Instant unified local & Telegram cloud search matching
 * - Inset grouped row results
 */
@Composable
fun ElovaireSearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    musicRepository: MusicRepository,
    telegramRepository: TelegramMusicRepository,
    playbackState: PlaybackState,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val view = LocalView.current

    val cloudTracks by telegramRepository.cloudTracks.collectAsStateWithLifecycle()
    var searchResults by remember { mutableStateOf<List<Track>>(emptyList()) }

    LaunchedEffect(Unit) {
        delay(100)
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    LaunchedEffect(query, cloudTracks) {
        if (query.isBlank()) {
            searchResults = emptyList()
        } else {
            searchResults = withContext(Dispatchers.Default) {
                val localMatches = musicRepository.search(query)
                val q = query.trim().lowercase()
                val cloudMatches = cloudTracks.filter {
                    it.title.lowercase().contains(q) ||
                    it.artist.lowercase().contains(q) ||
                    it.album.lowercase().contains(q)
                }
                localMatches + cloudMatches
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Spotlight Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ElovaireSpacing.screenHorizontal, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(ElovaireRadii.pill))
                    .background(ElovaireColors.Carbon)
                    .border(1.dp, ElovaireColors.BorderStrong, RoundedCornerShape(ElovaireRadii.pill))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = ElovaireColors.TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Search tracks, artists, albums...",
                                color = ElovaireColors.TextMuted,
                                fontSize = 14.sp
                            )
                        }

                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            singleLine = true,
                            textStyle = TextStyle(
                                color = ElovaireColors.TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = SolidColor(ElovaireColors.AzureAccent),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                        )
                    }

                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = { onQueryChange("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = ElovaireColors.TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Close / Cancel text button
            Text(
                text = "Cancel",
                color = ElovaireColors.AzureAccent,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .elovairePressScale()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClose
                    )
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            )
        }

        // Search Results List
        if (query.isBlank()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(ElovaireColors.Graphite),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = ElovaireColors.AzureAccent,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Text(
                        text = "Search Your Music",
                        color = ElovaireColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Search across local files and Telegram cloud tracks by title, artist, or album.",
                        color = ElovaireColors.TextSecondary,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tracks found matching \"$query\"",
                    color = ElovaireColors.TextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = ElovaireSpacing.screenHorizontal, vertical = 6.dp)
            ) {
                item {
                    Text(
                        text = "${searchResults.size} MATCHES",
                        color = ElovaireColors.AzureAccent,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                itemsIndexed(searchResults, key = { _, it -> "search_${it.id}" }) { index, track ->
                    val isCurrent = playbackState.currentTrack?.id == track.id
                    val isPlaying = isCurrent && playbackState.isPlaying

                    GroupedListRowContainer(
                        index = index,
                        count = searchResults.size,
                        backgroundColor = if (isCurrent) ElovaireColors.Graphite else ElovaireColors.Carbon,
                        borderColor = if (isCurrent) ElovaireColors.BorderStrong else ElovaireColors.BorderSubtle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .elovairePressScale(pressedScale = 0.98f)
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                onTrackSelected(track, searchResults)
                            }
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = track.title,
                                        color = if (isCurrent) ElovaireColors.AzureAccent else ElovaireColors.TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    if (track.isCloudTrack) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(ElovaireColors.AzureAccent.copy(alpha = 0.2f))
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "CLOUD",
                                                color = ElovaireColors.AzureAccent,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
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

                    if (index < searchResults.size - 1) {
                        GroupedRowDivider(insetStart = 66.dp)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(ElovaireSpacing.listContentBottomPad))
                }
            }
        }
    }
}
