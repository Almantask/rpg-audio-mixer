package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryPlayer(
    private val trackFactory: TrackFactory,
    private val random: Random = Random.Default,
) {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private var trackPlayer: TrackPlayer? = null
    private var currentTrack: SoundscapeTrack? = null
    private var mixVolume: Float = 1f
    private var masterVolume: Float = 1f

    fun play(trackPath: String) {
        currentTrack = currentTrack?.copy(filePath = trackPath)
        replaceTrackPlayer(trackPath)
        trackPlayer?.play()
        syncIsPlaying()
    }

    fun play(track: SoundscapeTrack) {
        currentTrack = track
        replaceTrackPlayer(track.filePath)
        trackPlayer?.play()
        syncIsPlaying()
    }

    fun pause() {
        trackPlayer?.pause()
        syncIsPlaying()
    }

    fun resume() {
        trackPlayer?.resume()
        syncIsPlaying()
    }

    fun stop() {
        trackPlayer?.stop()
        syncIsPlaying()
    }

    fun rollRandomTrack(pool: List<SoundscapeTrack>): SoundscapeTrack? {
        if (pool.isEmpty()) return null

        val selectedTrack = pool.random(random)
        play(selectedTrack)
        return selectedTrack
    }

    fun setMixVolume(volume: Float) {
        mixVolume = volume.coerceIn(0f, 1f)
        applyVolume()
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        applyVolume()
    }

    fun release() {
        trackPlayer?.release()
        trackPlayer = null
        currentTrack = null
        _isPlaying.value = false
    }

    private fun replaceTrackPlayer(trackPath: String) {
        trackPlayer?.release()
        trackPlayer = trackFactory.createLoopableTrackPlayer(trackPath).also { player ->
            player.setVolume(currentOutputVolume())
        }
    }

    private fun applyVolume() {
        trackPlayer?.setVolume(currentOutputVolume())
    }

    private fun currentOutputVolume(): Float {
        val trackMix = currentTrack?.mixVolume ?: 1f
        return (trackMix * mixVolume * masterVolume).coerceIn(0f, 1f)
    }

    private fun syncIsPlaying() {
        _isPlaying.value = trackPlayer?.isPlaying == true
    }
}
