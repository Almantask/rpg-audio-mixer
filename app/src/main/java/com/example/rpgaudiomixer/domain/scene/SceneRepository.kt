package com.example.rpgaudiomixer.domain.scene

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun observeScenes(): Flow<List<Scene>>

    fun observeScene(sceneId: Long): Flow<Scene?>

    fun observeScenesForSession(sessionId: Long): Flow<List<Scene>>

    fun observeAvailableScenesForSession(sessionId: Long): Flow<List<Scene>>

    fun observeSoundscapesForScene(sceneId: Long): Flow<List<SceneSoundscape>>

    fun observeFxForScene(sceneId: Long): Flow<List<SceneFx>>

    suspend fun createScene(
        name: String,
        description: String?,
        tags: List<String>,
    ): Long

    suspend fun cloneScene(sceneId: Long, name: String): Long

    suspend fun updateScene(
        sceneId: Long,
        name: String,
        description: String?,
        tags: List<String>,
    )

    suspend fun deleteScene(sceneId: Long)

    suspend fun linkScenesToSession(sessionId: Long, sceneIds: List<Long>)

    suspend fun unlinkSceneFromSession(sessionId: Long, sceneId: Long)

    suspend fun addSoundscapeToScene(sceneId: Long, categoryId: Long)

    suspend fun updateSoundscapeInScene(
        sceneId: Long,
        categoryId: Long,
        displayOrder: Int,
        mixVolume: Float,
        intensityLevel: IntensityLevel,
    )

    suspend fun reorderSoundscapes(sceneId: Long, orderedCategoryIds: List<Long>)

    suspend fun removeSoundscapeFromScene(sceneId: Long, categoryId: Long)

    suspend fun addFxToScene(sceneId: Long, fxTrackId: Long)

    suspend fun reorderFx(sceneId: Long, orderedFxTrackIds: List<Long>)

    suspend fun removeFxFromScene(sceneId: Long, fxTrackId: Long)
}
