package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.FXTrack
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneSoundscapeCategory
import com.example.rpgaudiomixer.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeByCampaign(campaignId: Long): Flow<List<Session>>
    fun observeLatestByCampaign(campaignId: Long): Flow<Session?>
    fun observeDeleted(): Flow<List<Session>>
    suspend fun updateLastOpenedScene(sessionId: Long, sceneId: Long)
    suspend fun upsert(session: Session)
    suspend fun softDelete(id: Long)
    suspend fun restore(id: Long)
    suspend fun permanentDelete(id: Long)
    suspend fun purgeOldDeleted(threshold: Long)
}

interface SceneRepository {
    fun observeAll(): Flow<List<Scene>>
    fun observeDeleted(): Flow<List<Scene>>
    fun getById(id: Long): Flow<Scene?>
    fun observeScenesBySession(sessionId: Long): Flow<List<Scene>>
    suspend fun upsert(scene: Scene)
    suspend fun softDelete(id: Long)
    suspend fun restore(id: Long)
    suspend fun permanentDelete(id: Long)
    suspend fun purgeOldDeleted(threshold: Long)
    suspend fun linkToSession(sessionId: Long, sceneId: Long)
    suspend fun unlinkFromSession(sessionId: Long, sceneId: Long)

    // Scene Soundscape Management
    fun observeCategoriesByScene(sceneId: Long): Flow<List<SceneSoundscapeCategory>>
    suspend fun addCategoryToScene(sceneId: Long, categoryId: Long, displayOrder: Int)
    suspend fun removeCategoryFromScene(sceneId: Long, categoryId: Long)
    suspend fun updateSceneCategoryOrder(sceneId: Long, categoryId: Long, newOrder: Int)
    suspend fun updateSceneCategoryMixVolume(sceneId: Long, categoryId: Long, volume: Float)
    suspend fun updateSceneCategoryIntensity(sceneId: Long, categoryId: Long, intensity: IntensityLevel)

    // Scene FX Management
    fun observeFxByScene(sceneId: Long): Flow<List<FXTrack>>
    suspend fun addFxToScene(sceneId: Long, fxId: Long, displayOrder: Int)
    suspend fun removeFxFromScene(sceneId: Long, fxId: Long)
    suspend fun updateSceneFxOrder(sceneId: Long, fxId: Long, newOrder: Int)
}
