package com.example.rpgaudiomixer.domain.campaign

import kotlinx.coroutines.flow.Flow

interface CampaignRepository {
    fun observeAll(): Flow<List<Campaign>>
    suspend fun upsert(campaign: Campaign)
    suspend fun delete(id: Long)
}
