# ☁️ HyprMusic v1.2.0 — Telegram Personal Music Cloud (TPMC) Integration

HyprMusic v1.2.0 introduces native integration with Telegram Personal Music Cloud (TPMC) — turning private Telegram channels and bots into an infinite, free cloud music streaming and download system directly within HyprMusic.

---

## 🚀 Key Highlights in v1.2.0

### 📡 Direct HTTP 206 Partial Content Streaming via Media3 ExoPlayer
* **Byte-Range Seek & Scrub**: Streams FLAC, M4A, MP3, and WAV files directly from the TPMC server with HTTP 206 chunked byte-range requests.
* **500MB LRU Disk Cache**: Backed by Media3 `CacheDataSource` and `SimpleCache` with `StandaloneDatabaseProvider`. Loop playback and repeated listens consume zero extra data.
* **Cold-Start Wake Protection**: Automatic retry and Render wake-up handling for free-tier backend instances.
* **MTProto 128KB Chunk Alignment**: Server chunk requests strictly satisfy Telegram MTProto specification constraints for maximum streaming throughput and reliability.

### 📥 One-Tap Direct MediaStore Background Downloads
* **Scoped Storage Audio Integration**: Direct stream-to-disk downloader saves tracks into `Music/HyprMusic` on Android 10+ (API 29+) with `IS_PENDING = 1` staging.
* **Instant Catalog Indexing**: Upon download completion, tracks immediately appear in local library without rescan or duplicate entries.
* **Real-Time Progress Tracking**: Micro-progress indicators display live download percentage (`45%`, `80%`) and status badges (`[SAVED]`).

### 🎛️ Dual-Workspace Library & Hyprland Terminal Tabs
* **Scope Switcher**: Switch seamlessly between `[ 0 : local (N) ]` and `[ 1 : tpmc cloud (N) ]` with zero layout thrashing.
* **Audio Fidelity Badges**: Clear visual differentiation for `FLAC 24-bit`, `320kbps High Fidelity`, `M4A`, and lossless tracks.
* **Telegram Cloud Settings**: Dedicated configuration suite in `[4:rice]` with live server health test ping (`● CONNECTED`), user ID mapping, and API key token authentication.
* **Unified Global Search**: Bottom quick search runner simultaneously scans local storage and Telegram Cloud songs with dedicated `[CLOUD]` tags.

### 🔊 Real-Time PCM Spectrum Visualizer & Upright Cover Art
* **Song-Synchronized 16-Bar Spectrum Analyzer**: Direct PCM audio buffer analysis via custom Media3 `HyprVisualizerProcessor`. Visualizer bars jump and dance in 100% exact synchronization with real audio frequencies and beats.
* **Zero Vinyl Distortion**: Album artwork remains crisp, upright, and stable with subtle, spring-based beat-reactive scaling.
* **Smart Resting State**: Smooth decay to a resting baseline when playback is paused.

### 🛡️ True Background Playback (Survives Minimizing & Recents Dismissal)
* **Self-Managed MediaSessionService**: `HyprPlaybackService` maintains a persistent foreground media notification running with `FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK`.
* **Recents Dismissal Survivability**: Audio playback continues uninterrupted even when the app is swiped away from Android Recents.

### 📱 Ergonomic Bottom Navigation & Hyprland Ricing
* **Bottom Hyprland Dock**: Workspace switcher (`[1:home]`, `[2:lib]`, `[3:player]`, `[4:rice]`) positioned for thumb ergonomics.
* **8 Curated Themes**: Tokyo Night, Catppuccin Mocha, Nordic Frost, Gruvbox Dark, OLED Cyberpunk, Dracula Void, Rose Pine, and Monokai Pro.
* **Window Geometry Controls**: Custom window gaps (0–24dp), border radius (0–28dp), border thickness (1–4dp), and true OLED black mode.

---

## 📦 Package Information
* **Version**: `v1.2.0`
* **Version Code**: `3`
* **Package Name**: `com.example.hyprmusic`
* **Target SDK**: Android 16 (API 36)
* **Minimum SDK**: Android 10 (API 29)
* **License**: GNU General Public License v3.0

---

## 🛠️ Installation
Download the `HyprMusic-v1.2.0.apk` asset and install directly on your Android device (Android 10+).
