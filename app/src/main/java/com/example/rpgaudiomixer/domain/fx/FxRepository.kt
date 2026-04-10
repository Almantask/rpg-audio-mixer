package com.example.rpgaudiomixer.domain.fx

import com.example.rpgaudiomixer.domain.model.FxTrack
import kotlinx.coroutines.flow.Flow

interface FxRepository {

    fun observeAll(): Flow<List<FxTrack>>

    fun search(query: String): Flow<List<FxTrack>>

    suspend fun getById(trackId: Long): FxTrack?

    suspend fun create(track: FxTrack): Long

    suspend fun update(track: FxTrack)

    suspend fun delete(trackId: Long)

    suspend fun incrementPlayCount(trackId: Long)

    fun getMostPlayedFx(): Flow<FxTrack?>
}
