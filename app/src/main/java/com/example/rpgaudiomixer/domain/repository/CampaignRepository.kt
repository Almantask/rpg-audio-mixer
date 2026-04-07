package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Campaign
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Campaign operations.
 *
 * Abstracts data sources for campaigns.
 */
interface CampaignRepository {
    /**
     * Observe all campaigns as a Flow.
     */
    fun observeAll(): Flow<List<Campaign>>

    /**
     * Get a single campaign by ID.
     */
    suspend fun getById(id: Long): Campaign?

    /**
     * Create a new campaign.
     */
    suspend fun create(name: String, coverArtUri: String?): Long

    /**
     * Update an existing campaign.
     */
    suspend fun update(campaign: Campaign)

    /**
     * Delete a campaign by ID.
     */
    suspend fun delete(id: Long)
}
