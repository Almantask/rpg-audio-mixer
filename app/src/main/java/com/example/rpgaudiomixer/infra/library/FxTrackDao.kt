package com.example.rpgaudiomixer.infra.library

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FxTrackDao {
    @Query("SELECT * FROM fx_tracks ORDER BY name ASC")
    fun observeAll(): Flow<List<FxTrackEntity>>

    @Query("SELECT * FROM fx_tracks WHERE name LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): Flow<List<FxTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(fxTrack: FxTrackEntity): Long

    @Query("DELETE FROM fx_tracks WHERE id = :id")
    fun delete(id: Long): Int

    @Query("SELECT * FROM fx_tracks WHERE id = :id")
    fun observeById(id: Long): Flow<FxTrackEntity?>
}
