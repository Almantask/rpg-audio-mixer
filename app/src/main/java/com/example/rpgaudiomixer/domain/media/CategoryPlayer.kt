package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryPlayer(
    private val trackFactory: TrackFactory
) {
    private var currentPlayer: TrackPlayer? = null
    private var currentMixVolume: Float = 1.0f
    private var masterVolume: Float = 1.0f

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun play(trackPath: String) {
        stop()
        currentPlayer = trackFactory.createLoopableTrackPlayer(trackPath).apply {
            setVolume(calculateActualVolume())
            play()
        }
        _isPlaying.value = true
    }

    fun pause() {
        currentPlayer?.pause()
        _isPlaying.value = false
    }

    fun resume() {
        currentPlayer?.resume()
        _isPlaying.value = true
    }

    fun stop() {
        currentPlayer?.stop()
        currentPlayer?.release()
        currentPlayer = null
        _isPlaying.value = false
    }

    fun rollRandomTrack(pool: List<SoundscapeTrack>) {
        if (pool.isEmpty()) return
        val track = pool.random()
        play(track.filePath)
    }

    fun setMixVolume(volume: Float) {
        currentMixVolume = volume.coerceIn(0f, 1f)
        currentPlayer?.setVolume(calculateActualVolume())
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        currentPlayer?.setVolume(calculateActualVolume())
    }

    private fun calculateActualVolume(): Float {
        return currentMixVolume * masterVolume
    }

    fun release() {
        stop()
    }
}
