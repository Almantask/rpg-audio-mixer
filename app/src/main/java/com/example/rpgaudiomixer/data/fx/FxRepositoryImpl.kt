package com.example.rpgaudiomixer.data.fx

import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.FxTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FxRepositoryImpl @Inject constructor(
    private val dao: FxTrackDao,
) : FxRepository {

    override fun observeAll(): Flow<List<FxTrack>> =
        dao.observeAll().map { it.map { e -> e.toDomain() } }

    override fun search(query: String): Flow<List<FxTrack>> =
        dao.search(query).map { it.map { e -> e.toDomain() } }

    override suspend fun import(name: String, filePath: String, tags: List<String>, durationMs: Long): FxTrack {
        val entity = FxTrackEntity(
            name = name,
            filePath = filePath,
            tags = tags.joinToString(","),
            durationMs = durationMs,
        )
        val id = dao.upsert(entity)
        return entity.copy(id = id).toDomain()
    }

    override suspend fun update(track: FxTrack) {
        dao.update(track.toEntity())
    }

    override suspend fun delete(id: Long) {
        dao.delete(id)
    }

    override suspend fun incrementPlayCount(id: Long) {
        dao.incrementPlayCount(id)
    }
}
