package com.example.rpgaudiomixer.infra.session

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE campaignId = :campaignId AND deletedAt IS NULL ORDER BY date DESC")
    fun observeByCampaign(campaignId: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(session: SessionEntity): Long

    @Query("UPDATE sessions SET deletedAt = :timestamp WHERE id = :id")
    fun softDelete(id: Long, timestamp: Long)

    @Query("UPDATE sessions SET deletedAt = NULL WHERE id = :id")
    fun restore(id: Long)

    @Query("DELETE FROM sessions WHERE id = :id")
    fun delete(id: Long): Int

    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun getById(id: Long): SessionEntity?
}
