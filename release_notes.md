# HyprMusic v1.5.0 — Modern Brand Identity & Clean Professional Architecture

HyprMusic v1.5.0 delivers an official brand identity overhaul and a refined, professional design architecture. This release establishes a unified visual standard across all icon sets (Lucide, Phosphor, Remix), introduces responsive progress bar engines, eliminates all emojis and terminal ASCII artifacts in favor of clean digital typography, and equips the application with native adaptive vector launcher icons and high-density WebP mipmaps.

---

## What's New in v1.5.0

### Official Brand Identity & Design System
* **Wayland Pulse Dock & Concentric Vinyl**: New official emblem fusing audiophile vinyl geometry with Wayland's four compositor workspaces (`[1:home]`, `[2:lib]`, `[3:player]`, `[4:rice]`), dynamic 10px gaps, and an active vector play core.
* **Full Multi-Resolution Family**: Master vector assets (`logo.svg`, `icon.svg`) and high-fidelity PNG exports spanning 16px to 2048px for favicons, notification badges, headers, and social previews.
* **Native Android Adaptive Launcher Icons**: Replaced default robot drawables with hardware-accelerated vector XMLs (`ic_launcher_foreground.xml`, `ic_launcher_background.xml`) on an OLED dark backdrop (`#11111B`).
* **Multi-Density Mipmaps**: Fully refreshed square and circular WebP launcher bitmaps across all DPI tiers (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`).

---

### Clean Icon Architecture & Modern Progress Engines
* **Vector Icon Packs**: Integrated authentic icon designs inspired by:
  * `Lucide`: Clean 2px stroke line geometry.
  * `Phosphor`: Balanced, rounded outline vectors.
  * `Remix`: High-contrast solid UI glyphs.
* **Responsive Progress Bar Engines**:
  * `Capsule Seeker`: 5dp rounded track with a smooth 12dp tactile thumb and continuous horizontal drag/tap gestures.
  * `Minimal Line`: High-density linear track with edge-to-edge scrubbing.
  * `Dynamic Glow`: High-contrast accent slider with responsive glowing feedback.
* **High Density Audio Table**: Transformed the terminal view into a clean, modern audio table featuring vector playback controls and dynamic equalizer indicators.
* **Strict Aesthetic Polish**: Removed all emojis (`⚡`, `★`, `✔`, etc.) and ASCII text bars (`[████░░░]`) across the application, status indicators, and documentation.

---

### 120 FPS Zero-Lag Audio Engine
* **Draw-Phase Canvas Spectrum Visualizer**: Hardware-accelerated GPU canvas rendering with zero Compose layout/recomposition passes.
* **Isolated MiniEqualizers**: Zero-allocation Canvas equalizers keeping navigation docks and the MiniPlayer completely idle during playback.
* **Draw-Phase Album Art Breathing**: GPU-level graphicsLayer elevation eliminating full-screen recompositions.
* **Throttled PCM Buffer Processor**: 30 FPS analysis interval preventing audio thread backpressure and state contention.
* **Hardware Bitmap Recycling**: Coil `ImageLoader` with `Bitmap.Config.HARDWARE`, dedicated 25% RAM cache, and 100MB disk cache.
* **Asynchronous Disk I/O**: Background `Dispatchers.IO` coroutine scope for track metadata serialization and library scanning.

---

### Audiophile Sleep Timer
* **Quick Presets**: 15, 30, 45, and 60-minute instant timer chips.
* **End of Current Track**: Clean playback stop when the active song finishes.
* **Custom Duration Slider**: Smooth duration adjustment from 5 to 120 minutes.
* **15-Second Gentle Volume Fade-Out**: Natural volume taper from 100% to 0% prior to pausing.
* **Live Monospace Counter**: Dynamic countdown display with quick `+5 MIN` and `+15 MIN` extension options.

---

### Custom Playlists & Direct Navigation
* **Inline Playlist Creation**: Create and name new playlists directly from the playback modal without interrupting listening.
* **Reactive Membership Toggling**: Instant track addition and removal with tactile haptic feedback.
* **Direct "Go to Album" Navigation**: 1-tap jump from the active track header to its library album view.

---

## Package Information
* **Version**: `v1.5.0`
* **Version Code**: `7`
* **Package Name**: `com.example.hyprmusic`
* **Target SDK**: Android 16 (API 36)
* **Minimum SDK**: Android 10 (API 29)
* **License**: GNU General Public License v3.0

---

## Installation
Download the `HyprMusic-v1.5.0.apk` asset and install directly on your Android device (Android 10+), or use the built-in OTA update checker in Settings.
