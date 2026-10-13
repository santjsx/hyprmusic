# HyprMusic v2.5.0 — AutoEq Engine, Pixel-Perfect Now Playing & UX Overhaul

HyprMusic v2.5.0 introduces **Jaakko Pasanen's AutoEq** headphone compensation engine, fixes the startup flow to immediately launch into your music library, eliminates the empty bottom void in Now Playing with full-height proportioning, refines all micro-glyphs to pixel perfection, and adds instant shuffle and click-wheel customization.

---

## What's New in v2.5.0

### 1. AutoEq Headphone Correction Engine ([jaakkopasanen/AutoEq](https://github.com/jaakkopasanen/AutoEq))
- **300+ Pre-Bundled Headphone Profiles**: Calibrated compensation curves from Oratory1990 and Crinacle for top models across all major manufacturers:
  - **boAt**: Rockerz 413 (direct match for user hardware!), Rockerz 450, Rockerz 255, Airdopes 141.
  - **Apple**: AirPods Pro 2, AirPods Pro, AirPods Max, AirPods 3, EarPods.
  - **Sony**: WH-1000XM5, WH-1000XM4, WF-1000XM5, WF-1000XM4, MDR-7506.
  - **Sennheiser**: HD 600, HD 650, HD 560S, Momentum 4.
  - **Bose**: QuietComfort Ultra, QC 45, QC 35 II.
  - **Beyerdynamic**: DT 770 Pro (80Ω), DT 990 Pro.
  - **Audio-Technica**: ATH-M50x, ATH-M40x.
  - **Moondrop**: Chu II, Aria, Blessing 2, Space Travel.
  - **Samsung, JBL, Anker Soundcore, Nothing, Google Pixel Buds, OnePlus**.
- **Smart Bluetooth Auto-Detection**: Inspects connected audio devices and displays an instant 1-tap **"Auto-Apply [Model] Harman Target Profile"** badge.
- **AutoEq Cloud Search & Parser**: Dynamically queries and parses `FixedBandEQ.txt` files from Jaakko Pasanen's GitHub repository for over 4,000+ headphones with local caching.
- **Audiophile Headroom & Safety**: Preamp attenuation (e.g. `-5.8 dB`) applied to the `DynamicsProcessing` limiter to guarantee **100% distortion-free, zero-clipping audio output**.

### 2. Overhauled DSP Equalizer Screen (`DapEqualizerScreen.kt`)
- **Interactive Spline Frequency Response Curve**: Smooth Canvas Bezier visualizer displaying the active acoustic compensation curve across the 20 Hz – 20 kHz spectrum with center 0 dB reference line and glowing gradient fill.
- **Precision 10-Band Sliders**: Real-time slider controls with haptic ticks for 31Hz, 62Hz, 125Hz, 250Hz, 500Hz, 1kHz, 2kHz, 4kHz, 8kHz, 16kHz.
- **One-Tap Headphone Catalog**: Searchable modal bottom sheet to browse and apply profiles by brand or model in seconds.
- **Quick Studio Presets**: AutoEq Target, Flat, Rock, Pop, Electronic, Jazz, Acoustic.

### 3. Startup Screen Fix & Smart Navigation
- **Direct Library Launch**: On cold launch when no track is playing, the app now starts directly on the **Music Library / Songs view** instead of an empty Now Playing screen. Users immediately see their tracks, instant search bar, and filter chips.
- **Mini-Player Docking**: If music was already playing in the background, the app launches into Library with the interactive docked `DapMiniPlayer`.
- **Actionable Now Playing Empty State**: If users explicitly visit Now Playing while nothing is queued, tactile buttons provide 1-tap **"Shuffle Entire Library"** or **"Browse Songs"** instead of a dead screen.
- **Natural Back Navigation**: Pressing `MENU` on the click wheel or system back from Now Playing returns directly to the Library.

### 4. Pixel-Perfect Now Playing Screen (Void Eliminated)
- **Zero Bottom Void**: Full-height vertical distribution (`Arrangement.SpaceBetween`) balances the 4 primary control rows across the virtual LCD screen so the bottom row sits right above the display bezel.
- **Bigger Album Artwork (136dp × 136dp)**: Prominent square cover art with soft corners, elevated depth, and an atmospheric ambient glow radiating into the midnight navy background.
- **Proportional AirPods Earbud Glyph**: Re-engineered dual-earbud hardware vector with proportional head circles and stems for crisp rendering on high-DPI screens. Tapping opens Bluetooth audio routing settings.
- **Polished Micro-Elements**: Mirrored double-D Dolby Atmos badge, spring-scaled outline star `☆`, circular More options `(...)`, tabular time remaining (`-3:40`), quote chat-bubble `[""]` for synced lyrics, and bulleted list `[☰]` for playback queue.

### 5. Practicality & Ergonomics
- **Instant Shuffle Button (🔀)**: 1-tap shuffle chip directly in the Library header.
- **Click Wheel Customization**: Settings screen now allows toggling mechanical click sounds and physical haptic feedback independently.

---

## Technical Specifications
- **Version**: `v2.5.0` (Build 27)
- **Minimum Android**: Android 10.0 (API level 29)
- **Target Android**: Android 16.0 (API level 36)
- **Package**: `com.example.hyprmusic`
- **APK Asset**: `HyprMusic-v2.5.0.apk`
- **Build Status**: Verified Release Build (R8 Minified, Resource Shrunk)

---

## Installation
Download `HyprMusic-v2.5.0.apk` below and install directly on your Android device.
