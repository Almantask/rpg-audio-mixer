package com.example.rpgaudiomixer.data.scene.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class SceneSoundscapeRow(
    val sceneId: Long,
    val categoryId: Long,
    val categoryName: String,
    val displayOrder: Int,
    val mixVolumePercent: Int,
    val intensityLevel: Int,
)

@Dao
interface SceneSoundscapeDao {
    @Query(
        """
        SELECT scene_soundscape_cross_refs.sceneId AS sceneId,
               scene_soundscape_cross_refs.categoryId AS categoryId,
               soundscape_categories.name AS categoryName,
               scene_soundscape_cross_refs.displayOrder AS displayOrder,
               scene_soundscape_cross_refs.mixVolumePercent AS mixVolumePercent,
               scene_soundscape_cross_refs.intensityLevel AS intensityLevel
        FROM scene_soundscape_cross_refs
        INNER JOIN soundscape_categories
            ON soundscape_categories.id = scene_soundscape_cross_refs.categoryId
        WHERE scene_soundscape_cross_refs.sceneId = :sceneId
        ORDER BY scene_soundscape_cross_refs.displayOrder ASC,
                 soundscape_categories.name COLLATE NOCASE ASC,
                 scene_soundscape_cross_refs.categoryId ASC
        """,
    )
    fun observeByScene(sceneId: Long): Flow<List<SceneSoundscapeRow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(crossRef: SceneSoundscapeCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(crossRefs: List<SceneSoundscapeCrossRef>)

    @Query("DELETE FROM scene_soundscape_cross_refs WHERE sceneId = :sceneId AND categoryId = :categoryId")
    suspend fun delete(sceneId: Long, categoryId: Long)

    @Query("DELETE FROM scene_soundscape_cross_refs WHERE sceneId = :sceneId")
    suspend fun deleteByScene(sceneId: Long)

    @Query("DELETE FROM scene_soundscape_cross_refs")
    suspend fun clearAll()

    @Query("SELECT MAX(displayOrder) FROM scene_soundscape_cross_refs WHERE sceneId = :sceneId")
    suspend fun maxDisplayOrder(sceneId: Long): Int?

    @Query("SELECT * FROM scene_soundscape_cross_refs WHERE sceneId = :sceneId AND categoryId = :categoryId LIMIT 1")
    suspend fun get(sceneId: Long, categoryId: Long): SceneSoundscapeCrossRef?
}
