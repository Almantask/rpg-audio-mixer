package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.Flow

interface SoundscapeRepository {
    // Categories
    fun observeAllCategories(): Flow<List<SoundscapeCategory>>
    suspend fun getCategoryById(id: Long): SoundscapeCategory?
    suspend fun createCategory(name: String, iconResId: Int? = null, themeLabel: String? = null): Long
    suspend fun updateCategory(category: SoundscapeCategory)
    suspend fun deleteCategory(category: SoundscapeCategory)

    // Tracks
    fun observeTracksByCategory(categoryId: Long): Flow<List<SoundscapeTrack>>
    suspend fun getTrackById(id: Long): SoundscapeTrack?
    suspend fun getTrackCountByCategoryAndIntensity(categoryId: Long, intensityLevel: IntensityLevel): Int
    suspend fun getTracksByCategoryAndIntensity(categoryId: Long, intensityLevel: Int): List<SoundscapeTrack>
    suspend fun createTrack(
        categoryId: Long,
        name: String,
        filePath: String,
        intensityLevel: IntensityLevel,
        mixVolume: Float = 0.5f
    ): Long
    suspend fun updateTrack(track: SoundscapeTrack)
    suspend fun deleteTrack(track: SoundscapeTrack)
}
