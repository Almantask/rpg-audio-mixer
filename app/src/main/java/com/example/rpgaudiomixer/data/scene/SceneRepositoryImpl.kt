package com.example.rpgaudiomixer.data.scene

import androidx.room.withTransaction
import com.example.rpgaudiomixer.data.local.AppDatabase
import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import com.example.rpgaudiomixer.data.scene.local.SceneFxCrossRef
import com.example.rpgaudiomixer.data.scene.local.SceneFxDao
import com.example.rpgaudiomixer.data.scene.local.SceneSoundscapeCrossRef
import com.example.rpgaudiomixer.data.scene.local.SceneSoundscapeDao
import com.example.rpgaudiomixer.data.scene.local.asDomain
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SceneRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val sceneDao: SceneDao,
    private val sceneFxDao: SceneFxDao,
    private val sceneSoundscapeDao: SceneSoundscapeDao,
) : SceneRepository {

    override fun observeAll(): Flow<List<Scene>> {
        return sceneDao.observeAll().map { scenes ->
            scenes.map { it.asDomain() }
        }
    }

    override fun observeSoundscapes(sceneId: Long): Flow<List<SceneSoundscape>> {
        return sceneSoundscapeDao.observeByScene(sceneId).map { soundscapes ->
            soundscapes.map { it.asDomain() }
        }
    }

    override fun observeFx(sceneId: Long): Flow<List<SceneFx>> {
        return sceneFxDao.observeByScene(sceneId).map { fxTracks ->
            fxTracks.map { it.asDomain() }
        }
    }

    override suspend fun createScene(
        name: String,
        description: String?,
        tags: List<String>,
    ): Long {
        return sceneDao.upsert(
            SceneEntity(
                name = name,
                description = description?.trim()?.takeIf { it.isNotEmpty() },
                tags = tags
                    .map { tag -> tag.trim() }
                    .filter { tag -> tag.isNotEmpty() }
                    .distinct()
                    .joinToString(","),
                masterVolume = 1f,
            ),
        )
    }

    override suspend fun deleteScene(sceneId: Long) {
        sceneDao.softDelete(
            sceneId = sceneId,
            deletedAt = System.currentTimeMillis(),
        )
    }

    override suspend fun getScene(sceneId: Long): Scene? {
        return sceneDao.getById(sceneId)?.asDomain()
    }

    override suspend fun addFx(sceneId: Long, fxTrackIds: List<Long>) {
        appDatabase.withTransaction {
            val existingIds = sceneFxDao.getLinkedFxIds(sceneId).toSet()
            var nextDisplayOrder = sceneFxDao.getMaxDisplayOrder(sceneId) + 1
            fxTrackIds
                .distinct()
                .filterNot { fxTrackId -> fxTrackId in existingIds }
                .forEach { fxTrackId ->
                    sceneFxDao.upsert(
                        SceneFxCrossRef(
                            sceneId = sceneId,
                            fxTrackId = fxTrackId,
                            displayOrder = nextDisplayOrder++,
                        ),
                    )
                }
        }
    }

    override suspend fun removeFx(sceneId: Long, fxTrackId: Long) {
        sceneFxDao.delete(sceneId, fxTrackId)
    }

    override suspend fun reorderFx(sceneId: Long, orderedFxTrackIds: List<Long>) {
        appDatabase.withTransaction {
            orderedFxTrackIds.distinct().forEachIndexed { index, fxTrackId ->
                sceneFxDao.updateDisplayOrder(sceneId, fxTrackId, index)
            }
        }
    }

    override suspend fun addSoundscapes(sceneId: Long, categoryIds: List<Long>) {
        appDatabase.withTransaction {
            val existingIds = sceneSoundscapeDao.getLinkedCategoryIds(sceneId).toSet()
            var nextDisplayOrder = sceneSoundscapeDao.getMaxDisplayOrder(sceneId) + 1
            categoryIds
                .distinct()
                .filterNot { categoryId -> categoryId in existingIds }
                .forEach { categoryId ->
                    sceneSoundscapeDao.upsert(
                        SceneSoundscapeCrossRef(
                            sceneId = sceneId,
                            categoryId = categoryId,
                            displayOrder = nextDisplayOrder++,
                            mixVolume = 1f,
                            intensityLevel = IntensityLevel.I.value,
                        ),
                    )
                }
        }
    }

    override suspend fun removeSoundscape(sceneId: Long, categoryId: Long) {
        sceneSoundscapeDao.delete(sceneId, categoryId)
    }

    override suspend fun updateSceneMasterVolume(sceneId: Long, masterVolume: Float) {
        sceneDao.updateMasterVolume(sceneId, masterVolume.coerceIn(0f, 1f))
    }

    override suspend fun updateSoundscapeMix(sceneId: Long, categoryId: Long, mixVolume: Float) {
        sceneSoundscapeDao.updateMixVolume(
            sceneId = sceneId,
            categoryId = categoryId,
            mixVolume = mixVolume.coerceIn(0f, 1f),
        )
    }

    override suspend fun updateSoundscapeIntensity(
        sceneId: Long,
        categoryId: Long,
        intensityLevel: IntensityLevel,
    ) {
        sceneSoundscapeDao.updateIntensityLevel(sceneId, categoryId, intensityLevel.value)
    }

    override suspend fun reorderSoundscapes(sceneId: Long, orderedCategoryIds: List<Long>) {
        appDatabase.withTransaction {
            orderedCategoryIds.distinct().forEachIndexed { index, categoryId ->
                sceneSoundscapeDao.updateDisplayOrder(sceneId, categoryId, index)
            }
        }
    }
}
