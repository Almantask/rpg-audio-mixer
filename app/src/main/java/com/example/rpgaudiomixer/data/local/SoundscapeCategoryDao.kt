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
        WHERE soundscape_categories.deletedAt IS NULL
        GROUP BY soundscape_categories.id
        ORDER BY soundscape_categories.name ASC, soundscape_categories.id ASC
        """
    )
    fun observeAll(): Flow<List<SoundscapeCategorySummaryEntity>>

    @Query("SELECT * FROM soundscape_categories WHERE id = :categoryId AND deletedAt IS NULL LIMIT 1")
    fun observeById(categoryId: Long): Flow<SoundscapeCategoryEntity?>

    @Query("SELECT * FROM soundscape_categories WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC, id DESC")
    fun observeDeleted(): Flow<List<SoundscapeCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: SoundscapeCategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(categories: List<SoundscapeCategoryEntity>)

    @Query("UPDATE soundscape_categories SET deletedAt = :deletedAt WHERE id = :categoryId")
    suspend fun softDeleteById(categoryId: Long, deletedAt: Long)

    @Query("UPDATE soundscape_categories SET deletedAt = NULL WHERE id = :categoryId")
    suspend fun restoreById(categoryId: Long)

    @Query("DELETE FROM soundscape_categories WHERE deletedAt IS NOT NULL")
    suspend fun deleteAllDeleted()

    @Query("DELETE FROM soundscape_categories WHERE deletedAt IS NOT NULL AND deletedAt < :cutoffTimeMillis")
    suspend fun purgeDeletedBefore(cutoffTimeMillis: Long)

    @Query("DELETE FROM soundscape_categories WHERE id = :categoryId")
    suspend fun deleteById(categoryId: Long)
}
