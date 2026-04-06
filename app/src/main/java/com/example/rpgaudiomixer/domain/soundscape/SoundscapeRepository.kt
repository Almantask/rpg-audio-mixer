package com.example.rpgaudiomixer.domain.soundscape

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.Flow

interface SoundscapeRepository {
    fun observeAllCategories(): Flow<List<SoundscapeCategory>>
    fun observeCategory(id: Long): Flow<SoundscapeCategory?>
    suspend fun createCategory(name: String): SoundscapeCategory
    suspend fun deleteCategory(id: Long)
    suspend fun addTrack(
        categoryId: Long,
        name: String,
        filePath: String,
        intensityLevel: IntensityLevel,
        mixVolume: Float,
    ): SoundscapeTrack
    suspend fun updateTrack(track: SoundscapeTrack)
    suspend fun deleteTrack(id: Long)
}
