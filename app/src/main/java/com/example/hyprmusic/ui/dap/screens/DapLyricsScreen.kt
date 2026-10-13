package com.example.hyprmusic.ui.dap.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hyprmusic.core.lyrics.LyricsRepository
import com.example.hyprmusic.core.model.LyricLine
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.ui.dap.components.DapEmptyState
import com.example.hyprmusic.ui.dap.theme.DapColors
import com.example.hyprmusic.ui.dap.theme.DapTypography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * High-contrast virtual LCD synchronized lyrics viewport.
 * Automatically aligns to the active audio position and supports rotary seeking.
 */
@Composable
fun DapLyricsDisplay(
    track: Track?,
    currentPositionMs: Long,
    listState: LazyListState,
    onSeekTo: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (track == null) {
        DapEmptyState(
            title = "No Active Song",
            message = "Play a track to view synchronized lyrics.",
            icon = Icons.Default.Lyrics,
            modifier = modifier
        )
        return
    }

    var lyricsList by remember(track.id) { mutableStateOf<List<LyricLine>?>(null) }
    var isLoading by remember(track.id) { mutableStateOf(true) }

    LaunchedEffect(track.id) {
        isLoading = true
        lyricsList = withContext(Dispatchers.IO) {
            LyricsRepository.fetchLyrics(track)
        }
        isLoading = false
    }

    // Determine currently singing line index
    val activeIndex = remember(lyricsList, currentPositionMs) {
        val list = lyricsList ?: emptyList()
        val idx = list.indexOfLast { it.timestampMs <= currentPositionMs }
        if (idx >= 0) idx else 0
    }

    // Auto-scroll to active line
    LaunchedEffect(activeIndex) {
        if (!listState.isScrollInProgress && (lyricsList?.isNotEmpty() == true)) {
            listState.animateScrollToItem((activeIndex - 1).coerceAtLeast(0))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = DapColors.TextPrimary,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Lyrics • ${track.title}",
                style = DapTypography.MenuHeader,
                color = DapColors.TextPrimary,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider(
            color = DapColors.ScreenCardBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = DapColors.AccentIceBlue,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            lyricsList.isNullOrEmpty() -> {
                DapEmptyState(
                    title = "No Lyrics Available",
                    message = "We couldn't find synced lyrics for this track.",
                    icon = Icons.Default.Lyrics
                )
            }
            else -> {
                val list = lyricsList ?: emptyList()
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(list) { index, item ->
                        val isActive = index == activeIndex

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isActive) DapColors.ScreenCard else androidx.compose.ui.graphics.Color.Transparent
                                )
                                .clickable { onSeekTo(item.timestampMs) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = item.text,
                                color = if (isActive) DapColors.AccentIceBlue else DapColors.TextSecondary,
                                fontSize = if (isActive) 14.5.sp else 13.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
            }
        }
    }
}
