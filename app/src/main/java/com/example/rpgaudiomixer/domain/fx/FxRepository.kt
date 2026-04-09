package com.example.rpgaudiomixer.domain.fx

import com.example.rpgaudiomixer.domain.model.FxTrack
import kotlinx.coroutines.flow.Flow

interface FxRepository {
    fun observeTracks(): Flow<List<FxTrack>>
    fun observeMostPlayedTrack(): Flow<FxTrack?>
    fun searchTracks(query: String): Flow<List<FxTrack>>
    fun hasDemoTracks(): Flow<Boolean>
    suspend fun upsertTrack(track: FxTrack): Long
    suspend fun deleteTrack(trackId: Long)
    suspend fun incrementPlayCount(trackId: Long)
    suspend fun downloadDemoTracks()
    suspend fun clearAll()
}
