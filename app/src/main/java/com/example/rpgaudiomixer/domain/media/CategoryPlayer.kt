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

    private var currentPlayer: TrackPlayer? = null
    private var mixVolume: Float = 1f
    private var masterVolume: Float = 1f

    fun play(trackPath: String) {
        stop()
        currentPlayer = trackFactory.createLoopableTrackPlayer(trackPath).also { player ->
            player.setVolume(resolveOutputVolume())
            player.play()
        }
        _isPlaying.value = true
    }

    fun pause() {
        currentPlayer?.pause()
        _isPlaying.value = false
    }

    fun resume() {
        currentPlayer?.resume()
        _isPlaying.value = currentPlayer?.isPlaying == true
    }

    fun stop() {
        currentPlayer?.stop()
        currentPlayer?.release()
        currentPlayer = null
        _isPlaying.value = false
    }

    fun release() {
        currentPlayer?.release()
        currentPlayer = null
        _isPlaying.value = false
    }

    fun rollRandomTrack(pool: List<SoundscapeTrack>): Result<SoundscapeTrack> {
        if (pool.isEmpty()) {
            return Result.failure(IllegalArgumentException("No tracks are available in this intensity pool."))
        }

        val track = pool[random.nextInt(pool.size)]
        play(track.filePath)
        return Result.success(track)
    }

    fun setMixVolume(volume: Float) {
        mixVolume = volume.coerceIn(0f, 1f)
        currentPlayer?.setVolume(resolveOutputVolume())
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        currentPlayer?.setVolume(resolveOutputVolume())
    }

    private fun resolveOutputVolume(): Float {
        return (masterVolume * mixVolume).coerceIn(0f, 1f)
    }
}
