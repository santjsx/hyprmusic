package com.example.hyprmusic

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.hyprmusic.core.data.MusicRepository
import com.example.hyprmusic.core.media.HyprAudioPlayer
import com.example.hyprmusic.core.media.HyprEqualizer
import com.example.hyprmusic.core.theming.HyprThemeProvider
import com.example.hyprmusic.core.theming.ThemeManager
import com.example.hyprmusic.ui.components.EqualizerDialog
import com.example.hyprmusic.ui.components.HyprTopBar
import com.example.hyprmusic.ui.components.HyprWorkspace
import com.example.hyprmusic.ui.components.MiniPlayer
import com.example.hyprmusic.ui.screens.HomeScreen
import com.example.hyprmusic.ui.screens.LibraryScreen
import com.example.hyprmusic.ui.screens.NowPlayingScreen
import com.example.hyprmusic.ui.screens.SettingsScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var audioPlayer: HyprAudioPlayer
    private lateinit var musicRepository: MusicRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        audioPlayer = HyprAudioPlayer(applicationContext)
        musicRepository = MusicRepository(applicationContext)

        try {
            HyprEqualizer.init(applicationContext, audioPlayer.audioSessionId)
        } catch (_: Exception) {}

        setContent {
            val themeConfig by ThemeManager.themeConfig.collectAsState()

            HyprThemeProvider(themeConfig = themeConfig) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = themeConfig.backgroundColor
                ) {
                    HyprMusicApp(
                        audioPlayer = audioPlayer,
                        musicRepository = musicRepository
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.release()
    }
}

@Composable
fun HyprMusicApp(
    audioPlayer: HyprAudioPlayer,
    musicRepository: MusicRepository
) {
    val themeConfig by ThemeManager.themeConfig.collectAsState()
    val playbackState by audioPlayer.playbackState.collectAsState()
    val tracks by musicRepository.tracks.collectAsState()
    val albums by musicRepository.albums.collectAsState()
    val artists by musicRepository.artists.collectAsState()
    val isScanning by musicRepository.isScanning.collectAsState()

    var currentWorkspace by remember { mutableStateOf(HyprWorkspace.HOME) }
    var isNowPlayingExpanded by remember { mutableStateOf(false) }
    var showEqualizerDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Permissions check
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

    BackHandler(enabled = isNowPlayingExpanded) {
        isNowPlayingExpanded = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Hyprland Terminal Workspace Bar
            HyprTopBar(
                theme = themeConfig,
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
                }
            )

            // Dynamic Workspace Content
            Box(modifier = Modifier.weight(1f)) {
                when (currentWorkspace) {
                    HyprWorkspace.HOME -> {
                        HomeScreen(
                            theme = themeConfig,
                            playbackState = playbackState,
                            tracks = tracks,
                            onTrackSelected = { track, queue ->
                                audioPlayer.playTrack(track, queue)
                            },
                            onTogglePlayPause = { audioPlayer.togglePlayPause() },
                            onRandomMix = {
                                if (tracks.isNotEmpty()) {
                                    val shuffled = tracks.shuffled()
                                    audioPlayer.playTrack(shuffled.first(), shuffled)
                                }
                            }
                        )
                    }

                    HyprWorkspace.LIBRARY -> {
                        LibraryScreen(
                            theme = themeConfig,
                            playbackState = playbackState,
                            tracks = tracks,
                            albums = albums,
                            artists = artists,
                            isScanning = isScanning,
                            onRescan = { coroutineScope.launch { musicRepository.scanLocalMedia() } },
                            onTrackSelected = { track, queue ->
                                audioPlayer.playTrack(track, queue)
                            },
                            onToggleFavorite = { musicRepository.toggleFavorite(it) }
                        )
                    }

                    HyprWorkspace.PLAYING -> {
                        // Handled by fullscreen overlay or switch
                        NowPlayingScreen(
                            theme = themeConfig,
                            playbackState = playbackState,
                            onPlayPause = { audioPlayer.togglePlayPause() },
                            onSkipNext = { audioPlayer.skipNext() },
                            onSkipPrevious = { audioPlayer.skipPrevious() },
                            onSeekTo = { audioPlayer.seekTo(it) },
                            onToggleShuffle = { audioPlayer.toggleShuffle() },
                            onToggleRepeat = { audioPlayer.toggleRepeat() },
                            onToggleFavorite = { musicRepository.toggleFavorite(it) },
                            onOpenEqualizer = { showEqualizerDialog = true },
                            onBrowseLibrary = { currentWorkspace = HyprWorkspace.LIBRARY },
                            onRandomMix = {
                                if (tracks.isNotEmpty()) {
                                    val shuffled = tracks.shuffled()
                                    audioPlayer.playTrack(shuffled.first(), shuffled)
                                }
                            },
                            onDismiss = { currentWorkspace = HyprWorkspace.HOME }
                        )
                    }

                    HyprWorkspace.SETTINGS -> {
                        SettingsScreen(
                            theme = themeConfig,
                            isScanning = isScanning,
                            onRescanMedia = { coroutineScope.launch { musicRepository.scanLocalMedia() } },
                            onOpenEqualizer = { showEqualizerDialog = true }
                        )
                    }
                }
            }
        }

        // Persistent Floating Mini-Player Dock
        if (playbackState.currentTrack != null && !isNowPlayingExpanded && currentWorkspace != HyprWorkspace.PLAYING) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp)
            ) {
                MiniPlayer(
                    theme = themeConfig,
                    playbackState = playbackState,
                    onPlayPause = { audioPlayer.togglePlayPause() },
                    onSkipNext = { audioPlayer.skipNext() },
                    onClick = { isNowPlayingExpanded = true }
                )
            }
        }

        // Fullscreen Now Playing Overlay
        AnimatedVisibility(
            visible = isNowPlayingExpanded,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            NowPlayingScreen(
                theme = themeConfig,
                playbackState = playbackState,
                onPlayPause = { audioPlayer.togglePlayPause() },
                onSkipNext = { audioPlayer.skipNext() },
                onSkipPrevious = { audioPlayer.skipPrevious() },
                onSeekTo = { audioPlayer.seekTo(it) },
                onToggleShuffle = { audioPlayer.toggleShuffle() },
                onToggleRepeat = { audioPlayer.toggleRepeat() },
                onToggleFavorite = { musicRepository.toggleFavorite(it) },
                onOpenEqualizer = { showEqualizerDialog = true },
                onBrowseLibrary = {
                    isNowPlayingExpanded = false
                    currentWorkspace = HyprWorkspace.LIBRARY
                },
                onRandomMix = {
                    if (tracks.isNotEmpty()) {
                        val shuffled = tracks.shuffled()
                        audioPlayer.playTrack(shuffled.first(), shuffled)
                    }
                },
                onDismiss = { isNowPlayingExpanded = false },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Native DSP Audio Equalizer Dialog
        if (showEqualizerDialog) {
            EqualizerDialog(
                theme = themeConfig,
                onDismiss = { showEqualizerDialog = false }
            )
        }
    }
}

