# 🎧 HyprMusic v1.0.0 — Initial Release

Welcome to the inaugural public release of **HyprMusic**! 

HyprMusic is an offline-first, high-fidelity Android audio orchestrator built with Jetpack Compose, Kotlin 2.3, and AndroidX Media3. Inspired by the dynamic tiling aesthetics of the **Hyprland** Linux Wayland ecosystem, HyprMusic pairs studio-grade DSP audio engineering with a responsive, customizable terminal canvas.

---

## 🚀 Key Highlights & Features

### 🎛️ Audiophile-Grade Audio Engine
* **Native 16-Bit PCM Pipeline**: Engineered directly on AndroidX Media3 / ExoPlayer AudioSink, bypassing Android mixer resampling artifacts for bit-perfect output.
* **Studio Headroom Compensation**: Integrated dynamic pre-cut attenuation ($10^{-\text{boost}/20}$) to guarantee **zero digital clipping or harmonic distortion** when pushing equalizer bands or bass boost.
* **Hardware Buffer Protection**: Custom 500 ms – 2000 ms PCM buffer duration provider preventing underruns during garbage collection or background multitasking.
* **Lossless Audio Support**: Seamless playback for FLAC (up to 24-bit/96kHz), WAV, AAC, M4A, OGG, and MP3 with gapless playback and constant-bitrate seeking.

### 🎨 Hyprland Riced Terminal Aesthetics
* **Dynamic Workspace Navigation**: Terminal-style workspace switcher (`[1:home]`, `[2:lib]`, `[3:player]`, `[4:config]`).
* **Six Curated Ricing Themes**:
  * **Tokyo Night** (Classic neon cyberpunk blue & purple)
  * **Catppuccin Mocha** (Warm pastel dark theme)
  * **Nord** (Arctic, north-bluish clean palette)
  * **Gruvbox Dark** (Retro groove amber & warm forest)
  * **Dracula** (Vampire dark purple & pink)
  * **Cyberpunk Neon** (High-voltage electric gold & cyan)
* **Window Rice Details**: Glowing active borders, dynamic gaps, frosted glassmorphism, animated waveform visualizer, and tactile spring micro-interactions.

### 🎚️ 5-Band Studio Equalizer & Audio FX
* **Parametric 5-Band DSP**: Calibrated frequencies at **60 Hz**, **230 Hz**, **910 Hz**, **3.6 kHz**, and **14 kHz**.
* **Real-Time Spline Curve**: Dynamic cubic spline frequency response visualization.
* **8 Tailored Presets**: Flat, Bass Boost, Rock, Pop, Electronic, Vocal, Jazz, and Acoustic.
* **Hardware Bass Boost & 3D Virtualizer**: Integrated with system audio effects, complete with capability checks and non-blocking state persistence.

### 📜 Synchronized Lyrics Engine
* **Sub-Millisecond Synced LRC Engine**: Precise timestamp tracking with smooth auto-scroll to keep active lyrics centered.
* **Local & Offline Support**: Automatically parses `.lrc` files located alongside your music.
* **Polished Empty States**: Elegant, minimalist indicators for instrumental tracks and missing lyrics.

### ⚡ Massive Library & High-Performance Search
* **Zero-Lag Virtualized Lists**: Smooth 60/120fps scrolling even with massive local music collections (10,000+ tracks).
* **Live Multi-Facet Search**: Instant fuzzy filtering by title, artist, album, and track number directly from Home and Library screens.

### 🔄 In-App OTA Updates
* **GitHub Releases Integration**: Check for updates directly inside Settings (`[4:config]`).
* **One-Click Download & Install**: Secure background APK download and prompt via Android PackageInstaller.

---

## 📦 Package Information
* **Version**: `v1.0.0`
* **Package Name**: `com.example.hyprmusic`
* **Target SDK**: Android 16 (API 36)
* **Minimum SDK**: Android 10 (API 29)
* **License**: GNU General Public License v3.0

---

## 🛠️ Installation
Download the attached `HyprMusic-v1.0.0.apk` asset below and install it directly on your Android device (Android 10+). Ensure that **Install unknown apps** is permitted in your system settings.
