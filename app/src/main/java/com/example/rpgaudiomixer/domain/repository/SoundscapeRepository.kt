package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Soundscape domain operations.
 */
interface SoundscapeRepository {

    // Category operations
    fun observeAllCategories(): Flow<List<SoundscapeCategory>>
    suspend fun createCategory(name: String, iconResId: Int? = null, themeLabel: String? = null): Long
    suspend fun updateCategory(category: SoundscapeCategory)
    suspend fun deleteCategory(id: Long)
    suspend fun getCategoryById(id: Long): SoundscapeCategory?

    // Track operations
    fun observeTracksByCategory(categoryId: Long): Flow<List<SoundscapeTrack>>
    fun observeTracksByCategoryAndIntensity(categoryId: Long, intensity: IntensityLevel): Flow<List<SoundscapeTrack>>
    suspend fun createTrack(
        categoryId: Long,
        name: String,
        filePath: String,
        intensityLevel: IntensityLevel,
        mixVolume: Float = 0.75f
    ): Long
    suspend fun updateTrack(track: SoundscapeTrack)
    suspend fun deleteTrack(id: Long)
    suspend fun getTrackById(id: Long): SoundscapeTrack?

    // Statistics
    suspend fun getTrackCountByIntensity(categoryId: Long, intensity: IntensityLevel): Int
    suspend fun incrementPlayCount(trackId: Long)
    fun observeMostPlayedTrack(): Flow<SoundscapeTrack?>
}
