package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Scene operations
 */
@Dao
interface SceneDao {
    /**
     * Observe all scenes (global list)
     */
    @Query("SELECT * FROM scenes ORDER BY name ASC")
    fun observeAll(): Flow<List<SceneEntity>>

    /**
     * Get a scene by ID
     */
    @Query("SELECT * FROM scenes WHERE id = :id")
    suspend fun getById(id: Long): SceneEntity?

    /**
     * Insert or update a scene
     */
    @Upsert
    suspend fun upsert(scene: SceneEntity): Long

    /**
     * Delete a scene
     */
    @Delete
    suspend fun delete(scene: SceneEntity)

    /**
     * Delete a scene by ID
     */
    @Query("DELETE FROM scenes WHERE id = :id")
    suspend fun deleteById(id: Long)
}
