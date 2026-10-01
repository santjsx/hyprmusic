# HyprMusic v1.9.2 — Dual-Tier Studio Equalizer Suite, Zero-Scroll Architecture & Telemetry HUD

HyprMusic v1.9.2 introduces a complete overhaul of the Studio Equalizer & DSP Mastering Suite, establishing a dual-tier architecture that cleanly separates one-tap smart acoustic environments from surgical pro console controls while eliminating all vertical scrolling, touch gesture conflicts, and background transparency bleed.

---

## What's New in v1.9.2

### Dual-Tier Segmented Architecture (`SMART ACOUSTIC` vs `PRO CONSOLE`)
* **Segmented Mode Switcher**:
  * Seamlessly toggle between casual one-tap hardware optimization and audiophile surgical tuning via an animated, low-latency top segmented control.
* **Smart Acoustic Mode (Easy to Perfect)**:
  * Compact hardware environment grid presenting all 5 hardware profiles (`Intimate Buds`, `Studio Reference`, `Cabin Acoustic`, `Cinema Soundstage`, `Midnight Cinema`) with acoustic benefits and headroom gain badges.
  * Direct one-tap profile selection dynamically updates hardware DSP, pre-cut attenuation, and spatial parameters.
  * Dual macro dynamics sliders: `SUB-BASS PUNCH` (0% to 100%) and `SPATIAL ROOM` (0% to 100%).
  * One-touch `RE-APPLY MATRIX` action button to realign device bands to the calibrated target.
* **Pro Console Mode (Audiophile Precision)**:
  * Dynamic Frequency Response Curve canvas (54dp) rendering real-time spline curves with reference 0dB line, ±6dB reference grid, and theme-colored area fill.
  * 5-band vertical studio console faders (152dp) with individual dB gain readouts (`+6dB`, `0dB`, `-2dB`), center detent reference ticks, and center frequency labels.
  * Genre preset curve strip featuring quick-selection chips (`FLAT`, `DOLBY CINEMA`, `SPATIAL THEATER`, `VOCAL CLARITY`, `CLUB RUMBLE`, `POP`, `ROCK`, `ELECTRONIC`).
  * Dedicated `RESET ALL FADERS TO FLAT (0 dB)` action button.

---

### Zero-Scroll Design & Touch Ergonomics
* **Zero Dialog Scrolling**:
  * Removed internal vertical scrolling from the dialog. Both tabs fit comfortably within the viewport height without scrolling.
* **Touch Isolation**:
  * Dragging vertical faders or horizontal sliders operates with complete gesture isolation, preventing accidental dialog scroll jumps.

---

### Single Master Power Switch & Visual Polish
* **Unified Master Header**:
  * Replaced dual-power switch ambiguity with a single master toggle (`HYPR AUDIO DSP // MASTER`).
  * Features live status readout: `BIT-PERFECT DIRECT PASSTHROUGH` when off, or `${PROFILE} // DOLBY 3D ACTIVE` when engaged.
* **Solid Dialog Backplate**:
  * Enforced 100% opaque dialog surface backing (`theme.surfaceColor.copy(alpha = 1.0f)`), eliminating background text and button bleed from the underlying Now Playing screen.
* **Unified Telemetry HUD**:
  * Clean bottom telemetry bar displaying safe headroom margin (`HEADROOM: <gain>x SAFE` in green), dynamic range limiter state (`LIMITER: ARMED / PASS`), and active acoustic profile without text wrapping or collisions.

---

## Technical Specifications & Compatibility
* **Minimum Android Version**: Android 10.0 (API level 29)
* **Target Android Version**: Android 16.0 (API level 36)
* **Architecture**: Jetpack Compose 100% Kotlin Coroutines, ExoPlayer Media3, StateFlow Architecture
* **Version**: `v1.9.2` (Build 14)

---

## Installation
Download the `HyprMusic-v1.9.2.apk` asset and install directly on your Android device (Android 10+), or use the built-in OTA update checker in Settings.
