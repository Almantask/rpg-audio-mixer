package com.example.rpgaudiomixer.domain.media

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

/**
 * Implementation of CategoryPlayer that manages a single looping track player.
 */
class CategoryPlayerImpl(
    private val trackFactory: TrackFactory,
    private val masterVolumeProvider: () -> Float = { 1.0f }
) : CategoryPlayer {

    private var currentPlayer: TrackPlayer? = null
    private var mixVolume: Float = 1.0f

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    override fun playTrack(trackPath: String) {
        // Stop and release any existing player
        stopTrack()

        // Create and play new loopable player
        val player = trackFactory.createLoopableTrackPlayer(trackPath)
        currentPlayer = player
        updatePlayerVolume()
        player.playTrack()
        _isPlaying.value = true
    }

    override fun pauseTrack() {
        currentPlayer?.pauseTrack()
        _isPlaying.value = false
    }

    override fun resumeTrack() {
        currentPlayer?.resumeTrack()
        _isPlaying.value = true
    }

    override fun stopTrack() {
        currentPlayer?.let { player ->
            player.stopTrack()
            player.release()
            currentPlayer = null
            _isPlaying.value = false
        }
    }

    override fun rollRandomTrack(trackPool: List<String>) {
        if (trackPool.isEmpty()) return

        val randomTrack = trackPool[Random.nextInt(trackPool.size)]
        playTrack(randomTrack)
    }

    override fun setMixVolume(volume: Float) {
        mixVolume = volume.coerceIn(0.0f, 1.0f)
        updatePlayerVolume()
    }

    private fun updatePlayerVolume() {
        val effectiveVolume = mixVolume * masterVolumeProvider()
        currentPlayer?.setVolume(effectiveVolume)
    }

    override fun release() {
        stopTrack()
    }
}
