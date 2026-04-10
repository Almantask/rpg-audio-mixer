package com.example.rpgaudiomixer.domain.campaign

import com.example.rpgaudiomixer.domain.model.Campaign
import kotlinx.coroutines.flow.Flow

interface CampaignRepository {
    fun observeAll(): Flow<List<Campaign>>
    fun observeActiveCampaign(): Flow<Campaign?>
    suspend fun createCampaign(name: String, coverArtUri: String?): Long
    suspend fun deleteCampaign(id: Long)
    suspend fun getCampaign(id: Long): Campaign?
    suspend fun touchCampaign(id: Long)
}
