package com.example.rpgaudiomixer.app.audio

/**
 * Controls for a single audio player instance.
 *
 * Abstracts over the concrete player implementation (e.g. ExoPlayer)
 * so that [CategoryPlayer] and [SceneAudioEngine] can be tested with mocks.
 */
interface AudioPlayerControl {
    fun play()
    fun stop()
    fun pause()
    fun release()
    fun setVolume(volume: Float)
    val isPlaying: Boolean
}
