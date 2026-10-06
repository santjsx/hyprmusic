# HyprMusic v2.1.0 — Physical Mixtape Rack & Albums Library Redesign

HyprMusic v2.1.0 introduces a complete, ground-up UI redesign of the **Albums and Library experience**. Moving away from generic two-column card grids, v2.1.0 transforms your digital collection into an underground physical music archive—stacking albums in full-width, tactile rack slots inspired by mixtape shelves and Hyprland precision aesthetics.

---

## What's New in v2.1.0

### 1. Physical Album Rack Architecture (`AlbumRackView` & `AlbumRackItem`)
- **Horizontal Rack Slot Geometry**: Replaces the conventional 2-column square card grid with a vertical archive rack where every album occupies a full-width 86dp horizontal slot.
- **Full-Width Artwork Strips**: Album artwork fills the slot using high-resolution horizontal crops with intelligent gradient masking (`#101017`) ensuring 100% typography contrast and zero visual clashing.
- **Structural Shelf Framing**: Deep `#08080D` chassis with `#101017` slot surfaces, thin `#25232F` dividers, and subtle `#3A3545` shelf highlight edges.
- **Archive Indexing**: Subtle monospace archive slot numbers (`01`, `02`, `03`...) at the edge of each item reinforce the feeling of an indexed music vault.
- **Integrated Metadata**: Crisp typography featuring 16sp Semibold album titles and technical metadata (`${album.artist}  ·  ${album.trackCount} TRK  ·  ${album.releaseYear}`), completely eliminating bulky pill badges.
- **Active Playing Indication**: Lavender accent edge (`#C7A5FF`) and live animated mini equalizer bars for the currently playing album, removing the old green "ENGAGED" pill.
- **Zero-Lag Performance**: Hardware-accelerated `LazyColumn` with stable keys (`key = { album.id }`) and Coil memory/disk caching effortlessly renders 1,000+ albums at 120 FPS.

### 2. Segmented Library Source Switcher
- **Compact Segmented Bar**: Replaces large Material-style cards with a unified, dark rack segmented switcher (`● LOCAL  1013    ○ TPMC CLOUD  900`).
- **Real Database Counts**: Live counts directly connected to Room database and Telegram Music Cloud repository.
- **Lavender Outline Feedback**: Selected source is instantly highlighted with a subtle lavender border and dot indicator.

### 3. Slim Rack-Style Search Field
- **Precision 48dp Container**: Replaces the oversized search bar with a slim dark transparent input field (`#101017`, border `#25232F`).
- **Contextual Prompts**: Dynamic monospace prompts (`Search albums...` / `Search library...` / `Search cloud albums...`).
- **Integrated Counters**: In-field live match counter and rescan action button.

### 4. Lightweight Filter & Sort Command Row
- **Buttonless Command Bar**: Replaces heavy pill carousels with clean terminal-style command labels (`ALL`, `TRACKS`, `ALBUMS`, `ARTISTS`, `PLAYLISTS`, `HI-RES FLAC`, `FAVORITES`).
- **Subtle Lavender Indicator**: Active filter highlighted with a high-contrast label and 2dp lavender indicator line.
- **Direct `SORT ↕` Trigger**: One-tap access to persistent sorting options without bulky capsules.

### 5. Floating Compact Rack Footer (`MiniPlayer`)
- **Rack Footer Styling**: Replaces the bulky floating card with a compact slot-style rack footer (44dp artwork thumbnail, 15sp title, 12sp codec/artist metadata).
- **Luminous Audio Filament**: High-precision horizontal progress bar embedded directly into the base of the footer.
- **Haptic Swipe Gestures**: Preserves swipe-left and swipe-right track skipping with haptic feedback and tap-to-expand.

### 6. Terminal-Style Header & Navigation Dock
- **Header**: Minimalist terminal path `● ~/hypr/lib` with purple status dot (`#C7A5FF`) and technical status line `16-BIT / 44.1k     EQ` with 44dp touch target equalizer trigger.
- **Bottom Dock**: Clean Hyprland-inspired navigation (`HOME [1]`, `LIB [2]`, `PLAYER [3]`, `RICE [4]`) with subtle active outline.

---

## Technical Specifications
- **Version**: `v2.1.0` (Build 23)
- **Minimum Android**: Android 10.0 (API level 29)
- **Target Android**: Android 16.0 (API level 36)
- **APK Asset**: `HyprMusic-v2.1.0.apk` (~7.86 MB)
- **Build Status**: 100% Passed (R8 Minified, Resource Shrunk)

---

## Installation
Download `HyprMusic-v2.1.0.apk` below and install directly on your device.
