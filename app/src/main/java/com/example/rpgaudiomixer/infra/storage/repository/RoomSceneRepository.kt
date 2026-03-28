package com.example.rpgaudiomixer.infra.storage.repository

import com.example.rpgaudiomixer.domain.model.FxEffect
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.storage.SceneRepository
import com.example.rpgaudiomixer.infra.storage.db.dao.SceneDao
import com.example.rpgaudiomixer.infra.storage.db.dao.SceneFxWithEffect
import com.example.rpgaudiomixer.infra.storage.db.dao.SceneSoundscapeWithCategory
import com.example.rpgaudiomixer.infra.storage.db.entity.SceneEntity
import com.example.rpgaudiomixer.infra.storage.db.entity.SceneFxRefEntity
import com.example.rpgaudiomixer.infra.storage.db.entity.SceneSoundscapeRefEntity
import com.example.rpgaudiomixer.infra.storage.db.entity.SessionSceneCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomSceneRepository @Inject constructor(
    private val dao: SceneDao,
) : SceneRepository {

    override fun getAllScenes(): Flow<List<Scene>> =
        dao.getAllScenes().map { it.map(SceneEntity::toDomain) }

    override fun getSceneById(id: Long): Flow<Scene?> =
        dao.getSceneById(id).map { it?.toDomain() }

    override fun getScenesBySession(sessionId: Long): Flow<List<Scene>> =
        dao.getScenesBySession(sessionId).map { it.map(SceneEntity::toDomain) }

    override fun getSoundscapesForScene(sceneId: Long): Flow<List<SceneSoundscape>> =
        dao.getSceneSoundscapeRefs(sceneId).map { it.map(SceneSoundscapeWithCategory::toDomain) }

    override fun getFxForScene(sceneId: Long): Flow<List<SceneFx>> =
        dao.getSceneFxRefs(sceneId).map { it.map(SceneFxWithEffect::toDomain) }

    override suspend fun insert(scene: Scene): Long = dao.insert(scene.toEntity())

    override suspend fun update(scene: Scene) = dao.update(scene.toEntity())

    override suspend fun delete(scene: Scene) = dao.delete(scene.toEntity())

    override suspend fun addSceneToSession(sessionId: Long, sceneId: Long) =
        dao.insertSessionScene(SessionSceneCrossRef(sessionId, sceneId))

    override suspend fun removeSceneFromSession(sessionId: Long, sceneId: Long) =
        dao.deleteSessionScene(sessionId, sceneId)

    override suspend fun addSoundscapeToScene(sceneId: Long, categoryId: Long) =
        dao.insertSceneSoundscape(SceneSoundscapeRefEntity(sceneId, categoryId))

    override suspend fun removeSoundscapeFromScene(sceneId: Long, categoryId: Long) =
        dao.deleteSceneSoundscape(sceneId, categoryId)

    override suspend fun updateSceneSoundscapeMix(sceneId: Long, categoryId: Long, mix: Float) =
        dao.updateSceneSoundscapeMix(sceneId, categoryId, mix)

    override suspend fun updateSceneSoundscapeIntensity(sceneId: Long, categoryId: Long, intensity: Int) =
        dao.updateSceneSoundscapeIntensity(sceneId, categoryId, intensity)

    override suspend fun addFxToScene(sceneId: Long, fxEffectId: Long) =
        dao.insertSceneFx(SceneFxRefEntity(sceneId, fxEffectId))

    override suspend fun removeFxFromScene(sceneId: Long, fxEffectId: Long) =
        dao.deleteSceneFx(sceneId, fxEffectId)

    override suspend fun reorderSoundscapes(sceneId: Long, orderedCategoryIds: List<Long>) {
        orderedCategoryIds.forEachIndexed { index, categoryId ->
            dao.updateSceneSoundscapeOrder(sceneId, categoryId, index)
        }
    }

    override suspend fun reorderFx(sceneId: Long, orderedFxIds: List<Long>) {
        orderedFxIds.forEachIndexed { index, fxId ->
            dao.updateSceneFxOrder(sceneId, fxId, index)
        }
    }
}

private fun SceneEntity.toDomain() = Scene(
    id = id, name = name, description = description,
    tags = tags, coverArtUri = coverArtUri,
    soundboardMasterVolume = soundboardMasterVolume,
    atmosphereMasterVolume = atmosphereMasterVolume,
    playCount = playCount, createdAt = createdAt,
)

private fun Scene.toEntity() = SceneEntity(
    id = id, name = name, description = description,
    tags = tags, coverArtUri = coverArtUri,
    soundboardMasterVolume = soundboardMasterVolume,
    atmosphereMasterVolume = atmosphereMasterVolume,
    playCount = playCount, createdAt = createdAt,
)

private fun SceneSoundscapeWithCategory.toDomain() = SceneSoundscape(
    sceneId = sceneId,
    category = SoundscapeCategory(id = catId, name = catName, parentCategory = parentCategory, createdAt = catCreatedAt),
    mixVolume = mixVolume,
    activeIntensity = activeIntensity,
    order = orderIndex,
)

private fun SceneFxWithEffect.toDomain() = SceneFx(
    sceneId = sceneId,
    effect = FxEffect(
        id = fxId, name = fxName, trackFilePath = trackFilePath,
        tags = tags, durationMs = durationMs, playCount = playCount, createdAt = fxCreatedAt,
    ),
    order = orderIndex,
)
