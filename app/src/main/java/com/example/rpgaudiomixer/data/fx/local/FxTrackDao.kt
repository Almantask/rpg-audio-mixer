package com.example.rpgaudiomixer.data.fx.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FxTrackDao {

    @Query("SELECT * FROM fx_tracks ORDER BY name ASC")
    fun observeAll(): Flow<List<FxTrackEntity>>

    @Query("SELECT * FROM fx_tracks WHERE name LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): Flow<List<FxTrackEntity>>

    @Query("SELECT * FROM fx_tracks WHERE id = :trackId")
    suspend fun getById(trackId: Long): FxTrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: FxTrackEntity): Long

    @Update
    suspend fun update(track: FxTrackEntity)

    @Query("DELETE FROM fx_tracks WHERE id = :trackId")
    suspend fun delete(trackId: Long)

    @Query("UPDATE fx_tracks SET playCount = playCount + 1 WHERE id = :trackId")
    suspend fun incrementPlayCount(trackId: Long)

    @Query("SELECT * FROM fx_tracks ORDER BY playCount DESC LIMIT 1")
    fun getMostPlayedFx(): Flow<FxTrackEntity?>
}
