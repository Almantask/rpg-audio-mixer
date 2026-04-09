package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.library.FxTrack
import java.util.concurrent.ConcurrentHashMap

class SoundboardPlayer(
    private val trackFactory: TrackFactory
) {
    private val fxPlayers = ConcurrentHashMap<Long, TrackPlayer>()
    private var masterVolume: Float = 1f

    fun triggerFx(fxTrack: FxTrack) {
        val player = fxPlayers.getOrPut(fxTrack.id) {
            trackFactory.createOneTimeTrackPlayer(fxTrack.filePath).also {
                it.setVolume(masterVolume)
            }
        }
        player.play()
    }

    fun stopFx(fxTrackId: Long) {
        fxPlayers[fxTrackId]?.stop()
    }

    fun setMasterVolume(volume: Float) {
        this.masterVolume = volume
        fxPlayers.values.forEach { it.setVolume(volume) }
    }

    fun releaseAll() {
        fxPlayers.values.forEach { it.release() }
        fxPlayers.clear()
    }
}
