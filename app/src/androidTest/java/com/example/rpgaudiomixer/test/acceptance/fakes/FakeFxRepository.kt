package com.example.rpgaudiomixer.test.acceptance.fakes

import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.repository.FxRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class FakeFxRepository : FxRepository {

    private val fxTracks = mutableListOf<FxTrack>()
    private val _fxTracksFlow = MutableStateFlow<List<FxTrack>>(emptyList())

    fun setFxTracks(vararg tracks: FxTrack) {
        this.fxTracks.clear()
        this.fxTracks.addAll(tracks)
        _fxTracksFlow.value = fxTracks.toList()
    }

    fun addFxTrack(track: FxTrack) {
        fxTracks.add(track)
        _fxTracksFlow.value = fxTracks.toList()
    }

    fun clear() {
        fxTracks.clear()
        _fxTracksFlow.value = emptyList()
    }

    override fun observeAllFxTracks(): Flow<List<FxTrack>> {
        return _fxTracksFlow.asStateFlow()
    }

    override suspend fun getFxTrackById(id: String): FxTrack? {
        return fxTracks.find { it.id == id }
    }

    override suspend fun getAllFxTracks(): List<FxTrack> {
        return fxTracks.toList()
    }

    override suspend fun importFxTrack(name: String, filePath: String, tags: List<String>): FxTrack {
        val newTrack = FxTrack(
            id = UUID.randomUUID().toString(),
            name = name,
            filePath = filePath,
            tags = tags,
            durationMs = 0L,
            playCount = 0
        )
        fxTracks.add(newTrack)
        _fxTracksFlow.value = fxTracks.toList()
        return newTrack
    }

    override suspend fun updateFxTrack(track: FxTrack) {
        val index = fxTracks.indexOfFirst { it.id == track.id }
        if (index != -1) {
            fxTracks[index] = track
            _fxTracksFlow.value = fxTracks.toList()
        }
    }

    override suspend fun deleteFxTrack(id: String) {
        fxTracks.removeAll { it.id == id }
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
