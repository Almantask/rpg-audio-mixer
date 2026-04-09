package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE campaignId = :campaignId AND deletedAt IS NULL ORDER BY date DESC")
    fun observeByCampaign(campaignId: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun getById(id: Long): SessionEntity?

    @Upsert
    suspend fun upsert(session: SessionEntity): Long

    @Query("UPDATE sessions SET deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDelete(id: Long, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE sessions SET deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Delete
    suspend fun delete(session: SessionEntity)
}
