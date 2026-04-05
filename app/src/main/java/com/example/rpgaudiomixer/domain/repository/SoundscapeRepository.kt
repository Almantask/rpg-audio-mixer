package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.Flow

interface SoundscapeRepository {
    fun observeAllCategories(): Flow<List<SoundscapeCategory>>
    fun observeDeletedCategories(): Flow<List<SoundscapeCategory>>
    fun observeTracksByCategory(categoryId: Long): Flow<List<SoundscapeTrack>>
    fun observeTrackCountsByIntensity(categoryId: Long): Flow<Map<IntensityLevel, Int>>
    fun observeCategoryPlayCounts(): Flow<Map<Long, Int>>
    fun observeMostPlayed(): Flow<SoundscapeTrack?>
    suspend fun incrementTrackPlayCount(id: Long)
    
    suspend fun upsertCategory(category: SoundscapeCategory)
    suspend fun softDeleteCategory(id: Long)
    suspend fun restoreCategory(id: Long)
    suspend fun permanentDeleteCategory(id: Long)
    suspend fun purgeOldDeletedCategories(threshold: Long)
    
    suspend fun upsertTrack(track: SoundscapeTrack)
    suspend fun deleteTrack(id: Long)
}
