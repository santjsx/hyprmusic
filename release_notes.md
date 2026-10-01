# HyprMusic v1.8.0 — Audiophile Tonearm Physics, Physical Digipak Sleeve & Quick Actions Overhaul

HyprMusic v1.8.0 delivers an authentic physical audiophile centerpiece overhaul, a complete rewrite of the Home screen Audio Engine quick actions, album art restoration across all terminal theme rows, and a decluttered Settings page.

---

## What's New in v1.8.0

### Ultra-Realistic Master Vinyl Turntable Centerpiece
* **Fixed-Pivot Tonearm Physics**: Replaced container-level canvas rotation with a mathematically anchored pivot at `(width * 0.85f, height * 0.17f)` using DrawScope rotation around the plinth bearing base. The tonearm pivot stays permanently anchored to the turntable body.
* **Lead-In to Lead-Out Needle Tracking**: When playback is paused or stopped, the tonearm smoothly parks at `-12°` on the physical cueing arm-rest clip. While playing, the tonearm dynamically interpolates from `7°` (lead-in groove) to `20°` (lead-out runoff), tracking song progress strictly across the sound grooves and never piercing the circular center label.
* **Audiophile S-Curve Aluminum Tube**: Rendered an authentic cubic Bezier S-curved tonearm tube with polished chrome reflection gradient, rear knurled counterweight cylinder, angled headshell, phono cartridge body with accent highlight, and diamond stylus tip.
* **Platter Stroboscopic Detailing**: Features a recessed platter well with radial drop-shadow, heavy cast aluminum bevel rim with 4 rings of strobe calibration dots, and an anisotropic bowtie/butterfly optical reflection sheen across the rotating vinyl microgrooves.

---

### Tangible Physical Gatefold Digipak Sleeve
* **Authentic Cardboard Jacket**: Eliminated harsh wireframe neon borders in favor of soft ambient underglow, an authentic physical book-spine fold crease highlight on the left, and an open pocket edge on the right.
* **Sliding 12-Inch Vinyl LP**: Features an authentic vinyl disc that slides out from the sleeve pocket (58dp when playing with subtle RMS sway, 36dp when paused), showcasing grooved optical reflection sweep gradients and a circular center album artwork label with center spindle pin.

---

### Home Screen Audio Engine Quick Actions Overhaul
* **Play / Pause Button**:
  * Added fallback support for idle or newly launched states: clicking `PLAY` when no track is actively loaded automatically initiates playback with the first track from the indexed library rather than failing silently.
  * Hardened ExoPlayer resume logic against `STATE_IDLE` and `STATE_ENDED` edge conditions, ensuring instantaneous playback recovery.
  * Enhanced with `hyprBounceClick` and active state borders.
* **Shuffle Button**:
  * Implemented `playRandomMix`, which shuffles the entire library queue, persists `isShuffle = true` to the playback state, and starts audio immediately. Subsequent track skips remain in shuffle mode throughout the session.
  * Added visual active-state tracking: when shuffle is active, the button renders `spec.borderActive`, an active background fill, and accent-tinted iconography.
  * Tactile bounce feedback and a brief confirmation toast (`Shuffled X tracks`).
* **Rescan Button**:
  * Wired full observation of `isScanning` into the terminal rows home layout.
  * Added a smooth 360-degree rotation animation on the refresh icon while scanning is underway, accompanied by `SCANNING...` status typography.
  * Implemented atomic concurrency guards in `MusicRepository.scanLocalMedia()` to eliminate redundant background scans.
  * Interactive click-lock disables the button during an active scan to prevent double-tap storming.
  * Displays a completion toast confirming indexed track counts.
* **Dynamic Audio Engine Badge**:
  * The top-right badge now dynamically displays `[ SCANNING ]` during media indexing, `[ PLAYING ]` when streaming audio, `[ PAUSED ]` when paused with loaded media, and `[ IDLE ]` when stopped.

---

### Universal Album Art in Terminal Layouts
* **TerminalRowsHomeScreen Album Art Jackets**: Added a 38x38dp rounded album artwork jacket thumbnail with cached Coil loading, boundary border, and active mini equalizer bar overlay to `TerminalRowsHomeScreen`. Emerald Matrix, Monokai Pro, and all terminal theme layouts now display album art alongside track indices.

---

### Settings Page Cleanup
* **Eliminated Architecture Slop**: Removed the verbose `GESTURE SYSTEM // NAVIGATION` card from the Settings screen. The page now flows cleanly and directly from the Engine telemetry header into `Audio DSP // Equalizer`.

---

## Technical Specifications & Compatibility
* **Minimum Android Version**: Android 10.0 (API level 29)
* **Target Android Version**: Android 16.0 (API level 36)
* **Architecture**: Jetpack Compose 100% Kotlin Coroutines, ExoPlayer Media3, StateFlow Architecture
* **Version**: `v1.8.0` (Build 11)

---

## Installation
Download the `HyprMusic-v1.8.0.apk` asset and install directly on your Android device (Android 10+), or use the built-in OTA update checker in Settings.
