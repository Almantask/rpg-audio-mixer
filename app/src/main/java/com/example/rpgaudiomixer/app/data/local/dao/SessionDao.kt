package com.example.rpgaudiomixer.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rpgaudiomixer.app.data.local.entities.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE campaignId = :campaignId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun observeByCampaign(campaignId: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: SessionEntity): Long

    @Query("UPDATE sessions SET isDeleted = 1, deletedAt = :timestamp WHERE id = :id")
    suspend fun softDelete(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE sessions SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun hardDelete(id: Long)

    @Query("DELETE FROM sessions WHERE isDeleted = 1 AND deletedAt < :cutoff")
    suspend fun purgeOlderThan(cutoff: Long)

    @Query("DELETE FROM sessions WHERE isDeleted = 1")
    suspend fun purgeAllDeleted()

    @Query("DELETE FROM sessions WHERE campaignId = :campaignId")
    suspend fun deleteAllByCampaign(campaignId: Long)

    @Query("DELETE FROM sessions")
    suspend fun deleteAll()
}

