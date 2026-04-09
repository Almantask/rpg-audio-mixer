package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.data.local.SceneSoundscapeCrossRef
import kotlinx.coroutines.flow.Flow

interface SceneSoundscapeRepository {
    fun observeByScene(sceneId: Long): Flow<List<SceneSoundscapeCrossRef>>
    suspend fun addCategoryToScene(sceneId: Long, categoryId: Long, displayOrder: Int)
    suspend fun removeCategoryFromScene(sceneId: Long, categoryId: Long)
    suspend fun updateMixVolume(sceneId: Long, categoryId: Long, mixVolume: Float)
    suspend fun updateIntensityLevel(sceneId: Long, categoryId: Long, intensityLevel: Int)
    suspend fun updateDisplayOrders(sceneId: Long, categoryIds: List<Long>)
}
