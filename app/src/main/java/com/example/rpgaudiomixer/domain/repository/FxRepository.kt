package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.FxTrack
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing sound effect (FX) tracks in the audio library.
 */
interface FxRepository {
    /**
     * Observes all FX tracks in the library.
     * Returns a Flow that emits the current list whenever it changes.
     */
    fun observeAllFxTracks(): Flow<List<FxTrack>>

    /**
     * Gets a specific FX track by ID.
     * @return The track if found, null otherwise
     */
    suspend fun getFxTrackById(id: String): FxTrack?

    /**
     * Gets all FX tracks in the library.
     */
    suspend fun getAllFxTracks(): List<FxTrack>

    /**
     * Imports a new FX track from a file.
     * @param name Display name for the track
     * @param filePath File path or URI to the audio file
     * @param tags Optional list of tags
     * @return The created FxTrack
     */
    suspend fun importFxTrack(name: String, filePath: String, tags: List<String> = emptyList()): FxTrack

    /**
     * Updates an existing FX track.
     */
    suspend fun updateFxTrack(track: FxTrack)

    /**
     * Soft-deletes an FX track (moves to trash).
     */
    suspend fun deleteFxTrack(id: String)

    /**
     * Searches FX tracks by query string.
     * Searches in name and tags.
     * @param query Search query
     * @return List of matching tracks
     */
    suspend fun searchFxTracks(query: String): List<FxTrack>
}
