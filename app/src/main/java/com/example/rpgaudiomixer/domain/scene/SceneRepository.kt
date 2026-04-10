package com.example.rpgaudiomixer.domain.scene

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun observeAll(): Flow<List<Scene>>
    fun observeFx(sceneId: Long): Flow<List<SceneFx>>
    fun observeSoundscapes(sceneId: Long): Flow<List<SceneSoundscape>>
    suspend fun createScene(
        name: String,
        description: String?,
        tags: List<String>,
    ): Long

    suspend fun deleteScene(sceneId: Long)
    suspend fun getScene(sceneId: Long): Scene?
    suspend fun addFx(sceneId: Long, fxTrackIds: List<Long>)
    suspend fun removeFx(sceneId: Long, fxTrackId: Long)
    suspend fun reorderFx(sceneId: Long, orderedFxTrackIds: List<Long>)
    suspend fun addSoundscapes(sceneId: Long, categoryIds: List<Long>)
    suspend fun removeSoundscape(sceneId: Long, categoryId: Long)
    suspend fun updateSoundscapeMix(sceneId: Long, categoryId: Long, mixVolume: Float)
    suspend fun updateSoundscapeIntensity(
        sceneId: Long,
        categoryId: Long,
        intensityLevel: IntensityLevel,
    )

    suspend fun reorderSoundscapes(sceneId: Long, orderedCategoryIds: List<Long>)
}
