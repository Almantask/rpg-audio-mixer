package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Scene
import kotlinx.coroutines.flow.Flow

interface SessionSceneRepository {
    fun observeScenesBySession(sessionId: Long): Flow<List<Scene>>
    suspend fun linkSceneToSession(sessionId: Long, sceneId: Long)
    suspend fun unlinkSceneFromSession(sessionId: Long, sceneId: Long)
}
