package com.example.rpgaudiomixer.app.domain.repository

import com.example.rpgaudiomixer.app.domain.model.Scene
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun observeAll(): Flow<List<Scene>>
    fun observeBySession(sessionId: Long): Flow<List<Scene>>
    suspend fun createScene(name: String)
    suspend fun deleteScene(scene: Scene)
    suspend fun linkToSession(sceneId: Long, sessionId: Long)
    suspend fun unlinkFromSession(sceneId: Long, sessionId: Long)
    suspend fun deleteAll()
}
