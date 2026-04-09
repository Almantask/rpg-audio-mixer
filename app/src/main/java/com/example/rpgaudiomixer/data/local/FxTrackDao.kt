package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FxTrackDao {
    @Query("SELECT * FROM fx_tracks ORDER BY name ASC")
    fun observeAll(): Flow<List<FxTrackEntity>>

    @Query("SELECT * FROM fx_tracks WHERE id = :id")
    suspend fun getById(id: Long): FxTrackEntity?

    @Query("""
        SELECT * FROM fx_tracks
        WHERE name LIKE '%' || :query || '%'
        OR tags LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun search(query: String): Flow<List<FxTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(track: FxTrackEntity): Long

    @Query("DELETE FROM fx_tracks WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM fx_tracks ORDER BY playCount DESC LIMIT 1")
    fun getMostPlayed(): Flow<FxTrackEntity?>

    @Query("UPDATE fx_tracks SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: Long)
}
