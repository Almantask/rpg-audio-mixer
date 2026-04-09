package com.example.rpgaudiomixer.data.soundscape.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeTrackDao {
    @Query("SELECT * FROM soundscape_tracks WHERE categoryId = :categoryId ORDER BY id ASC")
    fun observeByCategory(categoryId: Long): Flow<List<SoundscapeTrackEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM soundscape_tracks WHERE filePath LIKE 'demo://%')")
    fun hasDemoTracks(): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(track: SoundscapeTrackEntity): Long

    @Query("DELETE FROM soundscape_tracks WHERE id = :trackId")
    suspend fun deleteById(trackId: Long)

    @Query("DELETE FROM soundscape_tracks WHERE categoryId = :categoryId AND id NOT IN (:trackIds)")
    suspend fun deleteMissingFromCategory(categoryId: Long, trackIds: List<Long>)

    @Query("DELETE FROM soundscape_tracks WHERE categoryId = :categoryId")
    suspend fun deleteByCategory(categoryId: Long)

    @Query("DELETE FROM soundscape_tracks")
    suspend fun clearAll()
}
