package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.FxTrack

class SoundboardPlayer(
    private val trackFactory: TrackFactory,
    private val maxConcurrentEffects: Int = DEFAULT_MAX_CONCURRENT_EFFECTS,
) {
    private val activeInstances = mutableListOf<ActiveEffect>()
    private var nextInstanceId: Long = 1L
    private var masterVolume: Float = 1f

    val activeInstanceCount: Int
        get() = activeInstances.size

    val activeInstanceIds: List<Long>
        get() = activeInstances.map { it.id }

    fun activeInstanceIdsForTrack(trackId: Long): List<Long> {
        return activeInstances.filter { activeEffect -> activeEffect.trackId == trackId }.map { activeEffect -> activeEffect.id }
    }

    fun triggerFx(track: FxTrack): Long {
        enforceConcurrencyLimit()

        val player = trackFactory.createOneTimeTrackPlayer(track.filePath).also { trackPlayer ->
            trackPlayer.setVolume(masterVolume)
            trackPlayer.play()
        }
        val instanceId = nextInstanceId++
        activeInstances += ActiveEffect(
            id = instanceId,
            trackId = track.id,
            player = player,
        )
        return instanceId
    }

    fun stopFx(instanceId: Long) {
        val activeEffect = activeInstances.firstOrNull { it.id == instanceId } ?: return
        activeEffect.player.stop()
        activeEffect.player.release()
        activeInstances.remove(activeEffect)
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        activeInstances.forEach { activeEffect ->
            activeEffect.player.setVolume(masterVolume)
        }
    }

    fun releaseAll() {
        activeInstances.forEach { activeEffect ->
            activeEffect.player.release()
        }
        activeInstances.clear()
    }

    private fun enforceConcurrencyLimit() {
        if (activeInstances.size < maxConcurrentEffects) {
            return
        }

        val oldestInstance = activeInstances.removeFirstOrNull() ?: return
        oldestInstance.player.stop()
        oldestInstance.player.release()
    }

    private data class ActiveEffect(
        val id: Long,
        val trackId: Long,
        val player: TrackPlayer,
    )

    companion object {
        const val DEFAULT_MAX_CONCURRENT_EFFECTS: Int = 5
    }
}
