package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RecordingCategoryPlayer : CategoryPlaybackController {
    private val playingState = MutableStateFlow(false)

    val playedTracks = mutableListOf<String>()
    val mixVolumeHistory = mutableListOf<Float>()
    var latestMixVolume: Float = 1f
        private set
    var releaseCalls: Int = 0
        private set

    override val isPlaying: StateFlow<Boolean> = playingState

    override fun play(trackPath: String) {
        playedTracks += trackPath
        playingState.value = true
    }

    override fun pause() {
        playingState.value = false
    }

    override fun resume() {
        playingState.value = true
    }

    override fun stop() {
        playingState.value = false
    }

    override fun rollRandomTrack(pool: List<SoundscapeTrack>): SoundscapeTrack? {
        return pool.firstOrNull()?.also { play(it.filePath) }
    }

    override fun setMixVolume(volume: Float) {
        latestMixVolume = volume
        mixVolumeHistory += volume
    }

    override fun release() {
        releaseCalls += 1
        playingState.value = false
    }
}

class SequenceCategoryPlayerFactory(
    vararg players: RecordingCategoryPlayer,
) : () -> CategoryPlaybackController {
    private val queue = ArrayDeque(players.toList())

    override fun invoke(): CategoryPlaybackController {
        return queue.removeFirst()
    }
}
