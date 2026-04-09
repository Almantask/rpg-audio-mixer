package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Scene Soundscape domain operations.
 */
interface SceneSoundscapeRepository {

    /**
     * Observe all soundscapes for a specific scene.
     */
    fun observeByScene(sceneId: Long): Flow<List<SceneSoundscape>>

    /**
     * Add a soundscape category to a scene.
     */
    suspend fun addToScene(
        sceneId: Long,
        categoryId: Long,
        intensityLevel: IntensityLevel,
        mixVolumePercent: Int = 100
    )

    /**
     * Update the intensity level for a soundscape in a scene.
     */
    suspend fun updateIntensity(sceneId: Long, categoryId: Long, intensityLevel: IntensityLevel)

    /**
     * Update the mix volume for a soundscape in a scene.
     */
    suspend fun updateMixVolume(sceneId: Long, categoryId: Long, mixVolumePercent: Int)

    /**
     * Remove a soundscape from a scene.
     */
    suspend fun removeFromScene(sceneId: Long, categoryId: Long)

    /**
     * Update display orders for soundscapes in a scene.
     */
    suspend fun updateDisplayOrders(sceneId: Long, soundscapes: List<SceneSoundscape>)
}
