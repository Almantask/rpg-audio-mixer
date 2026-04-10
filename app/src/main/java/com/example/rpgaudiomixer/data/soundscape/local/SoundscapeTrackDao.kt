package com.example.rpgaudiomixer.data.soundscape.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeTrackDao {

    @Query("SELECT * FROM soundscape_tracks WHERE categoryId = :categoryId ORDER BY intensityLevel ASC, name ASC")
    fun observeByCategory(categoryId: Long): Flow<List<SoundscapeTrackEntity>>

    @Query("SELECT * FROM soundscape_tracks WHERE id = :trackId")
    suspend fun getById(trackId: Long): SoundscapeTrackEntity?

    @Query("SELECT * FROM soundscape_tracks WHERE categoryId = :categoryId AND intensityLevel = :level")
    fun observeByIntensity(categoryId: Long, level: Int): Flow<List<SoundscapeTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: SoundscapeTrackEntity): Long

    @Update
    suspend fun update(track: SoundscapeTrackEntity)

    @Query("DELETE FROM soundscape_tracks WHERE id = :trackId")
    suspend fun delete(trackId: Long)

    @Query("UPDATE soundscape_tracks SET playCount = playCount + 1 WHERE id = :trackId")
    suspend fun incrementPlayCount(trackId: Long)
}
