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

    override fun observeAll(): Flow<List<FxTrack>> =
        fxTrackDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun search(query: String): Flow<List<FxTrack>> =
        fxTrackDao.search(query).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getById(id: Long): FxTrack? =
        fxTrackDao.getById(id)?.toDomain()

    override suspend fun upsert(track: FxTrack): Long =
        fxTrackDao.upsert(track.toEntity())

    override suspend fun delete(id: Long) =
        fxTrackDao.delete(id)

    override fun getMostPlayed(): Flow<FxTrack?> =
        fxTrackDao.getMostPlayed().map { it?.toDomain() }

    override suspend fun incrementPlayCount(id: Long) =
        fxTrackDao.incrementPlayCount(id)

    private fun FxTrackEntity.toDomain() = FxTrack(
        id = id,
        name = name,
        filePath = filePath,
        tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
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
}
