package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.FXTrack
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    // Soundscape categories
    fun getAllCategories(): Flow<List<SoundscapeCategory>>
    suspend fun getCategoryById(id: Long): SoundscapeCategory?
    suspend fun upsertCategory(category: SoundscapeCategory): Long
    suspend fun deleteCategory(id: Long)

    // Tracks within a category
    suspend fun getTracksForCategory(categoryId: Long): List<Track>
    suspend fun upsertTrack(track: Track): Long
    suspend fun deleteTrack(id: Long)
    suspend fun updateTrackMixVolume(trackId: Long, volume: Float)

    // FX Tracks
    fun getAllFXTracks(): Flow<List<FXTrack>>
    suspend fun getFXTrackById(id: Long): FXTrack?
    suspend fun upsertFXTrack(fxTrack: FXTrack): Long
    suspend fun deleteFXTrack(id: Long)
    suspend fun updateFXTrack(fxTrack: FXTrack)

    // Stats for Home screen
    suspend fun getMostPlayedLoopingTrack(): Track?
    suspend fun getMostPlayedFXTrack(): FXTrack?
    suspend fun incrementTrackPlayCount(trackId: Long)
    suspend fun incrementFXPlayCount(fxTrackId: Long)
}
