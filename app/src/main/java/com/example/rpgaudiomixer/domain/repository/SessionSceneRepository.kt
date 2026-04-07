package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Scene
import kotlinx.coroutines.flow.Flow

interface SessionSceneRepository {
    fun observeScenesBySession(sessionId: Long): Flow<List<Scene>>
    suspend fun linkScene(sessionId: Long, sceneId: Long)
    suspend fun unlinkScene(sessionId: Long, sceneId: Long)
}
