package com.example.rpgaudiomixer.app.data.scene

import com.example.rpgaudiomixer.app.data.local.dao.SceneDao
import com.example.rpgaudiomixer.app.data.local.dao.SoundscapeCategoryDao
import com.example.rpgaudiomixer.app.data.local.entities.SceneEntity
import com.example.rpgaudiomixer.app.data.local.entities.SessionSceneCrossRef
import com.example.rpgaudiomixer.app.data.local.entities.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.app.domain.model.Scene
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SceneRepositoryImpl @Inject constructor(
    private val sceneDao: SceneDao,
    private val soundscapeCategoryDao: SoundscapeCategoryDao,
) : SceneRepository {

    override fun observeAll(): Flow<List<Scene>> =
        sceneDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeLatest(): Flow<Scene?> =
        sceneDao.observeLatest().map { it?.toDomain() }

    override fun observeBySession(sessionId: Long): Flow<List<Scene>> =
        sceneDao.observeBySession(sessionId).map { entities -> entities.map { it.toDomain() } }

    override fun observeDeleted(): Flow<List<Scene>> =
        sceneDao.observeDeleted().map { entities -> entities.map { it.toDomain() } }

    override suspend fun createScene(name: String) {
        sceneDao.upsert(SceneEntity(name = name))
    }

    override suspend fun deleteScene(scene: Scene) {
        sceneDao.softDelete(scene.id)
    }

    override suspend fun restore(scene: Scene) {
        sceneDao.restore(scene.id)
    }

    override suspend fun hardDelete(scene: Scene) {
        sceneDao.hardDelete(scene.id)
    }

    override suspend fun purgeOlderThan(cutoff: Long) {
        sceneDao.purgeOlderThan(cutoff)
    }

    override suspend fun purgeAllDeleted() {
        sceneDao.purgeAllDeleted()
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

    override suspend fun cloneScene(sourceSceneId: Long, newName: String): Long {
        sceneDao.getById(sourceSceneId)
            ?: error("Source scene $sourceSceneId not found")
        val newScene = SceneEntity(name = newName, createdAt = System.currentTimeMillis())
        val newSceneId = sceneDao.upsert(newScene)

        // Clone all soundscape categories from the source scene
        val sourceCategories = soundscapeCategoryDao.getByScene(sourceSceneId)
        sourceCategories.forEachIndexed { index, category ->
            soundscapeCategoryDao.upsert(
                SoundscapeCategoryEntity(
                    sceneId = newSceneId,
                    name = category.name,
                    position = index,
                    type = category.type,
                )
            )
        }

        return newSceneId
    }

    private fun SceneEntity.toDomain() = Scene(
        id = id,
        name = name,
        createdAt = createdAt,
        deletedAt = deletedAt
    )
}

    override fun observeAll(): Flow<List<Scene>> =
        sceneDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeLatest(): Flow<Scene?> =
        sceneDao.observeLatest().map { it?.toDomain() }

    override fun observeBySession(sessionId: Long): Flow<List<Scene>> =
        sceneDao.observeBySession(sessionId).map { entities -> entities.map { it.toDomain() } }

    override fun observeDeleted(): Flow<List<Scene>> =
        sceneDao.observeDeleted().map { entities -> entities.map { it.toDomain() } }

    override suspend fun createScene(name: String) {
        sceneDao.upsert(SceneEntity(name = name))
    }

    override suspend fun deleteScene(scene: Scene) {
        sceneDao.softDelete(scene.id)
    }

    override suspend fun restore(scene: Scene) {
        sceneDao.restore(scene.id)
    }

    override suspend fun hardDelete(scene: Scene) {
        sceneDao.hardDelete(scene.id)
    }

    override suspend fun purgeOlderThan(cutoff: Long) {
        sceneDao.purgeOlderThan(cutoff)
    }

    override suspend fun purgeAllDeleted() {
        sceneDao.purgeAllDeleted()
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

    override suspend fun cloneScene(sourceSceneId: Long, newName: String): Long {
        val source = sceneDao.getById(sourceSceneId)
            ?: error("Source scene $sourceSceneId not found")
        val newScene = SceneEntity(name = newName, createdAt = System.currentTimeMillis())
        return sceneDao.upsert(newScene)
    }

    private fun SceneEntity.toDomain() = Scene(
        id = id,
        name = name,
        createdAt = createdAt,
        deletedAt = deletedAt
    )
}
