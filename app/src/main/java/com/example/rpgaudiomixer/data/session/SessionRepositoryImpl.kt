package com.example.rpgaudiomixer.data.session

import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.session.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val dao: SessionDao,
) : SessionRepository {

    override fun observeByCampaign(campaignId: Long): Flow<List<Session>> =
        dao.observeByCampaign(campaignId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun create(campaignId: Long, name: String, coverArtUri: String?): Session {
        val entity = SessionEntity(campaignId = campaignId, name = name, coverArtUri = coverArtUri)
        val id = dao.upsert(entity)
        return entity.copy(id = id).toDomain()
    }

    override suspend fun delete(id: Long) {
        dao.delete(id)
    }
}
