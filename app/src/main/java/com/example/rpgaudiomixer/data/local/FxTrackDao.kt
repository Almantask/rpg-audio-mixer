package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FxTrackDao {
    @Query("SELECT * FROM fx_tracks WHERE isDeleted = 0 ORDER BY name ASC, id ASC")
    fun observeAll(): Flow<List<FxTrackEntity>>

    @Query(
        """
        SELECT * FROM fx_tracks
        WHERE isDeleted = 0
          AND name LIKE '%' || :query || '%'
        ORDER BY name ASC, id ASC
        """
    )
    fun search(query: String): Flow<List<FxTrackEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM fx_tracks WHERE isDemo = 1 AND isDeleted = 0 LIMIT 1)")
    fun observeDemoContentAvailable(): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(track: FxTrackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(tracks: List<FxTrackEntity>)

    @Query("UPDATE fx_tracks SET isDeleted = 1 WHERE id = :trackId")
    suspend fun softDelete(trackId: Long)
}
