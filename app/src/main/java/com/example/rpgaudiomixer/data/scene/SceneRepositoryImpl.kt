package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import com.example.rpgaudiomixer.data.scene.local.SceneFxCrossRef
import com.example.rpgaudiomixer.data.scene.local.SceneFxDao
import com.example.rpgaudiomixer.data.scene.local.SceneFxRow
import com.example.rpgaudiomixer.data.scene.local.SceneSoundscapeCrossRef
import com.example.rpgaudiomixer.data.scene.local.SceneSoundscapeDao
import com.example.rpgaudiomixer.data.scene.local.SceneSoundscapeRow
import com.example.rpgaudiomixer.data.fx.local.FxTrackDao
import com.example.rpgaudiomixer.data.fx.local.FxTrackEntity
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryLibraryRow
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class SceneRepositoryImpl @Inject constructor(
    private val sceneDao: SceneDao,
    private val sceneFxDao: SceneFxDao,
    private val sceneSoundscapeDao: SceneSoundscapeDao,
    private val fxTrackDao: FxTrackDao,
    private val soundscapeCategoryDao: SoundscapeCategoryDao,
) : SceneRepository {
    override fun observeScenes(): Flow<List<Scene>> = sceneDao.observeAll()
        .map { scenes -> scenes.map(SceneEntity::toDomain) }

    override fun observeScene(sceneId: Long): Flow<Scene?> = sceneDao.observeById(sceneId)
        .map { scene -> scene?.toDomain() }

    override fun observeSceneSoundscapes(sceneId: Long): Flow<List<SceneSoundscape>> = combine(
        sceneDao.observeById(sceneId),
        sceneSoundscapeDao.observeByScene(sceneId),
        soundscapeCategoryDao.observeLibrary(),
    ) { scene, crossRefs, categories ->
        when {
            scene == null -> emptyList()
            crossRefs.isNotEmpty() -> crossRefs.map(SceneSoundscapeRow::toDomain)
            else -> scene.soundscapeCategoriesCsv.toCsvList().mapIndexedNotNull { index, categoryName ->
                categories.firstOrNull { row -> row.name == categoryName }?.toDefaultSceneSoundscape(sceneId, index)
            }
        }
    }

    override fun observeSceneFx(sceneId: Long): Flow<List<SceneFx>> = combine(
        sceneDao.observeById(sceneId),
        sceneFxDao.observeByScene(sceneId),
        fxTrackDao.observeAll(),
    ) { scene, crossRefs, tracks ->
        when {
            scene == null -> emptyList()
            crossRefs.isNotEmpty() -> crossRefs.map(SceneFxRow::toDomain)
            else -> scene.soundboardEffectsCsv.toCsvList().mapIndexedNotNull { index, effectName ->
                tracks.firstOrNull { track -> track.name == effectName }?.toDefaultSceneFx(sceneId, index)
            }
        }
    }

    override suspend fun upsertScene(scene: Scene): Long = sceneDao.upsert(scene.toEntity())

    override suspend fun deleteScene(sceneId: Long) {
        sceneFxDao.deleteByScene(sceneId)
        sceneSoundscapeDao.deleteByScene(sceneId)
        sceneDao.deleteById(sceneId)
    }

    override suspend fun addSoundscapeCategory(sceneId: Long, categoryName: String) {
        val scene = sceneDao.observeById(sceneId).first() ?: return
        val updatedCategories = (scene.soundscapeCategoriesCsv.toCsvList() + categoryName).distinct()
        sceneDao.upsert(scene.copy(soundscapeCategoriesCsv = updatedCategories.toCsvString()))

        val matchingCategory = soundscapeCategoryDao.observeLibrary().first().firstOrNull { row -> row.name == categoryName } ?: return
        val existing = sceneSoundscapeDao.get(sceneId, matchingCategory.id)
        if (existing == null) {
            sceneSoundscapeDao.upsert(
                SceneSoundscapeCrossRef(
                    sceneId = sceneId,
                    categoryId = matchingCategory.id,
                    displayOrder = sceneSoundscapeDao.maxDisplayOrder(sceneId)?.plus(1) ?: 0,
                    mixVolumePercent = 100,
                    intensityLevel = IntensityLevel.I.level,
                ),
            )
        }
    }

    override suspend fun removeSoundscapeCategory(sceneId: Long, categoryName: String) {
        val scene = sceneDao.observeById(sceneId).first() ?: return
        val updatedCategories = scene.soundscapeCategoriesCsv.toCsvList().filterNot { name -> name == categoryName }
        sceneDao.upsert(scene.copy(soundscapeCategoriesCsv = updatedCategories.toCsvString()))
        val matchingCategory = soundscapeCategoryDao.observeLibrary().first().firstOrNull { row -> row.name == categoryName } ?: return
        sceneSoundscapeDao.delete(sceneId, matchingCategory.id)
    }

    override suspend fun updateSoundscapeMix(sceneId: Long, categoryId: Long, mixVolumePercent: Int) {
        val existing = ensureSceneSoundscape(sceneId, categoryId) ?: return
        sceneSoundscapeDao.upsert(existing.copy(mixVolumePercent = mixVolumePercent.coerceIn(0, 100)))
    }

    override suspend fun updateSoundscapeIntensity(sceneId: Long, categoryId: Long, intensityLevel: IntensityLevel) {
        val existing = ensureSceneSoundscape(sceneId, categoryId) ?: return
        sceneSoundscapeDao.upsert(existing.copy(intensityLevel = intensityLevel.level))
    }

    override suspend fun reorderSoundscapes(sceneId: Long, orderedCategoryIds: List<Long>) {
        val existingById = observeSceneSoundscapes(sceneId).first().associateBy(SceneSoundscape::categoryId)
        val reordered = orderedCategoryIds.mapIndexedNotNull { index, categoryId ->
            val soundscape = existingById[categoryId] ?: return@mapIndexedNotNull null
            SceneSoundscapeCrossRef(
                sceneId = sceneId,
                categoryId = categoryId,
                displayOrder = index,
                mixVolumePercent = soundscape.mixVolumePercent,
                intensityLevel = soundscape.intensityLevel.level,
            )
        }
        if (reordered.isNotEmpty()) {
            sceneSoundscapeDao.upsertAll(reordered)
        }
    }

    override suspend fun addSoundboardEffect(sceneId: Long, effectName: String) {
        val scene = sceneDao.observeById(sceneId).first() ?: return
        val updatedEffects = (scene.soundboardEffectsCsv.toCsvList() + effectName).distinct()
        sceneDao.upsert(scene.copy(soundboardEffectsCsv = updatedEffects.toCsvString()))
        val matchingTrack = fxTrackDao.observeAll().first().firstOrNull { track -> track.name == effectName } ?: return
        addSoundboardEffect(sceneId, matchingTrack.id)
    }

    override suspend fun addSoundboardEffect(sceneId: Long, fxTrackId: Long) {
        val scene = sceneDao.observeById(sceneId).first() ?: return
        val track = fxTrackDao.getById(fxTrackId) ?: return
        val updatedEffects = (scene.soundboardEffectsCsv.toCsvList() + track.name).distinct()
        sceneDao.upsert(scene.copy(soundboardEffectsCsv = updatedEffects.toCsvString()))

        if (sceneFxDao.get(sceneId, fxTrackId) == null) {
            sceneFxDao.upsert(
                SceneFxCrossRef(
                    sceneId = sceneId,
                    fxTrackId = fxTrackId,
                    displayOrder = sceneFxDao.maxDisplayOrder(sceneId)?.plus(1) ?: 0,
                ),
            )
        }
    }

    override suspend fun removeSoundboardEffect(sceneId: Long, fxTrackId: Long) {
        val scene = sceneDao.observeById(sceneId).first() ?: return
        val track = fxTrackDao.getById(fxTrackId) ?: return
        val updatedEffects = scene.soundboardEffectsCsv.toCsvList().filterNot { name -> name == track.name }
        sceneDao.upsert(scene.copy(soundboardEffectsCsv = updatedEffects.toCsvString()))
        sceneFxDao.delete(sceneId, fxTrackId)
    }

    override suspend fun reorderSoundboardEffects(sceneId: Long, orderedTrackIds: List<Long>) {
        val existingById = observeSceneFx(sceneId).first().associateBy(SceneFx::fxTrackId)
        val reordered = orderedTrackIds.mapIndexedNotNull { index, trackId ->
            existingById[trackId]?.let { SceneFxCrossRef(sceneId = sceneId, fxTrackId = trackId, displayOrder = index) }
        }
        if (reordered.isNotEmpty()) {
            sceneFxDao.upsertAll(reordered)
        }
    }

    override suspend fun removeSoundboardEffect(effectName: String) {
        val scenes = sceneDao.observeAll().first()
        scenes.forEach { scene ->
            val existingEffects = scene.soundboardEffectsCsv.toCsvList()
            val updatedEffects = existingEffects.filterNot { name -> name == effectName }
            if (updatedEffects.size != existingEffects.size) {
                sceneDao.upsert(scene.copy(soundboardEffectsCsv = updatedEffects.toCsvString()))
            }
        }
        val tracks = fxTrackDao.observeAll().first().filter { track -> track.name == effectName }
        val scenesFx = sceneDao.observeAll().first()
        scenesFx.forEach { scene ->
            tracks.forEach { track ->
                sceneFxDao.delete(scene.id, track.id)
            }
        }
    }

    override suspend fun clearAll() {
        sceneFxDao.clearAll()
        sceneSoundscapeDao.clearAll()
        sceneDao.clearAll()
    }

    private suspend fun ensureSceneSoundscape(sceneId: Long, categoryId: Long): SceneSoundscapeCrossRef? {
        sceneSoundscapeDao.get(sceneId, categoryId)?.let { return it }
        val derived = observeSceneSoundscapes(sceneId).first().firstOrNull { soundscape -> soundscape.categoryId == categoryId } ?: return null
        return SceneSoundscapeCrossRef(
            sceneId = sceneId,
            categoryId = categoryId,
            displayOrder = derived.displayOrder,
            mixVolumePercent = derived.mixVolumePercent,
            intensityLevel = derived.intensityLevel.level,
        )
    }
}

