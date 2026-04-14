package com.example.rpgaudiomixer.app.domain.repository

import com.example.rpgaudiomixer.app.domain.model.Scene
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun observeAll(): Flow<List<Scene>>
    fun observeLatest(): Flow<Scene?>
    fun observeBySession(sessionId: Long): Flow<List<Scene>>
    fun observeDeleted(): Flow<List<Scene>>
    suspend fun createScene(name: String)
    suspend fun deleteScene(scene: Scene)
    suspend fun restore(scene: Scene)
    suspend fun hardDelete(scene: Scene)
    suspend fun purgeOlderThan(cutoff: Long)
    suspend fun purgeAllDeleted()
    suspend fun linkToSession(sceneId: Long, sessionId: Long)
    suspend fun unlinkFromSession(sceneId: Long, sessionId: Long)
    suspend fun deleteAll()
    suspend fun cloneScene(sourceSceneId: Long, newName: String): Long
}
