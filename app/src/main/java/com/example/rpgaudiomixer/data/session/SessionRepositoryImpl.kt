package com.example.rpgaudiomixer.data.session

import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import com.example.rpgaudiomixer.data.session.local.SessionDao
import com.example.rpgaudiomixer.data.session.local.SessionEntity
import com.example.rpgaudiomixer.data.session.local.SessionSceneDao
import com.example.rpgaudiomixer.data.session.local.SessionWithSceneCount
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

    override fun observeSessionsByCampaign(campaignId: Long): Flow<List<Session>> {
        return sessionDao.observeByCampaign(campaignId).map { rows -> rows.map { it.toDomainModel() } }
    }

    override fun observeSession(sessionId: Long): Flow<Session?> {
        return sessionDao.observeSession(sessionId).map { row -> row?.toDomainModel() }
    }

    override fun observeScenesBySession(sessionId: Long): Flow<List<Scene>> {
        return sessionSceneDao.observeScenesBySession(sessionId).map { rows ->
            rows.map { it.toDomainModel() }
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
                name = name.trim(),
                dateMillis = dateMillis,
                coverArtUri = coverArtUri,
            ),
        )
    }

    override suspend fun deleteSession(sessionId: Long) {
        sessionDao.deleteById(sessionId)
    }

    override suspend fun linkScenes(sessionId: Long, sceneIds: List<Long>) {
        sceneIds.forEach { sceneId ->
            sessionSceneDao.linkScene(sessionId, sceneId)
        }
    }

    override suspend fun unlinkScene(sessionId: Long, sceneId: Long) {
        sessionSceneDao.unlinkScene(sessionId, sceneId)
    }
}

private fun SessionWithSceneCount.toDomainModel(): Session {
    return Session(
        id = id,
        campaignId = campaignId,
        name = name,
        dateMillis = dateMillis,
        coverArtUri = coverArtUri,
        sceneCount = sceneCount,
    )
}

private fun SceneEntity.toDomainModel(): Scene {
    return Scene(
        id = id,
        name = name,
        description = description,
        tags = tags
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() },
    )
}
