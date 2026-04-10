package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import com.example.rpgaudiomixer.data.session.local.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.session.local.SessionSceneDao
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
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
        return sceneDao.insert(entity)
    }

    override suspend fun update(scene: Scene) {
        sceneDao.update(scene.toEntity())
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

    private fun SceneEntity.toDomain() = Scene(
        id = id,
        name = name,
        description = description,
        tags = tags.split(",").filter { it.isNotBlank() }
    )

    private fun Scene.toEntity() = SceneEntity(
        id = id,
        name = name,
        description = description,
        tags = tags.joinToString(",")
    )
}
