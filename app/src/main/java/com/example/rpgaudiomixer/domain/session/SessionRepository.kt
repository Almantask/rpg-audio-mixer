package com.example.rpgaudiomixer.domain.session

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.ResumeScene
import com.example.rpgaudiomixer.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeSessions(campaignId: Long): Flow<List<Session>>
    fun observeSession(sessionId: Long): Flow<Session?>
    fun observeScenesForSession(sessionId: Long): Flow<List<Scene>>
    fun observeLastOpenedScene(campaignId: Long): Flow<ResumeScene?>
    suspend fun upsertSession(session: Session): Long
    suspend fun deleteSession(sessionId: Long)
    suspend fun linkScenes(sessionId: Long, sceneIds: List<Long>)
    suspend fun unlinkScene(sessionId: Long, sceneId: Long)
    suspend fun markSceneOpened(sessionId: Long, sceneId: Long)
    suspend fun clearAll()
}
