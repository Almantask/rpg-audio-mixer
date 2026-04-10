package com.example.rpgaudiomixer.domain.soundscape

import com.example.rpgaudiomixer.domain.model.FeaturedSoundscapeTrack
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.model.SoundscapeTrackDraft
import kotlinx.coroutines.flow.Flow

interface SoundscapeRepository {
    fun observeCategories(): Flow<List<SoundscapeCategory>>
    fun observeTopPlayedTrack(): Flow<FeaturedSoundscapeTrack?>
    suspend fun getCategory(categoryId: Long): SoundscapeCategory?
    suspend fun getTracks(categoryId: Long): List<SoundscapeTrack>
    suspend fun saveComposition(
        categoryId: Long?,
        name: String,
        tracks: List<SoundscapeTrackDraft>,
    ): Long

    suspend fun deleteCategory(categoryId: Long)
}
