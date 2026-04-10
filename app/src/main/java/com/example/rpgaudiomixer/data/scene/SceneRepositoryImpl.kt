package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.local.SceneDao
import com.example.rpgaudiomixer.data.local.SceneFxCrossRef
import com.example.rpgaudiomixer.data.local.SceneFxDao
import com.example.rpgaudiomixer.data.local.SceneFxSummaryEntity
import com.example.rpgaudiomixer.data.local.SceneEntity
import com.example.rpgaudiomixer.data.local.SceneSoundscapeCrossRef
import com.example.rpgaudiomixer.data.local.SceneSoundscapeDao
import com.example.rpgaudiomixer.data.local.SceneSoundscapeSummaryEntity
import com.example.rpgaudiomixer.data.local.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.local.SessionSceneDao
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFx
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
    private val sceneFxDao: SceneFxDao,
) : SceneRepository {
    private var currentTimeProvider: () -> Long = System::currentTimeMillis

    internal constructor(
        sceneDao: SceneDao,
        sessionSceneDao: SessionSceneDao,
        sceneSoundscapeDao: SceneSoundscapeDao,
        sceneFxDao: SceneFxDao,
        currentTimeProvider: () -> Long,
    ) : this(
        sceneDao = sceneDao,
        sessionSceneDao = sessionSceneDao,
        sceneSoundscapeDao = sceneSoundscapeDao,
        sceneFxDao = sceneFxDao,
    ) {
        this.currentTimeProvider = currentTimeProvider
    }

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

    override fun observeFxForScene(sceneId: Long): Flow<List<SceneFx>> =
        sceneFxDao.observeFxByScene(sceneId).map { fx ->
            fx.map(SceneFxSummaryEntity::toDomain)
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

    override suspend fun cloneScene(sceneId: Long, name: String): Long {
        val sourceScene = requireNotNull(sceneDao.getById(sceneId)) { "Scene $sceneId was not found." }
        val clonedSceneId = sceneDao.upsert(
            sourceScene.copy(
                id = 0L,
                name = name,
            )
        )

        sceneSoundscapeDao.updateAll(
            sceneSoundscapeDao.getCrossRefs(sceneId).map { crossRef ->
                crossRef.copy(sceneId = clonedSceneId)
            }
        )
        sceneFxDao.updateAll(
            sceneFxDao.getCrossRefs(sceneId).map { crossRef ->
                crossRef.copy(sceneId = clonedSceneId)
            }
        )

        return clonedSceneId
    }

    override suspend fun updateScene(
        sceneId: Long,
        name: String,
        description: String?,
        tags: List<String>,
    ) {
        sceneDao.upsert(
            SceneEntity(
                id = sceneId,
                name = name,
                description = description,
                tags = tags.normalizedCsv(),
            )
        )
    }

    override suspend fun deleteScene(sceneId: Long) {
        sceneDao.softDeleteById(sceneId = sceneId, deletedAt = currentTimeProvider())
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
        val existingByCategoryId = sceneSoundscapeDao.getCrossRefs(sceneId).associateBy(SceneSoundscapeCrossRef::categoryId)
        sceneSoundscapeDao.updateAll(
            orderedCategoryIds.mapIndexed { index, categoryId ->
                val existing = existingByCategoryId[categoryId]
                SceneSoundscapeCrossRef(
                    sceneId = sceneId,
                    categoryId = categoryId,
                    displayOrder = index,
                    mixVolume = existing?.mixVolume ?: 1f,
                    intensityLevel = existing?.intensityLevel ?: IntensityLevel.I.persistedValue,
                )
            }
        )
    }

    override suspend fun removeSoundscapeFromScene(sceneId: Long, categoryId: Long) {
        sceneSoundscapeDao.remove(sceneId = sceneId, categoryId = categoryId)
    }

    override suspend fun addFxToScene(sceneId: Long, fxTrackId: Long) {
        sceneFxDao.upsert(
            SceneFxCrossRef(
                sceneId = sceneId,
                fxTrackId = fxTrackId,
                displayOrder = sceneFxDao.getNextDisplayOrder(sceneId),
            )
        )
    }

    override suspend fun reorderFx(sceneId: Long, orderedFxTrackIds: List<Long>) {
        sceneFxDao.updateAll(
            orderedFxTrackIds.mapIndexed { index, fxTrackId ->
                SceneFxCrossRef(
                    sceneId = sceneId,
                    fxTrackId = fxTrackId,
                    displayOrder = index,
                )
            }
        )
    }

    override suspend fun removeFxFromScene(sceneId: Long, fxTrackId: Long) {
        sceneFxDao.remove(sceneId = sceneId, fxTrackId = fxTrackId)
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

private fun SceneFxSummaryEntity.toDomain(): SceneFx = SceneFx(
    sceneId = sceneId,
    track = FxTrack(
        id = fxTrackId,
        name = name,
        filePath = filePath,
        tags = tags.toTagList(),
        durationMs = durationMs,
        playCount = playCount,
        isDemo = isDemo,
    ),
    displayOrder = displayOrder,
)

private fun List<String>.normalizedCsv(): String = asSequence()
    .map(String::trim)
    .filter(String::isNotBlank)
    .joinToString(separator = ",")

private fun String.toTagList(): List<String> = split(',')
    .map(String::trim)
    .filter(String::isNotBlank)
