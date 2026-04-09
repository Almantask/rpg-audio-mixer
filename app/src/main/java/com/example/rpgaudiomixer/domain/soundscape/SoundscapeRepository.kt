package com.example.rpgaudiomixer.domain.soundscape

import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.Flow

interface SoundscapeRepository {
    fun observeCategories(): Flow<List<SoundscapeCategory>>
    fun observeCategory(categoryId: Long): Flow<SoundscapeCategory?>
    fun hasDemoSoundscapes(): Flow<Boolean>
    suspend fun createCategory(name: String): Long
    suspend fun updateCategory(category: SoundscapeCategory)
    suspend fun deleteCategory(categoryId: Long)
    suspend fun upsertTrack(track: SoundscapeTrack): Long
    suspend fun deleteTrack(trackId: Long)
    suspend fun replaceTracks(categoryId: Long, tracks: List<SoundscapeTrack>)
    suspend fun downloadDemoSoundscapes()
    suspend fun clearAll()
}
