package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeCategoryDao {
    @Query(
        """
        SELECT soundscape_categories.id,
               soundscape_categories.name,
               soundscape_categories.iconResId,
               soundscape_categories.themeLabel,
               COALESCE(SUM(CASE WHEN soundscape_tracks.intensityLevel = 1 THEN 1 ELSE 0 END), 0) AS levelOneTrackCount,
               COALESCE(SUM(CASE WHEN soundscape_tracks.intensityLevel = 2 THEN 1 ELSE 0 END), 0) AS levelTwoTrackCount,
               COALESCE(SUM(CASE WHEN soundscape_tracks.intensityLevel = 3 THEN 1 ELSE 0 END), 0) AS levelThreeTrackCount
        FROM soundscape_categories
        LEFT JOIN soundscape_tracks
            ON soundscape_categories.id = soundscape_tracks.categoryId
        GROUP BY soundscape_categories.id
        ORDER BY soundscape_categories.name ASC, soundscape_categories.id ASC
        """
    )
    fun observeAll(): Flow<List<SoundscapeCategorySummaryEntity>>

    @Query("SELECT * FROM soundscape_categories WHERE id = :categoryId LIMIT 1")
    fun observeById(categoryId: Long): Flow<SoundscapeCategoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: SoundscapeCategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(categories: List<SoundscapeCategoryEntity>)

    @Query("DELETE FROM soundscape_categories WHERE id = :categoryId")
    suspend fun deleteById(categoryId: Long)
}
