package com.example.rpgaudiomixer.domain.media

import kotlinx.coroutines.flow.StateFlow

interface TrackPlayer {
    val isPlaying: StateFlow<Boolean>

    fun playTrack()
    fun pause()
    fun stop()
    fun resume()
    fun setVolume(volume: Float)
    fun release()
}