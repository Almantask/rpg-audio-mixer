package com.example.rpgaudiomixer.domain.library

import kotlinx.coroutines.flow.Flow

data class FxTrack(
    val id: Long = 0,
    val name: String,
    val filePath: String,
    val tags: List<String>,
    val durationMs: Long,
    val playCount: Int = 0
)

interface FxRepository {
    fun observeAll(): Flow<List<FxTrack>>
    fun search(query: String): Flow<List<FxTrack>>
    fun observeFxTrack(id: Long): Flow<FxTrack?>
    suspend fun upsert(fxTrack: FxTrack): Long
    suspend fun delete(id: Long)
}
