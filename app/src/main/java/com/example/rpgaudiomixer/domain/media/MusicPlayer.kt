package com.example.rpgaudiomixer.domain.media

/**
 * Contract for requesting sound playback.
 *
 * Note: In acceptance tests we inject a fake implementation and assert calls.
 */
interface MusicPlayer {
    fun play(soundId: SoundId)
}

@JvmInline
value class SoundId(val value: String)
