package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.SessionScenesDao
import com.example.rpgaudiomixer.data.local.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.local.SceneEntity
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SessionScenesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionScenesRepositoryImpl @Inject constructor(
    private val dao: SessionScenesDao
) : SessionScenesRepository {
    override fun observeBySession(sessionId: Long): Flow<List<Scene>> =
        dao.observeBySession(sessionId).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun linkSceneToSession(sessionId: Long, sceneId: Long) {
        dao.link(SessionSceneCrossRef(sessionId, sceneId))
    }

    override suspend fun unlinkSceneFromSession(sessionId: Long, sceneId: Long) {
        dao.unlink(sessionId, sceneId)
    }
}

private fun SceneEntity.toDomain() = Scene(
    id = id,
    name = name,
    description = description,
    tags = tags.split(',').map { it.trim() }.filter { it.isNotEmpty() }
)
