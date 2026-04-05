package com.example.rpgaudiomixer.infra.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.rpgaudiomixer.infra.local.entities.CampaignEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns WHERE deletedAt IS NULL ORDER BY lastPlayedAt DESC")
    fun observeAll(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE deletedAt IS NULL ORDER BY lastPlayedAt DESC LIMIT 1")
    fun observeLatest(): Flow<CampaignEntity?>

    @Query("SELECT * FROM campaigns WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<CampaignEntity>>

    @Query("UPDATE campaigns SET lastPlayedAt = :timestamp WHERE id = :id")
    suspend fun updateLastPlayed(id: Long, timestamp: Long)

    @Upsert
    suspend fun upsert(campaign: CampaignEntity)

    @Query("UPDATE campaigns SET deletedAt = :timestamp WHERE id = :id")
    suspend fun softDelete(id: Long, timestamp: Long)

    @Query("UPDATE campaigns SET deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM campaigns WHERE id = :id")
    suspend fun permanentDelete(id: Long)

    @Query("DELETE FROM campaigns WHERE deletedAt < :threshold")
    suspend fun purgeOldDeleted(threshold: Long)
}
