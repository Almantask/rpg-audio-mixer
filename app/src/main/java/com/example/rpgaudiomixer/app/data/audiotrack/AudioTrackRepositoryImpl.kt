package com.example.rpgaudiomixer.app.data.audiotrack

import com.example.rpgaudiomixer.app.data.local.dao.AudioTrackDao
import com.example.rpgaudiomixer.app.data.local.entities.AudioTrackEntity
import com.example.rpgaudiomixer.app.domain.model.AudioTrack
import com.example.rpgaudiomixer.app.domain.repository.AudioTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioTrackRepositoryImpl @Inject constructor(
    private val audioTrackDao: AudioTrackDao
) : AudioTrackRepository {

    override fun observeAll(): Flow<List<AudioTrack>> {
        return audioTrackDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addTrack(uri: String, displayName: String) {
        val entity = AudioTrackEntity(uri = uri, displayName = displayName)
        audioTrackDao.upsert(entity)
    }

    override suspend fun deleteTrack(track: AudioTrack) {
        audioTrackDao.softDelete(track.id)
    }

    override suspend fun deleteAll() {
        audioTrackDao.deleteAll()
    }

    private fun AudioTrackEntity.toDomain() = AudioTrack(
        id = id,
        uri = uri,
        displayName = displayName
    )
}
