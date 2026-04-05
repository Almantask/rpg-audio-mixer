package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.FXTrack
import kotlinx.coroutines.flow.Flow

interface FXRepository {
    fun observeAll(): Flow<List<FXTrack>>
    fun observeMostPlayed(): Flow<FXTrack?>
    fun observeDeleted(): Flow<List<FXTrack>>
    fun search(query: String): Flow<List<FXTrack>>
    suspend fun incrementPlayCount(id: Long)
    suspend fun upsert(track: FXTrack)
    suspend fun softDelete(id: Long)
    suspend fun restore(id: Long)
    suspend fun permanentDelete(id: Long)
    suspend fun purgeOldDeleted(threshold: Long)
}
