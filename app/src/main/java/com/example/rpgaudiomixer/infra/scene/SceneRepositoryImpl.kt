package com.example.rpgaudiomixer.infra.scene

import com.example.rpgaudiomixer.domain.library.IntensityLevel
import com.example.rpgaudiomixer.domain.library.SoundscapeCategory
import com.example.rpgaudiomixer.domain.library.SoundscapeRepository
import com.example.rpgaudiomixer.domain.scene.Scene
import com.example.rpgaudiomixer.domain.scene.SceneActiveSoundscape
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import com.example.rpgaudiomixer.domain.library.FxRepository
import com.example.rpgaudiomixer.domain.scene.SceneActiveFx
import javax.inject.Inject

class SceneRepositoryImpl @Inject constructor(
    private val sceneDao: SceneDao,
    private val soundscapeRepository: SoundscapeRepository,
    private val fxRepository: FxRepository
) : SceneRepository {

    override fun observeAll(): Flow<List<Scene>> {
        return sceneDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun upsert(scene: Scene) {
        withContext(Dispatchers.IO) {
            sceneDao.upsert(scene.toEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(Dispatchers.IO) {
            sceneDao.delete(id)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeSceneActiveSoundscapes(sceneId: Long): Flow<List<SceneActiveSoundscape>> {
        return sceneDao.observeCrossRefsForScene(sceneId).flatMapLatest { refs ->
            if (refs.isEmpty()) {
                flowOf(emptyList<SceneActiveSoundscape>())
            } else {
                observeCategoriesForRefs(refs)
            }
        }
    }

    private fun observeCategoriesForRefs(refs: List<SceneSoundscapeCrossRef>): Flow<List<SceneActiveSoundscape>> {
        val categoryFlows: List<Flow<SceneActiveSoundscape?>> = refs.map { ref ->
            soundscapeRepository.observeCategory(ref.categoryId).map { category: SoundscapeCategory? ->
                if (category != null) {
                    SceneActiveSoundscape(
                        category = category,
                        displayOrder = ref.displayOrder,
                        mixVolume = ref.mixVolume,
                        intensityLevel = IntensityLevel.fromInt(ref.intensityLevel)
                    )
                } else null
            }
        }
        return combine(categoryFlows) { flows: Array<SceneActiveSoundscape?> ->
            flows.filterNotNull().sortedBy { it.displayOrder }
        }
    }

    override suspend fun addCategoryToScene(sceneId: Long, categoryId: Long, displayOrder: Int) {
        withContext(Dispatchers.IO) {
            sceneDao.upsertCrossRef(
                SceneSoundscapeCrossRef(
                    sceneId = sceneId,
                    categoryId = categoryId,
                    displayOrder = displayOrder
                )
            )
        }
    }

    override suspend fun removeCategoryFromScene(sceneId: Long, categoryId: Long) {
        withContext(Dispatchers.IO) {
            sceneDao.deleteCrossRef(sceneId, categoryId)
        }
    }

    override suspend fun updateSoundscapeMetadata(
        sceneId: Long,
        categoryId: Long,
        mixVolume: Float,
        intensityLevel: IntensityLevel
    ) {
        withContext(Dispatchers.IO) {
            sceneDao.updateMetadata(sceneId, categoryId, mixVolume, intensityLevel.value)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeSceneActiveFx(sceneId: Long): Flow<List<SceneActiveFx>> {
        return sceneDao.observeFxCrossRefsForScene(sceneId).flatMapLatest { refs ->
            if (refs.isEmpty()) {
                flowOf(emptyList<SceneActiveFx>())
            } else {
                observeFxForRefs(refs)
            }
        }
    }

    private fun observeFxForRefs(refs: List<SceneFxCrossRef>): Flow<List<SceneActiveFx>> {
        val fxFlows = refs.map { ref ->
            fxRepository.observeFxTrack(ref.fxTrackId).map { fx ->
                fx?.let { SceneActiveFx(it, ref.displayOrder) }
            }
        }
        return combine(fxFlows) { flows ->
            flows.filterNotNull().sortedBy { it.displayOrder }
        }
    }

    override suspend fun addFxToScene(sceneId: Long, fxTrackId: Long, displayOrder: Int) {
        withContext(Dispatchers.IO) {
            sceneDao.upsertFxCrossRef(SceneFxCrossRef(sceneId, fxTrackId, displayOrder))
        }
    }

    override suspend fun removeFxFromScene(sceneId: Long, fxTrackId: Long) {
        withContext(Dispatchers.IO) {
            sceneDao.deleteFxCrossRef(sceneId, fxTrackId)
        }
    }

    override suspend fun reorderSoundscapes(sceneId: Long, categoryIds: List<Long>) {
        withContext(Dispatchers.IO) {
            categoryIds.forEachIndexed { index, categoryId ->
                sceneDao.updateSoundscapeOrder(sceneId, categoryId, index)
            }
        }
    }

    override suspend fun reorderFx(sceneId: Long, fxIds: List<Long>) {
        withContext(Dispatchers.IO) {
            fxIds.forEachIndexed { index, fxId ->
                sceneDao.updateFxOrder(sceneId, fxId, index)
            }
        }
    }
}

private fun SceneEntity.toDomain(): Scene = Scene(
    id = id,
    name = name,
    description = description,
    tags = if (tags.isEmpty()) emptyList() else tags.split(",")
)

private fun Scene.toEntity(): SceneEntity = SceneEntity(
    id = id,
    name = name,
    description = description,
    tags = tags.joinToString(",")
)
