package com.example.rpgaudiomixer.domain.scene

import com.example.rpgaudiomixer.domain.model.Scene
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun observeScenes(): Flow<List<Scene>>

    fun observeScene(sceneId: Long): Flow<Scene?>

    fun observeScenesForSession(sessionId: Long): Flow<List<Scene>>

    fun observeAvailableScenesForSession(sessionId: Long): Flow<List<Scene>>

    suspend fun createScene(
        name: String,
        description: String?,
        tags: List<String>,
    ): Long

    suspend fun deleteScene(sceneId: Long)

    suspend fun linkScenesToSession(sessionId: Long, sceneIds: List<Long>)

    suspend fun unlinkSceneFromSession(sessionId: Long, sceneId: Long)
}
