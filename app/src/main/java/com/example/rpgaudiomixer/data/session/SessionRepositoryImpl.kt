package com.example.rpgaudiomixer.data.session

import com.example.rpgaudiomixer.data.local.SessionDao
import com.example.rpgaudiomixer.data.local.SessionEntity
import com.example.rpgaudiomixer.data.local.SessionSummaryEntity
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.session.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao,
) : SessionRepository {
    private var currentTimeProvider: () -> Long = System::currentTimeMillis

    internal constructor(
        sessionDao: SessionDao,
        currentTimeProvider: () -> Long,
    ) : this(sessionDao) {
        this.currentTimeProvider = currentTimeProvider
    }

    override fun observeSessions(campaignId: Long): Flow<List<Session>> =
        sessionDao.observeByCampaign(campaignId).map { sessions ->
            sessions.map(SessionSummaryEntity::toDomain)
        }

    override fun observeSession(sessionId: Long): Flow<Session?> =
        sessionDao.observeById(sessionId).map { session -> session?.toDomain() }

    override suspend fun createSession(
        campaignId: Long,
        name: String,
        date: Long,
        coverArtUri: String?,
    ): Long = sessionDao.upsert(
        SessionEntity(
            campaignId = campaignId,
            name = name,
            date = date,
            coverArtUri = coverArtUri,
        )
    )

    override suspend fun recordOpenedScene(sessionId: Long, sceneId: Long) {
        sessionDao.recordOpenedScene(
            sessionId = sessionId,
            sceneId = sceneId,
            openedAt = currentTimeProvider(),
        )
    }

    override suspend fun deleteSession(sessionId: Long) {
        sessionDao.deleteById(sessionId)
    }
}

private fun SessionSummaryEntity.toDomain(): Session = Session(
    id = id,
    campaignId = campaignId,
    name = name,
    date = date,
    coverArtUri = coverArtUri,
    sceneCount = sceneCount,
    lastOpenedSceneId = lastOpenedSceneId,
    lastOpenedAt = lastOpenedAt,
)
