package com.example.rpgaudiomixer.app.data.audiotrack

import com.example.rpgaudiomixer.app.data.local.dao.AudioTrackDao
import com.example.rpgaudiomixer.app.data.local.entities.AudioTrackEntity
import com.example.rpgaudiomixer.app.domain.model.AudioTrack
import com.example.rpgaudiomixer.app.domain.model.AudioTrackType
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

    override fun observeByType(type: AudioTrackType): Flow<List<AudioTrack>> {
        return audioTrackDao.observeByType(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeDeleted(): Flow<List<AudioTrack>> {
        return audioTrackDao.observeDeleted().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createTrack(
        name: String,
        localPath: String,
        originalUri: String,
        type: AudioTrackType
    ): Long {
        val entity = AudioTrackEntity(
            name = name,
            localPath = localPath,
            originalUri = originalUri,
            type = type.name
        )
        return audioTrackDao.upsert(entity)
    }

    override suspend fun softDelete(id: Long) {
        audioTrackDao.softDelete(id)
    }

    override suspend fun restore(id: Long) {
        audioTrackDao.restore(id)
    }

    override suspend fun permanentlyDelete(id: Long) {
        audioTrackDao.permanentlyDelete(id)
    }

    override suspend fun deleteAll() {
        audioTrackDao.deleteAll()
    }

    private fun AudioTrackEntity.toDomain() = AudioTrack(
        id = id,
        name = name,
        localPath = localPath,
        originalUri = originalUri,
        type = AudioTrackType.valueOf(type),
        isDeleted = isDeleted,
        deletedAt = deletedAt
    )
}
