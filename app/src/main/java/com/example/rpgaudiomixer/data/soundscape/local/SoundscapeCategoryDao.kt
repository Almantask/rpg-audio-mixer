package com.example.rpgaudiomixer.data.soundscape.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class SoundscapeCategoryLibraryRow(
    val id: Long,
    val name: String,
    val themeLabel: String?,
    val iconName: String?,
    val levelICount: Int,
    val levelIICount: Int,
    val levelIIICount: Int,
    val totalPlayCount: Int,
)

@Dao
interface SoundscapeCategoryDao {
    @Query(
        """
        SELECT soundscape_categories.id,
               soundscape_categories.name,
               soundscape_categories.themeLabel,
               soundscape_categories.iconName,
               COALESCE(SUM(CASE WHEN soundscape_tracks.intensityLevel = 1 THEN 1 ELSE 0 END), 0) AS levelICount,
               COALESCE(SUM(CASE WHEN soundscape_tracks.intensityLevel = 2 THEN 1 ELSE 0 END), 0) AS levelIICount,
               COALESCE(SUM(CASE WHEN soundscape_tracks.intensityLevel = 3 THEN 1 ELSE 0 END), 0) AS levelIIICount,
               COALESCE(SUM(soundscape_tracks.playCount), 0) AS totalPlayCount
        FROM soundscape_categories
        LEFT JOIN soundscape_tracks ON soundscape_categories.id = soundscape_tracks.categoryId
        GROUP BY soundscape_categories.id
        ORDER BY soundscape_categories.name COLLATE NOCASE ASC, soundscape_categories.id ASC
        """,
    )
    fun observeLibrary(): Flow<List<SoundscapeCategoryLibraryRow>>

    @Query("SELECT * FROM soundscape_categories WHERE id = :categoryId LIMIT 1")
    fun observeById(categoryId: Long): Flow<SoundscapeCategoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: SoundscapeCategoryEntity): Long

    @Query("DELETE FROM soundscape_categories WHERE id = :categoryId")
    suspend fun deleteById(categoryId: Long)

    @Query("DELETE FROM soundscape_categories")
    suspend fun clearAll()
}
