package com.example.rpgaudiomixer.data.fx

import com.example.rpgaudiomixer.data.local.FxTrackDao
import com.example.rpgaudiomixer.data.local.FxTrackEntity
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.repository.FxRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

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

    override suspend fun create(name: String, filePath: String, tags: List<String>, durationMs: Long): Long {
        val entity = FxTrackEntity(
            name = name,
            filePath = filePath,
            tags = tags.joinToString(","),
            durationMs = durationMs
        )
        return fxTrackDao.upsert(entity)
    }

    override suspend fun update(track: FxTrack) {
        fxTrackDao.upsert(track.toEntity())
    }

    override suspend fun delete(id: Long) {
        fxTrackDao.deleteById(id)
    }

    override suspend fun incrementPlayCount(id: Long) {
        fxTrackDao.incrementPlayCount(id)
    }

    private fun FxTrackEntity.toDomain(): FxTrack {
        return FxTrack(
            id = id,
            name = name,
            filePath = filePath,
            tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() },
            durationMs = durationMs,
            playCount = playCount
        )
    }

    private fun FxTrack.toEntity(): FxTrackEntity {
        return FxTrackEntity(
            id = id,
            name = name,
            filePath = filePath,
            tags = tags.joinToString(","),
            durationMs = durationMs,
            playCount = playCount
        )
    }
}
