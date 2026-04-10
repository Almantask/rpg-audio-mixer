package com.example.rpgaudiomixer.domain.soundscape

import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.MostPlayedSoundscapeTrack
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.Flow

interface SoundscapeRepository {
    fun observeCategories(): Flow<List<SoundscapeCategory>>

    fun observeCategory(categoryId: Long): Flow<SoundscapeCategory?>

    fun observeTracks(categoryId: Long): Flow<List<SoundscapeTrack>>

    fun observeMostPlayedTrack(): Flow<MostPlayedSoundscapeTrack?>

    suspend fun createCategory(name: String): Long

    suspend fun deleteCategory(categoryId: Long)

    suspend fun saveTracks(categoryId: Long, tracks: List<SoundscapeTrack>)

    suspend fun installDemoSoundscapes()
}
