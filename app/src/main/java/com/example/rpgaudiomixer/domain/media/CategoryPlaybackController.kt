package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.StateFlow

interface CategoryPlaybackController {
    val isPlaying: StateFlow<Boolean>

    fun play(trackPath: String)

    fun pause()

    fun resume()

    fun stop()

    fun rollRandomTrack(pool: List<SoundscapeTrack>): SoundscapeTrack?

    fun setMixVolume(volume: Float)

    fun release()
}
