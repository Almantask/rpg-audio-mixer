package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.FxTrack
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for FX track domain operations.
 */
interface FxRepository {

    /**
     * Observe all non-deleted FX tracks.
     */
    fun observeAll(): Flow<List<FxTrack>>

    /**
     * Search FX tracks by name or tags.
     */
    fun search(query: String): Flow<List<FxTrack>>

    /**
     * Get FX track by ID.
     */
    suspend fun getById(id: Long): FxTrack?

    /**
     * Create or update an FX track.
     */
    suspend fun upsert(track: FxTrack): Long

    /**
     * Create a new FX track.
     */
    suspend fun create(name: String, filePath: String, tags: List<String> = emptyList()): Long

    /**
     * Update an existing FX track.
     */
    suspend fun update(track: FxTrack)

    /**
     * Soft-delete an FX track (moves to trash).
     */
    suspend fun delete(id: Long)

    /**
     * Permanently delete an FX track.
     */
    suspend fun hardDelete(id: Long)

    /**
     * Observe all soft-deleted FX tracks (trash).
     */
    fun observeDeleted(): Flow<List<FxTrack>>

    /**
     * Restore a soft-deleted FX track from trash.
     */
    suspend fun restore(id: Long)
}
