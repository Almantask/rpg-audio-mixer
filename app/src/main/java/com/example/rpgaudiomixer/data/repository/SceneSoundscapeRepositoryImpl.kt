package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.SceneSoundscapeCrossRef
import com.example.rpgaudiomixer.data.local.SceneSoundscapeDao
import com.example.rpgaudiomixer.data.local.SceneSoundscapeWithCategory
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.repository.SceneSoundscapeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of SceneSoundscapeRepository using Room.
 */
class SceneSoundscapeRepositoryImpl @Inject constructor(
    private val sceneSoundscapeDao: SceneSoundscapeDao
) : SceneSoundscapeRepository {

    override fun observeByScene(sceneId: Long): Flow<List<SceneSoundscape>> {
        return sceneSoundscapeDao.observeByScene(sceneId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addToScene(
        sceneId: Long,
        categoryId: Long,
        intensityLevel: IntensityLevel,
        mixVolumePercent: Int
    ) {
        // Get the current max display order for this scene
        val existing = sceneSoundscapeDao.observeByScene(sceneId)
        val maxOrder = 0 // For the first item, or we could query for max

        val crossRef = SceneSoundscapeCrossRef(
            sceneId = sceneId,
            categoryId = categoryId,
            intensityLevel = intensityLevel.value,
            mixVolumePercent = mixVolumePercent,
            displayOrder = maxOrder + 1
        )
        sceneSoundscapeDao.insert(crossRef)
    }

    override suspend fun updateIntensity(
        sceneId: Long,
        categoryId: Long,
        intensityLevel: IntensityLevel
    ) {
        sceneSoundscapeDao.updateIntensity(sceneId, categoryId, intensityLevel.value)
    }

    override suspend fun updateMixVolume(
        sceneId: Long,
        categoryId: Long,
        mixVolumePercent: Int
    ) {
        sceneSoundscapeDao.updateMixVolume(sceneId, categoryId, mixVolumePercent)
    }

    override suspend fun removeFromScene(sceneId: Long, categoryId: Long) {
        val crossRef = sceneSoundscapeDao.getBySceneAndCategory(sceneId, categoryId)
        if (crossRef != null) {
            sceneSoundscapeDao.delete(crossRef)
        }
    }

    override suspend fun updateDisplayOrders(sceneId: Long, soundscapes: List<SceneSoundscape>) {
        val crossRefs = soundscapes.mapIndexed { index, soundscape ->
            SceneSoundscapeCrossRef(
                sceneId = sceneId,
                categoryId = soundscape.category.id,
                intensityLevel = soundscape.intensityLevel.value,
                mixVolumePercent = soundscape.mixVolumePercent,
                displayOrder = index
            )
        }
        sceneSoundscapeDao.updateDisplayOrders(crossRefs)
    }

    private fun SceneSoundscapeWithCategory.toDomain() = SceneSoundscape(
        sceneId = crossRef.sceneId,
        category = SoundscapeCategory(
            id = category.id,
            name = category.name,
            iconResId = category.iconResId,
            themeLabel = category.themeLabel
        ),
        intensityLevel = IntensityLevel.fromValue(crossRef.intensityLevel),
        mixVolumePercent = crossRef.mixVolumePercent,
        displayOrder = crossRef.displayOrder
    )
}
