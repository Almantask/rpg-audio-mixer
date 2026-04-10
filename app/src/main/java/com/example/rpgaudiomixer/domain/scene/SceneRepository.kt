package com.example.rpgaudiomixer.domain.scene

import com.example.rpgaudiomixer.domain.model.Scene
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun observeAll(): Flow<List<Scene>>
    fun observeBySession(sessionId: Long): Flow<List<Scene>>
    suspend fun getById(id: Long): Scene?
    suspend fun create(name: String, description: String?, tags: List<String>): Long
    suspend fun update(scene: Scene)
    suspend fun delete(id: Long)
    suspend fun linkToSession(sessionId: Long, sceneId: Long)
    suspend fun unlinkFromSession(sessionId: Long, sceneId: Long)
}
