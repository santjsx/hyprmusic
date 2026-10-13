package com.example.hyprmusic.ui.dap.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hyprmusic.core.model.Album
import com.example.hyprmusic.core.model.Artist
import com.example.hyprmusic.core.model.Playlist
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.ui.dap.components.DapEmptyState
import com.example.hyprmusic.ui.dap.theme.DapColors
import com.example.hyprmusic.ui.dap.theme.DapTypography

/**
 * Universal Virtual LCD Track Browser.
 * Supports wheel-scrolled lists for individual tracks and active playback queues.
 */
@Composable
fun DapTrackListDisplay(
    title: String,
    tracks: List<Track>,
    currentTrackId: String?,
    selectedIndex: Int,
    listState: LazyListState,
    onTrackSelect: (Track, List<Track>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    text = title,
                    style = DapTypography.MenuHeader,
                    color = DapColors.TextPrimary,
                    maxLines = 1
                )
            }

            Text(
                text = "${tracks.size} tracks",
                style = DapTypography.TimeCounter,
                color = DapColors.TextSecondary
            )
        }

        HorizontalDivider(
            color = DapColors.ScreenCardBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        if (tracks.isEmpty()) {
            DapEmptyState(
                title = "No Tracks Found",
                message = "Scan your device or check your audio library folder.",
                icon = Icons.Default.MusicNote
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(tracks) { index, track ->
                    val isWheelSelected = index == selectedIndex
                    val isPlayingNow = track.id == currentTrackId

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isWheelSelected) DapColors.ScreenCard else androidx.compose.ui.graphics.Color.Transparent
                            )
                            .clickable { onTrackSelect(track, tracks) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                style = DapTypography.MenuItem,
                                color = when {
                                    isPlayingNow -> DapColors.AccentIceBlue
                                    isWheelSelected -> DapColors.TextPrimary
                                    else -> DapColors.TextPrimary.copy(alpha = 0.9f)
                                },
                                fontWeight = if (isPlayingNow || isWheelSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${track.artist} • ${track.album}",
                                style = DapTypography.MenuSecondary,
                                color = DapColors.TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = track.formattedDuration,
                            style = DapTypography.TimeCounter,
                            color = DapColors.TextSecondary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Album Browser Display with Artwork Thumbnails.
 */
@Composable
fun DapAlbumListDisplay(
    albums: List<Album>,
    selectedIndex: Int,
    listState: LazyListState,
    onAlbumSelect: (Album) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    text = "Albums",
                    style = DapTypography.MenuHeader,
                    color = DapColors.TextPrimary
                )
            }

            Text(
                text = "${albums.size} albums",
                style = DapTypography.TimeCounter,
                color = DapColors.TextSecondary
            )
        }

        HorizontalDivider(
            color = DapColors.ScreenCardBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        if (albums.isEmpty()) {
            DapEmptyState(
                title = "No Albums Found",
                message = "Scan your library to organize music into albums.",
                icon = Icons.Default.Album
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(albums) { index, album ->
                    val isWheelSelected = index == selectedIndex

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isWheelSelected) DapColors.ScreenCard else androidx.compose.ui.graphics.Color.Transparent
                            )
                            .clickable { onAlbumSelect(album) }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Album Art Thumbnail
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(DapColors.ScreenCard)
                                .border(0.8.dp, DapColors.ScreenCardBorder, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!album.coverUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(album.coverUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = album.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Album,
                                    contentDescription = null,
                                    tint = DapColors.TextSecondary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = album.title,
                                style = DapTypography.MenuItem,
                                color = if (isWheelSelected) DapColors.AccentIceBlue else DapColors.TextPrimary,
                                fontWeight = if (isWheelSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${album.artist} • ${album.trackCount} tracks",
                                style = DapTypography.MenuSecondary,
                                color = DapColors.TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = if (isWheelSelected) DapColors.AccentIceBlue else DapColors.TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Artist Browser Display.
 */
@Composable
fun DapArtistListDisplay(
    artists: List<Artist>,
    selectedIndex: Int,
    listState: LazyListState,
    onArtistSelect: (Artist) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    text = "Artists",
                    style = DapTypography.MenuHeader,
                    color = DapColors.TextPrimary
                )
            }

            Text(
                text = "${artists.size} artists",
                style = DapTypography.TimeCounter,
                color = DapColors.TextSecondary
            )
        }

        HorizontalDivider(
            color = DapColors.ScreenCardBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        if (artists.isEmpty()) {
            DapEmptyState(
                title = "No Artists Found",
                message = "Scan your audio collection to index artists.",
                icon = Icons.Default.Person
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(artists) { index, artist ->
                    val isWheelSelected = index == selectedIndex

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isWheelSelected) DapColors.ScreenCard else androidx.compose.ui.graphics.Color.Transparent
                            )
                            .clickable { onArtistSelect(artist) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = artist.name,
                                style = DapTypography.MenuItem,
                                color = if (isWheelSelected) DapColors.AccentIceBlue else DapColors.TextPrimary,
                                fontWeight = if (isWheelSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${artist.trackCount} tracks",
                                style = DapTypography.MenuSecondary,
                                color = DapColors.TextSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = if (isWheelSelected) DapColors.AccentIceBlue else DapColors.TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Playlist Browser Display.
 */
@Composable
fun DapPlaylistListDisplay(
    playlists: List<Playlist>,
    selectedIndex: Int,
    listState: LazyListState,
    onPlaylistSelect: (Playlist) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    text = "Playlists",
                    style = DapTypography.MenuHeader,
                    color = DapColors.TextPrimary
                )
            }

            Text(
                text = "${playlists.size} playlists",
                style = DapTypography.TimeCounter,
                color = DapColors.TextSecondary
            )
        }

        HorizontalDivider(
            color = DapColors.ScreenCardBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        if (playlists.isEmpty()) {
            DapEmptyState(
                title = "No Playlists",
                message = "Create custom playlists to organize your favorite music.",
                icon = Icons.AutoMirrored.Filled.QueueMusic
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(playlists) { index, playlist ->
                    val isWheelSelected = index == selectedIndex

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isWheelSelected) DapColors.ScreenCard else androidx.compose.ui.graphics.Color.Transparent
                            )
                            .clickable { onPlaylistSelect(playlist) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = playlist.name,
                                style = DapTypography.MenuItem,
                                color = if (isWheelSelected) DapColors.AccentIceBlue else DapColors.TextPrimary,
                                fontWeight = if (isWheelSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${playlist.trackCount} tracks",
                                style = DapTypography.MenuSecondary,
                                color = DapColors.TextSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = if (isWheelSelected) DapColors.AccentIceBlue else DapColors.TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
