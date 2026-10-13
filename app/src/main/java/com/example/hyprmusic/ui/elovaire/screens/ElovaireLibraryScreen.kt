package com.example.hyprmusic.ui.elovaire.screens

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hyprmusic.core.cloud.telegram.TelegramMusicRepository
import com.example.hyprmusic.core.data.PlaylistRepository
import com.example.hyprmusic.core.model.Album
import com.example.hyprmusic.core.model.Artist
import com.example.hyprmusic.core.model.PlaybackState
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.ui.elovaire.components.ElovaireArtworkImage
import com.example.hyprmusic.ui.elovaire.components.GroupedListRowContainer
import com.example.hyprmusic.ui.elovaire.components.GroupedRowDivider
import com.example.hyprmusic.ui.elovaire.motion.elovairePressScale
import com.example.hyprmusic.ui.elovaire.theme.ElovaireColors
import com.example.hyprmusic.ui.elovaire.theme.ElovaireRadii
import com.example.hyprmusic.ui.elovaire.theme.ElovaireSpacing

enum class LibraryTab(val label: String) {
    TRACKS("Tracks"),
    ALBUMS("Albums"),
    ARTISTS("Artists"),
    PLAYLISTS("Playlists"),
    CLOUD("Telegram Cloud")
}

/**
 * Editorial Elovaire Library Screen.
 * Features:
 * - Segmented category pill switcher
 * - Inset grouped row lists for tracks
 * - 2-Column artwork grid for albums & artists
 * - Deep dive album view with hero artwork header
 * - Native Telegram cloud sync tab
 */
