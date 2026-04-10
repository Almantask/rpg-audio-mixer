package com.example.rpgaudiomixer.domain.session

import com.example.rpgaudiomixer.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeByCampaign(campaignId: Long): Flow<List<Session>>
    suspend fun getById(id: Long): Session?
    suspend fun create(campaignId: Long, name: String, date: Long, coverArtUri: String?): Long
    suspend fun update(session: Session)
    suspend fun delete(id: Long)
}
