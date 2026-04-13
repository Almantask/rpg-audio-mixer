package com.example.rpgaudiomixer.app.domain.repository

import com.example.rpgaudiomixer.app.domain.model.Scene
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun observeAll(): Flow<List<Scene>>
    fun observeDeleted(): Flow<List<Scene>>
    suspend fun getById(id: Long): Scene?
    suspend fun createScene(name: String, description: String? = null, tags: String? = null): Long
    suspend fun updateScene(scene: Scene)
    suspend fun deleteScene(id: Long)
    suspend fun restoreScene(id: Long)
    suspend fun permanentlyDeleteScene(id: Long)
    suspend fun cloneScene(sceneId: Long): Long
    fun observeScenesForSession(sessionId: Long): Flow<List<Scene>>
    suspend fun linkSceneToSession(sessionId: Long, sceneId: Long)
    suspend fun unlinkSceneFromSession(sessionId: Long, sceneId: Long)
    suspend fun deleteAll()
}
