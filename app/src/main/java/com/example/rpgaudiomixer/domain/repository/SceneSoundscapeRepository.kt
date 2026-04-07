package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import kotlinx.coroutines.flow.Flow

interface SceneSoundscapeRepository {
    fun observeByScene(sceneId: Long): Flow<List<SceneSoundscape>>
    suspend fun add(
        sceneId: Long,
        categoryId: Long,
        displayOrder: Int,
        mixVolume: Float,
        intensityLevel: IntensityLevel
    )
    suspend fun remove(sceneId: Long, categoryId: Long)
    suspend fun updateMixVolume(sceneId: Long, categoryId: Long, mixVolume: Float)
    suspend fun updateIntensityLevel(sceneId: Long, categoryId: Long, intensityLevel: IntensityLevel)
    suspend fun updateDisplayOrders(sceneId: Long, categoryIds: List<Long>)
}
