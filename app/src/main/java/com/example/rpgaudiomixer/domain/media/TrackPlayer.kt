package com.example.rpgaudiomixer.domain.media

interface TrackPlayer {
    fun play()
    fun pause()
    fun stop()
    fun resume()
    fun setVolume(volume: Float)
    val isPlaying: Boolean
    fun release()
}
