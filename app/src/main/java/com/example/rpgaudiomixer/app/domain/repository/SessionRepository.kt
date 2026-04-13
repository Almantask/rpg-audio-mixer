package com.example.rpgaudiomixer.app.domain.repository

import com.example.rpgaudiomixer.app.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeByCampaign(campaignId: Long): Flow<List<Session>>
    fun observeDeleted(): Flow<List<Session>>
    suspend fun createSession(campaignId: Long, name: String)
    suspend fun deleteSession(session: Session)
    suspend fun restore(session: Session)
    suspend fun hardDelete(session: Session)
    suspend fun purgeOlderThan(cutoff: Long)
    suspend fun purgeAllDeleted()
    suspend fun deleteAll()
}
