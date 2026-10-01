# ⚡ HyprMusic v1.2.1 — Ergonomic Dock, Instant Startup & GitHub OTA Engine

HyprMusic v1.2.1 brings critical usability refinements, eliminates startup UI flashes, redesigns the bottom navigation bar for effortless one-handed thumb ergonomics, and introduces a fully functional GitHub Releases OTA update engine.

---

## 🚀 Key Improvements in v1.2.1

### ⚡ Zero-Flash Instant Library Startup
* **Persistent Disk Metadata Cache**: Scanned audio tracks are now persisted to a local disk cache (`tracks_cache.json`). On app launch, your entire library loads synchronously from frame 1 with zero latency.
* **Scan State Awareness**: Fixed the jarring startup screen flash where `[ SCAN STORAGE ]` briefly flickered before tracks populated. The empty state now only displays if a full scan completes with genuinely zero tracks found.

### 🎛️ Redesigned Ergonomic Bottom Navigation Dock
* **Comfortable 50dp+ Touch Targets**: Expanded dock height to 58dp with balanced column weights, adhering strictly to Android accessibility guidelines for comfortable, accurate one-handed thumb navigation.
* **Hybrid Icon & Terminal Aesthetic**: Added clean vector glyphs alongside classic Hyprland workspace tags (`[1:home]`, `[2:lib]`, `[3:player]`, `[4:rice]`).
* **Live Player Tab Equalizer**: The `[3:player]` tab displays animated audio visualizer frequency bars in real time while music is playing.
* **Tactile Haptic Feedback**: Every tab switch triggers subtle, mechanical haptic feedback for a responsive desktop-grade feel.
* **Active Glow & Indicator**: Active workspaces feature an accent pill highlight with a glowing micro indicator bar.
* **Dedicated Search Launcher**: Sized quick search trigger with clear active and inactive state visual feedback.

### 🔄 Fully Functional GitHub Releases OTA Update Engine
* **Corrected Repository Integration**: Updated release endpoints to point directly to `santjsx/hyprmusic/releases/latest`.
* **Direct Streaming Coroutine Downloader**: Downloads APK releases directly with automatic redirect handling (following HTTP 302/301 redirects to GitHub S3 storage objects).
* **Real-Time Progress & Speed**: Displays a live `LinearProgressIndicator`, percentage counter, downloaded/total MB (e.g. `75.4 MB`), and transfer speed directly in Settings.
* **Automated Package Installation**: Resolves secure content URIs via `FileProvider` and prompts the Android package installer with `FLAG_GRANT_READ_URI_PERMISSION`.
* **Unknown Apps Permission Prompt**: Automatically handles Android 8.0+ (Oreo through 16) `canRequestPackageInstalls()` checks, directing to system settings when necessary.
* **Test & Re-Download Option**: Added `RE-DOWNLOAD / TEST OTA` and `INSTALL UPDATE NOW` actions in `[4:rice]` > Settings for effortless verification.

---

## 📦 Package Information
* **Version**: `v1.2.1`
* **Version Code**: `4`
* **Package Name**: `com.example.hyprmusic`
* **Target SDK**: Android 16 (API 36)
* **Minimum SDK**: Android 10 (API 29)
* **License**: GNU General Public License v3.0

---

## 🛠️ Installation
Download the `HyprMusic-v1.2.1.apk` asset and install directly on your Android device (Android 10+).
