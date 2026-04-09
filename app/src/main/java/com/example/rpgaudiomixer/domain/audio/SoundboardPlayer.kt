package com.example.rpgaudiomixer.domain.audio

import com.example.rpgaudiomixer.domain.media.TrackFactory
import com.example.rpgaudiomixer.domain.media.TrackPlayer
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.repository.FxRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Manages one-shot FX playback with overlap and re-trigger support.
 *
 * Each trigger creates a new player instance that can overlap with others.
 * Master volume affects all FX proportionally.
 */
class SoundboardPlayer(
    private val trackFactory: TrackFactory,
    private val fxRepository: FxRepository,
    private val coroutineScope: CoroutineScope
) {
    private val activePlayers = mutableMapOf<String, TrackPlayer>()
    private var _masterVolume: Float = 1.0f

    val masterVolume: Float
        get() = _masterVolume

    /**
     * Trigger an FX track. Creates a new player instance for overlap support.
     * Returns the instance ID for stopping later if needed.
     */
    fun triggerFx(fxTrack: FxTrack): String {
        val instanceId = UUID.randomUUID().toString()
        val player = trackFactory.createOneTimeTrackPlayer(fxTrack.filePath).apply {
            setVolume(_masterVolume)
            playTrack()
        }
        activePlayers[instanceId] = player

        // Increment play count
        coroutineScope.launch {
            fxRepository.incrementPlayCount(fxTrack.id)
        }

        return instanceId
    }

    /**
     * Stop a specific FX instance.
     */
    fun stopFx(instanceId: String) {
        activePlayers[instanceId]?.let { player ->
            player.stop()
            player.release()
            activePlayers.remove(instanceId)
        }
    }

    /**
     * Stop all currently playing FX.
     */
    fun stopAllFx() {
        activePlayers.values.forEach { player ->
            player.stop()
            player.release()
        }
        activePlayers.clear()
    }

    fun setMasterVolume(volume: Float) {
        _masterVolume = volume.coerceIn(0f, 1f)
        activePlayers.values.forEach { it.setVolume(_masterVolume) }
    }

    /**
     * Get the count of currently playing instances.
     */
    fun getActiveInstanceCount(): Int = activePlayers.size

    fun release() {
        stopAllFx()
    }
}
