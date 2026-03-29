package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.SessionDao
import com.example.rpgaudiomixer.data.local.SessionEntity
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val dao: SessionDao
) : SessionRepository {
    override fun observeByCampaign(campaignId: Long): Flow<List<Session>> =
        dao.observeByCampaign(campaignId).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun upsert(session: Session): Long =
        dao.upsert(session.toEntity())

    override suspend fun delete(session: Session) =
        dao.delete(session.toEntity())
}

private fun SessionEntity.toDomain() = Session(
    id = id,
    campaignId = campaignId,
    name = name,
    date = date
)

private fun Session.toEntity() = SessionEntity(
    id = id,
    campaignId = campaignId,
    name = name,
    date = date
)
