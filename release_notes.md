# HyprMusic v2.0.0 — Complete Audiophile Now Playing Redesign

HyprMusic v2.0.0 delivers a complete, ground-up UI/UX redesign of the Now Playing experience. Eliminating visual chrome, bulky cards, and skeuomorphic elements, v2.0.0 introduces a modern, cinematic, and minimalist interface inspired by high-end standalone music players with subtle modern styling.

---

## Highlights in v2.0.0

### 1. 4-Layer Cinematic Ambient Background
* **Dark Base Layer**: Deep `#0B0A12` foundation with a dark translucent scrim guaranteeing high readability and zero text clashing.
* **Ambient Artwork Diffusion**: Real-time heavily blurred artwork backdrop using hardware-accelerated `RenderEffect` on Android 12+ with smooth crossfading on track transitions.
* **Subtle Perimeter Vignette**: Soft radial darkening that draws complete visual focus to the centerpiece artwork.

### 2. Centered Circular Album Artwork & Radial Playback Progress
* **Pure Circular Centerpiece**: Completely removes vinyl grooves, record discs, and skeuomorphic textures in favor of high-resolution circular album artwork rendered via Coil with 4-tier caching and fallback.
* **Canvas Radial Progress Arc**: Cradles the lower half and sides of the circular artwork with an interactive gradient arc (Pink `#FF5F8F` → Magenta `#D84BC4` → Violet `#9868FF`).
* **Subtle Outer Tick Marks**: 36 delicate radial tick marks providing analog visual precision without clutter.
* **Dual-Layer Progress Knob**: Circular white thumb knob with an inner violet center point positioned directly on the progress arc.
* **Interactive Radial Seeking**: Smooth tap and drag gestures along the radial arc calculate precise timestamps and seek playback in real time.

### 3. Integrated Audio Volume Controls
* **Flanking Volume Controls**: Minimal outline Mute icon on the left and Volume icon on the right surrounding the radial arc.
* **Native Audio Stream Integration**: Directly communicates with Android's `AudioManager` and `HyprAudioPlayer` with one-tap system volume overlay triggers (`FLAG_SHOW_UI`) and instant mute/restore toggles.

### 4. Left-Aligned Track Information & Floating Actions
* **Intentional Asymmetry**: Left-aligned song title (28sp bold) and artist name (17sp medium) contrasting with the centered artwork.
* **Real Play-Count Metadata**: Real-time play count readout (`▶ 43,00,563`) when available, clean and unobtrusive.
* **Floating Action Suite**: Add to playlist, album navigation, and animated outline/filled favorite heart icons with 44dp touch targets and zero rectangular containers.

### 5. Horizontal Waveform Visualization & Time Readout
* **Thin Waveform Seekbar**: Deterministic waveform visualization with light active bars and muted violet-gray unplayed bars.
* **Interactive Seek**: Tap or drag anywhere along the waveform to immediately seek to any section of the track.
* **Centered Time Counter**: Clean `elapsed / duration` readout (16sp) under the waveform supporting unknown duration, live streams, and zero-duration states safely.

### 6. Floating Playback Controls & Lightweight Secondary Dock
* **Cardless Floating Controls**: Shuffle, Previous, Hero Play/Pause, Next, and Repeat floating freely without containers or rectangular boxes.
* **78dp Hero Gradient Button**: Pink → Magenta → Violet gradient play/pause button with soft radial underglow and smooth press physics.
* **Lightweight Secondary Actions**: 4 minimal outline actions (`LYRICS`, `PLAYLIST`, `TIMER`, `EQ`) evenly spaced across the bottom.

### 7. Modern Expansive Bottom Sheets
* **Synced Lyrics Sheet**: Slides up into an expansive modal surface featuring synchronized lyrics with terminal prompt (`❯`), optical center scrolling, and hardware gradient fade masks.
* **Player Options Sheet**: Quick access to player minimize, album browsing, audio format details, DSP equalizer, sleep timer, and native Android song sharing.

---

## Technical Specifications & Compatibility
* **Minimum Android Version**: Android 10.0 (API level 29)
* **Target Android Version**: Android 16.0 (API level 36)
* **Architecture**: Jetpack Compose 100% Kotlin Coroutines, ExoPlayer Media3, Coil 2.7, StateFlow Architecture
* **Version**: `v2.0.0` (Build 22)
* **Asset**: `HyprMusic-v2.0.0.apk` (~7.87 MB)

---

## Installation
Download `HyprMusic-v2.0.0.apk` below and install directly on your Android device (Android 10+), or update via the built-in OTA checker in Settings.
