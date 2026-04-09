package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.data.local.SceneFxCrossRef
import kotlinx.coroutines.flow.Flow

interface SceneFxRepository {
    fun observeByScene(sceneId: Long): Flow<List<SceneFxCrossRef>>
    suspend fun addFxToScene(sceneId: Long, fxTrackId: Long, displayOrder: Int)
    suspend fun removeFxFromScene(sceneId: Long, fxTrackId: Long)
    suspend fun updateDisplayOrders(sceneId: Long, fxTrackIds: List<Long>)
}
