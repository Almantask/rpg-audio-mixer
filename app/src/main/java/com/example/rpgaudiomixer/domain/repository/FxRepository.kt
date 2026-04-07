package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.FxTrack
import kotlinx.coroutines.flow.Flow

interface FxRepository {
    fun observeAll(): Flow<List<FxTrack>>
    fun search(query: String): Flow<List<FxTrack>>
    suspend fun getById(id: Long): FxTrack?
    suspend fun create(name: String, filePath: String, tags: List<String>, durationMs: Long): Long
    suspend fun update(track: FxTrack)
    suspend fun delete(id: Long)
    suspend fun incrementPlayCount(id: Long)
}
