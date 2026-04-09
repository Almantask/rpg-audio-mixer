package com.example.rpgaudiomixer.domain.media

interface TrackPlayer {
    val isPlaying: Boolean
    fun play()
    fun pause()
    fun stop()
    fun resume()
    fun setVolume(volume: Float)
    fun release()
}
