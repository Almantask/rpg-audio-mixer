package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.SessionDao
import com.example.rpgaudiomixer.data.local.SessionEntity
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of SessionRepository using Room.
 */
class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : SessionRepository {

    override fun observeByCampaign(campaignId: Long): Flow<List<Session>> {
        return sessionDao.observeByCampaign(campaignId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun create(
        campaignId: Long,
        name: String,
        date: Long,
        coverArtUri: String?
    ): Long {
        val entity = SessionEntity(
            campaignId = campaignId,
            name = name,
            date = date,
            coverArtUri = coverArtUri
        )
        return sessionDao.upsert(entity)
    }

    override suspend fun update(session: Session) {
        sessionDao.upsert(session.toEntity())
    }

    override suspend fun delete(id: Long) {
        sessionDao.delete(id)
    }

    override suspend fun getById(id: Long): Session? {
        return sessionDao.getById(id)?.toDomain()
    }

    private fun SessionEntity.toDomain() = Session(
        id = id,
        campaignId = campaignId,
        name = name,
        date = date,
        coverArtUri = coverArtUri
    )

    private fun Session.toEntity() = SessionEntity(
        id = id,
        campaignId = campaignId,
        name = name,
        date = date,
        coverArtUri = coverArtUri
    )
}
