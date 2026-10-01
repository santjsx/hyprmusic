# ⚡ HyprMusic v1.3.0 — Zero-Lag 120 FPS Engine, Audiophile Sleep Timer, Playlists & Go to Album

HyprMusic v1.3.0 is a milestone performance and feature release. We completely dismantled the audio visualizer pipeline and recomposition tree to deliver locked 60–120 FPS fluid interactions across every screen, and introduced three highly requested features: an audiophile Sleep Timer with gradual volume fade-out, full Custom Playlist management, and 1-tap "Go to Album" navigation directly from the player.

---

## 🚀 Key Improvements in v1.3.0

### 🚀 120 FPS Zero-Lag Rendering Engine
* **Draw-Phase Canvas Spectrum Visualizer**: Replaced 16 individual Compose spring-animated boxes with a single, hardware-accelerated `Canvas` draw phase. Visualizer amplitudes now update directly on the GPU, completely bypassing the Compose composition and layout passes (0 recompositions).
* **Isolated Dock & Mini-Player Equalizers**: Migrated `MiniEqualizerBars` to an ultra-lightweight 13x14dp Canvas with zero animation allocations, keeping the Waybar dock and MiniPlayer completely idle during playback.
* **Draw-Phase Album Art Breathing**: Moved album art beat-reactive elevation into `Modifier.graphicsLayer` lambda execution, eliminating dozens of full-screen recompositions per second.
* **Throttled PCM Buffer Processor**: Capped PCM audio spectrum analysis to ~30 FPS (33ms interval) to prevent StateFlow contention and audio thread backpressure.
* **Hardware Bitmap Acceleration & Coil Memory Cache**: Configured Coil `ImageLoader` with `Bitmap.Config.HARDWARE`, dedicated 25% RAM cache, and 100MB disk cache for silky-smooth list scrolling.
* **Asynchronous Disk I/O**: Offloaded `MusicRepository` track metadata serialization and cache writing to a background `Dispatchers.IO` coroutine scope, eliminating UI thread hitching on favorites and library scans.

---

### 🌙 Audiophile Sleep Timer
* **Quick Presets**: Instant 1-tap timers for 15, 30, 45, and 60 minutes.
* **End of Current Track**: Intelligently halts playback exactly when the active song finishes, ideal for bedtime listening.
* **Custom Duration Slider**: Seamlessly set any timer from 5 to 120 minutes with a single touch.
* **15-Second Gentle Volume Fade-Out**: Rather than abruptly cutting audio, the player smoothly ramps volume down from 100% to 0% over the final 15 seconds (or 8 seconds for End-of-Track) before pausing.
* **Extensible & Live Counter**: View the live monospaced countdown `[00:14:32]` directly on the player screen or dialog, with quick `+5 MIN` and `+15 MIN` extension chips.
* **Safe Volume Restoration**: Volume is cleanly restored to 100% upon expiration or cancellation so your next playback session is never muted.

---

### 📑 Custom Playlists
* **Instant Add to Playlist Modal**: Accessible via the `Icons.AutoMirrored.Filled.PlaylistAdd` icon in the player header or bottom action bar.
* **Create On-The-Fly**: Create brand-new custom playlists inline without leaving the player screen.
* **Reactive Membership Toggling**: Tap any playlist to add or remove the track instantly with tactile haptic feedback and real-time checkmark state.
* **Asynchronous JSON Persistence**: Playlists are persisted to `playlists.json` via background coroutines on `Dispatchers.IO`.

---

### 💿 "Go to Album" Navigation
* **1-Tap Player Header Action**: Tap the album title or the album icon in the player header bar to instantly jump to the album's detail view.
* **Dedicated Bottom Action Chip**: Added an "ALBUM" quick chip in the player action bar.
* **Seamless Backstack Integration**: Directs the app to `[2:lib]` (Library) and opens `AlbumDetailView`, while preserving seamless navigation back to the library view.

---

## 📦 Package Information
* **Version**: `v1.3.0`
* **Version Code**: `5`
* **Package Name**: `com.example.hyprmusic`
* **Target SDK**: Android 16 (API 36)
* **Minimum SDK**: Android 10 (API 29)
* **License**: GNU General Public License v3.0

---

## 🛠️ Installation
Download the `HyprMusic-v1.3.0.apk` asset and install directly on your Android device (Android 10+), or use the built-in OTA update checker in Settings (`[4:rice]`).
