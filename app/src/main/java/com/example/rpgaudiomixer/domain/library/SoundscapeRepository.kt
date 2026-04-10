package com.example.rpgaudiomixer.domain.library

import kotlinx.coroutines.flow.Flow

interface SoundscapeRepository {
    fun observeCategories(): Flow<List<SoundscapeCategory>>
    fun observeMostPlayedTrack(): Flow<SoundscapeTrack?>
    fun observeCategory(id: Long): Flow<SoundscapeCategory?>
    fun observeTracksByCategory(categoryId: Long): Flow<List<SoundscapeTrack>>
    suspend fun getCategoryById(id: Long): SoundscapeCategory?
    
    fun observeDeletedCategories(): Flow<List<SoundscapeCategory>>
    suspend fun softDeleteCategory(id: Long)
    suspend fun restoreCategory(id: Long)
    suspend fun upsertCategory(category: SoundscapeCategory): Long
    suspend fun deleteCategory(id: Long)
    
    suspend fun upsertTrack(track: SoundscapeTrack)
    suspend fun deleteTrack(id: Long)
    suspend fun incrementTrackPlayCount(trackId: Long)
}
