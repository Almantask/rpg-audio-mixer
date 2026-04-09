package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns WHERE deletedAt IS NULL ORDER BY lastPlayedAt DESC")
    fun observeAll(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE deletedAt IS NULL ORDER BY lastPlayedAt DESC LIMIT 1")
    fun observeMostRecent(): Flow<CampaignEntity?>

    @Query("SELECT * FROM campaigns WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE id = :id")
    suspend fun getById(id: Long): CampaignEntity?

    @Upsert
    suspend fun upsert(campaign: CampaignEntity): Long

    @Query("UPDATE campaigns SET deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDelete(id: Long, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE campaigns SET deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("UPDATE campaigns SET lastPlayedAt = :timestamp WHERE id = :id")
    suspend fun updateLastPlayedAt(id: Long, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun delete(campaign: CampaignEntity)
}
