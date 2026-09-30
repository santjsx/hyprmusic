package com.example.hyprmusic.core.data

import com.example.hyprmusic.core.model.Album
import com.example.hyprmusic.core.model.Artist
import com.example.hyprmusic.core.model.Track

object DemoTracks {
    val sampleTracks = listOf(
        Track(
            id = "demo_1",
            title = "Resonance & Neon Horizon",
            artist = "HOME / HyprVibe",
            album = "Odyssey In Wayland",
            durationMs = 212000L,
            contentUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            albumArtUri = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
            bitrate = 320,
            sampleRate = 48000,
            mimeType = "audio/flac",
            isFavorite = true
        ),
        Track(
            id = "demo_2",
            title = "Midnight Tiling Drive",
            artist = "Kavinsky Wave",
            album = "Outrun The Grid",
            durationMs = 185000L,
            contentUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            albumArtUri = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80",
            bitrate = 320,
            sampleRate = 44100,
            mimeType = "audio/mp3",
            isFavorite = true
        ),
        Track(
            id = "demo_3",
            title = "Tokyo Rain & Catppuccin",
            artist = "Lofi Linux Core",
            album = "Mocha Sessions Vol. 1",
            durationMs = 240000L,
            contentUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            albumArtUri = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
            bitrate = 320,
            sampleRate = 96000,
            mimeType = "audio/flac",
            isFavorite = false
        ),
        Track(
            id = "demo_4",
            title = "Cyberpunk Gaps & Borders",
            artist = "Archway Terminal",
            album = "Configured Kernel",
            durationMs = 195000L,
            contentUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            albumArtUri = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&auto=format&fit=crop&q=80",
            bitrate = 320,
            sampleRate = 48000,
            mimeType = "audio/mp3",
            isFavorite = true
        )
    )

    val sampleAlbums = listOf(
        Album(
            id = "album_1",
            title = "Odyssey In Wayland",
            artist = "HOME / HyprVibe",
            coverUri = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
            trackCount = 1,
            releaseYear = 2024
        ),
        Album(
            id = "album_2",
            title = "Outrun The Grid",
            artist = "Kavinsky Wave",
            coverUri = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80",
            trackCount = 1,
            releaseYear = 2023
        ),
        Album(
            id = "album_3",
            title = "Mocha Sessions Vol. 1",
            artist = "Lofi Linux Core",
            coverUri = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
            trackCount = 1,
            releaseYear = 2024
        ),
        Album(
            id = "album_4",
            title = "Configured Kernel",
            artist = "Archway Terminal",
            coverUri = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&auto=format&fit=crop&q=80",
            trackCount = 1,
            releaseYear = 2024
        )
    )

    val sampleArtists = listOf(
        Artist(id = "artist_1", name = "HOME / HyprVibe", trackCount = 1, albumCount = 1),
        Artist(id = "artist_2", name = "Kavinsky Wave", trackCount = 1, albumCount = 1),
        Artist(id = "artist_3", name = "Lofi Linux Core", trackCount = 1, albumCount = 1),
        Artist(id = "artist_4", name = "Archway Terminal", trackCount = 1, albumCount = 1)
    )
}
