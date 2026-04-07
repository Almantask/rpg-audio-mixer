package com.example.rpgaudiomixer.domain.media

import kotlinx.coroutines.flow.StateFlow

interface TrackPlayer {
    fun playTrack()
    fun pauseTrack()
    fun stopTrack()
    fun resumeTrack()
    fun setVolume(volume: Float)
    val isPlaying: StateFlow<Boolean>
    fun release()
}