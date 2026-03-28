package com.example.rpgaudiomixer.infra.storage.repository

import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.storage.SessionRepository
import com.example.rpgaudiomixer.infra.storage.db.dao.SessionDao
import com.example.rpgaudiomixer.infra.storage.db.entity.SessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomSessionRepository @Inject constructor(
    private val dao: SessionDao,
) : SessionRepository {

    override fun getSessionsByCampaign(campaignId: Long): Flow<List<Session>> =
        dao.getSessionsByCampaign(campaignId).map { it.map(SessionEntity::toDomain) }

    override fun getSessionById(id: Long): Flow<Session?> =
        dao.getSessionById(id).map { it?.toDomain() }

    override suspend fun insert(session: Session): Long =
        dao.insert(session.toEntity())

    override suspend fun update(session: Session) =
        dao.update(session.toEntity())

    override suspend fun delete(session: Session) =
        dao.delete(session.toEntity())
}

private fun SessionEntity.toDomain() = Session(
    id = id,
    campaignId = campaignId,
    name = name,
    description = description,
    coverArtUri = coverArtUri,
    playedAt = playedAt,
    createdAt = createdAt,
)

private fun Session.toEntity() = SessionEntity(
    id = id,
    campaignId = campaignId,
    name = name,
    description = description,
    coverArtUri = coverArtUri,
    playedAt = playedAt,
    createdAt = createdAt,
)
