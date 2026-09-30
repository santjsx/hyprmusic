package com.example.hyprmusic.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hyprmusic.core.model.LyricLine
import com.example.hyprmusic.core.model.PlaybackState
import com.example.hyprmusic.core.theming.HyprThemeConfig
import com.example.hyprmusic.core.theming.hyprAnimatedGlow
import com.example.hyprmusic.core.theming.hyprTile

enum class HyprWorkspace(val index: Int, val label: String) {
    HOME(1, "home"),
    LIBRARY(2, "lib"),
    PLAYING(3, "player"),
    SETTINGS(4, "config")
}

@Composable
fun HyprTopBar(
    theme: HyprThemeConfig,
    currentWorkspace: HyprWorkspace,
    onWorkspaceSelected: (HyprWorkspace) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = theme.windowGapsDp.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Workspace Indicator Tiles
        Row(
            modifier = Modifier.weight(1f, fill = false),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HyprWorkspace.values().forEach { ws ->
                val isActive = ws == currentWorkspace
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(8).dp))
                        .background(
                            if (isActive) theme.accentColor.copy(alpha = 0.25f)
                            else theme.surfaceVariantColor
                        )
                        .clickable { onWorkspaceSelected(ws) }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "[${ws.index}:${ws.label}]",
                        color = if (isActive) theme.accentColor else theme.textSecondaryColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // System telemetry badge (Bit-perfect Wayland Audio)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(theme.borderRadiusDp.coerceAtMost(8).dp))
                .background(theme.surfaceVariantColor)
                .padding(horizontal = 7.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = null,
                tint = theme.accentColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "WAYLAND.FLAC",
                color = theme.textSecondaryColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
fun MiniPlayer(
    theme: HyprThemeConfig,
    playbackState: PlaybackState,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack ?: return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .hyprTile(theme = theme, isActive = playbackState.isPlaying)
            .clickable { onClick() }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cover Art / Thumbnail
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.surfaceVariantColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (!track.albumArtUri.isNullOrBlank()) {
                        AsyncImage(
                            model = track.albumArtUri,
                            contentDescription = track.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title and Artist
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        color = theme.textPrimaryColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${track.artist} • ${track.album}",
                        color = theme.textSecondaryColor,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Controls
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = theme.accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                IconButton(onClick = onSkipNext) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = theme.textPrimaryColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // High Precision Mini Progress Bar
            LinearProgressIndicator(
                progress = { playbackState.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = theme.accentColor,
                trackColor = theme.surfaceVariantColor
            )
        }
    }
}

@Composable
fun SyncedLyricsView(
    lyrics: List<LyricLine>?,
    isLoading: Boolean,
    currentPositionMs: Long,
    theme: HyprThemeConfig,
    onSeekTo: (Long) -> Unit,
    onRetry: () -> Unit,
    onShowCoverArt: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp,
                    color = theme.accentColor
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "grep -i lyrics in lrclib...",
                    color = theme.textSecondaryColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        }
        return
    }

    if (lyrics == null || lyrics.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.surfaceVariantColor.copy(alpha = 0.6f))
                    .border(1.dp, theme.accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = theme.accentColor,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "[!] NO SYNCED LYRICS FOUND",
                    color = theme.textPrimaryColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Instrumental track or unindexed in database",
                    color = theme.textSecondaryColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.surfaceColor)
                            .clickable(onClick = onRetry)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "[ RETRY ]",
                            color = theme.accentColor,
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.surfaceColor)
                            .clickable(onClick = onShowCoverArt)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "[ SHOW ART ]",
                            color = theme.textSecondaryColor,
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        return
    }

    val activeIndex = lyrics.indexOfLast { it.timestampMs <= currentPositionMs }.coerceAtLeast(0)
    val listState = rememberLazyListState()

    LaunchedEffect(activeIndex) {
        if (activeIndex in lyrics.indices) {
            listState.animateScrollToItem((activeIndex - 2).coerceAtLeast(0))
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(30.dp)) }

        itemsIndexed(
            items = lyrics,
            key = { index, item -> "$index-${item.timestampMs}" }
        ) { index, line ->
            val isActive = index == activeIndex

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSeekTo(line.timestampMs) }
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = line.text,
                    color = if (isActive) theme.accentColor else theme.textSecondaryColor.copy(alpha = 0.45f),
                    fontSize = if (isActive) 19.sp else 15.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    fontFamily = if (isActive) FontFamily.Default else FontFamily.Monospace
                )
            }
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}
