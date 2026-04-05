package com.example.rpgaudiomixer.domain.media

interface TrackPlayer {
    fun play()
    fun pause()
    fun stop()
    fun resume()
    fun setVolume(volume: Float)
    fun release()
    val isPlaying: Boolean
}