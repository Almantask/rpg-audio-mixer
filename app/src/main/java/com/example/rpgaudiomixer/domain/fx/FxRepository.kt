package com.example.rpgaudiomixer.domain.fx

import com.example.rpgaudiomixer.domain.model.FxTrack
import kotlinx.coroutines.flow.Flow

interface FxRepository {
    fun observeAll(): Flow<List<FxTrack>>
    fun search(query: String): Flow<List<FxTrack>>
    suspend fun import(name: String, filePath: String, tags: List<String>, durationMs: Long): FxTrack
    suspend fun update(track: FxTrack)
    suspend fun delete(id: Long)
    suspend fun incrementPlayCount(id: Long)
}
