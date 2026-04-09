package com.example.rpgaudiomixer.infra.library

import com.example.rpgaudiomixer.domain.library.FxRepository
import com.example.rpgaudiomixer.domain.library.FxTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
    
    override fun observeFxTrack(id: Long): Flow<FxTrack?> {
        return fxTrackDao.observeById(id).map { it?.toDomain() }
    }

    override suspend fun upsert(fxTrack: FxTrack): Long {
        return withContext(Dispatchers.IO) {
            fxTrackDao.upsert(fxTrack.toEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(Dispatchers.IO) {
            fxTrackDao.delete(id)
        }
    }
}

private fun FxTrackEntity.toDomain() = FxTrack(
    id = id,
    name = name,
    filePath = filePath,
    tags = if (tags.isEmpty()) emptyList() else tags.split(","),
    durationMs = durationMs,
    playCount = playCount
)

private fun FxTrack.toEntity() = FxTrackEntity(
    id = id,
    name = name,
    filePath = filePath,
    tags = tags.joinToString(","),
    durationMs = durationMs,
    playCount = playCount
)
