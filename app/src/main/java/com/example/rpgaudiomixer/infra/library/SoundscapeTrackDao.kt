package com.example.rpgaudiomixer.infra.library

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeTrackDao {
    @Query("SELECT * FROM soundscape_tracks WHERE categoryId = :categoryId")
    fun observeByCategoryId(categoryId: Long): Flow<List<SoundscapeTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(track: SoundscapeTrackEntity): Long

    @Query("DELETE FROM soundscape_tracks WHERE id = :id")
    fun delete(id: Long): Int

    @Query("SELECT * FROM soundscape_tracks ORDER BY playCount DESC LIMIT 1")
    fun getMostPlayed(): Flow<SoundscapeTrackEntity?>

    @Query("UPDATE soundscape_tracks SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: Long)
}
