package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.library.SoundscapeTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryPlayer(
    private val trackFactory: TrackFactory
) {
    private var currentPlayer: TrackPlayer? = null
    private var currentTrackPath: String? = null
    private var mixVolume: Float = 1f
    private var masterVolume: Float = 1f

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun play(trackPath: String) {
        if (currentTrackPath == trackPath && currentPlayer != null) {
            resume()
            return
        }

        stop()
        currentTrackPath = trackPath
        val player = trackFactory.createLoopableTrackPlayer(trackPath)
        currentPlayer = player
        updatePlayerVolume()
        player.play()
        _isPlaying.value = true
    }

    fun pause() {
        currentPlayer?.pause()
        _isPlaying.value = false
    }

    fun resume() {
        currentPlayer?.resume()
        if (currentPlayer != null) {
            _isPlaying.value = true
        }
    }

    fun stop() {
        currentPlayer?.stop()
        currentPlayer?.release()
        currentPlayer = null
        currentTrackPath = null
        _isPlaying.value = false
    }

    fun rollRandomTrack(pool: List<SoundscapeTrack>) {
        if (pool.isEmpty()) return
        val randomTrack = pool.random()
        play(randomTrack.filePath)
    }

    fun setMixVolume(volume: Float) {
        this.mixVolume = volume
        updatePlayerVolume()
    }

    fun setMasterVolume(volume: Float) {
        this.masterVolume = volume
        updatePlayerVolume()
    }

    private fun updatePlayerVolume() {
        currentPlayer?.setVolume(mixVolume * masterVolume)
    }

    suspend fun fadeVolume(targetMixVolume: Float, durationMs: Long) {
        val startVolume = this.mixVolume
        val steps = 20
        val interval = durationMs / steps
        val volumeStep = (targetMixVolume - startVolume) / steps

        for (i in 1..steps) {
            kotlinx.coroutines.delay(interval)
            this.mixVolume = startVolume + (volumeStep * i)
            updatePlayerVolume()
        }
    }

    fun release() {
        stop()
    }
}
