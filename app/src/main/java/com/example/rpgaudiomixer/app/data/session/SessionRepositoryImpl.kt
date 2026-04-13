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

    override suspend fun createSession(campaignId: Long, name: String) {
        val entity = SessionEntity(
            campaignId = campaignId,
            name = name
        )
        sessionDao.upsert(entity)
    }

    override suspend fun deleteSession(session: Session) {
        sessionDao.softDelete(session.id)
    }

    override suspend fun restore(session: Session) {
        sessionDao.restore(session.id)
    }

    override suspend fun hardDelete(session: Session) {
        sessionDao.hardDelete(session.id)
    }

    override suspend fun purgeOlderThan(cutoff: Long) {
        sessionDao.purgeOlderThan(cutoff)
    }

    override suspend fun purgeAllDeleted() {
        sessionDao.purgeAllDeleted()
    }

    override suspend fun deleteAll() {
        sessionDao.deleteAll()
    }

    private fun SessionEntity.toDomain() = Session(
        id = id,
        campaignId = campaignId,
        name = name,
        createdAt = createdAt,
        deletedAt = deletedAt
    )
}
