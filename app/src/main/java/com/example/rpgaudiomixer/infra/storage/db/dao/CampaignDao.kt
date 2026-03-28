package com.example.rpgaudiomixer.infra.storage.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rpgaudiomixer.infra.storage.db.entity.CampaignEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns ORDER BY last_played_at DESC")
    fun getAllCampaigns(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE id = :id")
    fun getCampaignById(id: Long): Flow<CampaignEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(campaign: CampaignEntity): Long

    @Update
    suspend fun update(campaign: CampaignEntity)

    @Delete
    suspend fun delete(campaign: CampaignEntity)
}
