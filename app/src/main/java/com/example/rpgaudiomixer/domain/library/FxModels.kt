package com.example.rpgaudiomixer.domain.library

import kotlinx.coroutines.flow.Flow

data class FxTrack(
    val id: Long = 0,
    val name: String,
    val filePath: String,
    val tags: List<String>,
    val durationMs: Long,
    val playCount: Int = 0,
    val deletedAt: Long? = null
)

interface FxRepository {
    fun observeAll(): Flow<List<FxTrack>>
    fun observeMostPlayedTrack(): Flow<FxTrack?>
    fun search(query: String): Flow<List<FxTrack>>
    fun observeFxTrack(id: Long): Flow<FxTrack?>
    fun observeDeleted(): Flow<List<FxTrack>>
    suspend fun softDelete(id: Long)
    suspend fun restore(id: Long)
    suspend fun upsert(fxTrack: FxTrack): Long
    suspend fun delete(id: Long)
    suspend fun incrementPlayCount(id: Long)
}
