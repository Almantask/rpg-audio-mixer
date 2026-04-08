package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Campaign operations
 */
@Dao
interface CampaignDao {
    /**
     * Observe all campaigns, sorted by most recently played first
     */
    @Query("SELECT * FROM campaigns ORDER BY lastPlayedAt DESC")
    fun observeAll(): Flow<List<CampaignEntity>>

    /**
     * Get a campaign by ID
     */
    @Query("SELECT * FROM campaigns WHERE id = :id")
    suspend fun getById(id: Long): CampaignEntity?

    /**
     * Insert or update a campaign
     */
    @Upsert
    suspend fun upsert(campaign: CampaignEntity): Long

    /**
     * Delete a campaign
     */
    @Delete
    suspend fun delete(campaign: CampaignEntity)

    /**
     * Delete a campaign by ID
     */
    @Query("DELETE FROM campaigns WHERE id = :id")
    suspend fun deleteById(id: Long)
}
