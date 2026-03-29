package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.SceneDao
import com.example.rpgaudiomixer.data.local.SceneEntity
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SceneRepositoryImpl @Inject constructor(
    private val dao: SceneDao
) : SceneRepository {
    override fun observeAll(): Flow<List<Scene>> =
        dao.observeAll().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun upsert(scene: Scene): Long =
        dao.upsert(scene.toEntity())

    override suspend fun delete(scene: Scene) =
        dao.delete(scene.toEntity())
}

private fun SceneEntity.toDomain() = Scene(
    id = id,
    name = name,
    description = description,
    tags = tags.split(',').map { it.trim() }.filter { it.isNotEmpty() }
)

private fun Scene.toEntity() = SceneEntity(
    id = id,
    name = name,
    description = description,
    tags = tags.joinToString(",")
)
