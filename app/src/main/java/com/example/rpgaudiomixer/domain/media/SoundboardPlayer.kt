package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.FxTrack
import java.util.concurrent.atomic.AtomicLong

class SoundboardPlayer(
    private val trackFactory: TrackFactory,
    private val maxConcurrentEffects: Int = 5,
) {
    data class ActiveFxInstance(
        val instanceId: Long,
        val track: FxTrack,
        val player: TrackPlayer,
    )

    private val instanceIds = AtomicLong(0L)
    private val activeInstances = ArrayDeque<ActiveFxInstance>()
    private var masterVolume: Float = 1f

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        activeInstances.forEach { instance ->
            instance.player.setVolume(masterVolume)
        }
    }

    fun triggerFx(track: FxTrack): Long {
        if (activeInstances.size >= maxConcurrentEffects) {
            stopFx(activeInstances.first().instanceId)
        }

        val player = trackFactory.createOneTimeTrackPlayer(track.filePath).also { trackPlayer ->
            trackPlayer.setVolume(masterVolume)
            trackPlayer.play()
        }
        val instanceId = instanceIds.incrementAndGet()
        activeInstances.addLast(ActiveFxInstance(instanceId = instanceId, track = track, player = player))
        return instanceId
    }

    fun stopFx(instanceId: Long) {
        val instance = activeInstances.firstOrNull { activeInstance -> activeInstance.instanceId == instanceId } ?: return
        instance.player.stop()
        instance.player.release()
        activeInstances.remove(instance)
    }

    fun stopTrack(trackName: String) {
        activeInstances.filter { instance -> instance.track.name == trackName }
            .map(ActiveFxInstance::instanceId)
            .forEach(::stopFx)
    }

    fun activeInstances(): List<ActiveFxInstance> = activeInstances.toList()

    fun releaseAll() {
        activeInstances.map(ActiveFxInstance::instanceId).toList().forEach(::stopFx)
    }
}
