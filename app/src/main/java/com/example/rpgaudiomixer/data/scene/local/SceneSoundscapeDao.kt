package com.example.rpgaudiomixer.data.scene.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneSoundscapeDao {
    @Query(
        """
        SELECT ss.sceneId,
               ss.categoryId,
               c.name AS categoryName,
               c.themeLabel,
               c.iconResId,
               c.isDemoContent,
               ss.mixVolume,
               ss.intensityLevel,
               ss.displayOrder,
               COALESCE(SUM(CASE WHEN t.intensityLevel = 1 THEN 1 ELSE 0 END), 0) AS levelOneCount,
               COALESCE(SUM(CASE WHEN t.intensityLevel = 2 THEN 1 ELSE 0 END), 0) AS levelTwoCount,
               COALESCE(SUM(CASE WHEN t.intensityLevel = 3 THEN 1 ELSE 0 END), 0) AS levelThreeCount
        FROM scene_soundscape_cross_refs ss
        INNER JOIN soundscape_categories c ON c.id = ss.categoryId
        LEFT JOIN soundscape_tracks t ON t.categoryId = ss.categoryId
        WHERE ss.sceneId = :sceneId
        GROUP BY ss.sceneId,
                 ss.categoryId,
                 c.name,
                 c.themeLabel,
                 c.iconResId,
                 c.isDemoContent,
                 ss.mixVolume,
                 ss.intensityLevel,
                 ss.displayOrder
        ORDER BY ss.displayOrder ASC, c.name COLLATE NOCASE ASC
        """,
    )
    fun observeSoundscapesByScene(sceneId: Long): Flow<List<SceneSoundscapeRow>>

    @Query(
        """
        SELECT categoryId
        FROM scene_soundscape_cross_refs
        WHERE sceneId = :sceneId
        ORDER BY displayOrder ASC
        """,
    )
    fun observeLinkedCategoryIds(sceneId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(crossRef: SceneSoundscapeCrossRef)

    @Query(
        """
        SELECT COALESCE(MAX(displayOrder) + 1, 0)
        FROM scene_soundscape_cross_refs
        WHERE sceneId = :sceneId
        """,
    )
    suspend fun nextDisplayOrder(sceneId: Long): Int

    @Query(
        """
        DELETE FROM scene_soundscape_cross_refs
        WHERE sceneId = :sceneId AND categoryId = :categoryId
        """,
    )
    suspend fun delete(sceneId: Long, categoryId: Long)

    @Query(
        """
        UPDATE scene_soundscape_cross_refs
        SET mixVolume = :mixVolume
        WHERE sceneId = :sceneId AND categoryId = :categoryId
        """,
    )
    suspend fun updateMixVolume(sceneId: Long, categoryId: Long, mixVolume: Float)

    @Query(
        """
        UPDATE scene_soundscape_cross_refs
        SET intensityLevel = :intensityLevel
        WHERE sceneId = :sceneId AND categoryId = :categoryId
        """,
    )
    suspend fun updateIntensityLevel(sceneId: Long, categoryId: Long, intensityLevel: Int)

    @Query(
        """
        UPDATE scene_soundscape_cross_refs
        SET displayOrder = :displayOrder
        WHERE sceneId = :sceneId AND categoryId = :categoryId
        """,
    )
    suspend fun updateDisplayOrder(sceneId: Long, categoryId: Long, displayOrder: Int)
}
