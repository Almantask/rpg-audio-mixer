package com.example.rpgaudiomixer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeTrackDao {
    @Query("SELECT * FROM soundscape_tracks WHERE categoryId = :categoryId ORDER BY intensityLevel ASC, name ASC")
    fun observeByCategory(categoryId: Long): Flow<List<SoundscapeTrackEntity>>

    @Query("SELECT * FROM soundscape_tracks WHERE id = :id")
    suspend fun getById(id: Long): SoundscapeTrackEntity?

    @Upsert
    suspend fun upsert(track: SoundscapeTrackEntity): Long

    @Delete
    suspend fun delete(track: SoundscapeTrackEntity)

    @Query("DELETE FROM soundscape_tracks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE soundscape_tracks SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: Long)
}
