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
        SELECT
            refs.sceneId,
            refs.categoryId,
            refs.displayOrder,
            refs.mixVolume,
            refs.intensityLevel,
            soundscape_categories.name,
            soundscape_categories.iconResId,
            soundscape_categories.themeLabel,
            COALESCE(SUM(CASE WHEN soundscape_tracks.intensityLevel = 1 THEN 1 ELSE 0 END), 0) AS levelOneTrackCount,
            COALESCE(SUM(CASE WHEN soundscape_tracks.intensityLevel = 2 THEN 1 ELSE 0 END), 0) AS levelTwoTrackCount,
            COALESCE(SUM(CASE WHEN soundscape_tracks.intensityLevel = 3 THEN 1 ELSE 0 END), 0) AS levelThreeTrackCount
        FROM scene_soundscape_cross_refs AS refs
        INNER JOIN soundscape_categories
            ON soundscape_categories.id = refs.categoryId
        LEFT JOIN soundscape_tracks
            ON soundscape_tracks.categoryId = refs.categoryId
        WHERE refs.sceneId = :sceneId
        GROUP BY
            refs.sceneId,
            refs.categoryId,
            refs.displayOrder,
            refs.mixVolume,
            refs.intensityLevel,
            soundscape_categories.name,
            soundscape_categories.iconResId,
            soundscape_categories.themeLabel
        ORDER BY refs.displayOrder ASC, refs.categoryId ASC
        """,
    )
    fun observeByScene(sceneId: Long): Flow<List<SceneSoundscapeListItemEntity>>

    @Query("SELECT categoryId FROM scene_soundscape_cross_refs WHERE sceneId = :sceneId ORDER BY displayOrder ASC")
    suspend fun getLinkedCategoryIds(sceneId: Long): List<Long>

    @Query("SELECT COALESCE(MAX(displayOrder), -1) FROM scene_soundscape_cross_refs WHERE sceneId = :sceneId")
    suspend fun getMaxDisplayOrder(sceneId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(crossRef: SceneSoundscapeCrossRef)

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

    @Query("DELETE FROM scene_soundscape_cross_refs WHERE sceneId = :sceneId AND categoryId = :categoryId")
    suspend fun delete(sceneId: Long, categoryId: Long)
}
