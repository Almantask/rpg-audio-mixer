package com.example.rpgaudiomixer.domain.session

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeByCampaign(campaignId: Long): Flow<List<Session>>
    fun observeDeleted(): Flow<List<Session>>
    suspend fun softDelete(id: Long)
    suspend fun restore(id: Long)
    suspend fun upsert(session: Session)
    suspend fun delete(id: Long)
    
    fun observeScenesBySession(sessionId: Long): Flow<List<com.example.rpgaudiomixer.domain.scene.Scene>>
    suspend fun updateSessionActivity(sessionId: Long)
    suspend fun linkScene(sessionId: Long, sceneId: Long)
    suspend fun unlinkScene(sessionId: Long, sceneId: Long)
}
