package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.SoundscapeTrack

interface SceneAudioController {
    fun addCategory(categoryId: Long)

    fun removeCategory(categoryId: Long)

    fun play(categoryId: Long, trackPath: String)

    fun pause(categoryId: Long)

    fun resume(categoryId: Long)

    fun stop(categoryId: Long)

    fun rollRandomTrack(categoryId: Long, pool: List<SoundscapeTrack>): SoundscapeTrack?

    fun setCategoryMixVolume(categoryId: Long, mixVolume: Float)

    fun setMasterVolume(volume: Float)

    fun releaseAll()
}
