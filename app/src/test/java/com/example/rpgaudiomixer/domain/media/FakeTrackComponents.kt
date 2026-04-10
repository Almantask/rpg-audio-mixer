package com.example.rpgaudiomixer.domain.media

class FakeTrackFactory : TrackFactory {
    val createdLoopTracks = mutableListOf<String>()
    val oneTimeTracks = mutableListOf<String>()
    val loopPlayers = mutableListOf<FakeTrackPlayer>()
    val oneTimePlayers = mutableListOf<FakeTrackPlayer>()

    override fun createLoopableTrackPlayer(track: String): TrackPlayer {
        createdLoopTracks += track
        return FakeTrackPlayer().also(loopPlayers::add)
    }

    override fun createOneTimeTrackPlayer(track: String): TrackPlayer {
        oneTimeTracks += track
        return FakeTrackPlayer().also(oneTimePlayers::add)
    }
}

class FakeTrackPlayer : TrackPlayer {
    var playCalls = 0
    var pauseCalls = 0
    var stopCalls = 0
    var resumeCalls = 0
    var releaseCalls = 0
    val volumeHistory = mutableListOf<Float>()

    override var isPlaying: Boolean = false

    override fun play() {
        playCalls += 1
        isPlaying = true
    }

    override fun pause() {
        pauseCalls += 1
        isPlaying = false
    }

    override fun stop() {
        stopCalls += 1
        isPlaying = false
    }

    override fun resume() {
        resumeCalls += 1
        isPlaying = true
    }

    override fun setVolume(volume: Float) {
        volumeHistory += volume
    }

    override fun release() {
        releaseCalls += 1
        isPlaying = false
    }
}
