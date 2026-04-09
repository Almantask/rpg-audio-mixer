package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.local.SceneDao
import com.example.rpgaudiomixer.data.local.SceneEntity
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SceneRepositoryImpl @Inject constructor(
    private val sceneDao: SceneDao
) : SceneRepository {

    override fun observeAll(): Flow<List<Scene>> {
        return sceneDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long): Scene? {
        return sceneDao.getById(id)?.toDomain()
    }

    override suspend fun create(name: String, description: String?, tags: List<String>): Long {
        val entity = SceneEntity(
            name = name,
            description = description,
            tags = tags.joinToString(",")
        )
        return sceneDao.upsert(entity)
    }

    override suspend fun update(scene: Scene) {
        sceneDao.upsert(scene.toEntity())
    }

    override suspend fun delete(scene: Scene) {
        sceneDao.delete(scene.toEntity())
    }

    private fun SceneEntity.toDomain() = Scene(
        id = id,
        name = name,
        description = description,
        tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() }
    )

    private fun Scene.toEntity() = SceneEntity(
        id = id,
        name = name,
        description = description,
        tags = tags.joinToString(",")
    )
}
