package com.example.rpgaudiomixer.domain.scene

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun observeScenes(): Flow<List<Scene>>
    fun observeScene(sceneId: Long): Flow<Scene?>
    fun observeSceneSoundscapes(sceneId: Long): Flow<List<SceneSoundscape>>
    fun observeSceneFx(sceneId: Long): Flow<List<SceneFx>>
    suspend fun upsertScene(scene: Scene): Long
    suspend fun deleteScene(sceneId: Long)
    suspend fun addSoundscapeCategory(sceneId: Long, categoryName: String)
    suspend fun removeSoundscapeCategory(sceneId: Long, categoryName: String)
    suspend fun updateSoundscapeMix(sceneId: Long, categoryId: Long, mixVolumePercent: Int)
    suspend fun updateSoundscapeIntensity(sceneId: Long, categoryId: Long, intensityLevel: IntensityLevel)
    suspend fun reorderSoundscapes(sceneId: Long, orderedCategoryIds: List<Long>)
    suspend fun updateSceneAtmosphereVolume(sceneId: Long, volumePercent: Int)
    suspend fun updateSceneSoundboardVolume(sceneId: Long, volumePercent: Int)
    suspend fun addSoundboardEffect(sceneId: Long, effectName: String)
    suspend fun addSoundboardEffect(sceneId: Long, fxTrackId: Long)
    suspend fun removeSoundboardEffect(sceneId: Long, fxTrackId: Long)
    suspend fun reorderSoundboardEffects(sceneId: Long, orderedTrackIds: List<Long>)
    suspend fun removeSoundboardEffect(effectName: String)
    suspend fun clearAll()
}
