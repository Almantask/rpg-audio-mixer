package com.example.rpgaudiomixer.infra.campaign

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns ORDER BY lastPlayedAt DESC")
    fun observeAll(): Flow<List<CampaignEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(campaign: CampaignEntity): Long

    @Query("DELETE FROM campaigns WHERE id = :id")
    fun delete(id: Long): Int
}
