package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.local.*
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SceneRepositoryImpl @Inject constructor(
    private val sceneDao: SceneDao,
    private val sessionSceneDao: SessionSceneDao
) : SceneRepository {

    override fun observeAll(): Flow<List<Scene>> {
        return sceneDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeBySession(sessionId: Long): Flow<List<Scene>> {
        return sessionSceneDao.observeScenesBySession(sessionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long): Scene? {
        return sceneDao.getById(id)?.toDomain()
    }

    override suspend fun create(name: String, description: String?, tags: List<String>): Long {
        val entity = SceneEntity(
            name = name,
            description = description,
            tags = tags.joinToString(",")
        )
        return sceneDao.upsert(entity)
    }

    override suspend fun update(scene: Scene) {
        sceneDao.upsert(scene.toEntity())
    }

    override suspend fun delete(id: Long) {
        sceneDao.deleteById(id)
    }

    override suspend fun linkToSession(sessionId: Long, sceneId: Long) {
        sessionSceneDao.link(SessionSceneCrossRef(sessionId, sceneId))
    }

    override suspend fun unlinkFromSession(sessionId: Long, sceneId: Long) {
        sessionSceneDao.unlinkByIds(sessionId, sceneId)
    }

    private fun SceneEntity.toDomain(): Scene {
        return Scene(
            id = id,
            name = name,
            description = description,
            tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() }
        )
    }

    private fun Scene.toEntity(): SceneEntity {
        return SceneEntity(
            id = id,
            name = name,
            description = description,
            tags = tags.joinToString(",")
        )
    }
}
