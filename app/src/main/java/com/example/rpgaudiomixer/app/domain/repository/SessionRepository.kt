package com.example.rpgaudiomixer.app.domain.repository

import com.example.rpgaudiomixer.app.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeByCampaign(campaignId: Long): Flow<List<Session>>
    suspend fun createSession(campaignId: Long, name: String)
    suspend fun deleteSession(session: Session)
    suspend fun deleteAll()
}
