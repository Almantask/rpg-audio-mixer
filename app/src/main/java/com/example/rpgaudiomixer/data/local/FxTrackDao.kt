package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FxTrackDao {
    @Query("SELECT * FROM fx_tracks WHERE deletedAt IS NULL ORDER BY name ASC")
    fun observeAll(): Flow<List<FxTrackEntity>>

    @Query("SELECT * FROM fx_tracks WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<FxTrackEntity>>

    @Query("SELECT * FROM fx_tracks WHERE id = :id")
    suspend fun getById(id: Long): FxTrackEntity?

    @Query("""
        SELECT * FROM fx_tracks
        WHERE deletedAt IS NULL AND (name LIKE '%' || :query || '%'
        OR tags LIKE '%' || :query || '%')
        ORDER BY name ASC
    """)
    fun search(query: String): Flow<List<FxTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(track: FxTrackEntity): Long

    @Query("UPDATE fx_tracks SET deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDelete(id: Long, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE fx_tracks SET deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM fx_tracks WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM fx_tracks WHERE deletedAt IS NULL ORDER BY playCount DESC LIMIT 1")
    fun getMostPlayed(): Flow<FxTrackEntity?>

    @Query("UPDATE fx_tracks SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: Long)
}
