package com.example.rpgaudiomixer.data.soundscape

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeCategoryDao {
    @Query("SELECT * FROM soundscape_categories ORDER BY name ASC")
    fun observeAll(): Flow<List<SoundscapeCategoryEntity>>

    @Query("SELECT * FROM soundscape_categories WHERE id = :id")
    fun observeById(id: Long): Flow<SoundscapeCategoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SoundscapeCategoryEntity): Long

    @Query("DELETE FROM soundscape_categories WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface SoundscapeTrackDao {
    @Query("SELECT * FROM soundscape_tracks WHERE categoryId = :categoryId ORDER BY name ASC")
    fun observeByCategory(categoryId: Long): Flow<List<SoundscapeTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SoundscapeTrackEntity): Long

    @Update
    suspend fun update(entity: SoundscapeTrackEntity)

    @Query("DELETE FROM soundscape_tracks WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE soundscape_tracks SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: Long)

    @Query("SELECT * FROM soundscape_tracks ORDER BY playCount DESC LIMIT 1")
    fun observeMostPlayed(): Flow<SoundscapeTrackEntity?>
}
