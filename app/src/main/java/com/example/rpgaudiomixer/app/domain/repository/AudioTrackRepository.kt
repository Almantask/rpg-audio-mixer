package com.example.rpgaudiomixer.app.domain.repository

import com.example.rpgaudiomixer.app.domain.model.AudioTrack
import com.example.rpgaudiomixer.app.domain.model.AudioTrackType
import kotlinx.coroutines.flow.Flow

interface AudioTrackRepository {
    fun observeAll(): Flow<List<AudioTrack>>
    fun observeByType(type: AudioTrackType): Flow<List<AudioTrack>>
    fun observeDeleted(): Flow<List<AudioTrack>>
    suspend fun createTrack(
        name: String,
        localPath: String,
        originalUri: String,
        type: AudioTrackType
    ): Long
    suspend fun softDelete(id: Long)
    suspend fun restore(id: Long)
    suspend fun permanentlyDelete(id: Long)
    suspend fun deleteAll()
}
