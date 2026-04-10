package com.example.rpgaudiomixer.domain.scene

import com.example.rpgaudiomixer.domain.library.FxTrack
import com.example.rpgaudiomixer.domain.library.IntensityLevel
import com.example.rpgaudiomixer.domain.library.SoundscapeCategory
import kotlinx.coroutines.flow.Flow

data class SceneActiveSoundscape(
    val category: SoundscapeCategory,
    val displayOrder: Int,
    val mixVolume: Float,
    val intensityLevel: IntensityLevel
)

data class SceneActiveFx(
    val fxTrack: FxTrack,
    val displayOrder: Int
)

interface SceneRepository {
    fun observeAll(): Flow<List<Scene>>
    fun observeById(id: Long): Flow<Scene?>
    fun observeDeleted(): Flow<List<Scene>>
    suspend fun softDelete(id: Long)
    suspend fun restore(id: Long)
    suspend fun upsert(scene: Scene)
    suspend fun delete(id: Long)
    
    fun observeSceneActiveSoundscapes(sceneId: Long): Flow<List<SceneActiveSoundscape>>
    suspend fun addCategoryToScene(sceneId: Long, categoryId: Long, displayOrder: Int)
    suspend fun removeCategoryFromScene(sceneId: Long, categoryId: Long)
    suspend fun updateSoundscapeMetadata(sceneId: Long, categoryId: Long, mixVolume: Float, intensityLevel: IntensityLevel)
    
    fun observeSceneActiveFx(sceneId: Long): Flow<List<SceneActiveFx>>
    suspend fun addFxToScene(sceneId: Long, fxTrackId: Long, displayOrder: Int)
    suspend fun removeFxFromScene(sceneId: Long, fxTrackId: Long)

    suspend fun reorderSoundscapes(sceneId: Long, categoryIds: List<Long>)
    suspend fun reorderFx(sceneId: Long, fxIds: List<Long>)
}
