package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getSessionsForCampaign(campaignId: Long): Flow<List<Session>>
    suspend fun getSessionById(id: Long): Session?
    suspend fun upsertSession(session: Session): Long
    suspend fun deleteSession(id: Long)
}
