package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Session
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Session operations
 */
interface SessionRepository {
    /**
     * Observe sessions for a specific campaign, sorted by date descending
     */
    fun observeByCampaign(campaignId: Long): Flow<List<Session>>

    /**
     * Get a session by ID
     */
    suspend fun getById(id: Long): Session?

    /**
     * Create or update a session
     */
    suspend fun upsert(session: Session): Long

    /**
     * Delete a session by ID
     */
    suspend fun deleteById(id: Long)
}
