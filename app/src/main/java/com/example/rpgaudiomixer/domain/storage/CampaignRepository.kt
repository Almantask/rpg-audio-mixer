package com.example.rpgaudiomixer.domain.storage

import com.example.rpgaudiomixer.domain.model.Campaign
import kotlinx.coroutines.flow.Flow

interface CampaignRepository {
    fun getAllCampaigns(): Flow<List<Campaign>>
    fun getCampaignById(id: Long): Flow<Campaign?>
    suspend fun insert(campaign: Campaign): Long
    suspend fun update(campaign: Campaign)
    suspend fun delete(campaign: Campaign)
}
