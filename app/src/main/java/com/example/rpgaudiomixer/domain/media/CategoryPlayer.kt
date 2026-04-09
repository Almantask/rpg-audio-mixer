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
    private var player: TrackPlayer? = null
    private var masterVolume: Float = 1f
    private var mixVolume: Float = 1f
    private var currentTrackPath: String? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun play(trackPath: String) {
        stop()
        player = trackFactory.createLoopableTrackPlayer(trackPath).also { trackPlayer ->
            currentTrackPath = trackPath
            trackPlayer.setVolume(masterVolume * mixVolume)
            trackPlayer.play()
            _isPlaying.value = trackPlayer.isPlaying
        }
    }

    fun pause() {
        player?.pause()
        _isPlaying.value = player?.isPlaying == true
    }

    fun resume() {
        player?.resume()
        _isPlaying.value = player?.isPlaying == true
    }

    fun stop() {
        player?.stop()
        player?.release()
        player = null
        currentTrackPath = null
        _isPlaying.value = false
    }

    fun rollRandomTrack(pool: List<SoundscapeTrack>): SoundscapeTrack? {
        if (pool.isEmpty()) return null
        val selectedTrack = pool[random.nextInt(pool.size)]
        play(selectedTrack.filePath)
        return selectedTrack
    }

    fun setMixVolume(volume: Float) {
        mixVolume = volume.coerceIn(0f, 1f)
        player?.setVolume(masterVolume * mixVolume)
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        player?.setVolume(masterVolume * mixVolume)
    }

    fun currentTrackPath(): String? = currentTrackPath

    fun actualVolume(): Float = masterVolume * mixVolume

    fun release() {
        stop()
    }
}
