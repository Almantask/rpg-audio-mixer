package com.example.rpgaudiomixer.domain.fx

import com.example.rpgaudiomixer.domain.model.FxTrack
import kotlinx.coroutines.flow.Flow

interface FxRepository {
    fun observeTracks(): Flow<List<FxTrack>>

    fun observeMostPlayedTrack(): Flow<FxTrack?>

    suspend fun importTrack(name: String, filePath: String): Result<Long>

    suspend fun installDemoTracks()

    suspend fun updateTrack(track: FxTrack)

    suspend fun deleteTrack(trackId: Long, deletedAtMillis: Long = System.currentTimeMillis())
}
