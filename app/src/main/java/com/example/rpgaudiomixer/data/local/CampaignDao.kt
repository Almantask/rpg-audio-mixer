package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns WHERE deletedAt IS NULL ORDER BY lastPlayedAt DESC, id DESC")
    fun observeAll(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE id = :campaignId AND deletedAt IS NULL LIMIT 1")
    fun observeById(campaignId: Long): Flow<CampaignEntity?>

    @Query("SELECT * FROM campaigns WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC, id DESC")
    fun observeDeleted(): Flow<List<CampaignEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(campaign: CampaignEntity): Long

    @Query("UPDATE campaigns SET deletedAt = :deletedAt WHERE id = :campaignId")
    suspend fun softDeleteById(campaignId: Long, deletedAt: Long)

    @Query("UPDATE campaigns SET deletedAt = NULL WHERE id = :campaignId")
    suspend fun restoreById(campaignId: Long)

    @Query("DELETE FROM campaigns WHERE deletedAt IS NOT NULL")
    suspend fun deleteAllDeleted()

    @Query("DELETE FROM campaigns WHERE deletedAt IS NOT NULL AND deletedAt < :cutoffTimeMillis")
    suspend fun purgeDeletedBefore(cutoffTimeMillis: Long)

    @Query("DELETE FROM campaigns WHERE id = :campaignId")
    suspend fun deleteById(campaignId: Long)
}
