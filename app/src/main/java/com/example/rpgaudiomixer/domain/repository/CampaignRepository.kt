package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Campaign
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Campaign operations
 */
interface CampaignRepository {
    /**
     * Observe all campaigns, sorted by most recently played first
     */
    fun observeAll(): Flow<List<Campaign>>

    /**
     * Get a campaign by ID
     */
    suspend fun getById(id: Long): Campaign?

    /**
     * Create or update a campaign
     */
    suspend fun upsert(campaign: Campaign): Long

    /**
     * Delete a campaign by ID
     */
    suspend fun deleteById(id: Long)
}
