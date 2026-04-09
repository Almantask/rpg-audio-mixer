package com.example.rpgaudiomixer.test.acceptance.fakes.audio

import com.example.rpgaudiomixer.domain.media.TrackPlayer

class FakeTrackPlayer(
    val trackPath: String,
) : TrackPlayer {
    var playCount: Int = 0
        private set
    var pauseCount: Int = 0
        private set
    var stopCount: Int = 0
        private set
    var releaseCount: Int = 0
        private set
    var volume: Float = 1f
        private set
    override var isPlaying: Boolean = false
        private set

    override fun play() {
        playCount += 1
        isPlaying = true
    }

    override fun pause() {
        pauseCount += 1
        isPlaying = false
    }

    override fun stop() {
        stopCount += 1
        isPlaying = false
    }

    override fun resume() {
        isPlaying = true
    }

    override fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0f, 1f)
    }

    override fun release() {
        releaseCount += 1
        isPlaying = false
    }
}
