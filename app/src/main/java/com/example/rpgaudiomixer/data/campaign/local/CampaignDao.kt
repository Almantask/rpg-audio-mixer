package com.example.rpgaudiomixer.data.campaign.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns ORDER BY lastPlayedAt DESC")
    fun observeAll(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns ORDER BY lastPlayedAt DESC LIMIT 1")
    fun observeActiveCampaign(): Flow<CampaignEntity?>

    @Query("SELECT * FROM campaigns WHERE id = :campaignId LIMIT 1")
    suspend fun getById(campaignId: Long): CampaignEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CampaignEntity): Long

    @Query("UPDATE campaigns SET lastPlayedAt = :lastPlayedAt WHERE id = :campaignId")
    suspend fun updateLastPlayedAt(campaignId: Long, lastPlayedAt: Long)

    @Query("DELETE FROM campaigns WHERE id = :campaignId")
    suspend fun delete(campaignId: Long)
}
