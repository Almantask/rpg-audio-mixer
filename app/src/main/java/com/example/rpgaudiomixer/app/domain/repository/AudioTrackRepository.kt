package com.example.rpgaudiomixer.app.domain.repository

import com.example.rpgaudiomixer.app.domain.model.AudioTrack
import kotlinx.coroutines.flow.Flow

interface AudioTrackRepository {
    fun observeAll(): Flow<List<AudioTrack>>
    suspend fun addTrack(uri: String, displayName: String)
    suspend fun deleteTrack(track: AudioTrack)
    suspend fun deleteAll()
}
