package com.example.rpgaudiomixer.domain.media

interface TrackPlayer {
    fun play()
    fun pause()
    fun resume()
    fun stop()
    fun setVolume(volume: Float)
    val isPlaying: Boolean
    fun release()
}