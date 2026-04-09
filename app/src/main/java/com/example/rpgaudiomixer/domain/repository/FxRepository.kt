package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.FxTrack
import kotlinx.coroutines.flow.Flow

interface FxRepository {
    fun observeAll(): Flow<List<FxTrack>>
    fun search(query: String): Flow<List<FxTrack>>
    suspend fun getById(id: Long): FxTrack?
    suspend fun upsert(track: FxTrack): Long
    suspend fun delete(id: Long)
    fun getMostPlayed(): Flow<FxTrack?>
    suspend fun incrementPlayCount(id: Long)
}
