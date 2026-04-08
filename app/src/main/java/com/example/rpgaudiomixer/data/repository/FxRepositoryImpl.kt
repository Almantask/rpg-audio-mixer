package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.FxTrackDao
import com.example.rpgaudiomixer.data.local.FxTrackEntity
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.repository.FxRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of FxRepository.
 *
 * Maps between FxTrackEntity (data layer) and FxTrack (domain layer).
 */
class FxRepositoryImpl @Inject constructor(
    private val fxTrackDao: FxTrackDao
) : FxRepository {

    override fun observeAll(): Flow<List<FxTrack>> {
        return fxTrackDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun search(query: String): Flow<List<FxTrack>> {
        return fxTrackDao.search(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long): FxTrack? {
        return fxTrackDao.getById(id)?.toDomain()
    }

    override suspend fun upsert(track: FxTrack): Long {
        return fxTrackDao.upsert(track.toEntity())
    }

    override suspend fun create(name: String, filePath: String, tags: List<String>): Long {
        val entity = FxTrackEntity(
            name = name,
            filePath = filePath,
            tags = tags.joinToString(",")
        )
        return fxTrackDao.upsert(entity)
    }

    override suspend fun update(track: FxTrack) {
        fxTrackDao.upsert(track.toEntity())
    }

    override suspend fun delete(id: Long) {
        fxTrackDao.softDelete(id)
    }

    override suspend fun hardDelete(id: Long) {
        fxTrackDao.hardDelete(id)
    }

    override fun observeDeleted(): Flow<List<FxTrack>> {
        return fxTrackDao.observeDeleted().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun restore(id: Long) {
        fxTrackDao.restore(id)
    }

    /**
     * Convert FxTrackEntity to FxTrack domain model.
     */
    private fun FxTrackEntity.toDomain(): FxTrack {
        return FxTrack(
            id = id,
            name = name,
            filePath = filePath,
            tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() },
            createdAt = createdAt
        )
    }

    /**
     * Convert FxTrack domain model to FxTrackEntity.
     */
    private fun FxTrack.toEntity(): FxTrackEntity {
        return FxTrackEntity(
            id = id,
            name = name,
            filePath = filePath,
            tags = tags.joinToString(","),
            createdAt = createdAt
        )
    }
}
