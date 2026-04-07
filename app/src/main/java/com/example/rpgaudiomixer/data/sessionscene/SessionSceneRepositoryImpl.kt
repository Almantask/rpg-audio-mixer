package com.example.rpgaudiomixer.data.sessionscene

import com.example.rpgaudiomixer.data.local.SceneEntity
import com.example.rpgaudiomixer.data.local.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.local.SessionSceneDao
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SessionSceneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionSceneRepositoryImpl @Inject constructor(
    private val sessionSceneDao: SessionSceneDao
) : SessionSceneRepository {

    override fun observeScenesBySession(sessionId: Long): Flow<List<Scene>> {
        return sessionSceneDao.observeScenesBySession(sessionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun linkScene(sessionId: Long, sceneId: Long) {
        sessionSceneDao.link(SessionSceneCrossRef(sessionId, sceneId))
    }

    override suspend fun unlinkScene(sessionId: Long, sceneId: Long) {
        sessionSceneDao.unlinkByIds(sessionId, sceneId)
    }

    private fun SceneEntity.toDomain() = Scene(
        id = id,
        name = name,
        description = description,
        tags = tags
    )
}
