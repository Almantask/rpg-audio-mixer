package com.example.rpgaudiomixer.domain.storage

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun getAllScenes(): Flow<List<Scene>>
    fun getSceneById(id: Long): Flow<Scene?>
    fun getScenesBySession(sessionId: Long): Flow<List<Scene>>
    fun getSoundscapesForScene(sceneId: Long): Flow<List<SceneSoundscape>>
    fun getFxForScene(sceneId: Long): Flow<List<SceneFx>>
    suspend fun insert(scene: Scene): Long
    suspend fun update(scene: Scene)
    suspend fun delete(scene: Scene)
    suspend fun addSceneToSession(sessionId: Long, sceneId: Long)
    suspend fun removeSceneFromSession(sessionId: Long, sceneId: Long)
    suspend fun addSoundscapeToScene(sceneId: Long, categoryId: Long)
    suspend fun removeSoundscapeFromScene(sceneId: Long, categoryId: Long)
    suspend fun updateSceneSoundscapeMix(sceneId: Long, categoryId: Long, mix: Float)
    suspend fun updateSceneSoundscapeIntensity(sceneId: Long, categoryId: Long, intensity: Int)
    suspend fun addFxToScene(sceneId: Long, fxEffectId: Long)
    suspend fun removeFxFromScene(sceneId: Long, fxEffectId: Long)
    suspend fun reorderSoundscapes(sceneId: Long, orderedCategoryIds: List<Long>)
    suspend fun reorderFx(sceneId: Long, orderedFxIds: List<Long>)
}
