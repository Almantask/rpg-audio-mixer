package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.FxTrack
import java.util.concurrent.atomic.AtomicLong

class SoundboardPlayer(
    private val trackFactory: TrackFactory
) {
    private val activePlayers = mutableMapOf<Long, TrackPlayer>()
    private val instanceIdGenerator = AtomicLong(0)
    private var _masterVolume: Float = 1.0f

    val masterVolume: Float
        get() = _masterVolume

    fun setMasterVolume(volume: Float) {
        _masterVolume = volume.coerceIn(0f, 1f)
        activePlayers.values.forEach { player ->
            player.setVolume(_masterVolume)
        }
    }

    fun triggerFx(fxTrack: FxTrack): Long {
        val instanceId = instanceIdGenerator.incrementAndGet()
        val player = trackFactory.createOneTimeTrackPlayer(fxTrack.filePath).apply {
            setVolume(_masterVolume)
            play()
        }
        activePlayers[instanceId] = player

        // Auto-cleanup when done (simplified - would need completion callback in real impl)
        return instanceId
    }

    fun stopFx(instanceId: Long) {
        activePlayers[instanceId]?.let { player ->
            player.stop()
            player.release()
            activePlayers.remove(instanceId)
        }
    }

    fun stopAll() {
        activePlayers.values.forEach { player ->
            player.stop()
            player.release()
        }
        activePlayers.clear()
    }

    fun release() {
        stopAll()
    }
}
