package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFXTrack
import com.example.rpgaudiomixer.domain.model.SceneSoundscapeCategory
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun getAllScenes(): Flow<List<Scene>>
    fun getScenesForSession(sessionId: Long): Flow<List<Scene>>
    suspend fun getSceneById(id: Long): Scene?
    suspend fun upsertScene(scene: Scene): Long
    suspend fun deleteScene(id: Long)

    // Session ↔ Scene mapping
    suspend fun addSceneToSession(sessionId: Long, sceneId: Long)
    suspend fun removeSceneFromSession(sessionId: Long, sceneId: Long)

    // Soundscape categories attached to a scene
    fun getSceneSoundscapeCategories(sceneId: Long): Flow<List<SceneSoundscapeCategory>>
    suspend fun addCategoryToScene(sceneId: Long, categoryId: Long): Long
    suspend fun updateCategoryMixVolume(sceneCategoryId: Long, volume: Float)
    suspend fun updateCategorySortOrder(sceneCategoryId: Long, sortOrder: Int)
    suspend fun removeCategoryFromScene(sceneCategoryId: Long)

    // FX attached to a scene
    fun getSceneFXTracks(sceneId: Long): Flow<List<SceneFXTrack>>
    suspend fun addFXToScene(sceneId: Long, fxTrackId: Long): Long
    suspend fun updateFXSortOrder(sceneFxId: Long, sortOrder: Int)
    suspend fun removeFXFromScene(sceneFxId: Long)

    // Volume persistence for scene
    suspend fun updateSceneMasterAtmosphereVolume(sceneId: Long, volume: Float)
    suspend fun updateSceneMasterSoundboardVolume(sceneId: Long, volume: Float)
}
