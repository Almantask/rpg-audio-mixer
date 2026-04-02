package com.example.rpgaudiomixer.infra.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.rpgaudiomixer.infra.db.entities.CampaignEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns ORDER BY lastPlayedAt DESC")
    fun getAllCampaigns(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE id = :id LIMIT 1")
    suspend fun getCampaignById(id: Long): CampaignEntity?

    @Upsert
    suspend fun upsertCampaign(campaign: CampaignEntity): Long

    @Query("DELETE FROM campaigns WHERE id = :id")
    suspend fun deleteCampaign(id: Long)

    @Query("UPDATE campaigns SET lastPlayedAt = :timestamp WHERE id = :id")
    suspend fun touchLastPlayed(id: Long, timestamp: Long)

    @Query("SELECT * FROM campaigns ORDER BY lastPlayedAt DESC LIMIT 1")
    fun getMostRecentCampaign(): Flow<CampaignEntity?>
}
