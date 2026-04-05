package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.*
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import com.example.rpgaudiomixer.infra.local.dao.*
import com.example.rpgaudiomixer.infra.local.entities.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : SessionRepository {
    override fun observeByCampaign(campaignId: Long): Flow<List<Session>> {
        return sessionDao.observeByCampaign(campaignId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeLatestByCampaign(campaignId: Long): Flow<Session?> {
        return sessionDao.observeLatestByCampaign(campaignId).map { it?.toDomain() }
    }

    override fun observeDeleted(): Flow<List<Session>> {
        return sessionDao.observeDeleted().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateLastOpenedScene(sessionId: Long, sceneId: Long) {
        sessionDao.updateLastOpenedScene(sessionId, sceneId)
    }

    override suspend fun upsert(session: Session) {
        sessionDao.upsert(SessionEntity.fromDomain(session))
    }

    override suspend fun softDelete(id: Long) {
        sessionDao.softDelete(id, System.currentTimeMillis())
    }

    override suspend fun restore(id: Long) {
        sessionDao.restore(id)
    }

    override suspend fun permanentDelete(id: Long) {
        sessionDao.permanentDelete(id)
    }

    override suspend fun purgeOldDeleted(threshold: Long) {
        sessionDao.purgeOldDeleted(threshold)
    }
}

class SceneRepositoryImpl @Inject constructor(
    private val sceneDao: SceneDao,
    private val sessionSceneDao: SessionSceneDao,
    private val sceneSoundscapeDao: SceneSoundscapeDao,
    private val sceneFxDao: SceneFxDao,
    private val soundscapeRepository: SoundscapeRepository
) : SceneRepository {
    override fun observeAll(): Flow<List<Scene>> {
        return sceneDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getById(id: Long): Flow<Scene?> {
        return sceneDao.getById(id).map { it?.toDomain() }
    }

    override fun observeScenesBySession(sessionId: Long): Flow<List<Scene>> {
        return sessionSceneDao.observeScenesBySession(sessionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeDeleted(): Flow<List<Scene>> {
        return sceneDao.observeDeleted().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun upsert(scene: Scene) {
        sceneDao.upsert(SceneEntity.fromDomain(scene))
    }

    override suspend fun softDelete(id: Long) {
        sceneDao.softDelete(id, System.currentTimeMillis())
    }

    override suspend fun restore(id: Long) {
        sceneDao.restore(id)
    }

    override suspend fun permanentDelete(id: Long) {
        sceneDao.permanentDelete(id)
    }

    override suspend fun purgeOldDeleted(threshold: Long) {
        sceneDao.purgeOldDeleted(threshold)
    }

    override suspend fun linkToSession(sessionId: Long, sceneId: Long) {
        sessionSceneDao.link(SessionSceneCrossRef(sessionId, sceneId))
    }

    override suspend fun unlinkFromSession(sessionId: Long, sceneId: Long) {
        sessionSceneDao.unlink(sessionId, sceneId)
    }

    override fun observeCategoriesByScene(sceneId: Long): Flow<List<SceneSoundscapeCategory>> {
        return combine(
            sceneSoundscapeDao.observeCategoriesByScene(sceneId),
            sceneSoundscapeDao.observeCrossRefsByScene(sceneId)
        ) { categories, crossRefs ->
            val crossRefMap = crossRefs.associateBy { it.categoryId }
            
            val flows = categories.map { entity ->
                val crossRef = crossRefMap[entity.id] ?: return@map flowOf(null)
                soundscapeRepository.observeTrackCountsByIntensity(entity.id).map { counts ->
                    SceneSoundscapeCategory(
                        sceneId = sceneId,
                        category = entity.toDomain(counts),
                        displayOrder = crossRef.displayOrder,
                        mixVolume = crossRef.mixVolume,
                        intensityLevel = when (crossRef.intensityLevel) {
                            1 -> IntensityLevel.I
                            2 -> IntensityLevel.II
                            3 -> IntensityLevel.III
                            else -> IntensityLevel.I
                        }
                    )
                }
            }
            
            if (flows.isEmpty()) flowOf(emptyList())
            else combine(flows) { it.filterNotNull().toList() }
        }.flatMapLatest { it }
    }

    override suspend fun addCategoryToScene(sceneId: Long, categoryId: Long, displayOrder: Int) {
        sceneSoundscapeDao.upsert(
            SceneSoundscapeCrossRef(
                sceneId = sceneId,
                categoryId = categoryId,
                displayOrder = displayOrder
            )
        )
    }

    override suspend fun removeCategoryFromScene(sceneId: Long, categoryId: Long) {
        sceneSoundscapeDao.delete(sceneId, categoryId)
    }

    override suspend fun updateSceneCategoryOrder(sceneId: Long, categoryId: Long, newOrder: Int) {
        sceneSoundscapeDao.updateOrder(sceneId, categoryId, newOrder)
    }

    override suspend fun updateSceneCategoryMixVolume(sceneId: Long, categoryId: Long, volume: Float) {
        sceneSoundscapeDao.updateMixVolume(sceneId, categoryId, volume)
    }

    override suspend fun updateSceneCategoryIntensity(sceneId: Long, categoryId: Long, intensity: IntensityLevel) {
        sceneSoundscapeDao.updateIntensity(sceneId, categoryId, intensity.value)
    }

    override fun observeFxByScene(sceneId: Long): Flow<List<FXTrack>> {
        return sceneFxDao.observeFxByScene(sceneId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addFxToScene(sceneId: Long, fxId: Long, displayOrder: Int) {
        sceneFxDao.upsert(
            SceneFxCrossRef(
                sceneId = sceneId,
                fxTrackId = fxId,
                displayOrder = displayOrder
            )
        )
    }

    override suspend fun removeFxFromScene(sceneId: Long, fxId: Long) {
        sceneFxDao.delete(sceneId, fxId)
    }

    override suspend fun updateSceneFxOrder(sceneId: Long, fxId: Long, newOrder: Int) {
        sceneFxDao.updateOrder(sceneId, fxId, newOrder)
    }
}
