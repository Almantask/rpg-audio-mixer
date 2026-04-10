package com.example.rpgaudiomixer.domain.campaign

import com.example.rpgaudiomixer.domain.model.Campaign
import kotlinx.coroutines.flow.Flow

interface CampaignRepository {
    fun observeCampaigns(): Flow<List<Campaign>>

    fun observeCampaign(campaignId: Long): Flow<Campaign?>

    fun observeActiveCampaign(): Flow<Campaign?>

    suspend fun createCampaign(name: String, coverArtUri: String?): Long

    suspend fun deleteCampaign(campaignId: Long, deletedAtMillis: Long = System.currentTimeMillis())

    suspend fun markCampaignPlayed(campaignId: Long, playedAtMillis: Long = System.currentTimeMillis())
}
