package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.local.SceneDao
import com.example.rpgaudiomixer.data.local.SceneEntity
import com.example.rpgaudiomixer.data.local.SceneSoundscapeCrossRef
import com.example.rpgaudiomixer.data.local.SceneSoundscapeDao
import com.example.rpgaudiomixer.data.local.SceneSoundscapeSummaryEntity
import com.example.rpgaudiomixer.data.local.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.local.SessionSceneDao
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class SceneRepositoryImpl @Inject constructor(
    private val sceneDao: SceneDao,
    private val sessionSceneDao: SessionSceneDao,
    private val sceneSoundscapeDao: SceneSoundscapeDao,
) : SceneRepository {

    override fun observeScenes(): Flow<List<Scene>> =
        sceneDao.observeAll().map { scenes -> scenes.map(SceneEntity::toDomain) }

    override fun observeScene(sceneId: Long): Flow<Scene?> =
        sceneDao.observeById(sceneId).map { scene -> scene?.toDomain() }

    override fun observeScenesForSession(sessionId: Long): Flow<List<Scene>> =
        sessionSceneDao.observeScenesBySession(sessionId).map { scenes ->
            scenes.map(SceneEntity::toDomain)
        }

    override fun observeAvailableScenesForSession(sessionId: Long): Flow<List<Scene>> =
        sessionSceneDao.observeAvailableScenesForSession(sessionId).map { scenes ->
            scenes.map(SceneEntity::toDomain)
        }

    override fun observeSoundscapesForScene(sceneId: Long): Flow<List<SceneSoundscape>> =
        sceneSoundscapeDao.observeSoundscapesByScene(sceneId).map { soundscapes ->
            soundscapes.map(SceneSoundscapeSummaryEntity::toDomain)
        }

    override suspend fun createScene(
        name: String,
        description: String?,
        tags: List<String>,
    ): Long = sceneDao.upsert(
        SceneEntity(
            name = name,
            description = description,
            tags = tags.normalizedCsv(),
        )
    )

    override suspend fun deleteScene(sceneId: Long) {
        sceneDao.deleteById(sceneId)
    }

    override suspend fun linkScenesToSession(sessionId: Long, sceneIds: List<Long>) {
        sessionSceneDao.link(
            sceneIds.distinct().map { sceneId ->
                SessionSceneCrossRef(sessionId = sessionId, sceneId = sceneId)
            }
        )
    }

    override suspend fun unlinkSceneFromSession(sessionId: Long, sceneId: Long) {
        sessionSceneDao.unlink(sessionId = sessionId, sceneId = sceneId)
    }

    override suspend fun addSoundscapeToScene(sceneId: Long, categoryId: Long) {
        sceneSoundscapeDao.upsert(
            SceneSoundscapeCrossRef(
                sceneId = sceneId,
                categoryId = categoryId,
                displayOrder = sceneSoundscapeDao.getNextDisplayOrder(sceneId),
                mixVolume = 1f,
                intensityLevel = IntensityLevel.I.persistedValue,
            )
        )
    }

    override suspend fun updateSoundscapeInScene(
        sceneId: Long,
        categoryId: Long,
        displayOrder: Int,
        mixVolume: Float,
        intensityLevel: IntensityLevel,
    ) {
        sceneSoundscapeDao.upsert(
            SceneSoundscapeCrossRef(
                sceneId = sceneId,
                categoryId = categoryId,
                displayOrder = displayOrder,
                mixVolume = mixVolume.coerceIn(0f, 1f),
                intensityLevel = intensityLevel.persistedValue,
            )
        )
    }

    override suspend fun reorderSoundscapes(sceneId: Long, orderedCategoryIds: List<Long>) {
        sceneSoundscapeDao.updateAll(
            orderedCategoryIds.mapIndexed { index, categoryId ->
                SceneSoundscapeCrossRef(
                    sceneId = sceneId,
                    categoryId = categoryId,
                    displayOrder = index,
                    mixVolume = 1f,
                    intensityLevel = IntensityLevel.I.persistedValue,
                )
            }
        )
    }

    override suspend fun removeSoundscapeFromScene(sceneId: Long, categoryId: Long) {
        sceneSoundscapeDao.remove(sceneId = sceneId, categoryId = categoryId)
    }
}

private fun SceneEntity.toDomain(): Scene = Scene(
    id = id,
    name = name,
    description = description,
    tags = tags.toTagList(),
    soundscapeCount = 0,
)

private fun SceneSoundscapeSummaryEntity.toDomain(): SceneSoundscape = SceneSoundscape(
    sceneId = sceneId,
    category = SoundscapeCategory(
        id = categoryId,
        name = categoryName,
        iconResId = iconResId,
        themeLabel = themeLabel,
        levelOneTrackCount = levelOneTrackCount,
        levelTwoTrackCount = levelTwoTrackCount,
        levelThreeTrackCount = levelThreeTrackCount,
    ),
    displayOrder = displayOrder,
    mixVolume = mixVolume,
    intensityLevel = IntensityLevel.fromPersistedValue(intensityLevel),
)

private fun List<String>.normalizedCsv(): String = asSequence()
    .map(String::trim)
    .filter(String::isNotBlank)
    .joinToString(separator = ",")

private fun String.toTagList(): List<String> = split(',')
    .map(String::trim)
    .filter(String::isNotBlank)
