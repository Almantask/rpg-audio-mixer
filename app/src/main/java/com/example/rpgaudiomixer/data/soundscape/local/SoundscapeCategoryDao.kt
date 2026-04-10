package com.example.rpgaudiomixer.data.soundscape.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeCategoryDao {
    @Query(
        """
        SELECT
            soundscape_categories.id,
            soundscape_categories.name,
            soundscape_categories.iconResId,
            soundscape_categories.themeLabel,
            SUM(CASE WHEN soundscape_tracks.intensityLevel = 1 THEN 1 ELSE 0 END) AS levelOneTrackCount,
            SUM(CASE WHEN soundscape_tracks.intensityLevel = 2 THEN 1 ELSE 0 END) AS levelTwoTrackCount,
            SUM(CASE WHEN soundscape_tracks.intensityLevel = 3 THEN 1 ELSE 0 END) AS levelThreeTrackCount
        FROM soundscape_categories
        LEFT JOIN soundscape_tracks
            ON soundscape_categories.id = soundscape_tracks.categoryId
        WHERE soundscape_categories.deletedAt IS NULL
        GROUP BY soundscape_categories.id
        ORDER BY soundscape_categories.name ASC
        """,
    )
    fun observeAll(): Flow<List<SoundscapeCategoryListItemEntity>>

    @Query("SELECT * FROM soundscape_categories WHERE id = :categoryId AND deletedAt IS NULL LIMIT 1")
    suspend fun getById(categoryId: Long): SoundscapeCategoryEntity?

    @Query("SELECT * FROM soundscape_categories WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<SoundscapeCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SoundscapeCategoryEntity): Long

    @Query("UPDATE soundscape_categories SET deletedAt = :deletedAt WHERE id = :categoryId")
    suspend fun softDelete(categoryId: Long, deletedAt: Long)

    @Query("UPDATE soundscape_categories SET deletedAt = NULL WHERE id = :categoryId")
    suspend fun restore(categoryId: Long)

    @Query("DELETE FROM soundscape_categories WHERE id = :categoryId")
    suspend fun delete(categoryId: Long)

    @Query("DELETE FROM soundscape_categories WHERE deletedAt IS NOT NULL")
    suspend fun deleteAllDeleted()

    @Query("DELETE FROM soundscape_categories WHERE deletedAt IS NOT NULL AND deletedAt < :cutoffMillis")
    suspend fun purgeDeletedBefore(cutoffMillis: Long)
}
