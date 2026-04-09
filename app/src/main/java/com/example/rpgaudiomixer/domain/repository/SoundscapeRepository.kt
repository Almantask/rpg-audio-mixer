package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing soundscape categories and their tracks.
 */
interface SoundscapeRepository {
    /**
     * Observes all soundscape categories with track counts per intensity level.
     */
    fun observeAllCategories(): Flow<List<SoundscapeCategory>>

    /**
     * Gets a specific soundscape category by ID.
     */
    suspend fun getCategoryById(id: String): SoundscapeCategory?

    /**
     * Gets all categories as a list.
     */
    suspend fun getAllCategories(): List<SoundscapeCategory>

    /**
     * Creates a new soundscape category.
     */
    suspend fun createCategory(name: String, iconResId: Int? = null, themeLabel: String? = null): SoundscapeCategory

    /**
     * Updates an existing category.
     */
    suspend fun updateCategory(category: SoundscapeCategory)

    /**
     * Deletes a category and all its tracks.
     */
    suspend fun deleteCategory(id: String)

    /**
     * Observes all tracks in a specific category.
     */
    fun observeTracksByCategory(categoryId: String): Flow<List<SoundscapeTrack>>

    /**
     * Gets all tracks in a specific category.
     */
    suspend fun getTracksByCategory(categoryId: String): List<SoundscapeTrack>

    /**
     * Gets tracks filtered by category and intensity level.
     */
    suspend fun getTracksByCategoryAndIntensity(categoryId: String, intensityLevel: IntensityLevel): List<SoundscapeTrack>

    /**
     * Creates a new soundscape track within a category.
     */
    suspend fun createTrack(
        categoryId: String,
        name: String,
        filePath: String,
        intensityLevel: IntensityLevel,
        mixVolume: Float = 1.0f
    ): SoundscapeTrack

    /**
     * Updates an existing track.
     */
    suspend fun updateTrack(track: SoundscapeTrack)

    /**
     * Deletes a track.
     */
    suspend fun deleteTrack(id: String)
}
