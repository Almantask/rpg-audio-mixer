package com.example.rpgaudiomixer.domain.scene

import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.SceneFx
import kotlinx.coroutines.flow.Flow

interface SceneFxRepository {
    fun observeSceneFx(sceneId: Long): Flow<List<SceneFx>>

    fun observeAvailableFx(sceneId: Long): Flow<List<FxTrack>>

    suspend fun addFxToScene(sceneId: Long, fxTrackId: Long)

    suspend fun removeFxFromScene(sceneId: Long, fxTrackId: Long)

    suspend fun reorderFx(sceneId: Long, orderedFxTrackIds: List<Long>)
}
