# ☁️ HyprMusic v1.2.0 — Telegram Personal Music Cloud (TPMC) Integration

HyprMusic v1.2.0 introduces native integration with Telegram Personal Music Cloud (TPMC) — turning private Telegram channels and bots into an infinite, free cloud music streaming and download system directly within HyprMusic.

---

## 🚀 Key Highlights in v1.2.0

### 📡 Direct HTTP 206 Partial Content Streaming via Media3 ExoPlayer
* **Byte-Range Seek & Scrub**: Streams FLAC, M4A, MP3, and WAV files directly from the TPMC server with HTTP 206 chunked byte-range requests.
* **500MB LRU Disk Cache**: Backed by Media3 `CacheDataSource` and `SimpleCache` with `StandaloneDatabaseProvider`. Loop playback and repeated listens consume zero extra data.
* **Cold-Start Wake Protection**: Automatic retry and Render wake-up handling for free-tier backend instances.

### 📥 One-Tap Direct MediaStore Background Downloads
* **Scoped Storage Audio Integration**: Direct stream-to-disk downloader saves tracks into `Music/HyprMusic` on Android 10+ (API 29+) with `IS_PENDING = 1` staging.
* **Instant Catalog Indexing**: Upon download completion, tracks immediately appear in local library without rescan or duplicate entries.
* **Real-Time Progress Tracking**: Micro-progress indicators display live download percentage (`45%`, `80%`) and status badges (`[SAVED]`).

### 🎛️ Dual-Workspace Library & Hyprland Terminal Tabs
* **Scope Switcher**: Switch seamlessly between `[ 0 : local (111) ]` and `[ 1 : tpmc cloud (N) ]` with zero layout thrashing.
* **Audio Fidelity Badges**: Clear visual differentiation for `FLAC 24-bit`, `320kbps High Fidelity`, `M4A`, and lossless tracks.
* **Telegram Cloud Settings**: Dedicated configuration suite in `[4:rice]` with live server health test ping (`● TPMC ONLINE: @bot`), user ID mapping, and API key token authentication.
* **Unified Global Search**: Bottom quick search runner simultaneously scans local storage and Telegram Cloud songs with dedicated `[CLOUD]` tags.

---

# 🎧 HyprMusic v1.1.0 — Architecture Overhaul & Audiophile Visualizer

HyprMusic v1.1.0 delivers major architectural upgrades, zero synthetic mock data, true background service survivability, ergonomic bottom navigation, deep Hyprland rice customization, and a studio-grade real-time PCM audio spectrum visualizer.

---

## 🚀 Key Highlights in v1.1.0

### 🔊 Real-Time PCM Spectrum Visualizer & Upright Cover Art
* **Zero Vinyl Distortion**: Completely removed square vinyl spinning animation. The high-resolution album cover art remains upright, crisp, and stable with subtle, spring-based beat-reactive scaling.
* **Song-Synchronized 16-Bar Spectrum Analyzer**: Direct PCM audio buffer analysis via custom Media3 `HyprVisualizerProcessor`. Visualizer bars jump and dance in 100% exact synchronization with real audio frequencies and beats.
* **Smart resting state**: When playback is paused or silent, bars smoothly decay to an elegant 4dp resting baseline with zero flickering.

### 🛡️ True Background Playback (Survives Minimizing & Recents Dismissal)
* **Self-Managed MediaSessionService**: `HyprPlaybackService` maintains a persistent foreground media notification running with `FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK`.
* **Recents Dismissal Survivability**: Overrode `onTaskRemoved()` in `HyprPlaybackService` to ensure audio playback continues seamlessly when the app is swiped away from Android Recents.
* **Decoupled Lifecycle**: `HyprAudioPlayer` is engineered as a thread-safe Singleton, preventing audio playback teardown during Activity destruction.

### 🚫 Complete Purge of Mock Tracks
* **Zero Fake Tracks on Startup**: Removed `DemoTracks.kt` completely. Library loads real local files asynchronously with zero synthetic fallback.
* **Hyprland Terminal Empty State**: Informative ASCII terminal box (`[ hypr-storage: 0 tracks ]`) when no audio files are present.

### ❤️ Persistent Favorites System & Real Heavy Rotation
* **Favorites Engine**: Backed by `SharedPreferences` via `FavoritesRepository`. Reactive heart toggles across NowPlaying, Library, and MiniPlayer.
* **Dedicated Favorites View**: Filter tracks instantly using the `[ FAVORITES ]` chip in the Library workspace.
* **Data-Driven Heavy Rotation**: `PlaybackStatsRepository` records listen counts and timestamps, computing true most-played tracks.

### 📱 Ergonomic Bottom Navigation & Quick Search
* **Bottom Hyprland Dock**: Relocated workspace switcher (`[1:home]`, `[2:lib]`, `[3:player]`, `[4:rice]`) to the bottom for effortless one-handed thumb interaction.
* **Integrated Search Runner**: Quick search launcher with instantaneous fuzzy filtering by title, artist, and album.
* **Linux Waybar Status Header**: Sleek top telemetry displaying active audio format, sample rate, bit depth (`16-BIT / 44.1k`), and active theme.

### 🎨 Deep Hyprland UI Customization
* **8 Curated Themes**: Tokyo Night, Catppuccin Mocha, Nordic Frost, Gruvbox Dark, OLED Cyberpunk, Dracula Void, Rose Pine, and Monokai Pro.
* **Window Geometry Controls**: Sliders for window gaps (0–24dp), border radius (0–28dp), border thickness (1–4dp), and OLED mode, persisting permanently across restarts.

---

## 📦 Package Information
* **Version**: `v1.1.0`
* **Package Name**: `com.example.hyprmusic`
* **Target SDK**: Android 16 (API 36)
* **Minimum SDK**: Android 10 (API 29)
* **License**: GNU General Public License v3.0

---

## 🛠️ Installation
Download the `HyprMusic-v1.1.0.apk` asset and install directly on your Android device (Android 10+).
