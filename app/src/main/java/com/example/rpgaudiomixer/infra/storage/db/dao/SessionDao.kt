package com.example.rpgaudiomixer.infra.storage.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rpgaudiomixer.infra.storage.db.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE campaign_id = :campaignId ORDER BY played_at DESC")
    fun getSessionsByCampaign(campaignId: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE id = :id")
    fun getSessionById(id: Long): Flow<SessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: SessionEntity): Long

    @Update
    suspend fun update(session: SessionEntity)

    @Delete
    suspend fun delete(session: SessionEntity)
}
