package com.example.hyprmusic.ui.elovaire.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hyprmusic.core.cloud.telegram.TelegramMusicRepository
import com.example.hyprmusic.core.data.MusicRepository
import com.example.hyprmusic.core.data.PlaylistRepository
import com.example.hyprmusic.core.media.HyprAudioPlayer
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.unit.IntOffset
import com.example.hyprmusic.core.model.Album
import com.example.hyprmusic.ui.components.HyprWorkspace
import com.example.hyprmusic.ui.elovaire.components.ElovaireBottomDock
import com.example.hyprmusic.ui.elovaire.components.ElovaireMiniPlayer
import com.example.hyprmusic.ui.elovaire.components.ElovaireTopBar
import com.example.hyprmusic.ui.elovaire.motion.ElovaireMotionSpecs
import com.example.hyprmusic.ui.elovaire.theme.ElovaireColors
import kotlinx.coroutines.launch

/**
 * Master Elovaire Shell Host.
 * Connects the Presentation layer seamlessly with HyprMusic's core services,
 * managing the viewport transitions, floating mini-player dock, and Now Playing overlay.
 */
@Composable
fun ElovaireShell(
    audioPlayer: HyprAudioPlayer,
    musicRepository: MusicRepository,
    telegramRepository: TelegramMusicRepository,
    playlistRepository: PlaylistRepository,
    modifier: Modifier = Modifier
) {
    val playbackState by audioPlayer.playbackState.collectAsStateWithLifecycle()
    val tracks by musicRepository.tracks.collectAsStateWithLifecycle()
    val albums by musicRepository.albums.collectAsStateWithLifecycle()
    val artists by musicRepository.artists.collectAsStateWithLifecycle()
    val isScanning by musicRepository.isScanning.collectAsStateWithLifecycle()
    val hasInitialScanCompleted by musicRepository.hasInitialScanCompleted.collectAsStateWithLifecycle()

    var currentWorkspace by remember { mutableStateOf(HyprWorkspace.HOME) }
    var isNowPlayingExpanded by remember { mutableStateOf(false) }
    var showEqualizerDialog by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedAlbumForLibrary by remember { mutableStateOf<Album?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val navigateToAlbum: (String) -> Unit = { albumTitle ->
        val matchedAlbum = albums.firstOrNull { it.title.equals(albumTitle, ignoreCase = true) }
            ?: Album(
                id = albumTitle.hashCode().toString(),
                title = albumTitle,
                artist = playbackState.currentTrack?.artist ?: "Unknown Artist",
                coverUri = playbackState.currentTrack?.albumArtUri,
                trackCount = tracks.count { it.album.equals(albumTitle, ignoreCase = true) }.coerceAtLeast(1)
            )
        selectedAlbumForLibrary = matchedAlbum
        isNowPlayingExpanded = false
        currentWorkspace = HyprWorkspace.LIBRARY
    }

    // Media Permissions check
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

    var lastBackPressMs by remember { mutableStateOf(0L) }

    BackHandler(enabled = true) {
        when {
            showEqualizerDialog -> showEqualizerDialog = false
            isNowPlayingExpanded -> isNowPlayingExpanded = false
            isSearchActive -> {
                isSearchActive = false
                searchQuery = ""
            }
            selectedAlbumForLibrary != null -> selectedAlbumForLibrary = null
            currentWorkspace != HyprWorkspace.HOME -> currentWorkspace = HyprWorkspace.HOME
            else -> {
                val now = System.currentTimeMillis()
                if (now - lastBackPressMs < 2000L) {
                    (context as? ComponentActivity)?.finish()
                } else {
                    lastBackPressMs = now
                    Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElovaireColors.Night)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Frosted Top Bar
            if (!isSearchActive) {
                ElovaireTopBar(
                    title = when (currentWorkspace) {
                        HyprWorkspace.HOME -> "Home"
                        HyprWorkspace.LIBRARY -> "Library"
                        HyprWorkspace.PLAYING -> "Now Playing"
                        HyprWorkspace.SETTINGS -> "Settings"
                    },
                    subtitle = if (currentWorkspace == HyprWorkspace.LIBRARY && selectedAlbumForLibrary != null) {
                        selectedAlbumForLibrary?.title
                    } else null,
                    onOpenSearch = { isSearchActive = true },
                    onOpenEqualizer = { showEqualizerDialog = true }
                )
            }

            // Viewport
            Box(modifier = Modifier.weight(1f)) {
                if (isSearchActive) {
                    ElovaireSearchScreen(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        musicRepository = musicRepository,
                        telegramRepository = telegramRepository,
                        playbackState = playbackState,
                        onTrackSelected = { track, queue ->
                            audioPlayer.playTrack(track, queue)
                        },
                        onClose = {
                            isSearchActive = false
                            searchQuery = ""
                        }
                    )
                } else {
                    when (currentWorkspace) {
                        HyprWorkspace.HOME -> {
                            val heavyRotation = remember(tracks, musicRepository.playbackStatsRepository.playCountsChanged) {
                                musicRepository.getHeavyRotationTracks(10)
                            }

                            ElovaireHomeScreen(
                                playbackState = playbackState,
                                tracks = tracks,
                                heavyRotationTracks = heavyRotation,
                                isScanning = isScanning,
                                hasInitialScanCompleted = hasInitialScanCompleted,
                                onTrackSelected = { track, queue ->
                                    audioPlayer.playTrack(track, queue)
                                },
                                onTogglePlayPause = {
                                    audioPlayer.togglePlayPause(fallbackQueue = tracks)
                                },
                                onRandomMix = {
                                    if (tracks.isNotEmpty()) {
                                        audioPlayer.playRandomMix(tracks)
                                        Toast.makeText(context, "Shuffled ${tracks.size} tracks", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onRescan = {
                                    coroutineScope.launch {
                                        musicRepository.scanLocalMedia()
                                        Toast.makeText(context, "Indexed ${tracks.size} tracks", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }

                        HyprWorkspace.LIBRARY -> {
                            ElovaireLibraryScreen(
                                playbackState = playbackState,
                                tracks = tracks,
                                albums = albums,
                                artists = artists,
                                isScanning = isScanning,
                                onRescan = { coroutineScope.launch { musicRepository.scanLocalMedia() } },
                                onTrackSelected = { track, queue ->
                                    audioPlayer.playTrack(track, queue)
                                },
                                onToggleFavorite = { trackId ->
                                    val isFav = musicRepository.toggleFavorite(trackId)
                                    audioPlayer.updateFavoriteStatus(trackId, isFav)
                                },
                                telegramRepository = telegramRepository,
                                playlistRepository = playlistRepository,
                                initialAlbum = selectedAlbumForLibrary,
                                onAlbumCleared = { selectedAlbumForLibrary = null }
                            )
                        }

                        HyprWorkspace.PLAYING -> {
                            ElovaireNowPlayingScreen(
                                playbackState = playbackState,
                                audioPlayer = audioPlayer,
                                playlistRepository = playlistRepository,
                                onPlayPause = { audioPlayer.togglePlayPause() },
                                onSkipNext = { audioPlayer.skipNext() },
                                onSkipPrevious = { audioPlayer.skipPrevious() },
                                onSeekTo = { audioPlayer.seekTo(it) },
                                onToggleShuffle = { audioPlayer.toggleShuffle() },
                                onToggleRepeat = { audioPlayer.toggleRepeat() },
                                onToggleFavorite = { trackId ->
                                    val isFav = musicRepository.toggleFavorite(trackId)
                                    audioPlayer.updateFavoriteStatus(trackId, isFav)
                                },
                                onOpenEqualizer = { showEqualizerDialog = true },
                                onNavigateToAlbum = navigateToAlbum,
                                onDismiss = { currentWorkspace = HyprWorkspace.HOME }
                            )
                        }

                        HyprWorkspace.SETTINGS -> {
                            ElovaireSettingsScreen(
                                isScanning = isScanning,
                                onRescanMedia = { coroutineScope.launch { musicRepository.scanLocalMedia() } },
                                onOpenEqualizer = { showEqualizerDialog = true },
                                telegramRepository = telegramRepository
                            )
                        }
                    }
                }
            }

            // Bottom Navigation Chrome (Mini-Player & Bottom Dock)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding()
            ) {
                // Persistent Floating Mini-Player
                if (playbackState.currentTrack != null && !isNowPlayingExpanded && currentWorkspace != HyprWorkspace.PLAYING && !isSearchActive) {
                    ElovaireMiniPlayer(
                        playbackState = playbackState,
                        onPlayPause = { audioPlayer.togglePlayPause() },
                        onSkipNext = { audioPlayer.skipNext() },
                        onSkipPrevious = { audioPlayer.skipPrevious() },
                        onClick = { isNowPlayingExpanded = true }
                    )
                }

                // Floating Bottom Dock
                if (!isSearchActive) {
                    ElovaireBottomDock(
                        currentWorkspace = if (isNowPlayingExpanded) HyprWorkspace.PLAYING else currentWorkspace,
                        onWorkspaceSelected = { ws ->
                            if (ws == HyprWorkspace.PLAYING) {
                                if (playbackState.currentTrack == null && tracks.isNotEmpty()) {
                                    audioPlayer.prepareTrack(tracks.first(), tracks)
                                }
                                isNowPlayingExpanded = true
                            } else {
                                isNowPlayingExpanded = false
                                currentWorkspace = ws
                            }
                        },
                        isPlaying = playbackState.isPlaying
                    )
                }
            }
        }

        // Fullscreen Slide-Up Now Playing Overlay
        AnimatedVisibility(
            visible = isNowPlayingExpanded,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = ElovaireMotionSpecs.overlayEnter<IntOffset>()),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = ElovaireMotionSpecs.overlayExit<IntOffset>())
        ) {
            ElovaireNowPlayingScreen(
                playbackState = playbackState,
                audioPlayer = audioPlayer,
                playlistRepository = playlistRepository,
                onPlayPause = { audioPlayer.togglePlayPause() },
                onSkipNext = { audioPlayer.skipNext() },
                onSkipPrevious = { audioPlayer.skipPrevious() },
                onSeekTo = { audioPlayer.seekTo(it) },
                onToggleShuffle = { audioPlayer.toggleShuffle() },
                onToggleRepeat = { audioPlayer.toggleRepeat() },
                onToggleFavorite = { trackId ->
                    val isFav = musicRepository.toggleFavorite(trackId)
                    audioPlayer.updateFavoriteStatus(trackId, isFav)
                },
                onOpenEqualizer = { showEqualizerDialog = true },
                onNavigateToAlbum = navigateToAlbum,
                onDismiss = { isNowPlayingExpanded = false },
                modifier = Modifier.fillMaxSize()
            )
        }

        // DSP Audio Equalizer Sheet
        AnimatedVisibility(
            visible = showEqualizerDialog,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = ElovaireMotionSpecs.overlayEnter<IntOffset>()) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = ElovaireMotionSpecs.overlayExit<IntOffset>()) + fadeOut()
        ) {
            ElovaireEqualizerSheet(
                onClose = { showEqualizerDialog = false }
            )
        }
    }
}
