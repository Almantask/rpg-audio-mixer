package com.example.rpgaudiomixer.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rpgaudiomixer.app.data.local.entities.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE campaignId = :campaignId AND isDeleted = 0 ORDER BY date DESC")
    fun observeByCampaign(campaignId: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE isDeleted = 1")
    fun observeDeleted(): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: SessionEntity): Long

    @Query("UPDATE sessions SET isDeleted = 1, deletedAt = (strftime('%s','now') * 1000) WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("UPDATE sessions SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun permanentlyDelete(id: Long)

    @Query(
        "UPDATE sessions SET isDeleted = 1, deletedAt = (strftime('%s','now') * 1000) " +
            "WHERE campaignId = :campaignId"
    )
    suspend fun softDeleteByCampaign(campaignId: Long)

    @Query("UPDATE sessions SET isDeleted = 0, deletedAt = NULL WHERE campaignId = :campaignId")
    suspend fun restoreByCampaign(campaignId: Long)

    @Query("DELETE FROM sessions")
    suspend fun deleteAll()
}
