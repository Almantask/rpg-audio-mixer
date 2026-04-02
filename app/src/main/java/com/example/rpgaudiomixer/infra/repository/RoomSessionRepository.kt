package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import com.example.rpgaudiomixer.infra.db.dao.SessionDao
import com.example.rpgaudiomixer.infra.db.toDomain
import com.example.rpgaudiomixer.infra.db.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomSessionRepository @Inject constructor(
    private val dao: SessionDao,
) : SessionRepository {

    override fun getSessionsForCampaign(campaignId: Long): Flow<List<Session>> =
        dao.getSessionsForCampaign(campaignId).map { list -> list.map { it.toDomain() } }

    override suspend fun getSessionById(id: Long): Session? =
        dao.getSessionById(id)?.toDomain()

    override suspend fun upsertSession(session: Session): Long =
        dao.upsertSession(session.toEntity())

    override suspend fun deleteSession(id: Long) =
        dao.deleteSession(id)
}
