package com.example.rpgaudiomixer.app.data.local.dao

import androidx.room.*
import com.example.rpgaudiomixer.app.data.local.entities.CampaignEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns ORDER BY lastPlayedAt DESC")
    fun observeAll(): Flow<List<CampaignEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(campaign: CampaignEntity): Long

    @Delete
    suspend fun delete(campaign: CampaignEntity)

    @Query("DELETE FROM campaigns")
    suspend fun deleteAll()
}
