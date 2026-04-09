package com.example.rpgaudiomixer.domain.audio

import com.example.rpgaudiomixer.domain.media.TrackFactory
import com.example.rpgaudiomixer.domain.media.TrackPlayer
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

/**
 * Manages playback for a single soundscape category.
 *
 * Can play one track at a time, roll random tracks from a pool,
 * and control mix volume for the category.
 */
class CategoryPlayer(
    private val categoryId: Long,
    private val trackFactory: TrackFactory
) {
    private var currentPlayer: TrackPlayer? = null
    private var currentMixVolume: Float = 0.5f
    private var masterVolume: Float = 1.0f

    val isPlaying: StateFlow<Boolean>
        get() = currentPlayer?.isPlaying ?: kotlinx.coroutines.flow.MutableStateFlow(false)

    fun play(trackPath: String) {
        stop()
        currentPlayer = trackFactory.createLoopableTrackPlayer(trackPath).apply {
            setVolume(currentMixVolume * masterVolume)
            playTrack()
        }
    }

    fun pause() {
        currentPlayer?.pause()
    }

    fun resume() {
        currentPlayer?.resume()
    }

    fun stop() {
        currentPlayer?.stop()
        currentPlayer?.release()
        currentPlayer = null
    }

    fun rollRandomTrack(pool: List<SoundscapeTrack>) {
        if (pool.isEmpty()) return

        val randomTrack = pool[Random.nextInt(pool.size)]
        play(randomTrack.filePath)
    }

    fun setMixVolume(volume: Float) {
        currentMixVolume = volume.coerceIn(0f, 1f)
        updateVolume()
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        updateVolume()
    }

    private fun updateVolume() {
        currentPlayer?.setVolume(currentMixVolume * masterVolume)
    }

    fun release() {
        stop()
    }
}
