package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.local.SceneDao
import com.example.rpgaudiomixer.data.local.SceneEntity
import com.example.rpgaudiomixer.data.local.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.local.SessionSceneDao
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of SceneRepository
 * Maps between entity and domain models
 */
class SceneRepositoryImpl @Inject constructor(
    private val sceneDao: SceneDao,
    private val sessionSceneDao: SessionSceneDao
) : SceneRepository {

    override fun observeAll(): Flow<List<Scene>> =
        sceneDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeBySession(sessionId: Long): Flow<List<Scene>> =
        sessionSceneDao.observeScenesBySession(sessionId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getById(id: Long): Scene? =
        sceneDao.getById(id)?.toDomain()

    override suspend fun upsert(scene: Scene): Long =
        sceneDao.upsert(scene.toEntity())

    override suspend fun deleteById(id: Long) =
        sceneDao.deleteById(id)

    override suspend fun linkToSession(sessionId: Long, sceneId: Long) {
        sessionSceneDao.link(SessionSceneCrossRef(sessionId, sceneId))
    }

    override suspend fun unlinkFromSession(sessionId: Long, sceneId: Long) {
        sessionSceneDao.unlink(sessionId, sceneId)
    }

    override suspend fun isSceneLinkedToSession(sessionId: Long, sceneId: Long): Boolean =
        sessionSceneDao.isSceneLinked(sessionId, sceneId) > 0
}

/**
 * Extension functions for mapping between Entity and Domain models
 */
private fun SceneEntity.toDomain() = Scene(
    id = id,
    name = name,
    description = description,
    tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
)

private fun Scene.toEntity() = SceneEntity(
    id = id,
    name = name,
    description = description,
    tags = tags.joinToString(",")
)
