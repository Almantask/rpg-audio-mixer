package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryPlayer(
    private val trackFactory: TrackFactory,
    private val randomIndexPicker: (Int) -> Int = { size -> Random.Default.nextInt(size) },
) : CategoryPlaybackController {
    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private var currentTrackPath: String? = null
    private var currentPlayer: TrackPlayer? = null
    private var mixVolume: Float = 1f

    override fun play(trackPath: String) {
        if (currentTrackPath != trackPath || currentPlayer == null) {
            replaceCurrentPlayer(trackPath)
        }

        currentPlayer?.play()
        _isPlaying.value = currentPlayer?.isPlaying == true
    }

    override fun pause() {
        currentPlayer?.pause()
        _isPlaying.value = false
    }

    override fun resume() {
        currentPlayer?.resume()
        _isPlaying.value = currentPlayer?.isPlaying == true
    }

    override fun stop() {
        currentPlayer?.stop()
        _isPlaying.value = false
    }

    override fun rollRandomTrack(pool: List<SoundscapeTrack>): SoundscapeTrack? {
        if (pool.isEmpty()) {
            return null
        }

        return pool[randomIndexPicker(pool.size)].also { selectedTrack ->
            play(selectedTrack.filePath)
        }
    }

    override fun setMixVolume(volume: Float) {
        mixVolume = volume.coerceIn(0f, 1f)
        currentPlayer?.setVolume(mixVolume)
    }

    override fun release() {
        currentPlayer?.release()
        currentPlayer = null
        currentTrackPath = null
        _isPlaying.value = false
    }

    private fun replaceCurrentPlayer(trackPath: String) {
        currentPlayer?.stop()
        currentPlayer?.release()
        currentPlayer = trackFactory.createLoopableTrackPlayer(trackPath).also { player ->
            player.setVolume(mixVolume)
        }
        currentTrackPath = trackPath
    }
}
