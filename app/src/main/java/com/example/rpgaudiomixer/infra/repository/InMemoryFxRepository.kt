package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.repository.FxRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryFxRepository @Inject constructor() : FxRepository {

    private val fxTracks = mutableListOf<FxTrack>()
    private val _fxTracksFlow = MutableStateFlow<List<FxTrack>>(emptyList())

    override fun observeAllFxTracks(): Flow<List<FxTrack>> {
        return _fxTracksFlow.asStateFlow()
    }

    override suspend fun getFxTrackById(id: String): FxTrack? {
        return fxTracks.firstOrNull { it.id == id }
    }

    override suspend fun getAllFxTracks(): List<FxTrack> {
        return fxTracks.toList()
    }

    override suspend fun importFxTrack(name: String, filePath: String, tags: List<String>): FxTrack {
        val track = FxTrack(
            id = UUID.randomUUID().toString(),
            name = name,
            filePath = filePath,
            tags = tags,
            durationMs = 0L, // Would be calculated from file in real implementation
            playCount = 0
        )
        fxTracks.add(track)
        _fxTracksFlow.value = fxTracks.toList()
        return track
    }

    override suspend fun updateFxTrack(track: FxTrack) {
        val index = fxTracks.indexOfFirst { it.id == track.id }
        if (index != -1) {
            fxTracks[index] = track
            _fxTracksFlow.value = fxTracks.toList()
        }
    }

    override suspend fun deleteFxTrack(id: String) {
        fxTracks.removeIf { it.id == id }
        _fxTracksFlow.value = fxTracks.toList()
    }

    override suspend fun searchFxTracks(query: String): List<FxTrack> {
        if (query.isBlank()) {
            return getAllFxTracks()
        }
        val lowercaseQuery = query.lowercase()
        return fxTracks.filter { track ->
            track.name.lowercase().contains(lowercaseQuery) ||
            track.tags.any { it.lowercase().contains(lowercaseQuery) }
        }
    }
}
