package com.example.rpgaudiomixer.domain.campaign

import kotlinx.coroutines.flow.Flow

interface CampaignRepository {
    fun observeAll(): Flow<List<Campaign>>
    fun observeMostRecent(): Flow<Campaign?>
    suspend fun updateCampaignActivity(campaignId: Long, lastOpenedSceneId: Long?)
    fun observeDeleted(): Flow<List<Campaign>>
    suspend fun softDelete(id: Long)
    suspend fun restore(id: Long)
    suspend fun upsert(campaign: Campaign)
    suspend fun delete(id: Long)
}
