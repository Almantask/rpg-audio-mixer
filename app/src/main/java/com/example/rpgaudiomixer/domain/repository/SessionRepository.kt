package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Session
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Session domain operations.
 */
interface SessionRepository {

    /**
     * Observe all sessions for a specific campaign.
     */
    fun observeByCampaign(campaignId: Long): Flow<List<Session>>

    /**
     * Create a new session.
     */
    suspend fun create(campaignId: Long, name: String, date: Long, coverArtUri: String? = null): Long

    /**
     * Update an existing session.
     */
    suspend fun update(session: Session)

    /**
     * Delete a session by ID.
     */
    suspend fun delete(id: Long)

    /**
     * Get a session by ID.
     */
    suspend fun getById(id: Long): Session?
}
