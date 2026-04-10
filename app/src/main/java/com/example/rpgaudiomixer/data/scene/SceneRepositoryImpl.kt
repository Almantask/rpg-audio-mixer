package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SceneRepositoryImpl @Inject constructor(
    private val sceneDao: SceneDao,
) : SceneRepository {

    override fun observeScenes(): Flow<List<Scene>> {
        return sceneDao.observeAll().map { entities -> entities.map { it.toDomainModel() } }
    }

    override fun observeScene(sceneId: Long): Flow<Scene?> {
        return sceneDao.observeScene(sceneId).map { entity -> entity?.toDomainModel() }
    }

    override suspend fun createScene(name: String, description: String?, tags: List<String>): Long {
        return sceneDao.upsert(
            SceneEntity(
                name = name.trim(),
                description = description?.trim().takeUnless { it.isNullOrBlank() },
                tags = tags
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .joinToString(","),
                masterVolume = 1f,
                deletedAt = null,
            ),
        )
    }

    override suspend fun updateMasterVolume(sceneId: Long, masterVolume: Float) {
        sceneDao.updateMasterVolume(sceneId = sceneId, masterVolume = masterVolume.coerceIn(0f, 1f))
    }

    override suspend fun deleteScene(sceneId: Long, deletedAtMillis: Long) {
        sceneDao.softDeleteById(sceneId, deletedAtMillis)
    }
}

private fun SceneEntity.toDomainModel(): Scene {
    return Scene(
        id = id,
        name = name,
        description = description,
        tags = tags
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() },
        masterVolume = masterVolume,
    )
}
