package com.example.rpgaudiomixer.data.fx

import com.example.rpgaudiomixer.data.fx.local.FxTrackDao
import com.example.rpgaudiomixer.data.fx.local.FxTrackEntity
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.FxTrack
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

    override suspend fun getById(trackId: Long): FxTrack? {
        return fxTrackDao.getById(trackId)?.toDomain()
    }

    override suspend fun create(track: FxTrack): Long {
        return fxTrackDao.insert(track.toEntity())
    }

    override suspend fun update(track: FxTrack) {
        fxTrackDao.update(track.toEntity())
    }

    override suspend fun delete(trackId: Long) {
        fxTrackDao.delete(trackId)
    }

    override suspend fun incrementPlayCount(trackId: Long) {
        fxTrackDao.incrementPlayCount(trackId)
    }

    override fun getMostPlayedFx(): Flow<FxTrack?> {
        return fxTrackDao.getMostPlayedFx().map { it?.toDomain() }
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
