package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import com.example.rpgaudiomixer.data.scene.local.asDomain
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SceneRepositoryImpl @Inject constructor(
    private val sceneDao: SceneDao,
) : SceneRepository {

    override fun observeAll(): Flow<List<Scene>> {
        return sceneDao.observeAll().map { scenes ->
            scenes.map { it.asDomain() }
        }
    }

    override suspend fun createScene(
        name: String,
        description: String?,
        tags: List<String>,
    ): Long {
        return sceneDao.upsert(
            SceneEntity(
                name = name,
                description = description?.trim()?.takeIf { it.isNotEmpty() },
                tags = tags
                    .map { tag -> tag.trim() }
                    .filter { tag -> tag.isNotEmpty() }
                    .distinct()
                    .joinToString(","),
            ),
        )
    }

    override suspend fun deleteScene(sceneId: Long) {
        sceneDao.delete(sceneId)
    }

    override suspend fun getScene(sceneId: Long): Scene? {
        return sceneDao.getById(sceneId)?.asDomain()
    }
}
