package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Campaign
import kotlinx.coroutines.flow.Flow

interface CampaignRepository {
    fun observeAll(): Flow<List<Campaign>>
    fun observeLatest(): Flow<Campaign?>
    fun observeDeleted(): Flow<List<Campaign>>
    suspend fun updateLastPlayed(id: Long)
    suspend fun upsert(campaign: Campaign)
    suspend fun softDelete(id: Long)
    suspend fun restore(id: Long)
    suspend fun permanentDelete(id: Long)
    suspend fun purgeOldDeleted(threshold: Long)
}
