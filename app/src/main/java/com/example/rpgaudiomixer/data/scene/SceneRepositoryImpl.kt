package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class SceneRepositoryImpl @Inject constructor(
    private val sceneDao: SceneDao,
) : SceneRepository {
    override fun observeScenes(): Flow<List<Scene>> = sceneDao.observeAll()
        .map { scenes -> scenes.map(SceneEntity::toDomain) }

    override fun observeScene(sceneId: Long): Flow<Scene?> = sceneDao.observeById(sceneId)
        .map { scene -> scene?.toDomain() }

    override suspend fun upsertScene(scene: Scene): Long = sceneDao.upsert(scene.toEntity())

    override suspend fun deleteScene(sceneId: Long) {
        sceneDao.deleteById(sceneId)
    }

    override suspend fun addSoundscapeCategory(sceneId: Long, categoryName: String) {
        val scene = sceneDao.observeById(sceneId).first() ?: return
        val updatedCategories = (scene.soundscapeCategoriesCsv.toCsvList() + categoryName)
            .distinct()
            .sorted()
        sceneDao.upsert(scene.copy(soundscapeCategoriesCsv = updatedCategories.toCsvString()))
    }

    override suspend fun addSoundboardEffect(sceneId: Long, effectName: String) {
        val scene = sceneDao.observeById(sceneId).first() ?: return
        val updatedEffects = (scene.soundboardEffectsCsv.toCsvList() + effectName)
            .distinct()
            .sorted()
        sceneDao.upsert(scene.copy(soundboardEffectsCsv = updatedEffects.toCsvString()))
    }

    override suspend fun removeSoundboardEffect(effectName: String) {
        val scenes = sceneDao.observeAll().first()
        scenes.forEach { scene ->
            val existingEffects = scene.soundboardEffectsCsv.toCsvList()
            val updatedEffects = existingEffects
                .filterNot { name -> name == effectName }
            if (updatedEffects.size != existingEffects.size) {
                sceneDao.upsert(scene.copy(soundboardEffectsCsv = updatedEffects.toCsvString()))
            }
        }
    }

    override suspend fun clearAll() {
        sceneDao.clearAll()
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

private fun String.toCsvList(): List<String> = split(',')
    .map(String::trim)
    .filter(String::isNotBlank)

private fun List<String>.toCsvString(): String = joinToString(",")
