# HyprMusic v1.7.0 — Gesture Navigation Shield, Audiophile Turntable & Component Uniqueness Overhaul

HyprMusic v1.7.0 introduces full system-wide Android edge gesture navigation protection, an audiophile centerpiece overhaul featuring a dual-mode master vinyl turntable and digipak sleeve, an integrated custom playlist engine with multi-quadrant collage artwork, a floating glass bottom dock with kinetic spring indicators, an interactive floating island mini player with 33 RPM spinning vinyl record, depth-of-field kinetic lyrics typography, and machined telemetry rack settings.

---

## What's New in v1.7.0

### Intelligent Android Gesture Navigation & Hierarchical Dismissal Shield
* **Edge Swipe Back Protection**: Single-swipe back gestures on secondary or child screens now cleanly pop the current view (dismissing equalizer dialog, collapsing Now Playing player, closing search runner, or popping album/artist/playlist detail views) sequentially without terminating playback or abruptly closing the app.
* **Home Screen Double-Back Safety**: When on the primary Home workspace, edge swipe gestures are protected by a 2000ms double-tap threshold with terminal notification toast `[ PRESS BACK AGAIN TO EXIT ]`, preventing accidental app termination during navigation.
* **Child Screen Local Back Handlers**: `LibraryScreen` intercepts system back gestures locally inside Album, Artist, and Playlist views, returning to the catalog index smoothly.

---

### Audiophile Centerpiece: Master Turntable vs Digipak Sleeve
* **Dual-Mode Interactive Switcher**: Seamlessly toggle between direct-drive vinyl turntable and high-fidelity digipak sleeve presentation via screen tap or action chip.
* **Master Vinyl Turntable**:
  * 12-inch micro-grooved vinyl record rotating continuously at 33 1/3 RPM during playback with realistic light sheen.
  * Heavy direct-drive platter base with 36-point strobe perimeter speed calibration dots.
  * 4-inch center circular album art label with central spindle hole.
  * Precision tonearm assembly with pivot gimbal, counterweight, stainless steel tube, headshell cartridge, and needle stylus that tracks onto the groove when playing and smoothly parks when paused.
* **Digipak Sleeve Mode**:
  * Dual-layer zero-crop sleeve architecture with ambient reactive backdrop and fitted foreground jacket.
  * Beat-synchronized RMS audio pulsation.
  * Vinyl record peeking out of the right sleeve opening with grooved reflections.

---

### Zero-Crop Album Cover & Digipak Sleeve Engine
* **Dual-Layer Zero-Crop Architecture**:
  * Wide movie posters, horizontal film soundtrack covers, and non-square album art now render 100% of their imagery without cropped heads or sliced titles.
  * Ambient blurred backdrop scales with `ContentScale.Crop` at 35% alpha to eliminate empty black letterboxing.
  * Foreground jacket renders with `ContentScale.Fit` inside a true 1:1 vinyl sleeve aspect ratio.
  * Corner audio chip badge (`[ X TRK ]`) and tactile bounce feedback.

---

### Complete Playlists Engine & Management Screen
* **Dedicated Playlists Tab**: Added `PLAYLISTS` filter tab with live match count badges.
* **2-Column Playlists Grid**:
  * Hero card: `+ NEW PLAYLIST` (`mkplaylist <name>`).
  * Dynamic 4-Quadrant Artwork Collages: Automatically generated from tracks inside each playlist.
* **Playlist Detail Screen**:
  * Real-time aggregate duration calculation, Play All, Shuffle Mix, and Playlist Delete confirmation dialog.
  * Track rows with individual removal actions and direct audio launching.
* **`CreatePlaylistDialog`**:
  * Fast modal dialog for creating new playlists on demand.

---

### Detached Floating Glass Capsule Bottom Dock
* **Frosted Glass Styling**: Detached floating pill dock with vertical gradient translucency, subtle border highlights, and window gaps padding.
* **Kinetic Sliding Indicator Pill**: Active workspace highlighted with `animateDpAsState` spring-loaded width indicator pill.
* **Active Equalizer Tab**: The `PLAYING` tab features live dancing `MiniEqualizerBars` when playback is active.
* **Cybernetic Runner Button**: Dedicated tactile `run` launcher button triggering the search command palette.

---

### Floating Island MiniPlayer with 33 RPM Vinyl Record
* **Interactive 46dp Mini Vinyl Record**:
  * Concentric microgrooves drawn on Canvas with outer vinyl rim, spinning continuously at 33 RPM during playback.
  * Center circular album art label with central spindle hole.
* **Kinetic Drag-to-Skip Gestures**:
  * Interactive horizontal drag translation with physical spring resistance and haptic feedback.
  * Swiping past 70dp triggers previous or next track skip with instant spring-back return.
* **Luminous Audio Progress Filament**:
  * Ultra-thin gradient progress filament running along the bottom edge showing live playback progress.

---

### Depth-of-Field Kinetic Typography Lyrics
* **Kinetic Depth-of-Field**:
  * Active line magnified in bold typography, highlighted in active accent color, and housed in an illuminated spotlight card with live timecode badges (`[02:15]`).
  * Distant lines feature progressive opacity falloff (0.65f to 0.22f) for true spatial depth.
  * Direct seek: Tapping any lyric line seeks playback directly with tactile haptic feedback.
  * Automatic smooth scrolling centered on active lyrics.

---

### Quick-Search Runner Command Palette
* **Spotlight / Wofi Command Palette**: Monospace terminal prompt pill `find >`, active border accent glow, query clear action, and seamless keyboard dismissal.

---

### Machined Telemetry Settings & Hardware Rack
* **Live Pipeline Telemetry Bar**: Real-time display of audio rendering engine (`AAudio PCM`), bit-depth and sample rate (`44.1kHz / 16b`), studio headroom (`+0.0dB Direct`), and driver state (`Bit-Perfect`).
* **Gesture System Monitoring Card**: Documents and verifies the Android edge gesture dismissal shield and double-back exit safety mechanism.

---

## Package Information
* **Version**: `v1.7.0`
* **Version Code**: `10`
* **Package Name**: `com.example.hyprmusic`
* **Target SDK**: Android 16 (API 36)
* **Minimum SDK**: Android 10 (API 29)
* **License**: GNU General Public License v3.0

---

## Installation
Download the `HyprMusic-v1.7.0.apk` asset and install directly on your Android device (Android 10+), or use the built-in OTA update checker in Settings.
