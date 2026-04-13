package com.example.rpgaudiomixer.app.data.scene

import com.example.rpgaudiomixer.app.data.local.dao.SceneDao
import com.example.rpgaudiomixer.app.data.local.dao.SessionSceneDao
import com.example.rpgaudiomixer.app.data.local.entities.SceneEntity
import com.example.rpgaudiomixer.app.data.local.entities.SessionSceneCrossRef
import com.example.rpgaudiomixer.app.domain.model.Scene
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SceneRepositoryImpl @Inject constructor(
    private val sceneDao: SceneDao,
    private val sessionSceneDao: SessionSceneDao
) : SceneRepository {

    override fun observeAll(): Flow<List<Scene>> {
        return sceneDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeDeleted(): Flow<List<Scene>> {
        return sceneDao.observeDeleted().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long): Scene? {
        return sceneDao.getById(id)?.toDomain()
    }

    override suspend fun createScene(name: String, description: String?, tags: String?): Long {
        val entity = SceneEntity(
            name = name,
            description = description,
            tags = tags,
            createdAt = System.currentTimeMillis()
        )
        return sceneDao.upsert(entity)
    }

    override suspend fun updateScene(scene: Scene) {
        sceneDao.upsert(scene.toEntity())
    }

    override suspend fun deleteScene(id: Long) {
        sceneDao.softDelete(id)
    }

    override suspend fun restoreScene(id: Long) {
        sceneDao.restore(id)
    }

    override suspend fun permanentlyDeleteScene(id: Long) {
        sceneDao.permanentlyDelete(id)
    }

    override suspend fun cloneScene(sceneId: Long): Long {
        val original = sceneDao.getById(sceneId)
            ?: throw IllegalArgumentException("Scene with id $sceneId not found")
        val clone = SceneEntity(
            name = "Copy of ${original.name}",
            description = original.description,
            tags = original.tags,
            createdAt = System.currentTimeMillis()
        )
        return sceneDao.upsert(clone)
    }

    override fun observeScenesForSession(sessionId: Long): Flow<List<Scene>> {
        return sessionSceneDao.observeScenesForSession(sessionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun linkSceneToSession(sessionId: Long, sceneId: Long) {
        sessionSceneDao.linkSceneToSession(
            SessionSceneCrossRef(sessionId = sessionId, sceneId = sceneId)
        )
    }

    override suspend fun unlinkSceneFromSession(sessionId: Long, sceneId: Long) {
        sessionSceneDao.unlinkSceneFromSession(sessionId, sceneId)
    }

    override suspend fun deleteAll() {
        sceneDao.deleteAll()
    }

    private fun SceneEntity.toDomain() = Scene(
        id = id,
        name = name,
        description = description,
        tags = tags,
        isDeleted = isDeleted,
        deletedAt = deletedAt,
        createdAt = createdAt
    )

    private fun Scene.toEntity() = SceneEntity(
        id = id,
        name = name,
        description = description,
        tags = tags,
        isDeleted = isDeleted,
        deletedAt = deletedAt,
        createdAt = createdAt
    )
}
