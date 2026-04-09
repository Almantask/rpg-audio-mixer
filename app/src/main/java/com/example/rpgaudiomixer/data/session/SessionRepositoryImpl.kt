package com.example.rpgaudiomixer.data.session

import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import com.example.rpgaudiomixer.data.session.local.SessionDao
import com.example.rpgaudiomixer.data.session.local.SessionEntity
import com.example.rpgaudiomixer.data.session.local.SessionListItemRow
import com.example.rpgaudiomixer.data.session.local.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.session.local.SessionSceneDao
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.session.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao,
    private val sessionSceneDao: SessionSceneDao,
) : SessionRepository {
    override fun observeSessions(campaignId: Long): Flow<List<Session>> = sessionDao.observeByCampaign(campaignId)
        .map { rows -> rows.map(SessionListItemRow::toDomain) }

    override fun observeSession(sessionId: Long): Flow<Session?> = sessionDao.observeById(sessionId)
        .map { entity -> entity?.toDomain() }

    override fun observeScenesForSession(sessionId: Long): Flow<List<Scene>> =
        sessionSceneDao.observeScenesBySession(sessionId).map { scenes ->
            scenes.map(SceneEntity::toDomain)
        }

    override suspend fun upsertSession(session: Session): Long = sessionDao.upsert(session.toEntity())

    override suspend fun deleteSession(sessionId: Long) {
        sessionDao.deleteById(sessionId)
    }

    override suspend fun linkScenes(sessionId: Long, sceneIds: List<Long>) {
        sessionSceneDao.linkAll(sceneIds.map { sceneId -> SessionSceneCrossRef(sessionId, sceneId) })
    }

    override suspend fun unlinkScene(sessionId: Long, sceneId: Long) {
        sessionSceneDao.unlink(sessionId, sceneId)
    }

    override suspend fun clearAll() {
        sessionSceneDao.clearAll()
        sessionDao.clearAll()
    }
}

private fun SessionListItemRow.toDomain(): Session = Session(
    id = id,
    campaignId = campaignId,
    name = name,
    dateMillis = dateMillis,
    coverArtUri = coverArtUri,
    sceneCount = sceneCount,
)

private fun SessionEntity.toDomain(): Session = Session(
    id = id,
    campaignId = campaignId,
    name = name,
    dateMillis = dateMillis,
    coverArtUri = coverArtUri,
)

private fun Session.toEntity(): SessionEntity = SessionEntity(
    id = id,
    campaignId = campaignId,
    name = name,
    dateMillis = dateMillis,
    coverArtUri = coverArtUri,
)

private fun SceneEntity.toDomain(): Scene = Scene(
    id = id,
    name = name,
    description = description,
    tags = tagsCsv.toCsvList(),
    soundscapeCategoryNames = soundscapeCategoriesCsv.toCsvList(),
)

private fun String.toCsvList(): List<String> = split(',')
    .map(String::trim)
    .filter(String::isNotBlank)
