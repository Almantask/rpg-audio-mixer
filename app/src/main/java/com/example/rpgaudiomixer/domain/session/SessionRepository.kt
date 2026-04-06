package com.example.rpgaudiomixer.domain.session

import com.example.rpgaudiomixer.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeByCampaign(campaignId: Long): Flow<List<Session>>
    suspend fun create(campaignId: Long, name: String, coverArtUri: String?): Session
    suspend fun delete(id: Long)
}
