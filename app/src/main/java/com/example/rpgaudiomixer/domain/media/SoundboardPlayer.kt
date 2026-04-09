package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.FxTrack

data class SoundboardTriggerResult(
    val startedInstanceId: Long,
    val evictedInstanceId: Long?,
)

class SoundboardPlayer(
    private val trackFactory: TrackFactory,
    private val maxConcurrentInstances: Int = 5,
) {
    private data class ActiveFxInstance(
        val id: Long,
        val trackId: Long,
        val player: TrackPlayer,
    )

    private val activeInstances = linkedMapOf<Long, ActiveFxInstance>()
    private var nextInstanceId: Long = 1L
    private var masterVolume: Float = 1f

    fun triggerFx(fxTrack: FxTrack): SoundboardTriggerResult {
        val evictedInstanceId = if (activeInstances.size >= maxConcurrentInstances) {
            activeInstances.entries.firstOrNull()?.value?.let { oldest ->
                activeInstances.remove(oldest.id)
                oldest.player.stop()
                oldest.player.release()
                oldest.id
            }
        } else {
            null
        }
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
        return SoundboardTriggerResult(
            startedInstanceId = instanceId,
            evictedInstanceId = evictedInstanceId,
        )
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
