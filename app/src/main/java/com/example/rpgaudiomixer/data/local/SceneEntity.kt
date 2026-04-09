package com.example.rpgaudiomixer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Scene entity - represents a reusable audio scene.
 *
 * Scenes are global (not tied to a specific campaign) and can be
 * linked to multiple sessions via SessionSceneCrossRef.
 */
@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "tags")
    val tags: String = "", // Comma-separated tags

    @ColumnInfo(name = "atmosphere_volume_percent")
    val atmosphereVolumePercent: Int = 100 // 0-100, master volume for soundscapes
)

/**
 * DAO for Scene entities.
 */
@Dao
interface SceneDao {

    /**
     * Observe all scenes.
     * Ordered by name ascending.
     */
    @Query("SELECT * FROM scenes ORDER BY name ASC")
    fun observeAll(): Flow<List<SceneEntity>>

    /**
     * Insert or update a scene.
     */
    @Upsert
    suspend fun upsert(scene: SceneEntity): Long

    /**
     * Delete a scene by ID.
     */
    @Query("DELETE FROM scenes WHERE id = :id")
    suspend fun delete(id: Long)

    /**
     * Get a scene by ID (one-shot).
     */
    @Query("SELECT * FROM scenes WHERE id = :id")
    suspend fun getById(id: Long): SceneEntity?

    /**
     * Search scenes by name or tags.
     */
    @Query("""
        SELECT * FROM scenes
        WHERE name LIKE '%' || :query || '%'
        OR tags LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun search(query: String): Flow<List<SceneEntity>>
}
