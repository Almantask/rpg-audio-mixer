package com.example.rpgaudiomixer.infra.local.dao

import androidx.room.*
import com.example.rpgaudiomixer.infra.local.entities.FXTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FXDao {
    @Query("SELECT * FROM fx_tracks WHERE deletedAt IS NULL ORDER BY name ASC")
    fun observeAll(): Flow<List<FXTrackEntity>>

    @Query("SELECT * FROM fx_tracks WHERE (name LIKE :query OR tags LIKE :query) AND deletedAt IS NULL ORDER BY name ASC")
    fun search(query: String): Flow<List<FXTrackEntity>>

    @Query("SELECT * FROM fx_tracks WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<FXTrackEntity>>

    @Upsert
    suspend fun upsert(track: FXTrackEntity)

    @Query("SELECT * FROM fx_tracks WHERE id = :id")
    suspend fun getById(id: Long): FXTrackEntity?

    @Query("SELECT * FROM fx_tracks WHERE deletedAt IS NULL ORDER BY playCount DESC LIMIT 1")
    fun observeMostPlayed(): Flow<FXTrackEntity?>

    @Query("UPDATE fx_tracks SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: Long)

    @Query("UPDATE fx_tracks SET deletedAt = :timestamp WHERE id = :id")
    suspend fun softDelete(id: Long, timestamp: Long)

    @Query("UPDATE fx_tracks SET deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM fx_tracks WHERE id = :id")
    suspend fun permanentDelete(id: Long)

    @Query("DELETE FROM fx_tracks WHERE deletedAt < :threshold")
    suspend fun purgeOldDeleted(threshold: Long)
}
