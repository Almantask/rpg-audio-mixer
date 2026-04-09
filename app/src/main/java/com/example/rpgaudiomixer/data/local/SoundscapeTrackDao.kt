package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeTrackDao {
    @Query("SELECT * FROM soundscape_tracks WHERE categoryId = :categoryId ORDER BY id ASC")
    fun observeByCategory(categoryId: Long): Flow<List<SoundscapeTrackEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM soundscape_tracks WHERE isDemo = 1 LIMIT 1)")
    fun observeDemoContentAvailable(): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(track: SoundscapeTrackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(tracks: List<SoundscapeTrackEntity>)

    @Query("DELETE FROM soundscape_tracks WHERE id = :trackId")
    suspend fun deleteById(trackId: Long)

    @Query("DELETE FROM soundscape_tracks WHERE categoryId = :categoryId")
    suspend fun deleteByCategoryId(categoryId: Long)

    @Query(
        """
        DELETE FROM soundscape_tracks
        WHERE categoryId = :categoryId
        AND id NOT IN (:keepTrackIds)
        """
    )
    suspend fun deleteByCategoryIdExcept(
        categoryId: Long,
        keepTrackIds: List<Long>,
    )

    @Query("UPDATE soundscape_tracks SET playCount = playCount + 1 WHERE id = :trackId")
    suspend fun incrementPlayCount(trackId: Long)
}
