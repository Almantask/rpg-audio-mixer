package com.example.rpgaudiomixer.domain.media

/**
 * Contract for requesting sound playback.
 *
 * Note: In acceptance tests we inject a fake implementation and assert calls.
 */
interface MixedMusicPlayer {
    fun playSingleSound(soundId: String)
    fun playLoopingSound(categoryId: String)
    fun pauseLoopingSound(categoryId: String)
    fun stopAll()
    fun isLooping(categoryId: String): Boolean
    fun playRandomTrack(categoryId: String)
}
