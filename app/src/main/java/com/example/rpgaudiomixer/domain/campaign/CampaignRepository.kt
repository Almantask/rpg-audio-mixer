package com.example.rpgaudiomixer.domain.campaign

import com.example.rpgaudiomixer.domain.model.Campaign
import kotlinx.coroutines.flow.Flow

interface CampaignRepository {
    fun observeCampaigns(): Flow<List<Campaign>>
    fun observeCampaign(id: Long): Flow<Campaign?>
    fun observeMostRecentCampaign(): Flow<Campaign?>
    suspend fun upsertCampaign(campaign: Campaign): Long
    suspend fun deleteCampaign(id: Long)
    suspend fun clearAll()
}
