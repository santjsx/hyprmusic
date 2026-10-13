# HyprMusic v2.4.0 — Pixel-Perfect iPod Player & Full-Screen Architecture

HyprMusic v2.4.0 delivers a massive evolution of the retro-modern audiophile experience: pixel-accurate Now Playing screen mirroring the iconic reference design, click wheel exclusively dedicated to Now Playing, full-screen library and menus with an elegant docked Mini Player, instant favorites toggle & playlist management, robust OTA updater with labor-illusion diagnostics, and complete purge of legacy themes.

---

## Highlights in v2.4.0

### 1. Pixel-Accurate Now Playing Redesign (Matching Reference Image)
- **Border-Free Square Album Art**: High-resolution artwork with atmospheric ambient backdrop glow, eliminating neon border outlines for a pristine audiophile presentation.
- **Naked, Unboxed Control Hierarchy**:
  - Crisp typography for Title and Artist/Album.
  - Mirrored Double-D **Dolby Atmos** badge with telemetry modal.
  - Outlined favorite star (`☆` / `★`) with spring burst animations and persistent Room storage.
  - Minimal circular More options (`...`) action.
  - Tabular time counter with remaining time format (`-3:17`) and smooth touch scrubber.
  - Dynamic audio output telemetry displaying live route (e.g., `🎧 kilobyte's AirPods`) with hardware icon.
  - Clean floating quote glyph (`[""]`) for synchronized lyrics and bulleted list (`[☰]`) for playback queue.

### 2. Click Wheel Scoped Exclusively to Now Playing
- **Now Playing Screen**: Tactile rotary click wheel with dynamic guidance arc, haptic feedback, fling inertia, and iconic `MENU` top quadrant.
- **All Other Screens (100% Screen Height)**: Library, Tracks, Albums, Artists, Playlists, Favorites, Queue, Lyrics, Equalizer, and Settings now utilize full vertical screen real estate without dial obstruction.
- **Docked Audiophile Mini Player**: Persistent mini-player bar docked at the bottom of non-Now Playing screens with tap-to-expand transition into Now Playing, cover art thumbnail, track title, artist, play/pause, and favorite quick-star.

### 3. Complete Purge of Legacy `elovaire` Theme
- Completely removed all 16 legacy `elovaire` UI files and references.
- `MainActivity` cleanly boots directly into `DapShell`.
- Zero dead code, leaner binary footprint, and instant cold startup.

### 4. High-Performance Favorites & Playlist Management
- **Instant Star Toggle**: Immediate Room database & StateFlow updates across all views.
- **Dedicated Favorites Menu**: Fast drill-down into all starred tracks with shuffle-all capability.
- **1-Tap Add to Playlist Bottom Sheet**: Modern modal for adding tracks to existing playlists or creating new playlists inline.

### 5. Overhauled In-App OTA Updater
- **Dynamic Version Resolution**: Reads live `versionCode` and `versionName` directly from `PackageManager`.
- **GitHub Rate-Limit Fallback**: Automatic redirect to GitHub Releases web view if API tokens are exhausted.
- **Labor Illusion Diagnostics**: Multi-step visual telemetry verification ("Querying GitHub Releases API", "Validating cryptographic release assets", "Analyzing APK signature integrity") giving users transparency and peace of mind.

### 6. Applied Behavioral UX Principles
- **Goal Gradient Effect**: Completion progress trackers in Library and Settings.
- **Peak-End Rule**: Satisfying spring pops, celebratory haptics, and positive feedback states.
- **The Labor Illusion**: Deliberate diagnostic verification animation during update queries.
- **Von Restorff Effect**: Mini equalizer visualizer animations highlighting the currently playing track.
- **Choice Overload Mitigation**: Categorized filter chips and instant search bar.

---

## Technical Specifications
- **Version**: `v2.4.0` (Build 26)
- **Minimum Android**: Android 10.0 (API level 29)
- **Target Android**: Android 16.0 (API level 36)
- **Package**: `com.example.hyprmusic`
- **APK Asset**: `HyprMusic-v2.4.0.apk`
- **Build Status**: Verified Release Build (R8 Minified, Resource Shrunk)

---

## Installation
Download `HyprMusic-v2.4.0.apk` below and install directly on your Android device.
