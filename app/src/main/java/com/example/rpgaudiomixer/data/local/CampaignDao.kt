package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Campaign entities.
 *
 * Provides methods to read, create, update, and delete campaigns.
 */
@Dao
interface CampaignDao {
    /**
     * Observe all campaigns, sorted by last played (most recent first).
     */
    @Query("SELECT * FROM campaigns ORDER BY lastPlayedAt DESC")
    fun observeAll(): Flow<List<CampaignEntity>>

    /**
     * Get a single campaign by ID.
     */
    @Query("SELECT * FROM campaigns WHERE id = :id")
    suspend fun getById(id: Long): CampaignEntity?

    /**
     * Insert a new campaign or update if it already exists.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(campaign: CampaignEntity): Long

    /**
     * Update an existing campaign.
     */
    @Update
    suspend fun update(campaign: CampaignEntity)

    /**
     * Delete a campaign.
     */
    @Delete
    suspend fun delete(campaign: CampaignEntity)

    /**
     * Delete a campaign by ID.
     */
    @Query("DELETE FROM campaigns WHERE id = :id")
    suspend fun deleteById(id: Long)
}
