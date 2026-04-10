package com.example.rpgaudiomixer.domain.fx

import com.example.rpgaudiomixer.domain.model.FxTrack
import kotlinx.coroutines.flow.Flow

interface FxRepository {
    fun observeAll(): Flow<List<FxTrack>>
    fun search(query: String): Flow<List<FxTrack>>
    suspend fun upsert(track: FxTrack): Long
    suspend fun delete(trackId: Long)
}
