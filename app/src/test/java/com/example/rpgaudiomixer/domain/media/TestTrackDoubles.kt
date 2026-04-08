package com.example.rpgaudiomixer.domain.media

class RecordingTrackFactory : TrackFactory {
    val createdLoopablePlayers = mutableListOf<RecordingTrackPlayer>()
    val createdOneTimePlayers = mutableListOf<RecordingTrackPlayer>()

    override fun createLoopableTrackPlayer(track: String): TrackPlayer {
        return RecordingTrackPlayer(track = track).also(createdLoopablePlayers::add)
    }

    override fun createOneTimeTrackPlayer(track: String): TrackPlayer {
        return RecordingTrackPlayer(track = track).also(createdOneTimePlayers::add)
    }
}

class RecordingTrackPlayer(
    val track: String,
) : TrackPlayer {
    var playCalls: Int = 0
        private set
    var pauseCalls: Int = 0
        private set
    var stopCalls: Int = 0
        private set
    var resumeCalls: Int = 0
        private set
    var releaseCalls: Int = 0
        private set
    var latestVolume: Float = 1f
        private set
    private var currentlyPlaying: Boolean = false

    override val isPlaying: Boolean
        get() = currentlyPlaying

    override fun play() {
        playCalls += 1
        currentlyPlaying = true
    }

    override fun pause() {
        pauseCalls += 1
        currentlyPlaying = false
    }

    override fun stop() {
        stopCalls += 1
        currentlyPlaying = false
    }

    override fun resume() {
        resumeCalls += 1
        currentlyPlaying = true
    }

    override fun setVolume(volume: Float) {
        latestVolume = volume
    }

    override fun release() {
        releaseCalls += 1
        currentlyPlaying = false
    }
}
