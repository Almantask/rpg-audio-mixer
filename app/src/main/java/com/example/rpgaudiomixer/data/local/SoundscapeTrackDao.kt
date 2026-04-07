package com.example.rpgaudiomixer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeTrackDao {
    @Query("SELECT * FROM soundscape_tracks WHERE categoryId = :categoryId ORDER BY intensityLevel ASC, name ASC")
    fun observeByCategory(categoryId: Long): Flow<List<SoundscapeTrackEntity>>

    @Query("SELECT * FROM soundscape_tracks WHERE categoryId = :categoryId AND intensityLevel = :level")
    fun observeByCategoryAndLevel(categoryId: Long, level: Int): Flow<List<SoundscapeTrackEntity>>

    @Query("SELECT * FROM soundscape_tracks WHERE id = :id")
    suspend fun getById(id: Long): SoundscapeTrackEntity?

    @Query("SELECT * FROM soundscape_tracks ORDER BY playCount DESC LIMIT 1")
    fun getMostPlayed(): Flow<SoundscapeTrackEntity?>

    @Upsert
    suspend fun upsert(track: SoundscapeTrackEntity): Long

    @Delete
    suspend fun delete(track: SoundscapeTrackEntity)

    @Query("UPDATE soundscape_tracks SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: Long)
}
