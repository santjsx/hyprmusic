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
import com.example.hyprmusic.core.data.MusicRepository
import com.example.hyprmusic.core.data.PlaylistRepository
import com.example.hyprmusic.core.media.HyprAudioPlayer
import com.example.hyprmusic.core.model.Album
import com.example.hyprmusic.core.model.Artist
import com.example.hyprmusic.core.model.Playlist
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.ui.dap.components.DapAddToPlaylistSheet
import com.example.hyprmusic.ui.dap.components.DapClickWheel
import com.example.hyprmusic.ui.dap.components.DapMiniPlayer
import com.example.hyprmusic.ui.dap.state.DapScreen
import com.example.hyprmusic.ui.dap.state.rememberConnectedAudioDevice
import com.example.hyprmusic.ui.dap.theme.DapColors
import com.example.hyprmusic.ui.dap.theme.DapMotion
import kotlinx.coroutines.launch

/**
 * Master DAP Hardware Shell.
 * - When in NOW_PLAYING: Displays the exact iPod card on top + tactile click wheel on bottom.
 * - In all other screens (Tracks, Albums, Playlists, Favorites, Equalizer, Settings):
 *   Allocates 100% full screen height (NO wheel) with sticky docked Mini-Player.
 */
@Composable
fun DapShell(
    audioPlayer: HyprAudioPlayer,
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
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

    // Navigation Stack - Defaults directly to LIBRARY on startup!
    val initialScreen = remember {
        if (audioPlayer.playbackState.value.isPlaying) {
            DapScreen.NOW_PLAYING
        } else {
            DapScreen.LIBRARY
        }
    }
    var currentScreen by remember { mutableStateOf(initialScreen) }
    var selectedAlbum by remember { mutableStateOf<Album?>(null) }
    var selectedArtist by remember { mutableStateOf<Artist?>(null) }
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }

    // Add to Playlist Bottom Sheet State
    var trackForPlaylistSheet by remember { mutableStateOf<Track?>(null) }

    // Scroll Indices
    var menuSelectedIndex by remember { mutableIntStateOf(0) }
    var albumSelectedIndex by remember { mutableIntStateOf(0) }
    var artistSelectedIndex by remember { mutableIntStateOf(0) }
    var playlistSelectedIndex by remember { mutableIntStateOf(0) }
    var trackSelectedIndex by remember { mutableIntStateOf(0) }

    // Lazy List States
    val menuListState = rememberLazyListState()
    val albumListState = rememberLazyListState()
    val artistListState = rememberLazyListState()
    val playlistListState = rememberLazyListState()
    val trackListState = rememberLazyListState()
    val lyricsListState = rememberLazyListState()

    // Filter tracks based on active drill-down context
    val currentFilteredTracks = remember(tracks, selectedAlbum, selectedArtist, selectedPlaylist, currentScreen) {
        when {
            currentScreen == DapScreen.FAVORITES_LIST -> tracks.filter { it.isFavorite }
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
                currentScreen = DapScreen.LIBRARY
            }
            currentScreen == DapScreen.LIBRARY -> {
                currentScreen = DapScreen.MAIN_MENU
            }
            currentScreen != DapScreen.MAIN_MENU -> {
                currentScreen = DapScreen.LIBRARY
            }
            currentScreen == DapScreen.MAIN_MENU -> {
                if (playbackState.currentTrack != null) {
                    currentScreen = DapScreen.NOW_PLAYING
                } else {
                    Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
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
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // =========================================================================
            // SCREEN CONTENT DISPLAY
            // If NOW_PLAYING: Height is weighted to accommodate Click Wheel below.
            // If OTHER SCREEN: Takes 100% of the upper and middle area with NO Wheel!
            // =========================================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (currentScreen == DapScreen.NOW_PLAYING) 1.05f else 1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(DapColors.BezelBackground)
                    .border(1.2.dp, DapColors.ChassisBorder, RoundedCornerShape(22.dp))
                    .padding(4.5.dp) // Outer hardware bezel
                    .clip(RoundedCornerShape(18.dp))
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
                    label = "DapNavigation"
                ) { targetScreen ->
                    when (targetScreen) {
                        DapScreen.NOW_PLAYING -> {
                            DapNowPlayingDisplay(
                                playbackState = playbackState,
                                connectedDevice = connectedDevice,
                                onToggleFavorite = { trackId ->
                                    val isFav = musicRepository.toggleFavorite(trackId)
                                    audioPlayer.updateFavoriteStatus(trackId, isFav)
                                    Toast.makeText(
                                        context,
                                        if (isFav) "Added to Favorites ⭐" else "Removed from Favorites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onSeekTo = { audioPlayer.seekTo(it) },
                                onOpenLyrics = { currentScreen = DapScreen.LYRICS },
                                onOpenQueue = { currentScreen = DapScreen.QUEUE },
                                onAddToPlaylist = { track ->
                                    trackForPlaylistSheet = track
                                },
                                onBrowseLibrary = { currentScreen = DapScreen.LIBRARY },
                                onShuffleAll = {
                                    if (tracks.isNotEmpty()) {
                                        audioPlayer.playRandomMix(tracks)
                                    }
                                }
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

                        DapScreen.TRACKS_LIST, DapScreen.LIBRARY -> {
                            val activeTitle = when {
                                selectedAlbum != null -> selectedAlbum?.title ?: "Album Tracks"
                                selectedArtist != null -> selectedArtist?.name ?: "Artist Tracks"
                                selectedPlaylist != null -> selectedPlaylist?.name ?: "Playlist Tracks"
                                else -> "All Tracks"
                            }

                            DapTrackListDisplay(
                                title = activeTitle,
                                tracks = currentFilteredTracks,
                                currentTrackId = playbackState.currentTrack?.id,
                                selectedIndex = trackSelectedIndex,
                                listState = trackListState,
                                onTrackSelect = { selectedTrack, trackList ->
                                    audioPlayer.playTrack(selectedTrack, trackList)
                                    currentScreen = DapScreen.NOW_PLAYING
                                },
                                onBack = { navigateBackOrUp() },
                                onToggleFavorite = { trackId ->
                                    val isFav = musicRepository.toggleFavorite(trackId)
                                    audioPlayer.updateFavoriteStatus(trackId, isFav)
                                },
                                onAddToPlaylist = { track ->
                                    trackForPlaylistSheet = track
                                }
                            )
                        }

                        DapScreen.FAVORITES_LIST -> {
                            val favoriteTracks = remember(tracks) { tracks.filter { it.isFavorite } }
                            DapTrackListDisplay(
                                title = "⭐ Favorites",
                                tracks = favoriteTracks,
                                currentTrackId = playbackState.currentTrack?.id,
                                selectedIndex = trackSelectedIndex,
                                listState = trackListState,
                                onTrackSelect = { selectedTrack, trackList ->
                                    audioPlayer.playTrack(selectedTrack, trackList)
                                    currentScreen = DapScreen.NOW_PLAYING
                                },
                                onBack = { currentScreen = DapScreen.MAIN_MENU },
                                onToggleFavorite = { trackId ->
                                    val isFav = musicRepository.toggleFavorite(trackId)
                                    audioPlayer.updateFavoriteStatus(trackId, isFav)
                                },
                                onAddToPlaylist = { track ->
                                    trackForPlaylistSheet = track
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
                                onBack = { currentScreen = DapScreen.MAIN_MENU },
                                onCreateNewPlaylist = {
                                    if (tracks.isNotEmpty()) {
                                        trackForPlaylistSheet = tracks.first()
                                    }
                                }
                            )
                        }

                        DapScreen.QUEUE -> {
                            DapTrackListDisplay(
                                title = "Playback Queue",
                                tracks = playbackState.queue,
                                currentTrackId = playbackState.currentTrack?.id,
                                selectedIndex = trackSelectedIndex,
                                listState = trackListState,
                                onTrackSelect = { selectedTrack, queueList ->
                                    audioPlayer.playTrack(selectedTrack, queueList)
                                    currentScreen = DapScreen.NOW_PLAYING
                                },
                                onBack = { currentScreen = DapScreen.NOW_PLAYING },
                                onToggleFavorite = { trackId ->
                                    val isFav = musicRepository.toggleFavorite(trackId)
                                    audioPlayer.updateFavoriteStatus(trackId, isFav)
                                }
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
                                        Toast.makeText(context, "Scanning local audio storage...", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onBack = { currentScreen = DapScreen.MAIN_MENU }
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // LOWER VIEWPORT:
            // 1. If NOW_PLAYING: Display the iconic Rotary Click Wheel!
            // 2. If OTHER SCREEN: Display the docked Mini Player bar (NO Click Wheel)!
            // =========================================================================
            if (currentScreen == DapScreen.NOW_PLAYING) {
                Spacer(modifier = Modifier.height(8.dp))

                val wheelPrefs = remember { context.getSharedPreferences("dap_wheel_prefs", android.content.Context.MODE_PRIVATE) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.95f),
                    contentAlignment = Alignment.Center
                ) {
                    DapClickWheel(
                        isPlaying = playbackState.isPlaying,
                        enableAuditoryClick = wheelPrefs.getBoolean("wheel_sound_enabled", true),
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
                                Toast.makeText(
                                    context,
                                    if (isFav) "Added to Favorites ⭐" else "Removed from Favorites",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onCenterClick = {
                            audioPlayer.togglePlayPause(fallbackQueue = tracks)
                        },
                        onRotateStep = { isClockwise ->
                            val delta = if (isClockwise) 2000L else -2000L
                            val newPos = (playbackState.currentPositionMs + delta).coerceIn(0L, playbackState.durationMs)
                            audioPlayer.seekTo(newPos)
                        }
                    )
                }
            } else {
                // Docked Mini-Player Bar for all other screens (Tracks, Playlists, Albums, Settings, etc.)
                if (playbackState.currentTrack != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    DapMiniPlayer(
                        playbackState = playbackState,
                        onTapMiniPlayer = { currentScreen = DapScreen.NOW_PLAYING },
                        onPlayPauseClick = { audioPlayer.togglePlayPause(fallbackQueue = tracks) },
                        onNextClick = { audioPlayer.skipNext() }
                    )
                }
            }
        }

        // =========================================================================
        // ADD TO PLAYLIST BOTTOM SHEET MODAL
        // =========================================================================
        trackForPlaylistSheet?.let { trackToAdd ->
            DapAddToPlaylistSheet(
                track = trackToAdd,
                playlists = playlists,
                onAddToPlaylist = { playlistId, trackId ->
                    coroutineScope.launch {
                        playlistRepository.addTrackToPlaylist(playlistId, trackId)
                        val pName = playlists.firstOrNull { it.id == playlistId }?.name ?: "Playlist"
                        Toast.makeText(context, "Added '${trackToAdd.title}' to $pName 🎵", Toast.LENGTH_SHORT).show()
                        trackForPlaylistSheet = null
                    }
                },
                onCreatePlaylist = { newName, trackId ->
                    coroutineScope.launch {
                        val newPl = playlistRepository.createPlaylist(newName)
                        if (newPl != null) {
                            playlistRepository.addTrackToPlaylist(newPl.id, trackId)
                        }
                        Toast.makeText(context, "Created '$newName' with '${trackToAdd.title}' 🎵", Toast.LENGTH_SHORT).show()
                        trackForPlaylistSheet = null
                    }
                },
                onDismiss = {
                    trackForPlaylistSheet = null
                }
            )
        }
    }
}
