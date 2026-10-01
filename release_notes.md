# HyprMusic v1.9.1 — Studio-Grade Hardware Acoustic Profiles & Studio Mastering Equalizer

HyprMusic v1.9.1 introduces studio-grade hardware acoustic profile matrices to the Dolby Mastering Engine, pairing dynamic frequency response mapping and profile-calibrated headroom pre-cut gain with an overhaul of the Studio Equalizer interface.

---

## What's New in v1.9.1

### Hardware-Targeted Acoustic Profile Matrices (DolbyPresetProfile)
* **Cinema Soundstage (`HOME_THEATER`)**:
  * Designed for external speakers, soundbars, and multi-channel systems.
  * Spatial room boundary expanded to `780` for a broad, three-dimensional acoustic soundstage.
  * Calibrated sub-bass boost (`+7.0 dB`) and mid-bass punch (`+3.0 dB`) with `0.65f` (-3.7 dB) headroom pre-cut gain to maintain distortion-free dynamic peaks.
* **Cabin Acoustic (`CAR_AUDIO`)**:
  * Engineered specifically for automobile interior acoustics and road vibration compensation.
  * Sub-bass rumble elevated to `+8.0 dB` to overcome tire and engine rumble, paired with an upper-mid boost (`+3.0 dB`) to preserve vocal articulation against wind noise.
  * Spatial boundary restricted to `250` for focused imaging inside cabin enclosures; safe headroom pre-cut set to `0.62f` (-4.1 dB).
* **Intimate Buds (`IN_EAR_BUDS`)**:
  * Acoustically tuned for in-ear monitors (IEMs) and true wireless earbuds.
  * Introduces a surgical lower-mid scoop (`-1.5 dB`) across the 250Hz - 500Hz region to eliminate enclosed skull resonance and ear-canal boxiness.
  * Air and brilliance elevated (`+3.5 dB`), spatial width set to `320`, with `0.75f` (-2.5 dB) headroom pre-cut.
* **Studio Reference (`OVER_EAR_HEADPHONES`)**:
  * Tuned for open-back and closed-back circumaural studio headphones.
  * Delivers a neutral, transparent mid-range with airy top-end brilliance (`+6.0 dB`) and subtle sub-bass extension (`+4.0 dB`).
  * Expansive spatial strength (`550`) with `0.70f` (-3.1 dB) headroom pre-cut for mastering-grade fidelity.
* **Midnight Cinema (`NIGHT_LOUDNESS`)**:
  * Optimized for late-night listening without abrupt volume spikes or muddy dialogue.
  * Boosts center mid-range dialogue (`+3.5 dB`) while reigning in sub-bass impact (`+2.0 dB`) and preserving upper clarity.
  * Headroom pre-cut set to `0.80f` (-1.9 dB) with `400` spatial room strength.

---

### Dynamic Frequency Response & Safe Headroom Routing
* **Center Frequency Interpolation**: Maps each profile's precise acoustic offsets dynamically against the Android device's physical hardware equalizer bands (from 31Hz sub-bass up to 16kHz brilliance).
* **Profile-Specific Dynamic Headroom Pre-Cut**: Core playback volume dynamically adapts to the selected acoustic profile's required attenuation factor, preventing inter-sample clipping and digital distortion regardless of hardware EQ boost amplitudes.
* **Virtualizer Room Sync**: Seamlessly syncs hardware audio virtualizer spatial width with the active profile's acoustic environment parameters.

---

### Studio Equalizer Panel UI Overhaul
* **Acoustic Environment Card Carousel**:
  * Horizontal scrolling carousel featuring dedicated interactive cards for all 5 acoustic environments.
  * Live telemetry badges for each profile showing room boundary strength (`ROOM: <val>`), safe pre-cut gain (`GAIN: <val>x`), and sub-bass emphasis (`SUB: +<val>dB`).
  * Smooth theme-aware selection border animations and active state indicators.
* **Dolby Mastering Hero Selector**:
  * Elevated hero card displaying active profile naming, active acoustic telemetry (`ACOUSTICS: <profile>`), and safe headroom readouts (`HEADROOM: <gain>x SAFE`).
  * One-tap toggle to immediately engage or disengage the mastering engine.
  * One-touch matrix re-apply action button to instantly realign all bands to the active hardware profile target.

---

## Technical Specifications & Compatibility
* **Minimum Android Version**: Android 10.0 (API level 29)
* **Target Android Version**: Android 16.0 (API level 36)
* **Architecture**: Jetpack Compose 100% Kotlin Coroutines, ExoPlayer Media3, StateFlow Architecture
* **Version**: `v1.9.1` (Build 13)

---

## Installation
Download the `HyprMusic-v1.9.1.apk` asset and install directly on your Android device (Android 10+), or use the built-in OTA update checker in Settings.
