package com.example.rpgaudiomixer.app.domain.repository

import com.example.rpgaudiomixer.app.domain.model.Campaign
import kotlinx.coroutines.flow.Flow

interface CampaignRepository {
    fun observeAll(): Flow<List<Campaign>>
    fun observeDeleted(): Flow<List<Campaign>>
    suspend fun createCampaign(name: String, coverArtUri: String? = null)
    suspend fun deleteCampaign(campaignId: Long)
    suspend fun restoreCampaign(id: Long)
    suspend fun permanentlyDeleteCampaign(id: Long)
    suspend fun deleteAll()
}
