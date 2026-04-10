package com.example.rpgaudiomixer.data.soundscape.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SoundscapeTrackDao {
    @Query("SELECT * FROM soundscape_tracks WHERE categoryId = :categoryId ORDER BY id ASC")
    suspend fun getByCategory(categoryId: Long): List<SoundscapeTrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SoundscapeTrackEntity): Long

    @Query("DELETE FROM soundscape_tracks WHERE id IN (:trackIds)")
    suspend fun deleteByIds(trackIds: List<Long>)

    @Query("DELETE FROM soundscape_tracks WHERE categoryId = :categoryId")
    suspend fun deleteByCategory(categoryId: Long)
}
