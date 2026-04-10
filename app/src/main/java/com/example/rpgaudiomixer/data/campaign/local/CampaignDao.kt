package com.example.rpgaudiomixer.data.campaign.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {

    @Query("SELECT * FROM campaigns ORDER BY lastPlayedAt DESC")
    fun observeAll(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE id = :id")
    suspend fun getById(id: Long): CampaignEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(campaign: CampaignEntity): Long

    @Update
    suspend fun update(campaign: CampaignEntity)

    @Delete
    suspend fun delete(campaign: CampaignEntity)

    @Query("DELETE FROM campaigns WHERE id = :id")
    suspend fun deleteById(id: Long)
}
