package com.example.rpgaudiomixer.infra.media

import com.example.rpgaudiomixer.domain.media.MusicPlayer
import com.example.rpgaudiomixer.domain.media.SoundId

/**
 * Placeholder Android implementation.
 *
 * A real implementation would use SoundPool/MediaPlayer/ExoPlayer depending on requirements.
 */
class AndroidMusicPlayer : MusicPlayer {
    override fun play(soundId: SoundId) {
        // TODO: Implement real playback.
        // Kept intentionally side-effect free for now.
    }
}