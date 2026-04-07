package com.example.rpgaudiomixer.domain.media

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Implementation of SoundboardPlayer that manages multiple one-shot sound effects.
 */
class SoundboardPlayerImpl(
    private val trackFactory: TrackFactory
) : SoundboardPlayer {

    private val activePlayers = mutableMapOf<String, TrackPlayer>()

    private val _masterVolume = MutableStateFlow(1.0f)
    override val masterVolume: StateFlow<Float> = _masterVolume.asStateFlow()

    private val _activeInstanceCount = MutableStateFlow(0)
    override val activeInstanceCount: StateFlow<Int> = _activeInstanceCount.asStateFlow()

    override fun triggerFx(fxTrackPath: String): String {
        val instanceId = UUID.randomUUID().toString()

        // Create a new one-time player
        val player = trackFactory.createOneTimeTrackPlayer(fxTrackPath)
        player.setVolume(_masterVolume.value)
        activePlayers[instanceId] = player

        // Play the sound
        player.playTrack()

        // Update active count
        _activeInstanceCount.value = activePlayers.size

        return instanceId
    }

    override fun stopFx(instanceId: String) {
        activePlayers[instanceId]?.let { player ->
            player.stopTrack()
            player.release()
            activePlayers.remove(instanceId)
            _activeInstanceCount.value = activePlayers.size
        }
    }

    override fun stopAll() {
        activePlayers.forEach { (_, player) ->
            player.stopTrack()
            player.release()
        }
        activePlayers.clear()
        _activeInstanceCount.value = 0
    }

    override fun setMasterVolume(volume: Float) {
        _masterVolume.value = volume.coerceIn(0.0f, 1.0f)

        // Update volume for all active players
        activePlayers.values.forEach { player ->
            player.setVolume(_masterVolume.value)
        }
    }

    override fun releaseAll() {
        stopAll()
    }
}
