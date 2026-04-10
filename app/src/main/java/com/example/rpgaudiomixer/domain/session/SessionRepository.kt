package com.example.rpgaudiomixer.domain.session

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeSessionsByCampaign(campaignId: Long): Flow<List<Session>>

    fun observeSession(sessionId: Long): Flow<Session?>

    fun observeScenesBySession(sessionId: Long): Flow<List<Scene>>

    suspend fun createSession(
        campaignId: Long,
        name: String,
        dateMillis: Long,
        coverArtUri: String?,
    ): Long

    suspend fun deleteSession(sessionId: Long)

    suspend fun linkScenes(sessionId: Long, sceneIds: List<Long>)

    suspend fun unlinkScene(sessionId: Long, sceneId: Long)
}
