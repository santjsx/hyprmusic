package com.example.hyprmusic

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.hyprmusic.ui.components.HyprArtworkImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.request.CachePolicy
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hyprmusic.core.data.MusicRepository
import com.example.hyprmusic.core.data.PlaylistRepository
import com.example.hyprmusic.core.media.HyprAudioPlayer
import com.example.hyprmusic.core.media.HyprEqualizer
import com.example.hyprmusic.core.model.Album
import com.example.hyprmusic.core.model.Track
import com.example.hyprmusic.core.theming.HyprThemeProvider
import com.example.hyprmusic.core.theming.ThemeManager
import com.example.hyprmusic.core.theming.hyprBounceClick
import com.example.hyprmusic.core.theming.hyprTile
import com.example.hyprmusic.ui.components.EqualizerDialog
import com.example.hyprmusic.ui.components.HyprBottomDock
import com.example.hyprmusic.ui.components.HyprBottomSearchRunner
import com.example.hyprmusic.ui.components.HyprWaybarHeader
import com.example.hyprmusic.ui.components.HyprWorkspace
import com.example.hyprmusic.ui.components.MiniPlayer
import com.example.hyprmusic.ui.screens.HomeScreen
import com.example.hyprmusic.ui.screens.LibraryScreen
import com.example.hyprmusic.ui.screens.NowPlayingScreen
import com.example.hyprmusic.ui.screens.SettingsScreen
import com.example.hyprmusic.core.cloud.telegram.TelegramMusicRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var audioPlayer: HyprAudioPlayer
    private lateinit var musicRepository: MusicRepository
    private lateinit var telegramRepository: TelegramMusicRepository
    private lateinit var playlistRepository: PlaylistRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ThemeManager.init(applicationContext)
        audioPlayer = HyprAudioPlayer.getInstance(applicationContext)
        musicRepository = MusicRepository(applicationContext)
        telegramRepository = TelegramMusicRepository(applicationContext, musicRepository)
        playlistRepository = PlaylistRepository(applicationContext)

        // Defer audio effect binding and Coil cache init to background thread to allow instant first-frame render
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                HyprEqualizer.init(applicationContext, audioPlayer.audioSessionId)
            } catch (_: Exception) {}

            val imageLoader = coil.ImageLoader.Builder(applicationContext)
                .memoryCache {
                    coil.memory.MemoryCache.Builder(applicationContext)
                        .maxSizePercent(0.25)
                        .build()
                }
                .diskCache {
                    coil.disk.DiskCache.Builder()
                        .directory(applicationContext.cacheDir.resolve("image_cache"))
                        .maxSizeBytes(100L * 1024 * 1024)
                        .build()
                }
                .bitmapConfig(android.graphics.Bitmap.Config.HARDWARE)
                .respectCacheHeaders(false)
                .crossfade(true)
                .build()
            coil.Coil.setImageLoader(imageLoader)
        }

        setContent {
            val designSpec by ThemeManager.designSpec.collectAsStateWithLifecycle()
            val themeConfig by ThemeManager.themeConfig.collectAsStateWithLifecycle()

            HyprThemeProvider(designSpec = designSpec, themeConfig = themeConfig) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = designSpec.bg
                ) {
                    HyprMusicApp(
                        audioPlayer = audioPlayer,
                        musicRepository = musicRepository,
                        telegramRepository = telegramRepository,
                        playlistRepository = playlistRepository
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Do not release audioPlayer here: allows audio to keep playing uninterrupted in background
    }
}

