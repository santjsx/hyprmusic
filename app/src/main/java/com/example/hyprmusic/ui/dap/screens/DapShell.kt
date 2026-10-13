package com.example.hyprmusic.ui.dap.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hyprmusic.core.data.CustomPlaylist
import com.example.hyprmusic.core.data.MusicRepository
import com.example.hyprmusic.core.data.PlaylistRepository
import com.example.hyprmusic.core.media.HyprAudioPlayer
import com.example.hyprmusic.core.model.Album
import com.example.hyprmusic.core.model.Artist
import com.example.hyprmusic.core.model.Playlist
import com.example.hyprmusic.ui.dap.components.DapClickWheel
import com.example.hyprmusic.ui.dap.state.DapScreen
import com.example.hyprmusic.ui.dap.state.rememberConnectedAudioDevice
import com.example.hyprmusic.ui.dap.theme.DapColors
import com.example.hyprmusic.ui.dap.theme.DapMotion
import kotlinx.coroutines.launch

/**
 * Master DAP Hardware Shell.
 * Unifies the upper virtual LCD viewport and the lower physical rotary click-wheel
 * into an authentic, hierarchical portable music player experience.
 */
@Composable
fun DapShell(
    audioPlayer: HyprAudioPlayer,
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    onSwitchUi: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Media Permissions and Storage Scanner
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val audioGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.READ_MEDIA_AUDIO] == true
        } else {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
        }
        if (audioGranted) {
            coroutineScope.launch { musicRepository.scanLocalMedia() }
        }
    }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permissions.toTypedArray())
        musicRepository.scanLocalMedia()
    }

    val playbackState by audioPlayer.playbackState.collectAsStateWithLifecycle()
    val tracks by musicRepository.tracks.collectAsStateWithLifecycle()
    val albums by musicRepository.albums.collectAsStateWithLifecycle()
    val artists by musicRepository.artists.collectAsStateWithLifecycle()
    val rawPlaylists by playlistRepository.playlists.collectAsStateWithLifecycle()
    val isScanning by musicRepository.isScanning.collectAsStateWithLifecycle()
    val connectedDevice = rememberConnectedAudioDevice()

    val playlists = remember(rawPlaylists) {
        rawPlaylists.map { Playlist(it.id, it.name, it.trackIds.size, it.createdAt) }
    }

    // Navigation Stack inside Virtual LCD
    var currentScreen by remember { mutableStateOf(DapScreen.NOW_PLAYING) }
    var selectedAlbum by remember { mutableStateOf<Album?>(null) }
    var selectedArtist by remember { mutableStateOf<Artist?>(null) }
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }

    // Scroll Indices
    var menuSelectedIndex by remember { mutableIntStateOf(0) }
    var albumSelectedIndex by remember { mutableIntStateOf(0) }
    var artistSelectedIndex by remember { mutableIntStateOf(0) }
    var playlistSelectedIndex by remember { mutableIntStateOf(0) }
    var trackSelectedIndex by remember { mutableIntStateOf(0) }

    // Lazy List States for rotary auto-scrolling
    val menuListState = rememberLazyListState()
    val albumListState = rememberLazyListState()
    val artistListState = rememberLazyListState()
    val playlistListState = rememberLazyListState()
    val trackListState = rememberLazyListState()
    val lyricsListState = rememberLazyListState()

    // Filter tracks based on active drill-down context
    val currentFilteredTracks = remember(tracks, selectedAlbum, selectedArtist, selectedPlaylist, currentScreen) {
        when {
            selectedAlbum != null -> tracks.filter { it.album.equals(selectedAlbum?.title, ignoreCase = true) }
            selectedArtist != null -> tracks.filter { it.artist.equals(selectedArtist?.name, ignoreCase = true) }
            selectedPlaylist != null -> {
                val p = rawPlaylists.firstOrNull { it.id == selectedPlaylist?.id }
                if (p != null) tracks.filter { p.trackIds.contains(it.id) } else emptyList()
            }
            else -> tracks
        }
    }

    // Classic iPod Hierarchy: Menu Button & Back Handling
    fun navigateBackOrUp() {
        when {
            selectedAlbum != null -> {
                selectedAlbum = null
                currentScreen = DapScreen.ALBUMS_LIST
            }
            selectedArtist != null -> {
                selectedArtist = null
                currentScreen = DapScreen.ARTISTS_LIST
            }
            selectedPlaylist != null -> {
                selectedPlaylist = null
                currentScreen = DapScreen.PLAYLISTS_LIST
            }
            currentScreen == DapScreen.NOW_PLAYING -> {
                currentScreen = DapScreen.MAIN_MENU
            }
            currentScreen != DapScreen.MAIN_MENU -> {
                currentScreen = DapScreen.MAIN_MENU
            }
            currentScreen == DapScreen.MAIN_MENU -> {
                if (playbackState.currentTrack != null) {
                    currentScreen = DapScreen.NOW_PLAYING
                } else {
                    Toast.makeText(context, "Press home to exit", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    BackHandler(enabled = true) {
        navigateBackOrUp()
    }

    // Outer Physical Hardware Chassis
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DapColors.ChassisGraphite)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ==========================================
            // UPPER SECTION: Virtual LCD Display (approx 52% height)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.15f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(DapColors.BezelBackground)
                    .border(1.5.dp, DapColors.ChassisBorder, RoundedCornerShape(26.dp))
                    .padding(5.5.dp) // Outer hardware bezel
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(DapColors.ScreenBgTop, DapColors.ScreenBgBottom)
                        )
                    )
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = DapMotion.lcdSlideEnter()
                        ) togetherWith slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = DapMotion.lcdSlideExit()
                        )
                    },
                    label = "DapLcdNavigation"
                ) { targetScreen ->
                    when (targetScreen) {
                        DapScreen.NOW_PLAYING -> {
                            DapNowPlayingDisplay(
                                playbackState = playbackState,
                                connectedDevice = connectedDevice,
                                onToggleFavorite = { trackId ->
                                    val isFav = musicRepository.toggleFavorite(trackId)
                                    audioPlayer.updateFavoriteStatus(trackId, isFav)
                                },
                                onSeekTo = { audioPlayer.seekTo(it) },
                                onOpenLyrics = { currentScreen = DapScreen.LYRICS },
                                onOpenQueue = { currentScreen = DapScreen.QUEUE }
                            )
                        }

                        DapScreen.MAIN_MENU -> {
                            DapMenuDisplay(
                                selectedIndex = menuSelectedIndex,
                                listState = menuListState,
                                onItemClick = { screen ->
                                    selectedAlbum = null
                                    selectedArtist = null
                                    selectedPlaylist = null
                                    currentScreen = screen
                                }
                            )
                        }

                        DapScreen.ALBUMS_LIST -> {
                            DapAlbumListDisplay(
                                albums = albums,
                                selectedIndex = albumSelectedIndex,
                                listState = albumListState,
                                onAlbumSelect = { album ->
                                    selectedAlbum = album
                                    trackSelectedIndex = 0
                                    currentScreen = DapScreen.TRACKS_LIST
                                },
                                onBack = { currentScreen = DapScreen.MAIN_MENU }
                            )
                        }

                        DapScreen.ARTISTS_LIST -> {
                            DapArtistListDisplay(
                                artists = artists,
                                selectedIndex = artistSelectedIndex,
                                listState = artistListState,
                                onArtistSelect = { artist ->
                                    selectedArtist = artist
                                    trackSelectedIndex = 0
                                    currentScreen = DapScreen.TRACKS_LIST
                                },
                                onBack = { currentScreen = DapScreen.MAIN_MENU }
                            )
                        }

                        DapScreen.PLAYLISTS_LIST -> {
                            DapPlaylistListDisplay(
                                playlists = playlists,
                                selectedIndex = playlistSelectedIndex,
                                listState = playlistListState,
                                onPlaylistSelect = { playlist ->
                                    selectedPlaylist = playlist
                                    trackSelectedIndex = 0
                                    currentScreen = DapScreen.TRACKS_LIST
                                },
                                onBack = { currentScreen = DapScreen.MAIN_MENU }
                            )
                        }

                        DapScreen.TRACKS_LIST -> {
                            val listTitle = when {
                                selectedAlbum != null -> selectedAlbum?.title ?: "Album"
                                selectedArtist != null -> selectedArtist?.name ?: "Artist"
                                selectedPlaylist != null -> selectedPlaylist?.name ?: "Playlist"
                                else -> "All Tracks"
                            }
                            DapTrackListDisplay(
                                title = listTitle,
                                tracks = currentFilteredTracks,
                                currentTrackId = playbackState.currentTrack?.id,
                                selectedIndex = trackSelectedIndex,
                                listState = trackListState,
                                onTrackSelect = { track, queue ->
                                    audioPlayer.playTrack(track, queue)
                                    currentScreen = DapScreen.NOW_PLAYING
                                },
                                onBack = { navigateBackOrUp() }
                            )
                        }

                        DapScreen.QUEUE -> {
                            DapTrackListDisplay(
                                title = "Playback Queue",
                                tracks = playbackState.queue,
                                currentTrackId = playbackState.currentTrack?.id,
                                selectedIndex = playbackState.currentIndex.coerceAtLeast(0),
                                listState = trackListState,
                                onTrackSelect = { track, queue ->
                                    audioPlayer.playTrack(track, queue)
                                    currentScreen = DapScreen.NOW_PLAYING
                                },
                                onBack = { currentScreen = DapScreen.NOW_PLAYING }
                            )
                        }

                        DapScreen.LYRICS -> {
                            DapLyricsDisplay(
                                track = playbackState.currentTrack,
                                currentPositionMs = playbackState.currentPositionMs,
                                listState = lyricsListState,
                                onSeekTo = { audioPlayer.seekTo(it) },
                                onBack = { currentScreen = DapScreen.NOW_PLAYING }
                            )
                        }

                        DapScreen.EQUALIZER -> {
                            DapEqualizerDisplay(
                                onBack = { currentScreen = DapScreen.MAIN_MENU }
                            )
                        }

                        DapScreen.SETTINGS -> {
                            DapSettingsDisplay(
                                trackCount = tracks.size,
                                isScanning = isScanning,
                                onRescan = {
                                    coroutineScope.launch {
                                        musicRepository.scanLocalMedia()
                                        Toast.makeText(context, "Scanning local storage...", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onBack = { currentScreen = DapScreen.MAIN_MENU },
                                onSwitchUi = onSwitchUi
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // LOWER SECTION: Tactile Click Wheel (approx 48% height)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.95f),
                contentAlignment = Alignment.Center
            ) {
                DapClickWheel(
                    isPlaying = playbackState.isPlaying,
                    onMenuClick = {
                        navigateBackOrUp()
                    },
                    onPrevClick = {
                        audioPlayer.skipPrevious()
                    },
                    onPrevLongPress = {
                        val newPos = (playbackState.currentPositionMs - 15000L).coerceAtLeast(0L)
                        audioPlayer.seekTo(newPos)
                        Toast.makeText(context, "Rewind 15s", Toast.LENGTH_SHORT).show()
                    },
                    onNextClick = {
                        audioPlayer.skipNext()
                    },
                    onNextLongPress = {
                        val newPos = (playbackState.currentPositionMs + 15000L).coerceAtMost(playbackState.durationMs)
                        audioPlayer.seekTo(newPos)
                        Toast.makeText(context, "Fast Forward 15s", Toast.LENGTH_SHORT).show()
                    },
                    onPlayPauseClick = {
                        audioPlayer.togglePlayPause(fallbackQueue = tracks)
                    },
                    onCenterLongPress = {
                        playbackState.currentTrack?.let { track ->
                            val isFav = musicRepository.toggleFavorite(track.id)
                            audioPlayer.updateFavoriteStatus(track.id, isFav)
                            Toast.makeText(context, if (isFav) "Added to Favorites" else "Removed from Favorites", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onCenterClick = {
                        when (currentScreen) {
                            DapScreen.MAIN_MENU -> {
                                val destinations = listOf(
                                    DapScreen.NOW_PLAYING,
                                    DapScreen.TRACKS_LIST,
                                    DapScreen.ALBUMS_LIST,
                                    DapScreen.ARTISTS_LIST,
                                    DapScreen.PLAYLISTS_LIST,
                                    DapScreen.QUEUE,
                                    DapScreen.LYRICS,
                                    DapScreen.EQUALIZER,
                                    DapScreen.SETTINGS
                                )
                                selectedAlbum = null
                                selectedArtist = null
                                selectedPlaylist = null
                                currentScreen = destinations.getOrElse(menuSelectedIndex) { DapScreen.NOW_PLAYING }
                            }
                            DapScreen.ALBUMS_LIST -> {
                                if (albums.isNotEmpty()) {
                                    val safeIndex = albumSelectedIndex.coerceIn(0, albums.lastIndex)
                                    selectedAlbum = albums[safeIndex]
                                    trackSelectedIndex = 0
                                    currentScreen = DapScreen.TRACKS_LIST
                                }
                            }
                            DapScreen.ARTISTS_LIST -> {
                                if (artists.isNotEmpty()) {
                                    val safeIndex = artistSelectedIndex.coerceIn(0, artists.lastIndex)
                                    selectedArtist = artists[safeIndex]
                                    trackSelectedIndex = 0
                                    currentScreen = DapScreen.TRACKS_LIST
                                }
                            }
                            DapScreen.PLAYLISTS_LIST -> {
                                if (playlists.isNotEmpty()) {
                                    val safeIndex = playlistSelectedIndex.coerceIn(0, playlists.lastIndex)
                                    selectedPlaylist = playlists[safeIndex]
                                    trackSelectedIndex = 0
                                    currentScreen = DapScreen.TRACKS_LIST
                                }
                            }
                            DapScreen.TRACKS_LIST -> {
                                if (currentFilteredTracks.isNotEmpty()) {
                                    val safeIndex = trackSelectedIndex.coerceIn(0, currentFilteredTracks.lastIndex)
                                    audioPlayer.playTrack(currentFilteredTracks[safeIndex], currentFilteredTracks)
                                    currentScreen = DapScreen.NOW_PLAYING
                                }
                            }
                            DapScreen.QUEUE -> {
                                if (playbackState.queue.isNotEmpty()) {
                                    val safeIndex = trackSelectedIndex.coerceIn(0, playbackState.queue.lastIndex)
                                    audioPlayer.playTrack(playbackState.queue[safeIndex], playbackState.queue)
                                    currentScreen = DapScreen.NOW_PLAYING
                                }
                            }
                            DapScreen.NOW_PLAYING -> {
                                audioPlayer.togglePlayPause(fallbackQueue = tracks)
                            }
                            else -> {}
                        }
                    },
                    onRotateStep = { isClockwise ->
                        when (currentScreen) {
                            DapScreen.NOW_PLAYING -> {
                                // Scrub by 5 seconds
                                val stepMs = if (isClockwise) 5000L else -5000L
                                val newPos = (playbackState.currentPositionMs + stepMs).coerceIn(0L, playbackState.durationMs)
                                audioPlayer.seekTo(newPos)
                            }
                            DapScreen.MAIN_MENU -> {
                                val totalItems = 9
                                menuSelectedIndex = if (isClockwise) {
                                    (menuSelectedIndex + 1).coerceAtMost(totalItems - 1)
                                } else {
                                    (menuSelectedIndex - 1).coerceAtLeast(0)
                                }
                                coroutineScope.launch {
                                    menuListState.animateScrollToItem(menuSelectedIndex)
                                }
                            }
                            DapScreen.ALBUMS_LIST -> {
                                if (albums.isNotEmpty()) {
                                    albumSelectedIndex = if (isClockwise) {
                                        (albumSelectedIndex + 1).coerceAtMost(albums.lastIndex)
                                    } else {
                                        (albumSelectedIndex - 1).coerceAtLeast(0)
                                    }
                                    coroutineScope.launch {
                                        albumListState.animateScrollToItem(albumSelectedIndex)
                                    }
                                }
                            }
                            DapScreen.ARTISTS_LIST -> {
                                if (artists.isNotEmpty()) {
                                    artistSelectedIndex = if (isClockwise) {
                                        (artistSelectedIndex + 1).coerceAtMost(artists.lastIndex)
                                    } else {
                                        (artistSelectedIndex - 1).coerceAtLeast(0)
                                    }
                                    coroutineScope.launch {
                                        artistListState.animateScrollToItem(artistSelectedIndex)
                                    }
                                }
                            }
                            DapScreen.PLAYLISTS_LIST -> {
                                if (playlists.isNotEmpty()) {
                                    playlistSelectedIndex = if (isClockwise) {
                                        (playlistSelectedIndex + 1).coerceAtMost(playlists.lastIndex)
                                    } else {
                                        (playlistSelectedIndex - 1).coerceAtLeast(0)
                                    }
                                    coroutineScope.launch {
                                        playlistListState.animateScrollToItem(playlistSelectedIndex)
                                    }
                                }
                            }
                            DapScreen.TRACKS_LIST -> {
                                if (currentFilteredTracks.isNotEmpty()) {
                                    trackSelectedIndex = if (isClockwise) {
                                        (trackSelectedIndex + 1).coerceAtMost(currentFilteredTracks.lastIndex)
                                    } else {
                                        (trackSelectedIndex - 1).coerceAtLeast(0)
                                    }
                                    coroutineScope.launch {
                                        trackListState.animateScrollToItem(trackSelectedIndex)
                                    }
                                }
                            }
                            DapScreen.QUEUE -> {
                                if (playbackState.queue.isNotEmpty()) {
                                    trackSelectedIndex = if (isClockwise) {
                                        (trackSelectedIndex + 1).coerceAtMost(playbackState.queue.lastIndex)
                                    } else {
                                        (trackSelectedIndex - 1).coerceAtLeast(0)
                                    }
                                    coroutineScope.launch {
                                        trackListState.animateScrollToItem(trackSelectedIndex)
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                )
            }
        }
    }
}
