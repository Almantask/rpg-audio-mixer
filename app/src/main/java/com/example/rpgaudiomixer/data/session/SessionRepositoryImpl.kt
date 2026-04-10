package com.example.rpgaudiomixer.data.session

import com.example.rpgaudiomixer.data.scene.local.asDomain
import com.example.rpgaudiomixer.data.session.local.SessionDao
import com.example.rpgaudiomixer.data.session.local.SessionEntity
import com.example.rpgaudiomixer.data.session.local.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.session.local.SessionSceneDao
import com.example.rpgaudiomixer.data.session.local.asDomain
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.session.SessionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao,
    private val sessionSceneDao: SessionSceneDao,
) : SessionRepository {

    override fun observeByCampaign(campaignId: Long): Flow<List<Session>> {
        return sessionDao.observeByCampaign(campaignId).map { sessions ->
            sessions.map { it.asDomain() }
        }
    }

    override fun observeScenesBySession(sessionId: Long): Flow<List<Scene>> {
        return sessionSceneDao.observeScenesBySession(sessionId).map { scenes ->
            scenes.map { it.asDomain() }
        }
    }

    override fun observeLinkedSceneIds(sessionId: Long): Flow<List<Long>> {
        return sessionSceneDao.observeLinkedSceneIds(sessionId)
    }

    override fun observeResumeScene(campaignId: Long): Flow<Scene?> {
        return sessionDao.observeResumeScene(campaignId).map { entity ->
            entity?.asDomain()
        }
    }

    override suspend fun createSession(
        campaignId: Long,
        name: String,
        dateMillis: Long,
        coverArtUri: String?,
    ): Long {
        return sessionDao.upsert(
            SessionEntity(
                campaignId = campaignId,
                name = name,
                dateMillis = dateMillis,
                coverArtUri = coverArtUri,
            ),
        )
    }

    override suspend fun deleteSession(sessionId: Long) {
        sessionDao.softDelete(
            sessionId = sessionId,
            deletedAt = System.currentTimeMillis(),
        )
    }

    override suspend fun getSession(sessionId: Long): Session? {
        return sessionDao.getById(sessionId)?.asDomain()
    }

    override suspend fun updateLastOpenedScene(sessionId: Long, sceneId: Long) {
        sessionDao.updateLastOpenedScene(sessionId, sceneId)
    }

    override suspend fun linkScenes(sessionId: Long, sceneIds: List<Long>) {
        sceneIds
            .distinct()
            .forEach { sceneId ->
                sessionSceneDao.link(
                    SessionSceneCrossRef(
                        sessionId = sessionId,
                        sceneId = sceneId,
                    ),
                )
            }
    }

    override suspend fun unlinkScene(sessionId: Long, sceneId: Long) {
        sessionSceneDao.unlink(sessionId, sceneId)
    }
}
