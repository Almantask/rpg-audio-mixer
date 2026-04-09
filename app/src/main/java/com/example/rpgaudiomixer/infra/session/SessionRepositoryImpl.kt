package com.example.rpgaudiomixer.infra.session

import com.example.rpgaudiomixer.domain.scene.Scene
import com.example.rpgaudiomixer.domain.session.Session
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.infra.scene.SceneEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao,
    private val sessionSceneDao: SessionSceneDao
) : SessionRepository {

    override fun observeByCampaign(campaignId: Long): Flow<List<Session>> {
        return sessionDao.observeByCampaign(campaignId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun upsert(session: Session) {
        withContext(Dispatchers.IO) {
            sessionDao.upsert(session.toEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(Dispatchers.IO) {
            sessionDao.delete(id)
        }
    }

    override fun observeScenesBySession(sessionId: Long): Flow<List<Scene>> {
        return sessionSceneDao.observeScenesBySession(sessionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun linkScene(sessionId: Long, sceneId: Long) {
        withContext(Dispatchers.IO) {
            sessionSceneDao.link(SessionSceneCrossRef(sessionId, sceneId))
        }
    }

    override suspend fun unlinkScene(sessionId: Long, sceneId: Long) {
        withContext(Dispatchers.IO) {
            sessionSceneDao.unlink(SessionSceneCrossRef(sessionId, sceneId))
        }
    }
}

private fun SessionEntity.toDomain(): Session = Session(
    id = id,
    campaignId = campaignId,
    name = name,
    date = date,
    coverArtUri = coverArtUri
)

private fun Session.toEntity(): SessionEntity = SessionEntity(
    id = id,
    campaignId = campaignId,
    name = name,
    date = date,
    coverArtUri = coverArtUri
)

private fun SceneEntity.toDomain(): Scene = Scene(
    id = id,
    name = name,
    description = description,
    tags = if (tags.isEmpty()) emptyList() else tags.split(",")
)
