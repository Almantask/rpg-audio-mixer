package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.FXTrack
import com.example.rpgaudiomixer.domain.repository.FXRepository
import com.example.rpgaudiomixer.infra.local.dao.FXDao
import com.example.rpgaudiomixer.infra.local.entities.FXTrackEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FXRepositoryImpl @Inject constructor(
    private val fxDao: FXDao
) : FXRepository {
    override fun observeAll(): Flow<List<FXTrack>> {
        return fxDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeMostPlayed(): Flow<FXTrack?> {
        return fxDao.observeMostPlayed().map { it?.toDomain() }
    }

    override fun observeDeleted(): Flow<List<FXTrack>> {
        return fxDao.observeDeleted().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun search(query: String): Flow<List<FXTrack>> {
        return fxDao.search("%$query%").map { entities ->
            entities.map { it.toDomain() }
        }
    }
    override suspend fun incrementPlayCount(id: Long) {
        fxDao.incrementPlayCount(id)
    }

    override suspend fun upsert(track: FXTrack) {
        fxDao.upsert(FXTrackEntity.fromDomain(track))
    }

    override suspend fun softDelete(id: Long) {
        fxDao.softDelete(id, System.currentTimeMillis())
    }

    override suspend fun restore(id: Long) {
        fxDao.restore(id)
    }

    override suspend fun permanentDelete(id: Long) {
        fxDao.permanentDelete(id)
    }

    override suspend fun purgeOldDeleted(threshold: Long) {
        fxDao.purgeOldDeleted(threshold)
    }
}
