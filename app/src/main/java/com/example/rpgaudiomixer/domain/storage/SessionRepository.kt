package com.example.rpgaudiomixer.domain.storage

import com.example.rpgaudiomixer.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getSessionsByCampaign(campaignId: Long): Flow<List<Session>>
    fun getSessionById(id: Long): Flow<Session?>
    suspend fun insert(session: Session): Long
    suspend fun update(session: Session)
    suspend fun delete(session: Session)
}
