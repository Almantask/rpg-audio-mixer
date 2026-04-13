package com.example.rpgaudiomixer.app.data.session

import com.example.rpgaudiomixer.app.data.local.dao.SessionDao
import com.example.rpgaudiomixer.app.data.local.entities.SessionEntity
import com.example.rpgaudiomixer.app.domain.model.Session
import com.example.rpgaudiomixer.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : SessionRepository {

    override fun observeByCampaign(campaignId: Long): Flow<List<Session>> {
        return sessionDao.observeByCampaign(campaignId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeDeleted(): Flow<List<Session>> {
        return sessionDao.observeDeleted().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createSession(
        campaignId: Long,
        name: String,
        coverArtUri: String?,
        date: Long
    ): Long {
        val entity = SessionEntity(
            campaignId = campaignId,
            name = name,
            coverArtUri = coverArtUri,
            date = date
        )
        return sessionDao.upsert(entity)
    }

    override suspend fun softDelete(id: Long) {
        sessionDao.softDelete(id)
    }

    override suspend fun restore(id: Long) {
        sessionDao.restore(id)
    }

    override suspend fun permanentlyDelete(id: Long) {
        sessionDao.permanentlyDelete(id)
    }

    override suspend fun softDeleteByCampaign(campaignId: Long) {
        sessionDao.softDeleteByCampaign(campaignId)
    }

    override suspend fun restoreByCampaign(campaignId: Long) {
        sessionDao.restoreByCampaign(campaignId)
    }

    override suspend fun deleteAll() {
        sessionDao.deleteAll()
    }

    private fun SessionEntity.toDomain() = Session(
        id = id,
        campaignId = campaignId,
        name = name,
        coverArtUri = coverArtUri,
        date = date,
        isDeleted = isDeleted,
        deletedAt = deletedAt
    )
}
