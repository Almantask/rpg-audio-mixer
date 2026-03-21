package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.IntensityLevel

/**
 * Contract for requesting sound playback.
 *
 * Note: In acceptance tests we inject a fake implementation and assert calls.
 */
interface MixedMusicPlayer {
    fun playSingleSound(soundId: String)
    fun playLoopingSound(categoryId: String)
    fun setIntensityLevel(categoryId: String, level: IntensityLevel)
}
