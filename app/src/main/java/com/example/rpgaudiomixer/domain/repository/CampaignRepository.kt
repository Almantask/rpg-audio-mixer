package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Campaign
import kotlinx.coroutines.flow.Flow

interface CampaignRepository {
    fun getAllCampaigns(): Flow<List<Campaign>>
    suspend fun getCampaignById(id: Long): Campaign?
    suspend fun upsertCampaign(campaign: Campaign): Long
    suspend fun deleteCampaign(id: Long)
    suspend fun touchLastPlayed(id: Long)
    fun getMostRecentCampaign(): Flow<Campaign?>
}
