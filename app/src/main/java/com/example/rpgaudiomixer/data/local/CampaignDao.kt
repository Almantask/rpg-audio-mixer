package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns ORDER BY lastPlayedAt DESC, id DESC")
    fun observeAll(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE id = :campaignId")
    fun observeById(campaignId: Long): Flow<CampaignEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CampaignEntity): Long

    @Query("DELETE FROM campaigns WHERE id = :campaignId")
    suspend fun deleteById(campaignId: Long)

    @Query("UPDATE campaigns SET lastPlayedAt = :playedAtMillis WHERE id = :campaignId")
    suspend fun updateLastPlayedAt(campaignId: Long, playedAtMillis: Long)
}
