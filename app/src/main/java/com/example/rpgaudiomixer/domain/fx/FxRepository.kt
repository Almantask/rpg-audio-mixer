package com.example.rpgaudiomixer.domain.fx

import com.example.rpgaudiomixer.domain.model.FxTrack
import kotlinx.coroutines.flow.Flow

interface FxRepository {
    fun observeFxTracks(): Flow<List<FxTrack>>

    fun searchFxTracks(query: String): Flow<List<FxTrack>>

    fun observeHasDemoFxTracks(): Flow<Boolean>

    suspend fun importFxTrack(sourceUri: String): FxTrack

    suspend fun updateFxTrack(track: FxTrack)

    suspend fun softDeleteFxTrack(trackId: Long)

    suspend fun seedDemoFxTracks()

    suspend fun incrementPlayCount(trackId: Long)
}
