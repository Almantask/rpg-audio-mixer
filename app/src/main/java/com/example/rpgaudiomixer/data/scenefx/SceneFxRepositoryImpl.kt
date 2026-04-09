package com.example.rpgaudiomixer.data.scenefx

import com.example.rpgaudiomixer.data.local.SceneFxCrossRef
import com.example.rpgaudiomixer.data.local.SceneFxDao
import com.example.rpgaudiomixer.domain.repository.SceneFxRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SceneFxRepositoryImpl @Inject constructor(
    private val sceneFxDao: SceneFxDao
) : SceneFxRepository {

    override fun observeByScene(sceneId: Long): Flow<List<SceneFxCrossRef>> {
        return sceneFxDao.observeByScene(sceneId)
    }

    override suspend fun addFxToScene(sceneId: Long, fxTrackId: Long, displayOrder: Int) {
        sceneFxDao.upsert(
            SceneFxCrossRef(
                sceneId = sceneId,
                fxTrackId = fxTrackId,
                displayOrder = displayOrder
            )
        )
    }

    override suspend fun removeFxFromScene(sceneId: Long, fxTrackId: Long) {
        sceneFxDao.delete(sceneId, fxTrackId)
    }

    override suspend fun updateDisplayOrders(sceneId: Long, fxTrackIds: List<Long>) {
        sceneFxDao.updateDisplayOrders(sceneId, fxTrackIds)
    }
}
