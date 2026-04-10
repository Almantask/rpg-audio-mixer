package com.example.rpgaudiomixer.infra.campaign

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns WHERE deletedAt IS NULL ORDER BY lastPlayedAt DESC")
    fun observeAll(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<CampaignEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(campaign: CampaignEntity): Long

    @Query("UPDATE campaigns SET deletedAt = :timestamp WHERE id = :id")
    fun softDelete(id: Long, timestamp: Long)

    @Query("UPDATE campaigns SET deletedAt = NULL WHERE id = :id")
    fun restore(id: Long)

    @Query("DELETE FROM campaigns WHERE id = :id")
    fun delete(id: Long): Int
}
