package com.example.rpgaudiomixer.app.domain.repository

import com.example.rpgaudiomixer.app.domain.model.Campaign
import kotlinx.coroutines.flow.Flow

interface CampaignRepository {
    fun observeAll(): Flow<List<Campaign>>
    fun observeLatest(): Flow<Campaign?>
    fun observeDeleted(): Flow<List<Campaign>>
    suspend fun createCampaign(name: String, coverArtUri: String? = null)
    suspend fun deleteCampaign(campaign: Campaign)
    suspend fun restore(campaign: Campaign)
    suspend fun hardDelete(campaign: Campaign)
    suspend fun purgeOlderThan(cutoff: Long)
    suspend fun purgeAllDeleted()
    suspend fun deleteAll()
}
