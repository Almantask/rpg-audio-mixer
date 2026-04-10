package com.example.rpgaudiomixer.domain.soundscape

import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.Flow

interface SoundscapeRepository {

    fun observeAllCategories(): Flow<List<SoundscapeCategory>>

    fun observeCategoryById(categoryId: Long): Flow<SoundscapeCategory?>

    suspend fun getCategoryById(categoryId: Long): SoundscapeCategory?

    fun observeTracksByCategory(categoryId: Long): Flow<List<SoundscapeTrack>>

    fun observeTracksByIntensity(categoryId: Long, level: Int): Flow<List<SoundscapeTrack>>

    suspend fun getTrackById(trackId: Long): SoundscapeTrack?

    suspend fun createCategory(category: SoundscapeCategory): Long

    suspend fun updateCategory(category: SoundscapeCategory)

    suspend fun deleteCategory(categoryId: Long)

    suspend fun createTrack(track: SoundscapeTrack): Long

    suspend fun updateTrack(track: SoundscapeTrack)

    suspend fun deleteTrack(trackId: Long)

    suspend fun incrementTrackPlayCount(trackId: Long)
}
