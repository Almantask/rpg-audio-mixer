package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Campaign
import kotlinx.coroutines.flow.Flow

interface CampaignRepository {
    fun observeAll(): Flow<List<Campaign>>
    suspend fun getById(id: Long): Campaign?
    suspend fun create(name: String, coverArtUri: String?): Long
    suspend fun update(campaign: Campaign)
    suspend fun delete(id: Long)
}
