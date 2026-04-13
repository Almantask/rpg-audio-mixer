package com.example.rpgaudiomixer.app.data.scene

import com.example.rpgaudiomixer.app.data.local.dao.SceneDao
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
    private val sceneDao: SceneDao
) : SceneRepository {

    override fun observeAll(): Flow<List<Scene>> =
        sceneDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeBySession(sessionId: Long): Flow<List<Scene>> =
        sceneDao.observeBySession(sessionId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun createScene(name: String) {
        sceneDao.upsert(SceneEntity(name = name))
    }

    override suspend fun deleteScene(scene: Scene) {
        sceneDao.softDelete(scene.id)
    }

    override suspend fun linkToSession(sceneId: Long, sessionId: Long) {
        sceneDao.linkSceneToSession(SessionSceneCrossRef(sessionId = sessionId, sceneId = sceneId))
    }

    override suspend fun unlinkFromSession(sceneId: Long, sessionId: Long) {
        sceneDao.unlinkSceneFromSession(sessionId = sessionId, sceneId = sceneId)
    }

    override suspend fun deleteAll() {
        sceneDao.deleteAll()
        sceneDao.deleteAllCrossRefs()
    }

    private fun SceneEntity.toDomain() = Scene(
        id = id,
        name = name,
        createdAt = createdAt
    )
}
