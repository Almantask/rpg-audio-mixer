package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeTrackDao {
    @Query("SELECT * FROM soundscape_tracks WHERE categoryId = :categoryId ORDER BY intensityLevel ASC, name ASC")
    fun observeByCategory(categoryId: Long): Flow<List<SoundscapeTrackEntity>>

    @Query("SELECT * FROM soundscape_tracks WHERE id = :id")
    suspend fun getById(id: Long): SoundscapeTrackEntity?

    @Query("SELECT COUNT(*) FROM soundscape_tracks WHERE categoryId = :categoryId AND intensityLevel = :intensityLevel")
    suspend fun getCountByCategoryAndIntensity(categoryId: Long, intensityLevel: Int): Int

    @Query("SELECT * FROM soundscape_tracks WHERE categoryId = :categoryId AND intensityLevel = :intensityLevel")
    suspend fun getByCategoryAndIntensity(categoryId: Long, intensityLevel: Int): List<SoundscapeTrackEntity>

    @Query("SELECT * FROM soundscape_tracks ORDER BY playCount DESC LIMIT 1")
    fun observeMostPlayed(): Flow<SoundscapeTrackEntity?>

    @Query("UPDATE soundscape_tracks SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: Long)

    @Upsert
    suspend fun upsert(track: SoundscapeTrackEntity): Long

    @Delete
    suspend fun delete(track: SoundscapeTrackEntity)
}
