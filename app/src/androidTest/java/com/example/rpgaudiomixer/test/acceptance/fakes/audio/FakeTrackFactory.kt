package com.example.rpgaudiomixer.test.acceptance.fakes.audio

import com.example.rpgaudiomixer.domain.media.TrackFactory
import com.example.rpgaudiomixer.domain.media.TrackPlayer

class FakeTrackFactory : TrackFactory {
    private val loopablePlayersByTrack = linkedMapOf<String, MutableList<FakeTrackPlayer>>()
    private val oneShotPlayersByTrack = linkedMapOf<String, MutableList<FakeTrackPlayer>>()

    override fun createLoopableTrackPlayer(track: String): TrackPlayer {
        val player = FakeTrackPlayer(track)
        loopablePlayersByTrack.getOrPut(track) { mutableListOf() }.add(player)
        return player
    }

    override fun createOneTimeTrackPlayer(track: String): TrackPlayer {
        val player = FakeTrackPlayer(track)
        oneShotPlayersByTrack.getOrPut(track) { mutableListOf() }.add(player)
        return player
    }

    fun latestLoopablePlayer(track: String): FakeTrackPlayer? = loopablePlayersByTrack[track]?.lastOrNull()

    fun latestOneShotPlayer(track: String): FakeTrackPlayer? = oneShotPlayersByTrack[track]?.lastOrNull()

    fun allLoopablePlayers(): List<FakeTrackPlayer> = loopablePlayersByTrack.values.flatten()

    fun allOneShotPlayers(): List<FakeTrackPlayer> = oneShotPlayersByTrack.values.flatten()

    fun reset() {
        loopablePlayersByTrack.clear()
        oneShotPlayersByTrack.clear()
    }
}
