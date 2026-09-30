package com.example.hyprmusic.core.media

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class HyprPlaybackService : MediaSessionService() {

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return HyprAudioPlayer.activeMediaSession
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

