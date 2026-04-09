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

    @Query("SELECT * FROM campaigns WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<CampaignEntity?>

    @Query("SELECT * FROM campaigns ORDER BY lastPlayedAt DESC LIMIT 1")
    fun observeMostRecent(): Flow<CampaignEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(campaign: CampaignEntity): Long

    @Query("DELETE FROM campaigns WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM campaigns")
    suspend fun clearAll()
}
