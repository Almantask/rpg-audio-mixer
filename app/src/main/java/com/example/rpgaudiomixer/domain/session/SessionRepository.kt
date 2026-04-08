package com.example.rpgaudiomixer.domain.session

import com.example.rpgaudiomixer.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeSessions(campaignId: Long): Flow<List<Session>>

    fun observeSession(sessionId: Long): Flow<Session?>

    suspend fun createSession(
        campaignId: Long,
        name: String,
        date: Long,
        coverArtUri: String?,
    ): Long

    suspend fun deleteSession(sessionId: Long)
}
