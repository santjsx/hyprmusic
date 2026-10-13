# HyprMusic v2.3.0 — iPod-Inspired Retro DAP & Tactile Physical Player UI

HyprMusic v2.3.0 introduces a flagship redesign of the complete application experience, transforming the music player into an iconic **tactile Retro-DAP (Digital Audio Player)** inspired by the classic iPod and audiophile high-end portable hardware.

---

## What's New in v2.3.0

### 1. Physical Hardware Chassis & Bezel Styling
- **Graphite Anodized Finish**: Hardware casing styled in dark graphite (`#1E232B`) with chamfered edge highlights and hardware corner radiuses.
- **Recessed LCD Frame**: Near-black matte display frame (`#090C12`) with subtle bevel depth.
- **Midnight Navy Display Canvas**: High-contrast virtual LCD screen with midnight navy gradient (`#0A1526` to `#040A14`).

### 2. High-Precision Tactile Rotary Click-Wheel Engine
- **Continuous Rotary Math**: Touch drag gestures track angular movement via $\operatorname{atan2}(dy, dx)$, quantizing rotation into $18^\circ$ step intervals.
- **Rotary Fling Inertia**: Flinging the wheel continues in an inertial glide with friction decay ($0.90\times$ per frame), emitting deceleration ticks just like a physical wheel.
- **Synchronized Haptics & Sound**: Fires Tier 1 micro-haptics in lockstep with subtle mechanical click sounds.
- **Dynamic Guidance Arc**: Real-time glowing radial gradient arc follows the user's touch angle on the wheel and smoothly fades out upon release.
- **Hardware Seek & Controls**:
  - **Menu**: Navigates up the iPod OS hierarchy.
  - **Left (Prev)**: Tap restarts/skips previous; Long-press rewinds 15 seconds.
  - **Right (Next)**: Tap skips next; Long-press fast-forwards 15 seconds.
  - **Bottom (Play/Pause)**: Toggles playback with reactive icon transitions.
  - **Center (Select)**: Spring-compressed recessed button with confirm haptics.

### 3. Pixel-Accurate Reference Now Playing Display (Callouts 1–13)
- **Album Art [1]**: Square artwork with multi-stop neon lavender/cyan gradient stroke border (`#C084FC` $\to$ `#6DA5FF` $\to$ `#818CF8`).
- **Song Title & [E] Badge [2]**: Crisp semibold white typography with ice-blue explicit `[E]` badge.
- **Artist & Album [3, 4]**: Clean hierarchy with muted blue-gray typography.
- **Dolby Atmos Badge [5]**: Custom-drawn vector mirrored double-D glyph and interactive telemetry modal.
- **Favorite Star [6]**: Outlined star toggle with spring scale pop and persistent Room database storage.
- **More Options [7]**: Context menu with playlist actions, track sharing, and telemetry details.
- **Jitter-Free Timers [8, 10]**: Elapsed time and remaining time pill rendered with tabular figures (`tnum`).
- **Tactile Scrubber [9]**: Slim progress bar expanding gently on touch with real-time drag seeking.
- **Audio Output Telemetry [11]**: Real-time `AudioManager` detection displaying live device names (e.g., "AirPods Pro", "Wired Headphones", or "Phone Speaker") with custom dual-earbud hardware glyph.
- **Lyrics [12] & Queue [13] Buttons**: Custom quote chat bubble glyph for synchronized lyrics and bulleted list button for queue view.

### 4. Virtual LCD OS & Hierarchical Menus
- **iPod OS Navigation**: Full drill-down across Songs, Albums, Artists, Playlists, Queue, Lyrics, Equalizer, and Settings.
- **Rotary Selection**: Rotating the click wheel scrolls through and highlights items across all lists.
- **Synchronized Lyrics**: Real-time auto-scrolling synced LRC lyrics with tap-to-seek timestamp support.
- **10-Band DSP Equalizer**: Retro frequency bars showing live dB level telemetry.
- **Dual Shell Switcher**: Toggle between Retro DAP Mode and Modern View directly from Settings.

---

## Technical Specifications
- **Version**: `v2.3.0` (Build 25)
- **Minimum Android**: Android 10.0 (API level 29)
- **Target Android**: Android 16.0 (API level 36)
- **APK Asset**: `HyprMusic-v2.3.0.apk` (~7.69 MB)
- **Build Status**: 100% Passed (R8 Minified, Resource Shrunk)

---

## Installation
Download `HyprMusic-v2.3.0.apk` below and install directly on your device.
