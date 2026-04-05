package com.example.rpgaudiomixer.infra.local.dao

import androidx.room.*
import com.example.rpgaudiomixer.infra.local.entities.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.infra.local.entities.SoundscapeTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeCategoryDao {
    @Query("SELECT * FROM soundscape_categories WHERE deletedAt IS NULL ORDER BY name ASC")
    fun observeAll(): Flow<List<SoundscapeCategoryEntity>>

    @Query("SELECT * FROM soundscape_categories WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<SoundscapeCategoryEntity>>

    @Upsert
    suspend fun upsert(category: SoundscapeCategoryEntity)

    @Query("UPDATE soundscape_categories SET deletedAt = :timestamp WHERE id = :id")
    suspend fun softDelete(id: Long, timestamp: Long)

    @Query("UPDATE soundscape_categories SET deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM soundscape_categories WHERE id = :id")
    suspend fun permanentDelete(id: Long)

    @Query("DELETE FROM soundscape_categories WHERE deletedAt < :threshold")
    suspend fun purgeOldDeleted(threshold: Long)
}

@Dao
interface SoundscapeTrackDao {
    @Query("SELECT * FROM soundscape_tracks WHERE categoryId = :categoryId ORDER BY name ASC")
    fun observeByCategoryId(categoryId: Long): Flow<List<SoundscapeTrackEntity>>

    @Query("SELECT intensityLevel, COUNT(*) as count FROM soundscape_tracks WHERE categoryId = :categoryId GROUP BY intensityLevel")
    fun observeTrackCountsByIntensity(categoryId: Long): Flow<List<IntensityCount>>

    @Query("SELECT categoryId, SUM(playCount) as count FROM soundscape_tracks GROUP BY categoryId")
    fun observeCategoryPlayCounts(): Flow<List<CategoryPlayCount>>

    @Upsert
    suspend fun upsert(track: SoundscapeTrackEntity)

    @Query("SELECT * FROM soundscape_tracks WHERE id = :id")
    suspend fun getById(id: Long): SoundscapeTrackEntity?

    @Query("SELECT * FROM soundscape_tracks ORDER BY playCount DESC LIMIT 1")
    fun observeMostPlayed(): Flow<SoundscapeTrackEntity?>

    @Query("UPDATE soundscape_tracks SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: Long)

    @Query("DELETE FROM soundscape_tracks WHERE id = :id")
    suspend fun deleteById(id: Long)
}

data class IntensityCount(
    val intensityLevel: Int,
    val count: Int
)

data class CategoryPlayCount(
    val categoryId: Long,
    val count: Int
)
