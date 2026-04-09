package com.example.rpgaudiomixer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Junction table linking scenes to soundscape categories.
 *
 * Tracks which soundscape categories are active in a scene, along with their
 * intensity level, mix volume, and display order.
 */
@Entity(
    tableName = "scene_soundscape_cross_ref",
    primaryKeys = ["scene_id", "category_id"],
    foreignKeys = [
        ForeignKey(
            entity = SceneEntity::class,
            parentColumns = ["id"],
            childColumns = ["scene_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SoundscapeCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("scene_id"), Index("category_id")]
)
data class SceneSoundscapeCrossRef(
    @ColumnInfo(name = "scene_id")
    val sceneId: Long,

    @ColumnInfo(name = "category_id")
    val categoryId: Long,

    @ColumnInfo(name = "intensity_level")
    val intensityLevel: Int, // 1=I, 2=II, 3=III

    @ColumnInfo(name = "mix_volume_percent")
    val mixVolumePercent: Int = 100, // 0-100

    @ColumnInfo(name = "display_order")
    val displayOrder: Int = 0
)

/**
 * Data class combining scene soundscape cross-ref with full category details.
 */
data class SceneSoundscapeWithCategory(
    @Embedded val crossRef: SceneSoundscapeCrossRef,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: SoundscapeCategoryEntity
)

/**
 * DAO for SceneSoundscapeCrossRef operations.
 */
@Dao
interface SceneSoundscapeDao {

    /**
     * Observe all soundscape categories linked to a specific scene.
     * Ordered by display_order ascending.
     */
    @Transaction
    @Query("""
        SELECT * FROM scene_soundscape_cross_ref
        WHERE scene_id = :sceneId
        ORDER BY display_order ASC
    """)
    fun observeByScene(sceneId: Long): Flow<List<SceneSoundscapeWithCategory>>

    /**
     * Get a single cross-ref entry for a scene-category pair.
     */
    @Query("""
        SELECT * FROM scene_soundscape_cross_ref
        WHERE scene_id = :sceneId AND category_id = :categoryId
    """)
    suspend fun getBySceneAndCategory(sceneId: Long, categoryId: Long): SceneSoundscapeCrossRef?

    /**
     * Insert or replace a scene soundscape cross-ref.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(crossRef: SceneSoundscapeCrossRef)

    /**
     * Update intensity level for a category in a scene.
     */
    @Query("""
        UPDATE scene_soundscape_cross_ref
        SET intensity_level = :intensityLevel
        WHERE scene_id = :sceneId AND category_id = :categoryId
    """)
    suspend fun updateIntensity(sceneId: Long, categoryId: Long, intensityLevel: Int)

    /**
     * Update mix volume for a category in a scene.
     */
    @Query("""
        UPDATE scene_soundscape_cross_ref
        SET mix_volume_percent = :mixVolumePercent
        WHERE scene_id = :sceneId AND category_id = :categoryId
    """)
    suspend fun updateMixVolume(sceneId: Long, categoryId: Long, mixVolumePercent: Int)

    /**
     * Update display orders for scene soundscapes.
     */
    @Update
    suspend fun updateDisplayOrders(crossRefs: List<SceneSoundscapeCrossRef>)

    /**
     * Delete a soundscape from a scene.
     */
    @Delete
    suspend fun delete(crossRef: SceneSoundscapeCrossRef)

    /**
     * Delete all soundscapes from a scene.
     */
    @Query("DELETE FROM scene_soundscape_cross_ref WHERE scene_id = :sceneId")
    suspend fun deleteAllFromScene(sceneId: Long)
}
