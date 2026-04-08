package com.example.rpgaudiomixer.data.session

import com.example.rpgaudiomixer.data.local.SessionDao
import com.example.rpgaudiomixer.data.local.SessionEntity
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of SessionRepository
 * Maps between entity and domain models
 */
class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : SessionRepository {

    override fun observeByCampaign(campaignId: Long): Flow<List<Session>> =
        sessionDao.observeByCampaign(campaignId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getById(id: Long): Session? =
        sessionDao.getById(id)?.toDomain()

    override suspend fun upsert(session: Session): Long =
        sessionDao.upsert(session.toEntity())

    override suspend fun deleteById(id: Long) =
        sessionDao.deleteById(id)
}

/**
 * Extension functions for mapping between Entity and Domain models
 */
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
