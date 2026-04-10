package com.example.rpgaudiomixer.infra.library

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FxTrackDao {
    @Query("SELECT * FROM fx_tracks WHERE deletedAt IS NULL ORDER BY name ASC")
    fun observeAll(): Flow<List<FxTrackEntity>>

    @Query("SELECT * FROM fx_tracks WHERE (name LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%') AND deletedAt IS NULL ORDER BY name ASC")
    fun search(query: String): Flow<List<FxTrackEntity>>

    @Query("SELECT * FROM fx_tracks WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<FxTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(fxTrack: FxTrackEntity): Long

    @Query("UPDATE fx_tracks SET deletedAt = :timestamp WHERE id = :id")
    fun softDelete(id: Long, timestamp: Long)

    @Query("UPDATE fx_tracks SET deletedAt = NULL WHERE id = :id")
    fun restore(id: Long)

    @Query("DELETE FROM fx_tracks WHERE id = :id")
    fun delete(id: Long): Int

    @Query("SELECT * FROM fx_tracks WHERE id = :id")
    fun observeById(id: Long): Flow<FxTrackEntity?>

    @Query("SELECT * FROM fx_tracks ORDER BY playCount DESC LIMIT 1")
    fun getMostPlayed(): Flow<FxTrackEntity?>

    @Query("UPDATE fx_tracks SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: Long)
}
