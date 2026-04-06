package com.example.rpgaudiomixer.domain.audio

import com.example.rpgaudiomixer.domain.media.TrackPlayer
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryPlayer(
    private val trackFactory: (filePath: String) -> TrackPlayer,
) {
    private var currentPlayer: TrackPlayer? = null
    private var currentFilePath: String? = null
    private var mixVolume: Float = 1.0f

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    val currentTrackPath: String? get() = currentFilePath

    fun play(filePath: String) {
        currentPlayer?.stop()
        currentPlayer?.release()
        val player = trackFactory(filePath)
        player.setVolume(mixVolume)
        player.play()
        currentPlayer = player
        currentFilePath = filePath
        _isPlaying.value = true
    }

    fun pause() {
        currentPlayer?.pause()
        _isPlaying.value = false
    }

    fun resume() {
        currentPlayer?.resume()
        _isPlaying.value = currentPlayer?.isPlaying ?: false
    }

    fun stop() {
        currentPlayer?.stop()
        currentPlayer?.release()
        currentPlayer = null
        currentFilePath = null
        _isPlaying.value = false
    }

    fun setMixVolume(volume: Float) {
        mixVolume = volume
        currentPlayer?.setVolume(volume)
    }

    fun rollRandomTrack(pool: List<SoundscapeTrack>) {
        if (pool.isEmpty()) return
        val track = pool.random()
        play(track.filePath)
    }

    fun release() {
        currentPlayer?.release()
        currentPlayer = null
        _isPlaying.value = false
    }
}
