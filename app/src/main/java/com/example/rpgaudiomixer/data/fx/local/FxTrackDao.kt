package com.example.rpgaudiomixer.data.fx.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FxTrackDao {
    @Query("SELECT * FROM fx_tracks ORDER BY name COLLATE NOCASE ASC, id ASC")
    fun observeAll(): Flow<List<FxTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FxTrackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<FxTrackEntity>)

    @Query("SELECT COUNT(*) FROM fx_tracks WHERE isDemoContent = 1")
    suspend fun demoTrackCount(): Int

    @Query("DELETE FROM fx_tracks WHERE id = :trackId")
    suspend fun deleteById(trackId: Long)
}
