# HyprMusic v1.4.0 — Dynamic Adaptive Design System & Modular Architecture

HyprMusic v1.4.0 introduces a refined, professional **Structurally Adaptive Design System** driven by a unified `CompositionLocal` architecture (`HyprDesignSpec`). Users can dynamically customize every layer of the interface: Icon Pack architecture (Lucide, Phosphor, Remix), Progress Bar visual engines (Capsule Seeker, Minimal Line, Dynamic Glow), Home Grid layouts, and Monospace typography engines with instant, live reactive feedback.

---

## What's New in v1.4.0

### Structurally Adaptive Design System
* **Unified Design Contract (`HyprDesignSpec`)**: Centralized design tokens across color palettes, window geometry, squircle radiuses, active border gradients, and typography engines.
* **Component-Level Modular Customization**:
  * **Icon Pack Architecture**:
    * `Lucide`: Clean, minimalist 2px vector line geometry inspired by lucide.dev.
    * `Phosphor`: Balanced, rounded outline vector glyphs inspired by phosphoricons.com.
    * `Remix`: Solid, high-contrast UI vectors inspired by remixicon.com.
  * **Progress Bar Engines**:
    * `Capsule Seeker`: Smooth rounded track with a 12dp tactile scrubber thumb and continuous horizontal drag/tap gestures.
    * `Minimal Line`: High-density linear track with edge-to-edge scrubbing.
    * `Dynamic Glow`: High-contrast slider with responsive glowing accents.
  * **Home Grid Layout Architecture**:
    * `Asymmetric Bento Tiles`: Modern visual hierarchy featuring a Hero Now Playing card, dual telemetry tiles, and Heavy Rotation horizontal carousel.
    * `High Density Table`: Fast, high-density audio track table rendering hundreds of songs at locked 120 FPS.
  * **Typography Engines**:
    * `JetBrains Mono`: Developer-first monospace typeface.
    * `IBM Plex Mono`: Industrial mid-century console font.
    * `System Monospace`: Native Android monospace engine.
    * `Minimal Sans`: Ultra-sleek contemporary sans-serif font.
* **Live Interactive Preview**: Themer screen embeds real-time interactive playback controls and progress scrubber widgets to preview style changes instantly.

---

### 120 FPS Zero-Lag Rendering Engine
* **Draw-Phase Canvas Spectrum Visualizer**: Replaced individual Compose animated views with a single, hardware-accelerated `Canvas` draw phase. Visualizer amplitudes update directly on the GPU, completely bypassing Compose layout and composition passes (0 recompositions).
* **Isolated Equalizers**: Migrated `MiniEqualizerBars` to an ultra-lightweight Canvas with zero allocations, keeping the navigation dock and MiniPlayer completely idle during playback.
* **Draw-Phase Album Art Breathing**: Moved album art beat-reactive elevation into `Modifier.graphicsLayer` lambda execution, eliminating redundant full-screen recompositions.
* **Throttled PCM Buffer Processor**: Capped PCM audio spectrum analysis to ~30 FPS (33ms interval) to prevent StateFlow contention and audio thread backpressure.
* **Hardware Bitmap Acceleration & Coil Cache**: Configured Coil `ImageLoader` with `Bitmap.Config.HARDWARE`, dedicated 25% RAM cache, and 100MB disk cache for silky-smooth list scrolling.
* **Asynchronous Disk I/O**: Offloaded `MusicRepository` track metadata serialization and cache writing to a background `Dispatchers.IO` coroutine scope, eliminating UI thread hitching on library scans and playlist mutations.
* **R8 Minification & Resource Shrinking**: Full release optimization with custom ProGuard rules and baseline profiles for fast startup and minimal APK size.

---

### Audiophile Sleep Timer
* **Quick Presets**: Instant 1-tap timers for 15, 30, 45, and 60 minutes.
* **End of Current Track**: Halts playback cleanly when the active song finishes, ideal for bedtime listening.
* **Custom Duration Slider**: Seamlessly set any timer from 5 to 120 minutes with a single touch.
* **15-Second Gentle Volume Fade-Out**: Smoothly ramps volume down from 100% to 0% over the final 15 seconds (or 8 seconds for End-of-Track) before pausing.
* **Live Monospace Counter**: Live countdown display with quick `+5 MIN` and `+15 MIN` extension chips.
* **Safe Volume Restoration**: Volume is cleanly restored to 100% upon expiration or cancellation.

---

### Custom Playlists & Direct Navigation
* **Instant Add to Playlist Modal**: Accessible from the player header bar and action chips.
* **Create On-The-Fly**: Create brand-new custom playlists inline without leaving playback.
* **Reactive Membership Toggling**: Tap any playlist to add or remove tracks instantly with haptic feedback.
* **"Go to Album" Navigation**: 1-tap navigation directly from the player header or album chip to jump straight to the album's detail view in the library.

---

## Package Information
* **Version**: `v1.4.0`
* **Version Code**: `6`
* **Package Name**: `com.example.hyprmusic`
* **Target SDK**: Android 16 (API 36)
* **Minimum SDK**: Android 10 (API 29)
* **License**: GNU General Public License v3.0

---

## Installation
Download the `HyprMusic-v1.4.0.apk` asset and install directly on your Android device (Android 10+), or use the built-in OTA update checker in Settings.
