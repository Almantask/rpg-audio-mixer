package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.scene.local.SceneSoundscapeCrossRef
import com.example.rpgaudiomixer.data.scene.local.SceneSoundscapeDao
import com.example.rpgaudiomixer.data.scene.local.SceneSoundscapeRow
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategorySummaryRow
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackEntity
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.scene.SceneSoundscapeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class SceneSoundscapeRepositoryImpl @Inject constructor(
    private val sceneSoundscapeDao: SceneSoundscapeDao,
    private val soundscapeCategoryDao: SoundscapeCategoryDao,
    private val soundscapeTrackDao: SoundscapeTrackDao,
) : SceneSoundscapeRepository {

    override fun observeSceneSoundscapes(sceneId: Long): Flow<List<SceneSoundscape>> {
        return sceneSoundscapeDao.observeSoundscapesByScene(sceneId).map { rows ->
            rows.map { row -> row.toDomainModel() }
        }
    }

    override fun observeAvailableSoundscapes(sceneId: Long): Flow<List<SoundscapeCategory>> {
        return combine(
            soundscapeCategoryDao.observeCategorySummaries(),
            sceneSoundscapeDao.observeLinkedCategoryIds(sceneId),
        ) { categories, linkedIds ->
            categories
                .filterNot { category -> category.id in linkedIds }
                .filter { category ->
                    category.levelOneCount > 0 || category.levelTwoCount > 0 || category.levelThreeCount > 0
                }
                .map { category -> category.toDomainModel() }
        }
    }

    override fun observeTracks(categoryId: Long): Flow<List<SoundscapeTrack>> {
        return soundscapeTrackDao.observeByCategory(categoryId).map { entities ->
            entities.map { entity -> entity.toDomainModel() }
        }
    }

    override suspend fun addSoundscapeToScene(sceneId: Long, categoryId: Long) {
        sceneSoundscapeDao.insert(
            SceneSoundscapeCrossRef(
                sceneId = sceneId,
                categoryId = categoryId,
                displayOrder = sceneSoundscapeDao.nextDisplayOrder(sceneId),
                mixVolume = 1f,
                intensityLevel = IntensityLevel.I.dbValue,
            ),
        )
    }

    override suspend fun removeSoundscapeFromScene(sceneId: Long, categoryId: Long) {
        sceneSoundscapeDao.delete(sceneId, categoryId)
    }

    override suspend fun updateMixVolume(sceneId: Long, categoryId: Long, mixVolume: Float) {
        sceneSoundscapeDao.updateMixVolume(
            sceneId = sceneId,
            categoryId = categoryId,
            mixVolume = mixVolume.coerceIn(0f, 1f),
        )
    }

    override suspend fun updateIntensityLevel(sceneId: Long, categoryId: Long, intensityLevel: IntensityLevel) {
        sceneSoundscapeDao.updateIntensityLevel(
            sceneId = sceneId,
            categoryId = categoryId,
            intensityLevel = intensityLevel.dbValue,
        )
    }

    override suspend fun reorderSoundscapes(sceneId: Long, orderedCategoryIds: List<Long>) {
        orderedCategoryIds.forEachIndexed { index, categoryId ->
            sceneSoundscapeDao.updateDisplayOrder(
                sceneId = sceneId,
                categoryId = categoryId,
                displayOrder = index,
            )
        }
    }

    override suspend fun incrementTrackPlayCount(trackId: Long) {
        soundscapeTrackDao.incrementPlayCount(trackId)
    }
}

private fun SceneSoundscapeRow.toDomainModel(): SceneSoundscape {
    return SceneSoundscape(
        sceneId = sceneId,
        categoryId = categoryId,
        categoryName = categoryName,
        themeLabel = themeLabel,
        iconResId = iconResId,
        isDemoContent = isDemoContent,
        mixVolume = mixVolume,
        intensityLevel = IntensityLevel.fromDbValue(intensityLevel),
        displayOrder = displayOrder,
        levelOneCount = levelOneCount,
        levelTwoCount = levelTwoCount,
        levelThreeCount = levelThreeCount,
    )
}

private fun SoundscapeCategorySummaryRow.toDomainModel(): SoundscapeCategory {
    return SoundscapeCategory(
        id = id,
        name = name,
        themeLabel = themeLabel,
        iconResId = iconResId,
        isDemoContent = isDemoContent,
        levelOneCount = levelOneCount,
        levelTwoCount = levelTwoCount,
        levelThreeCount = levelThreeCount,
        totalPlayCount = totalPlayCount,
    )
}

private fun SoundscapeTrackEntity.toDomainModel(): SoundscapeTrack {
    return SoundscapeTrack(
        id = id,
        categoryId = categoryId,
        name = name,
        filePath = filePath,
        intensityLevel = IntensityLevel.fromDbValue(intensityLevel),
        mixVolumePercent = mixVolumePercent,
        displayOrder = displayOrder,
    )
}