@Composable
fun ElovaireLibraryScreen(
    playbackState: PlaybackState,
    tracks: List<Track>,
    albums: List<Album>,
    artists: List<Artist>,
    isScanning: Boolean,
    onRescan: () -> Unit,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onToggleFavorite: (String) -> Unit,
    telegramRepository: TelegramMusicRepository,
    playlistRepository: PlaylistRepository,
    initialAlbum: Album? = null,
    onAlbumCleared: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(LibraryTab.TRACKS) }
    var viewingAlbum by remember { mutableStateOf(initialAlbum) }
    val view = LocalView.current

    BackHandler(enabled = viewingAlbum != null) {
        viewingAlbum = null
        onAlbumCleared()
    }

    // Detail Album Screen drill-in
    if (viewingAlbum != null) {
        val album = viewingAlbum!!
        val albumTracks = remember(album, tracks) {
            tracks.filter { it.album.equals(album.title, ignoreCase = true) }
        }

        AlbumDetailView(
            album = album,
            tracks = albumTracks,
            playbackState = playbackState,
            onTrackSelected = onTrackSelected,
            onBack = {
                viewingAlbum = null
                onAlbumCleared()
            },
            modifier = modifier
        )
        return
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Segmented Horizontal Category Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = ElovaireSpacing.screenHorizontal, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LibraryTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .clip(RoundedCornerShape(ElovaireRadii.pill))
                        .background(if (isSelected) ElovaireColors.AzureAccent else ElovaireColors.Graphite)
                        .border(
                            1.dp,
                            if (isSelected) ElovaireColors.AzureAccent else ElovaireColors.BorderSubtle,
                            RoundedCornerShape(ElovaireRadii.pill)
                        )
                        .elovairePressScale(pressedScale = 0.92f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                selectedTab = tab
                            }
                        )
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.label,
                        color = if (isSelected) ElovaireColors.Cloud else ElovaireColors.TextSecondary,
                        fontSize = 12.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Main Tab Content
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "library_tab_content"
            ) { tab ->
                when (tab) {
                    LibraryTab.TRACKS -> {
                        TracksTabContent(
                            tracks = tracks,
                            playbackState = playbackState,
                            onTrackSelected = onTrackSelected,
                            onToggleFavorite = onToggleFavorite
                        )
                    }
                    LibraryTab.ALBUMS -> {
                        AlbumsTabContent(
                            albums = albums,
                            onAlbumSelected = { viewingAlbum = it }
                        )
                    }
                    LibraryTab.ARTISTS -> {
                        ArtistsTabContent(
                            artists = artists,
                            tracks = tracks,
                            onTrackSelected = onTrackSelected
                        )
                    }
                    LibraryTab.PLAYLISTS -> {
                        PlaylistsTabContent(
                            playlistRepository = playlistRepository,
                            tracks = tracks,
                            playbackState = playbackState,
                            onTrackSelected = onTrackSelected
                        )
                    }
                    LibraryTab.CLOUD -> {
                        CloudTabContent(
                            telegramRepository = telegramRepository,
                            playbackState = playbackState,
                            onTrackSelected = onTrackSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TracksTabContent(
    tracks: List<Track>,
    playbackState: PlaybackState,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    val view = LocalView.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = ElovaireSpacing.screenHorizontal,
            vertical = 6.dp
        ),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${tracks.size} TRACKS",
                    color = ElovaireColors.TextMuted,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        itemsIndexed(tracks, key = { _, it -> "track_${it.id}" }) { index, track ->
            val isCurrent = playbackState.currentTrack?.id == track.id
            val isPlaying = isCurrent && playbackState.isPlaying

            GroupedListRowContainer(
                index = index,
                count = tracks.size,
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

            if (index < tracks.size - 1) {
                GroupedRowDivider(insetStart = 66.dp)
            }
        }

        item {
            Spacer(modifier = Modifier.height(ElovaireSpacing.listContentBottomPad))
        }
    }
}

@Composable
private fun AlbumsTabContent(
    albums: List<Album>,
    onAlbumSelected: (Album) -> Unit
) {
    val view = LocalView.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = ElovaireSpacing.screenHorizontal,
            vertical = 10.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(albums, key = { "album_${it.id}" }) { album ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .elovairePressScale(pressedScale = 0.94f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onAlbumSelected(album)
                        }
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(ElovaireRadii.card))
                ) {
                    ElovaireArtworkImage(
                        artworkUri = album.coverUri,
                        title = album.title,
                        artist = album.artist,
                        isAlbum = true,
                        cornerRadius = ElovaireRadii.card,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = album.title,
                    color = ElovaireColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${album.artist} · ${album.trackCount} Tracks",
                    color = ElovaireColors.TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(ElovaireSpacing.listContentBottomPad))
        }
    }
}

@Composable
private fun ArtistsTabContent(
    artists: List<Artist>,
    tracks: List<Track>,
    onTrackSelected: (Track, List<Track>) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = ElovaireSpacing.screenHorizontal, vertical = 8.dp)
    ) {
        itemsIndexed(artists, key = { _, it -> "artist_${it.id}" }) { index, artist ->
            val artistTracks = remember(artist.name, tracks) {
                tracks.filter { it.artist.equals(artist.name, ignoreCase = true) }
            }

            GroupedListRowContainer(
                index = index,
                count = artists.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .elovairePressScale(pressedScale = 0.98f)
                    .clickable {
                        if (artistTracks.isNotEmpty()) {
                            onTrackSelected(artistTracks.first(), artistTracks)
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = artist.name,
                            color = ElovaireColors.TextPrimary,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        val count = if (artist.trackCount > 0) artist.trackCount else artistTracks.size
                        Text(
                            text = "$count Tracks",
                            color = ElovaireColors.TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Artist",
                        tint = ElovaireColors.AzureAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (index < artists.size - 1) {
                GroupedRowDivider(insetStart = 16.dp)
            }
        }

        item {
            Spacer(modifier = Modifier.height(ElovaireSpacing.listContentBottomPad))
        }
    }
}

@Composable
private fun PlaylistsTabContent(
    playlistRepository: PlaylistRepository,
    tracks: List<Track>,
    playbackState: PlaybackState,
    onTrackSelected: (Track, List<Track>) -> Unit
) {
    val playlists by playlistRepository.playlists.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = ElovaireSpacing.screenHorizontal, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "${playlists.size} Playlists",
                color = ElovaireColors.TextMuted,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        itemsIndexed(playlists) { index, playlist ->
            val pTracks = remember(playlist, tracks) {
                playlist.trackIds.mapNotNull { id -> tracks.find { it.id == id } }
            }

            GroupedListRowContainer(
                index = index,
                count = playlists.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .elovairePressScale(pressedScale = 0.98f)
                    .clickable {
                        if (pTracks.isNotEmpty()) {
                            onTrackSelected(pTracks.first(), pTracks)
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = playlist.name,
                            color = ElovaireColors.TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${pTracks.size} Tracks",
                            color = ElovaireColors.TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = ElovaireColors.AzureAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(ElovaireSpacing.listContentBottomPad))
        }
    }
}

@Composable
private fun CloudTabContent(
    telegramRepository: TelegramMusicRepository,
    playbackState: PlaybackState,
    onTrackSelected: (Track, List<Track>) -> Unit
) {
    val cloudTracks by telegramRepository.cloudTracks.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = ElovaireSpacing.screenHorizontal, vertical = 8.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = null,
                        tint = ElovaireColors.AzureAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "TELEGRAM CLOUD LIBRARY",
                        color = ElovaireColors.AzureAccent,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "${cloudTracks.size} Tracks",
                    color = ElovaireColors.TextMuted,
                    fontSize = 11.5.sp
                )
            }
        }

        itemsIndexed(cloudTracks, key = { _, it -> "cloud_${it.id}" }) { index, track ->
            val isCurrent = playbackState.currentTrack?.id == track.id

            GroupedListRowContainer(
                index = index,
                count = cloudTracks.size,
                backgroundColor = if (isCurrent) ElovaireColors.Graphite else ElovaireColors.Carbon,
                borderColor = if (isCurrent) ElovaireColors.BorderStrong else ElovaireColors.BorderSubtle,
                modifier = Modifier
                    .fillMaxWidth()
                    .elovairePressScale(pressedScale = 0.98f)
                    .clickable { onTrackSelected(track, cloudTracks) }
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
                            text = "${track.artist} · Cloud",
                            color = ElovaireColors.TextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            if (index < cloudTracks.size - 1) {
                GroupedRowDivider(insetStart = 66.dp)
            }
        }

        item {
            Spacer(modifier = Modifier.height(ElovaireSpacing.listContentBottomPad))
        }
    }
}

@Composable
private fun AlbumDetailView(
    album: Album,
    tracks: List<Track>,
    playbackState: PlaybackState,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Column(modifier = modifier.fillMaxSize()) {
        // Back Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ElovaireColors.TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = "Back to Library",
                color = ElovaireColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(
                horizontal = ElovaireSpacing.screenHorizontal,
                vertical = 6.dp
            )
        ) {
            // Hero Album Art & Details
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        ElovaireArtworkImage(
                            artworkUri = album.coverUri,
                            title = album.title,
                            artist = album.artist,
                            isAlbum = true,
                            cornerRadius = 16.dp,
                            showGlow = true,
                            glowTint = ElovaireColors.AzureGlow,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = album.title,
                        color = ElovaireColors.TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${album.artist} · ${tracks.size} Tracks",
                        color = ElovaireColors.TextSecondary,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Play & Shuffle Action Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .clip(RoundedCornerShape(ElovaireRadii.pill))
                                .background(ElovaireColors.AzureAccent)
                                .elovairePressScale()
                                .clickable {
                                    if (tracks.isNotEmpty()) onTrackSelected(tracks.first(), tracks)
                                }
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = ElovaireColors.Cloud,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Play",
                                    color = ElovaireColors.Cloud,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .clip(RoundedCornerShape(ElovaireRadii.pill))
                                .background(ElovaireColors.Graphite)
                                .border(1.dp, ElovaireColors.BorderSubtle, RoundedCornerShape(ElovaireRadii.pill))
                                .elovairePressScale()
                                .clickable {
                                    if (tracks.isNotEmpty()) {
                                        val shuffled = tracks.shuffled()
                                        onTrackSelected(shuffled.first(), shuffled)
                                    }
                                }
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shuffle,
                                    contentDescription = null,
                                    tint = ElovaireColors.TextPrimary,
                                    modifier = Modifier.size(17.dp)
                                )
                                Text(
                                    text = "Shuffle",
                                    color = ElovaireColors.TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Track List
            itemsIndexed(tracks) { index, track ->
                val isCurrent = playbackState.currentTrack?.id == track.id

                GroupedListRowContainer(
                    index = index,
                    count = tracks.size,
                    backgroundColor = if (isCurrent) ElovaireColors.Graphite else ElovaireColors.Carbon,
                    borderColor = if (isCurrent) ElovaireColors.BorderStrong else ElovaireColors.BorderSubtle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .elovairePressScale(pressedScale = 0.98f)
                        .clickable { onTrackSelected(track, tracks) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = if (isCurrent) ElovaireColors.AzureAccent else ElovaireColors.TextMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(24.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                color = if (isCurrent) ElovaireColors.AzureAccent else ElovaireColors.TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = track.artist,
                                color = ElovaireColors.TextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (isCurrent && playbackState.isPlaying) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = ElovaireColors.AzureAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (index < tracks.size - 1) {
                    GroupedRowDivider(insetStart = 44.dp)
                }
            }

            item {
                Spacer(modifier = Modifier.height(ElovaireSpacing.listContentBottomPad))
            }
        }
    }
}
