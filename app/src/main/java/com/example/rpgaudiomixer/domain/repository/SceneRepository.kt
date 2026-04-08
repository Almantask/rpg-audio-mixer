package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Scene
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Scene operations
 */
interface SceneRepository {
    /**
     * Observe all scenes (global list)
     */
    fun observeAll(): Flow<List<Scene>>

    /**
     * Observe scenes linked to a specific session
     */
    fun observeBySession(sessionId: Long): Flow<List<Scene>>

    /**
     * Get a scene by ID
     */
    suspend fun getById(id: Long): Scene?

    /**
     * Create or update a scene
     */
    suspend fun upsert(scene: Scene): Long

    /**
     * Delete a scene by ID
     */
    suspend fun deleteById(id: Long)

    /**
     * Link a scene to a session
     */
    suspend fun linkToSession(sessionId: Long, sceneId: Long)

    /**
     * Unlink a scene from a session
     */
    suspend fun unlinkFromSession(sessionId: Long, sceneId: Long)

    /**
     * Check if a scene is linked to a session
     */
    suspend fun isSceneLinkedToSession(sessionId: Long, sceneId: Long): Boolean
}