@Composable
fun HyprMusicApp(
    audioPlayer: HyprAudioPlayer,
    musicRepository: MusicRepository,
    telegramRepository: TelegramMusicRepository,
    playlistRepository: PlaylistRepository
) {
    val themeConfig by ThemeManager.themeConfig.collectAsStateWithLifecycle()
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

    val context = LocalContext.current
    var lastBackPressMs by remember { mutableStateOf(0L) }

    BackHandler(enabled = true) {
        if (showEqualizerDialog) {
            showEqualizerDialog = false
        } else if (isNowPlayingExpanded) {
            isNowPlayingExpanded = false
        } else if (isSearchActive) {
            isSearchActive = false
            searchQuery = ""
        } else if (selectedAlbumForLibrary != null) {
            selectedAlbumForLibrary = null
        } else if (currentWorkspace != HyprWorkspace.HOME) {
            currentWorkspace = HyprWorkspace.HOME
        } else {
            val now = System.currentTimeMillis()
            if (now - lastBackPressMs < 2000L) {
                (context as? ComponentActivity)?.finish()
            } else {
                lastBackPressMs = now
                android.widget.Toast.makeText(context, "[ PRESS BACK AGAIN TO EXIT ]", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Linux Waybar Top Status Header
            HyprWaybarHeader(
                theme = themeConfig,
                title = if (isSearchActive) "~ / hypr / search" else "~ / hypr / ${currentWorkspace.label}",
                onOpenEqualizer = { showEqualizerDialog = true }
            )

            // Dynamic Workspace Content or Live Search Results
            Box(modifier = Modifier.weight(1f)) {
                if (isSearchActive) {
                    if (searchQuery.isNotBlank()) {
                        val cloudTracks by telegramRepository.cloudTracks.collectAsStateWithLifecycle()
                        var searchResults by remember { mutableStateOf<List<Track>>(emptyList()) }

                        LaunchedEffect(searchQuery, tracks, cloudTracks) {
                            searchResults = withContext(Dispatchers.Default) {
                                val localMatches = musicRepository.search(searchQuery)
                                val q = searchQuery.trim().lowercase()
                                val cloudMatches = cloudTracks.filter {
                                    it.title.lowercase().contains(q) ||
                                    it.artist.lowercase().contains(q) ||
                                    it.album.lowercase().contains(q)
                                }
                                localMatches + cloudMatches
                            }
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = themeConfig.windowGapsDp.dp),
                            contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(themeConfig.windowGapsDp.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "SEARCH RESULTS: ${searchResults.size} MATCHES",
                                        color = themeConfig.accentColor,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "FILTER: \"$searchQuery\"",
                                        color = themeConfig.textSecondaryColor,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            if (searchResults.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 40.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.SearchOff,
                                                contentDescription = null,
                                                tint = themeConfig.textSecondaryColor.copy(alpha = 0.45f),
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Text(
                                                text = "NO TRACKS MATCHING \"$searchQuery\"",
                                                color = themeConfig.textSecondaryColor,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Check spelling or search by artist/album name",
                                                color = themeConfig.textSecondaryColor.copy(alpha = 0.6f),
                                                fontSize = 10.5.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(
                                    items = searchResults,
                                    key = { "search_${it.id}" },
                                    contentType = { "search_track_item" }
                                ) { track ->
                                    val isPlaying = playbackState.currentTrack?.id == track.id && playbackState.isPlaying
                                    val isCurrent = playbackState.currentTrack?.id == track.id

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .hyprTile(theme = themeConfig, isActive = isCurrent)
                                            .hyprBounceClick {
                                                audioPlayer.playTrack(track, searchResults)
                                            }
                                            .padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(themeConfig.surfaceVariantColor),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                HyprArtworkImage(
                                                    artworkUri = track.albumArtUri,
                                                    title = track.title,
                                                    artist = track.artist,
                                                    trackId = track.id,
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = track.title,
                                                        color = if (isCurrent) themeConfig.accentColor else themeConfig.textPrimaryColor,
                                                        fontSize = 13.5.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        modifier = Modifier.weight(1f, fill = false)
                                                    )
                                                    if (track.isCloudTrack) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(themeConfig.accentColor.copy(alpha = 0.2f))
                                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                                        ) {
                                                            Text(
                                                                text = "CLOUD",
                                                                color = themeConfig.accentColor,
                                                                fontSize = 9.sp,
                                                                fontFamily = FontFamily.Monospace,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "${track.artist} • ${track.album}",
                                                    color = themeConfig.textSecondaryColor,
                                                    fontSize = 11.5.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            if (isPlaying) {
                                                Icon(
                                                    imageVector = Icons.Default.GraphicEq,
                                                    contentDescription = null,
                                                    tint = themeConfig.accentColor,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Empty query initial helper state
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = themeConfig.windowGapsDp.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(themeConfig.accentColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = themeConfig.accentColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Text(
                                    text = "HYPR // SPOTLIGHT SEARCH",
                                    color = themeConfig.accentColor,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Search by track, artist, or album across local library & Telegram cloud",
                                    color = themeConfig.textSecondaryColor,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        }
                    }
                } else {
                    when (currentWorkspace) {
                        HyprWorkspace.HOME -> {
                            val heavyRotation = remember(tracks, musicRepository.playbackStatsRepository.playCountsChanged) {
                                musicRepository.getHeavyRotationTracks(10)
                            }

                            HomeScreen(
                                theme = themeConfig,
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
                            NowPlayingScreen(
                                theme = themeConfig,
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
                                onOpenEqualizer = { showEqualizerDialog = true },
                                telegramRepository = telegramRepository
                            )
                        }
                    }
                }
            }

            // Bottom Section: Search Runner, MiniPlayer & Waybar Bottom Dock
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding()
            ) {
                // Bottom Quick Search Runner
                if (isSearchActive) {
                    HyprBottomSearchRunner(
                        theme = themeConfig,
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onClose = {
                            isSearchActive = false
                            searchQuery = ""
                        }
                    )
                }

                // Persistent Floating Mini-Player Dock (dismissed during active search to maximize search viewport)
                if (playbackState.currentTrack != null && !isNowPlayingExpanded && currentWorkspace != HyprWorkspace.PLAYING && !isSearchActive) {
                    MiniPlayer(
                        theme = themeConfig,
                        playbackState = playbackState,
                        onPlayPause = { audioPlayer.togglePlayPause() },
                        onSkipNext = { audioPlayer.skipNext() },
                        onSkipPrevious = { audioPlayer.skipPrevious() },
                        onClick = { isNowPlayingExpanded = true },
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Bottom Hyprland Workspace Dock (dismissed during active search)
                if (!isSearchActive) {
                    HyprBottomDock(
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
                        },
                        onToggleSearch = { isSearchActive = !isSearchActive },
                        isSearchActive = isSearchActive,
                        isPlaying = playbackState.isPlaying
                    )
                }
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
