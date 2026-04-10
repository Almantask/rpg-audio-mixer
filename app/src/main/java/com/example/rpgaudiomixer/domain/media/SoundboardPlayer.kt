package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.FxTrack
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundboardPlayer @Inject constructor(
    private val trackFactory: TrackFactory,
) {
    private val nextInstanceId = AtomicLong(1L)
    private val activePlayers = mutableMapOf<Long, TrackPlayer>()
    private val fxInstanceIds = mutableMapOf<Long, MutableSet<Long>>()
    private var currentMasterVolume: Float = 1f

    val masterVolume: Float
        get() = currentMasterVolume

    fun setMasterVolume(volume: Float) {
        currentMasterVolume = volume.coerceIn(0f, 1f)
        activePlayers.values.forEach { player ->
            player.setVolume(currentMasterVolume)
        }
    }

    fun triggerFx(fxTrack: FxTrack): Long {
        pruneInactivePlayers()

        val instanceId = nextInstanceId.getAndIncrement()
        val player = trackFactory.createOneTimeTrackPlayer(fxTrack.filePath).also { createdPlayer ->
            createdPlayer.setVolume(currentMasterVolume)
            createdPlayer.play()
        }
        activePlayers[instanceId] = player
        fxInstanceIds.getOrPut(fxTrack.id) { linkedSetOf() }.add(instanceId)
        return instanceId
    }

    fun stopFx(instanceId: Long) {
        activePlayers.remove(instanceId)?.let { player ->
            player.stop()
            player.release()
            fxInstanceIds.values.forEach { instanceIds -> instanceIds.remove(instanceId) }
            fxInstanceIds.entries.removeAll { entry -> entry.value.isEmpty() }
        }
    }

    fun stopFxTrack(fxTrackId: Long) {
        pruneInactivePlayers()
        fxInstanceIds[fxTrackId]
            ?.toList()
            .orEmpty()
            .forEach { instanceId -> stopFx(instanceId) }
    }

    fun activeInstanceCounts(): Map<Long, Int> {
        pruneInactivePlayers()
        return fxInstanceIds.mapValues { (_, instanceIds) -> instanceIds.size }
            .filterValues { count -> count > 0 }
    }

    fun releaseAll() {
        activePlayers.values.forEach { player ->
            player.stop()
            player.release()
        }
        activePlayers.clear()
        fxInstanceIds.clear()
    }

    private fun pruneInactivePlayers() {
        val iterator = activePlayers.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (!entry.value.isPlaying) {
                entry.value.release()
                fxInstanceIds.values.forEach { instanceIds -> instanceIds.remove(entry.key) }
                iterator.remove()
            }
        }
        fxInstanceIds.entries.removeAll { entry -> entry.value.isEmpty() }
    }
}
