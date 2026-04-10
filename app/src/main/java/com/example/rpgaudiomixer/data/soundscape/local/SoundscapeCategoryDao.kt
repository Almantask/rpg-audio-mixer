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
        SELECT c.id,
               c.name,
               c.themeLabel,
               c.iconResId,
               c.isDemoContent,
               COALESCE(SUM(CASE WHEN t.intensityLevel = 1 THEN 1 ELSE 0 END), 0) AS levelOneCount,
               COALESCE(SUM(CASE WHEN t.intensityLevel = 2 THEN 1 ELSE 0 END), 0) AS levelTwoCount,
               COALESCE(SUM(CASE WHEN t.intensityLevel = 3 THEN 1 ELSE 0 END), 0) AS levelThreeCount,
               COALESCE(SUM(t.playCount), 0) AS totalPlayCount
        FROM soundscape_categories c
        LEFT JOIN soundscape_tracks t ON c.id = t.categoryId
        WHERE c.deletedAt IS NULL
        GROUP BY c.id
        ORDER BY c.name COLLATE NOCASE ASC, c.id ASC
        """,
    )
    fun observeCategorySummaries(): Flow<List<SoundscapeCategorySummaryRow>>

    @Query("SELECT * FROM soundscape_categories WHERE id = :categoryId AND deletedAt IS NULL")
    fun observeById(categoryId: Long): Flow<SoundscapeCategoryEntity?>

    @Query("SELECT * FROM soundscape_categories WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC, id DESC")
    fun observeDeleted(): Flow<List<SoundscapeCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SoundscapeCategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SoundscapeCategoryEntity>): List<Long>

    @Query("UPDATE soundscape_categories SET deletedAt = :deletedAt WHERE id = :categoryId")
    suspend fun softDeleteById(categoryId: Long, deletedAt: Long)

    @Query("UPDATE soundscape_categories SET deletedAt = NULL WHERE id = :categoryId")
    suspend fun restore(categoryId: Long)

    @Query("DELETE FROM soundscape_categories WHERE id = :categoryId")
    suspend fun hardDeleteById(categoryId: Long)

    @Query("DELETE FROM soundscape_categories WHERE deletedAt IS NOT NULL")
    suspend fun deleteAllDeleted()

    @Query("DELETE FROM soundscape_categories WHERE deletedAt IS NOT NULL AND deletedAt <= :cutoffMillis")
    suspend fun purgeDeletedBefore(cutoffMillis: Long)

    @Query("SELECT COUNT(*) FROM soundscape_categories WHERE isDemoContent = 1 AND deletedAt IS NULL")
    suspend fun demoCategoryCount(): Int
}
