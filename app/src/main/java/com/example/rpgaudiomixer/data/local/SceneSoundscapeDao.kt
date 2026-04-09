package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneSoundscapeDao {
    @Query(
        """
        SELECT scene_soundscape_cross_ref.sceneId AS sceneId,
               soundscape_categories.id AS categoryId,
               soundscape_categories.name AS categoryName,
               soundscape_categories.iconResId AS iconResId,
               soundscape_categories.themeLabel AS themeLabel,
               COALESCE(SUM(CASE WHEN soundscape_tracks.intensityLevel = 1 THEN 1 ELSE 0 END), 0) AS levelOneTrackCount,
               COALESCE(SUM(CASE WHEN soundscape_tracks.intensityLevel = 2 THEN 1 ELSE 0 END), 0) AS levelTwoTrackCount,
               COALESCE(SUM(CASE WHEN soundscape_tracks.intensityLevel = 3 THEN 1 ELSE 0 END), 0) AS levelThreeTrackCount,
               scene_soundscape_cross_ref.displayOrder AS displayOrder,
               scene_soundscape_cross_ref.mixVolume AS mixVolume,
               scene_soundscape_cross_ref.intensityLevel AS intensityLevel
        FROM scene_soundscape_cross_ref
        INNER JOIN soundscape_categories
            ON scene_soundscape_cross_ref.categoryId = soundscape_categories.id
        LEFT JOIN soundscape_tracks
            ON soundscape_tracks.categoryId = soundscape_categories.id
        WHERE scene_soundscape_cross_ref.sceneId = :sceneId
        GROUP BY soundscape_categories.id,
                 scene_soundscape_cross_ref.sceneId,
                 scene_soundscape_cross_ref.displayOrder,
                 scene_soundscape_cross_ref.mixVolume,
                 scene_soundscape_cross_ref.intensityLevel
        ORDER BY scene_soundscape_cross_ref.displayOrder ASC, soundscape_categories.name ASC
        """
    )
    fun observeSoundscapesByScene(sceneId: Long): Flow<List<SceneSoundscapeSummaryEntity>>

    @Query(
        """
        SELECT COALESCE(MAX(displayOrder) + 1, 0)
        FROM scene_soundscape_cross_ref
        WHERE sceneId = :sceneId
        """
    )
    suspend fun getNextDisplayOrder(sceneId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(crossRef: SceneSoundscapeCrossRef)

    @Transaction
    suspend fun updateAll(crossRefs: List<SceneSoundscapeCrossRef>) {
        crossRefs.forEach(::upsert)
    }

    @Query(
        """
        DELETE FROM scene_soundscape_cross_ref
        WHERE sceneId = :sceneId
        AND categoryId = :categoryId
        """
    )
    suspend fun remove(sceneId: Long, categoryId: Long)
}
