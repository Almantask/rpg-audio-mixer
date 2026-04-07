package com.example.rpgaudiomixer.data.scenesoundscape

import com.example.rpgaudiomixer.data.local.SceneSoundscapeDao
import com.example.rpgaudiomixer.data.local.SceneSoundscapeCrossRef
import com.example.rpgaudiomixer.data.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.repository.SceneSoundscapeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SceneSoundscapeRepositoryImpl @Inject constructor(
    private val sceneSoundscapeDao: SceneSoundscapeDao,
    private val categoryDao: SoundscapeCategoryDao
) : SceneSoundscapeRepository {

    override fun observeByScene(sceneId: Long): Flow<List<SceneSoundscape>> {
        return sceneSoundscapeDao.observeByScene(sceneId).map { crossRefs ->
            crossRefs.mapNotNull { crossRef ->
                val category = categoryDao.getById(crossRef.categoryId)
                category?.let {
                    SceneSoundscape(
                        sceneId = crossRef.sceneId,
                        categoryId = crossRef.categoryId,
                        categoryName = it.name,
                        displayOrder = crossRef.displayOrder,
                        mixVolume = crossRef.mixVolume,
                        intensityLevel = IntensityLevel.fromValue(crossRef.intensityLevel)
                    )
                }
            }
        }
    }

    override suspend fun add(
        sceneId: Long,
        categoryId: Long,
        displayOrder: Int,
        mixVolume: Float,
        intensityLevel: IntensityLevel
    ) {
        val crossRef = SceneSoundscapeCrossRef(
            sceneId = sceneId,
            categoryId = categoryId,
            displayOrder = displayOrder,
            mixVolume = mixVolume,
            intensityLevel = intensityLevel.value
        )
        sceneSoundscapeDao.insert(crossRef)
    }

    override suspend fun remove(sceneId: Long, categoryId: Long) {
        sceneSoundscapeDao.deleteByIds(sceneId, categoryId)
    }

    override suspend fun updateMixVolume(sceneId: Long, categoryId: Long, mixVolume: Float) {
        sceneSoundscapeDao.updateMixVolume(sceneId, categoryId, mixVolume)
    }

    override suspend fun updateIntensityLevel(
        sceneId: Long,
        categoryId: Long,
        intensityLevel: IntensityLevel
    ) {
        sceneSoundscapeDao.updateIntensityLevel(sceneId, categoryId, intensityLevel.value)
    }

    override suspend fun updateDisplayOrders(sceneId: Long, categoryIds: List<Long>) {
        sceneSoundscapeDao.updateDisplayOrders(sceneId, categoryIds)
    }
}
