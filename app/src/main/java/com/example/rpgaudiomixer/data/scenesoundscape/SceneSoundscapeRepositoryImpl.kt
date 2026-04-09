package com.example.rpgaudiomixer.data.scenesoundscape

import com.example.rpgaudiomixer.data.local.SceneSoundscapeCrossRef
import com.example.rpgaudiomixer.data.local.SceneSoundscapeDao
import com.example.rpgaudiomixer.domain.repository.SceneSoundscapeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SceneSoundscapeRepositoryImpl @Inject constructor(
    private val sceneSoundscapeDao: SceneSoundscapeDao
) : SceneSoundscapeRepository {

    override fun observeByScene(sceneId: Long): Flow<List<SceneSoundscapeCrossRef>> =
        sceneSoundscapeDao.observeByScene(sceneId)

    override suspend fun addCategoryToScene(sceneId: Long, categoryId: Long, displayOrder: Int) {
        val crossRef = SceneSoundscapeCrossRef(
            sceneId = sceneId,
            categoryId = categoryId,
            displayOrder = displayOrder,
            mixVolume = 0.5f,
            intensityLevel = 1
        )
        sceneSoundscapeDao.upsert(crossRef)
    }

    override suspend fun removeCategoryFromScene(sceneId: Long, categoryId: Long) {
        sceneSoundscapeDao.delete(sceneId, categoryId)
    }

    override suspend fun updateMixVolume(sceneId: Long, categoryId: Long, mixVolume: Float) {
        sceneSoundscapeDao.updateMixVolume(sceneId, categoryId, mixVolume)
    }

    override suspend fun updateIntensityLevel(sceneId: Long, categoryId: Long, intensityLevel: Int) {
        sceneSoundscapeDao.updateIntensityLevel(sceneId, categoryId, intensityLevel)
    }

    override suspend fun updateDisplayOrders(sceneId: Long, categoryIds: List<Long>) {
        sceneSoundscapeDao.updateDisplayOrders(sceneId, categoryIds)
    }
}