private fun SceneEntity.toDomain(): Scene = Scene(
    id = id,
    name = name,
    description = description,
    tags = tagsCsv.toCsvList(),
    soundscapeCategoryNames = soundscapeCategoriesCsv.toCsvList(),
    soundboardEffectNames = soundboardEffectsCsv.toCsvList(),
)

private fun Scene.toEntity(): SceneEntity = SceneEntity(
    id = id,
    name = name,
    description = description,
    tagsCsv = tags.toCsvString(),
    soundscapeCategoriesCsv = soundscapeCategoryNames.toCsvString(),
    soundboardEffectsCsv = soundboardEffectNames.toCsvString(),
)

private fun SceneSoundscapeRow.toDomain(): SceneSoundscape = SceneSoundscape(
    sceneId = sceneId,
    categoryId = categoryId,
    categoryName = categoryName,
    displayOrder = displayOrder,
    mixVolumePercent = mixVolumePercent,
    intensityLevel = IntensityLevel.fromLevel(intensityLevel),
)

private fun SoundscapeCategoryLibraryRow.toDefaultSceneSoundscape(sceneId: Long, displayOrder: Int): SceneSoundscape = SceneSoundscape(
    sceneId = sceneId,
    categoryId = id,
    categoryName = name,
    displayOrder = displayOrder,
    mixVolumePercent = 100,
    intensityLevel = IntensityLevel.I,
)

private fun SceneFxRow.toDomain(): SceneFx = SceneFx(
    sceneId = sceneId,
    fxTrackId = fxTrackId,
    name = name,
    filePath = filePath,
    tags = tagsCsv.toCsvList(),
    durationMs = durationMs,
    playCount = playCount,
    displayOrder = displayOrder,
)

private fun FxTrackEntity.toDefaultSceneFx(sceneId: Long, displayOrder: Int): SceneFx = SceneFx(
    sceneId = sceneId,
    fxTrackId = id,
    name = name,
    filePath = filePath,
    tags = tagsCsv.toCsvList(),
    durationMs = durationMs,
    playCount = playCount,
    displayOrder = displayOrder,
)

private fun String.toCsvList(): List<String> = split(',')
    .map(String::trim)
    .filter(String::isNotBlank)

private fun List<String>.toCsvString(): String = joinToString(",")
