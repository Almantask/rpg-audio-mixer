package com.example.rpgaudiomixer.domain.campaign

import com.example.rpgaudiomixer.domain.model.Campaign
import kotlinx.coroutines.flow.Flow

interface CampaignRepository {
    fun observeAll(): Flow<List<Campaign>>
    suspend fun create(name: String, coverArtUri: String?): Campaign
    suspend fun delete(id: Long)
    suspend fun updateLastPlayed(id: Long, timestamp: Long)
}
