package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Session operations
 */
@Dao
interface SessionDao {
    /**
     * Observe sessions for a specific campaign, sorted by date descending
     */
    @Query("SELECT * FROM sessions WHERE campaignId = :campaignId ORDER BY date DESC")
    fun observeByCampaign(campaignId: Long): Flow<List<SessionEntity>>

    /**
     * Get a session by ID
     */
    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun getById(id: Long): SessionEntity?

    /**
     * Insert or update a session
     */
    @Upsert
    suspend fun upsert(session: SessionEntity): Long

    /**
     * Delete a session
     */
    @Delete
    suspend fun delete(session: SessionEntity)

    /**
     * Delete a session by ID
     */
    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteById(id: Long)
}
