package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SceneRepositoryImpl @Inject constructor(
    private val dao: SceneDao,
) : SceneRepository {

    override fun observeAll(): Flow<List<Scene>> =
        dao.observeAll().map { it.map { e -> e.toDomain() } }

    override fun observeBySession(sessionId: Long): Flow<List<Scene>> =
        dao.observeBySession(sessionId).map { it.map { e -> e.toDomain() } }

    override suspend fun create(name: String, description: String?, tags: List<String>): Scene {
        val entity = SceneEntity(name = name, description = description, tags = tags.joinToString(","))
        val id = dao.upsert(entity)
        return entity.copy(id = id).toDomain()
    }

    override suspend fun update(scene: Scene) {
        dao.upsert(scene.toEntity())
    }

    override suspend fun delete(id: Long) {
        dao.delete(id)
    }

    override suspend fun linkToSession(sceneId: Long, sessionId: Long) {
        dao.linkToSession(SessionSceneCrossRef(sessionId = sessionId, sceneId = sceneId))
    }

    override suspend fun unlinkFromSession(sceneId: Long, sessionId: Long) {
        dao.unlinkFromSession(sceneId = sceneId, sessionId = sessionId)
    }
}
