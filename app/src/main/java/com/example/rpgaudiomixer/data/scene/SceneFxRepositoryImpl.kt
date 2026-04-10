package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.fx.local.FxTrackDao
import com.example.rpgaudiomixer.data.fx.local.FxTrackEntity
import com.example.rpgaudiomixer.data.scene.local.SceneFxCrossRef
import com.example.rpgaudiomixer.data.scene.local.SceneFxDao
import com.example.rpgaudiomixer.data.scene.local.SceneFxRow
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.scene.SceneFxRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class SceneFxRepositoryImpl @Inject constructor(
    private val sceneFxDao: SceneFxDao,
    private val fxTrackDao: FxTrackDao,
) : SceneFxRepository {

    override fun observeSceneFx(sceneId: Long): Flow<List<SceneFx>> {
        return sceneFxDao.observeFxByScene(sceneId).map { rows ->
            rows.map { row -> row.toDomainModel() }
        }
    }

    override fun observeAvailableFx(sceneId: Long): Flow<List<FxTrack>> {
        return combine(
            fxTrackDao.observeAll(),
            sceneFxDao.observeLinkedFxTrackIds(sceneId),
        ) { tracks, linkedIds ->
            tracks
                .filterNot { track -> track.id in linkedIds }
                .map { track -> track.toDomainModel() }
        }
    }

    override suspend fun addFxToScene(sceneId: Long, fxTrackId: Long) {
        sceneFxDao.insert(
            SceneFxCrossRef(
                sceneId = sceneId,
                fxTrackId = fxTrackId,
                displayOrder = sceneFxDao.nextDisplayOrder(sceneId),
            ),
        )
    }

    override suspend fun removeFxFromScene(sceneId: Long, fxTrackId: Long) {
        sceneFxDao.delete(sceneId, fxTrackId)
    }

    override suspend fun reorderFx(sceneId: Long, orderedFxTrackIds: List<Long>) {
        orderedFxTrackIds.forEachIndexed { index, fxTrackId ->
            sceneFxDao.updateDisplayOrder(
                sceneId = sceneId,
                fxTrackId = fxTrackId,
                displayOrder = index,
            )
        }
    }

    override suspend fun incrementTrackPlayCount(trackId: Long) {
        fxTrackDao.incrementPlayCount(trackId)
    }
}

private fun SceneFxRow.toDomainModel(): SceneFx {
    return SceneFx(
        sceneId = sceneId,
        fxTrackId = fxTrackId,
        name = name,
        filePath = filePath,
        tags = tags.split(",").map { tag -> tag.trim() }.filter { tag -> tag.isNotEmpty() },
        durationMs = durationMs,
        playCount = playCount,
        isDemoContent = isDemoContent,
        displayOrder = displayOrder,
    )
}

private fun FxTrackEntity.toDomainModel(): FxTrack {
    return FxTrack(
        id = id,
        name = name,
        filePath = filePath,
        tags = tags.split(",").map { tag -> tag.trim() }.filter { tag -> tag.isNotEmpty() },
        durationMs = durationMs,
        playCount = playCount,
        isDemoContent = isDemoContent,
    )
}
