package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Scene
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Scene domain operations.
 */
interface SceneRepository {

    /**
     * Observe all scenes.
     */
    fun observeAll(): Flow<List<Scene>>

    /**
     * Observe scenes linked to a specific session.
     */
    fun observeBySession(sessionId: Long): Flow<List<Scene>>

    /**
     * Create a new scene.
     */
    suspend fun create(name: String, description: String? = null, tags: List<String> = emptyList()): Long

    /**
     * Update an existing scene.
     */
    suspend fun update(scene: Scene)

    /**
     * Delete a scene by ID.
     */
    suspend fun delete(id: Long)

    /**
     * Get a scene by ID.
     */
    suspend fun getById(id: Long): Scene?

    /**
     * Search scenes by query.
     */
    fun search(query: String): Flow<List<Scene>>

    /**
     * Link a scene to a session.
     */
    suspend fun linkToSession(sessionId: Long, sceneId: Long)

    /**
     * Unlink a scene from a session.
     */
    suspend fun unlinkFromSession(sessionId: Long, sceneId: Long)
}
