package com.example.rpgaudiomixer.domain.audio

import com.example.rpgaudiomixer.domain.media.TrackPlayer
import com.example.rpgaudiomixer.domain.model.FxTrack
import java.util.UUID

class SoundboardPlayer(
    private val trackFactory: (filePath: String) -> TrackPlayer,
) {
    private val activePlayers = mutableMapOf<String, TrackPlayer>()
    private var masterVolume: Float = 1.0f

    val activeFxCount: Int get() = activePlayers.size

    fun triggerFx(fxTrack: FxTrack): String {
        val instanceId = UUID.randomUUID().toString()
        val player = trackFactory(fxTrack.filePath)
        player.setVolume(masterVolume)
        player.play()
        activePlayers[instanceId] = player
        return instanceId
    }

    fun stopFx(instanceId: String) {
        activePlayers[instanceId]?.stop()
        activePlayers[instanceId]?.release()
        activePlayers.remove(instanceId)
    }

    fun stopAll() {
        activePlayers.values.forEach {
            it.stop()
            it.release()
        }
        activePlayers.clear()
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume
        activePlayers.values.forEach { it.setVolume(volume) }
    }
}
