package com.example.rpgaudiomixer.app.data.local.dao

import androidx.room.*
import com.example.rpgaudiomixer.app.data.local.entities.CampaignEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns WHERE isDeleted = 0 ORDER BY lastPlayedAt DESC")
    fun observeAll(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE isDeleted = 0 ORDER BY lastPlayedAt DESC LIMIT 1")
    fun observeLatest(): Flow<CampaignEntity?>

    @Query("SELECT * FROM campaigns WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<CampaignEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(campaign: CampaignEntity): Long

    @Delete
    suspend fun delete(campaign: CampaignEntity)

    @Query("UPDATE campaigns SET isDeleted = 1, deletedAt = :timestamp WHERE id = :id")
    suspend fun softDelete(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE campaigns SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM campaigns WHERE id = :id")
    suspend fun hardDelete(id: Long)

    @Query("DELETE FROM campaigns WHERE isDeleted = 1 AND deletedAt < :cutoff")
    suspend fun purgeOlderThan(cutoff: Long)

    @Query("DELETE FROM campaigns WHERE isDeleted = 1")
    suspend fun purgeAllDeleted()

    @Query("DELETE FROM campaigns")
    suspend fun deleteAll()
}

