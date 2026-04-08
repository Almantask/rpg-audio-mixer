package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.FxTrack

class SoundboardPlayer(
    private val trackFactory: TrackFactory,
) {
    private data class ActiveFxInstance(
        val id: Long,
        val trackId: Long,
        val player: TrackPlayer,
    )

    private val activeInstances = linkedMapOf<Long, ActiveFxInstance>()
    private var nextInstanceId: Long = 1L
    private var masterVolume: Float = 1f

    fun triggerFx(fxTrack: FxTrack): Long {
        val instanceId = nextInstanceId++
        val player = trackFactory.createOneTimeTrackPlayer(fxTrack.filePath).also {
            it.setVolume(masterVolume)
            it.play()
        }

        activeInstances[instanceId] = ActiveFxInstance(
            id = instanceId,
            trackId = fxTrack.id,
            player = player,
        )
        return instanceId
    }

    fun stopFx(instanceId: Long) {
        activeInstances.remove(instanceId)?.player?.let { player ->
            player.stop()
            player.release()
        }
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        activeInstances.values.forEach { instance ->
            instance.player.setVolume(masterVolume)
        }
    }

    fun isTrackPlaying(trackId: Long): Boolean {
        return activeInstances.values.any { instance ->
            instance.trackId == trackId && instance.player.isPlaying
        }
    }

    fun releaseAll() {
        activeInstances.values.forEach { instance ->
            instance.player.stop()
            instance.player.release()
        }
        activeInstances.clear()
    }
}
