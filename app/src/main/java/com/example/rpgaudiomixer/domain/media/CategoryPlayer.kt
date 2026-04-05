package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages playback for a single soundscape category.
 * Holds one active [TrackPlayer] at a time.
 */
class CategoryPlayer(
    private val trackFactory: TrackFactory,
    private val soundscapeRepository: SoundscapeRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var currentPlayer: TrackPlayer? = null
    private var mixVolume: Float = 1.0f
    private var masterVolume: Float = 1.0f

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrack = MutableStateFlow<SoundscapeTrack?>(null)
    val currentTrack: StateFlow<SoundscapeTrack?> = _currentTrack.asStateFlow()

    /**
     * Starts playing a specific track, stopping any previous track in this category.
     */
    fun playTrack(track: SoundscapeTrack) {
        currentPlayer?.stop()
        currentPlayer?.release()

        _currentTrack.value = track
        val player = trackFactory.createLoopableTrackPlayer(track.filePath)
        currentPlayer = player
        player.setVolume(mixVolume * masterVolume)
        player.play()
        _isPlaying.value = true
        
        scope.launch {
            soundscapeRepository.incrementTrackPlayCount(track.id)
        }
    }

    /**
     * Pauses the current track.
     */
    fun pause() {
        currentPlayer?.pause()
        _isPlaying.value = false
    }

    /**
     * Resumes the current track if it exists.
     */
    fun resume() {
        currentPlayer?.resume()
        _isPlaying.value = currentPlayer?.isPlaying ?: false
    }

    /**
     * Stops and releases the current track.
     */
    fun stop() {
        currentPlayer?.stop()
        currentPlayer?.release()
        currentPlayer = null
        _isPlaying.value = false
        _currentTrack.value = null
    }

    /**
     * Adjusts the MIX volume for this category (0.0 to 1.0).
     */
    fun setMixVolume(volume: Float) {
        mixVolume = volume
        updatePlayerVolume()
    }

    /**
     * Adjusts the master atmosphere volume applied to this category (0.0 to 1.0).
     */
    fun setMasterVolume(volume: Float) {
        masterVolume = volume
        updatePlayerVolume()
    }

    private fun updatePlayerVolume() {
        currentPlayer?.setVolume(mixVolume * masterVolume)
    }

    /**
     * Randomly selects and plays a track from the provided pool.
     */
    fun rollRandomTrack(pool: List<SoundscapeTrack>) {
        if (pool.isEmpty()) return
        val randomTrack = pool.random()
        playTrack(randomTrack)
    }

    /**
     * Fully releases resources.
     */
    fun release() {
        stop()
    }
}
