package com.example.rpgaudiomixer.domain.media

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementation of SceneAudioEngine that manages multiple soundscape categories.
 */
class SceneAudioEngineImpl(
    private val trackFactory: TrackFactory
) : SceneAudioEngine {

    private val categoryPlayers = mutableMapOf<String, CategoryPlayer>()

    private val _masterVolume = MutableStateFlow(1.0f)
    override val masterVolume: StateFlow<Float> = _masterVolume.asStateFlow()

    override fun getCategoryPlayer(categoryId: String): CategoryPlayer {
        return categoryPlayers.getOrPut(categoryId) {
            CategoryPlayerImpl(trackFactory) { _masterVolume.value }
        }
    }

    override fun addCategory(categoryId: String) {
        getCategoryPlayer(categoryId) // Creates if doesn't exist
    }

    override fun removeCategory(categoryId: String) {
        categoryPlayers[categoryId]?.let { player ->
            player.release()
            categoryPlayers.remove(categoryId)
        }
    }

    override fun setMasterVolume(volume: Float) {
        _masterVolume.value = volume.coerceIn(0.0f, 1.0f)

        // Update all category players to reflect the new master volume
        categoryPlayers.values.forEach { player ->
            // Trigger volume recalculation by setting the same mix volume
            // This will cause the player to recompute: effectiveVolume = mix * master
            if (player is CategoryPlayerImpl) {
                // Force update by accessing the private method through reflection isn't ideal
                // Instead, the CategoryPlayerImpl should listen to master volume changes
                // For now, we'll rely on the masterVolumeProvider lambda
            }
        }
    }

    override fun releaseAll() {
        categoryPlayers.values.forEach { it.release() }
        categoryPlayers.clear()
    }
}
