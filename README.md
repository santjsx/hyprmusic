# HyprMusic

> **High-Fidelity, Offline-First Android Music Player & Riced Audio Canvas**  
> *Crafted by [Santhosh Reddy](https://github.com/santjsx)*

---

[![Android Platform](https://img.shields.io/badge/Platform-Android%2010%2B%20(API%2029%2B)-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.20-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2F%20Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Media3](https://img.shields.io/badge/Engine-AndroidX%20Media3%201.5.1-FF6F00?style=for-the-badge&logo=google&logoColor=white)](https://developer.android.com/media/media3)
[![License](https://img.shields.io/badge/License-GPL--3.0-blue?style=for-the-badge)](LICENSE)

---

## Overview

**HyprMusic** bridges the aesthetic divide between traditional offline audio players and modern, ultra-responsive riced interfaces. Drawing design inspiration from the Linux Wayland ricing ecosystem—specifically the **Hyprland** dynamic tiling window manager—HyprMusic transforms local music listening into an interactive, audiophile-grade audio canvas.

---

## Features

### Audiophile Audio Engine
- **Bit-Perfect Pipeline**: Native 16-bit PCM playback directly aligned with hardware audio HALs for zero driver stalls and pure fidelity.
- **Studio Headroom Compensation**: Real-time pre-cut attenuation factor ($10^{-\text{boost}/20}$) preventing digital clipping when equalizer bands or bass boost are adjusted.
- **Hardware Buffer Protection**: 500 ms – 2000 ms PCM buffer duration provider preventing underruns during garbage collection or background task switching.
- **Format Support**: High-resolution Lossless FLAC (up to 24-bit/96kHz), WAV, AAC, M4A, OGG, and MP3.
- **Gapless Playback & Constant-Bitrate Seeking**: Zero audible clicks between tracks with instant timeline scrubbing.

### Clean Modern & Adaptive Theming
- **Icon Pack Architecture**: Choose between vector icon sets inspired by **Lucide** (clean 2px lines), **Phosphor** (soft outlines), and **Remix** (high-contrast solids).
- **Progress Bar Engines**: Dynamic seeking options including **Capsule Seeker**, **Minimal Line**, and **Dynamic Glow**.
- **Dynamic Theming**: Eight curated color themes (**Catppuccin Mocha**, **Gruvbox Retro**, **Tokyo Night**, **Nordic Frost**, **OLED Cyberpunk**, **Dracula Void**, **Rose Pine**, and **Monokai Pro**).
- **Riced Window Aesthetics**: Glowing active borders, dynamic gaps, frosted surfaces, and smooth micro-interactions.
- **Floating Mini-Player Dock**: Compact floating player bar with real-time waveform visualization and playback controls.

### DSP Equalizer & Audio FX
- **5-Band Parametric EQ**: Studio bands (60 Hz, 230 Hz, 910 Hz, 3.6 kHz, 14 kHz) with smooth cubic spline curve visualization.
- **Acoustic Presets**: Balanced, subtractive bipolar curves for *Flat*, *Bass Boost*, *Rock*, *Pop*, *Electronic*, *Vocal*, *Jazz*, and *Acoustic*.
- **Hardware Bass Boost & 3D Virtualizer**: Integrated with system DSP with automated device capability verification.

### Synchronized Lyrics
- **Synchronized LRC Engine**: Line-by-line synchronized scrolling with smooth active-line highlighting.
- **Offline & Local Lyrics Support**: Automatically pairs with local `.lrc` files in your music directories.
- **Polished Fallback States**: Clean, minimal empty and no-lyrics found indicators.

### Scale & Performance
- **Sub-16ms Query Latency**: High-speed scanning across local storage libraries with thousands of songs, albums, and artists.
- **Unified MediaSession**: Seamless lockscreen playback controls, Bluetooth metadata support, and Android media notification integration.
- **OTA Updates via GitHub Releases**: Direct in-app update checker querying GitHub Releases with automated background download and package installation.

---

## Architecture & Tech Stack

```
HyprMusic
├── app/src/main/java/com/example/hyprmusic/
│   ├── core/
│   │   ├── data/          # MusicRepository, MediaStore scanner, Local audio models
│   │   ├── media/         # HyprAudioPlayer, HyprEqualizer, HyprPlaybackService (Media3)
│   │   ├── lyrics/        # LRC parser & Lyric models
│   │   ├── theming/       # HyprThemes, ThemeManager, Palette definitions
│   │   └── updater/       # HyprUpdateManager (GitHub Releases OTA)
│   ├── ui/
│   │   ├── components/    # TopBar, MiniPlayer, EqualizerDialog, SyncedLyricsSheet
│   │   └── screens/       # HomeScreen, LibraryScreen, NowPlayingScreen, SettingsScreen
│   └── MainActivity.kt    # Root Compose container & workspace routing
```

- **Language**: Kotlin 2.3.20 (JVM 17 toolchain)
- **UI Framework**: Jetpack Compose with Material 3
- **Audio Stack**: AndroidX Media3 (ExoPlayer 1.5.1, MediaSession, AudioSink)
- **Image Loading**: Coil 3 with hardware bitmap recycling
- **Serialization**: Kotlinx Serialization JSON
- **Asynchronous Flow**: Kotlin Coroutines & StateFlow

---

## Screenshots

| Workspace Home | Now Playing | DSP Equalizer | Terminal Settings |
| :---: | :---: | :---: | :---: |
| High-Density Audio Stack | Fullscreen Lossless Canvas | 5-Band Spline EQ | Theming & OTA Updates |

---

## Building & Installation

### Prerequisites
- Android Studio Ladybug / Meerkat or Command Line Tools
- Android SDK 36 (minSdk: 29, targetSdk: 36)
- JDK 17

### Build Debug APK
```bash
git clone https://github.com/santjsx/hyprmusic.git
cd hyprmusic
./gradlew assembleDebug
```
The output APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

### Install on Connected Device
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## Author

**Santhosh Reddy**
- GitHub: [@santjsx](https://github.com/santjsx)
- Project Repository: [https://github.com/santjsx/hyprmusic](https://github.com/santjsx/hyprmusic)

---

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
