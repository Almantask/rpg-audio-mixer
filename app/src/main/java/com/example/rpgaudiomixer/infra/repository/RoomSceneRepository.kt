package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFXTrack
import com.example.rpgaudiomixer.domain.model.SceneSoundscapeCategory
import com.example.rpgaudiomixer.domain.repository.LibraryRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.infra.db.dao.SceneDao
import com.example.rpgaudiomixer.infra.db.entities.SceneFXTrackEntity
import com.example.rpgaudiomixer.infra.db.entities.SceneSoundscapeCategoryEntity
import com.example.rpgaudiomixer.infra.db.entities.SessionSceneEntity
import com.example.rpgaudiomixer.infra.db.toDomain
import com.example.rpgaudiomixer.infra.db.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomSceneRepository @Inject constructor(
    private val dao: SceneDao,
    private val libraryRepository: LibraryRepository,
) : SceneRepository {

    override fun getAllScenes(): Flow<List<Scene>> =
        dao.getAllScenes().map { list -> list.map { it.toDomain() } }

    override fun getScenesForSession(sessionId: Long): Flow<List<Scene>> =
        dao.getScenesForSession(sessionId).map { list -> list.map { it.toDomain() } }

    override suspend fun getSceneById(id: Long): Scene? =
        dao.getSceneById(id)?.toDomain()

    override suspend fun upsertScene(scene: Scene): Long =
        dao.upsertScene(scene.toEntity())

    override suspend fun deleteScene(id: Long) =
        dao.deleteScene(id)

    override suspend fun addSceneToSession(sessionId: Long, sceneId: Long) =
        dao.addSceneToSession(SessionSceneEntity(sessionId, sceneId))

    override suspend fun removeSceneFromSession(sessionId: Long, sceneId: Long) =
        dao.removeSceneFromSession(sessionId, sceneId)

    override fun getSceneSoundscapeCategories(sceneId: Long): Flow<List<SceneSoundscapeCategory>> =
        dao.getSceneSoundscapeCategoryEntities(sceneId).map { entities ->
            entities.map { entity ->
                val category = libraryRepository.getCategoryById(entity.categoryId)
                    ?: return@map null
                entity.toDomain(category)
            }.filterNotNull()
        }

    override suspend fun addCategoryToScene(sceneId: Long, categoryId: Long): Long {
        val nextOrder = dao.getSceneSoundscapeCategoryEntities(sceneId)
            .map { it.size }
            .let { flow ->
                var count = 0
                flow.collect { count = it }
                count
            }
        return dao.upsertSceneSoundscapeCategory(
            SceneSoundscapeCategoryEntity(
                sceneId = sceneId,
                categoryId = categoryId,
                sortOrder = nextOrder,
            )
        )
    }

    override suspend fun updateCategoryMixVolume(sceneCategoryId: Long, volume: Float) =
        dao.updateCategoryMixVolume(sceneCategoryId, volume)

    override suspend fun updateCategorySortOrder(sceneCategoryId: Long, sortOrder: Int) =
        dao.updateCategorySortOrder(sceneCategoryId, sortOrder)

    override suspend fun removeCategoryFromScene(sceneCategoryId: Long) =
        dao.removeSceneSoundscapeCategory(sceneCategoryId)

    override fun getSceneFXTracks(sceneId: Long): Flow<List<SceneFXTrack>> =
        dao.getSceneFXTrackEntities(sceneId).map { entities ->
            entities.map { entity ->
                val fxTrack = libraryRepository.getFXTrackById(entity.fxTrackId)
                    ?: return@map null
                entity.toDomain(fxTrack)
            }.filterNotNull()
        }

    override suspend fun addFXToScene(sceneId: Long, fxTrackId: Long): Long {
        val nextOrder = dao.getSceneFXTrackEntities(sceneId)
            .map { it.size }
            .let { flow ->
                var count = 0
                flow.collect { count = it }
                count
            }
        return dao.upsertSceneFXTrack(
            SceneFXTrackEntity(
                sceneId = sceneId,
                fxTrackId = fxTrackId,
                sortOrder = nextOrder,
            )
        )
    }

    override suspend fun updateFXSortOrder(sceneFxId: Long, sortOrder: Int) =
        dao.updateFXSortOrder(sceneFxId, sortOrder)

    override suspend fun removeFXFromScene(sceneFxId: Long) =
        dao.removeSceneFXTrack(sceneFxId)

    override suspend fun updateSceneMasterAtmosphereVolume(sceneId: Long, volume: Float) =
        dao.updateMasterAtmosphereVolume(sceneId, volume)

    override suspend fun updateSceneMasterSoundboardVolume(sceneId: Long, volume: Float) =
        dao.updateMasterSoundboardVolume(sceneId, volume)
}
