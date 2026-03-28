package com.example.rpgaudiomixer.infra.storage.repository

import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeLayer
import com.example.rpgaudiomixer.domain.storage.SoundscapeRepository
import com.example.rpgaudiomixer.infra.storage.db.dao.SoundscapeDao
import com.example.rpgaudiomixer.infra.storage.db.entity.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.infra.storage.db.entity.SoundscapeLayerEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomSoundscapeRepository @Inject constructor(
    private val dao: SoundscapeDao,
) : SoundscapeRepository {

    override fun getAllCategories(): Flow<List<SoundscapeCategory>> =
        dao.getAllCategories().map { it.map(SoundscapeCategoryEntity::toDomain) }

    override fun getCategoryById(id: Long): Flow<SoundscapeCategory?> =
        dao.getCategoryById(id).map { it?.toDomain() }

    override fun getLayersForCategory(categoryId: Long): Flow<List<SoundscapeLayer>> =
        dao.getLayersForCategory(categoryId).map { it.map(SoundscapeLayerEntity::toDomain) }

    override suspend fun insertCategory(category: SoundscapeCategory): Long =
        dao.insertCategory(category.toEntity())

    override suspend fun updateCategory(category: SoundscapeCategory) =
        dao.updateCategory(category.toEntity())

    override suspend fun deleteCategory(category: SoundscapeCategory) =
        dao.deleteCategory(category.toEntity())

    override suspend fun insertLayer(layer: SoundscapeLayer): Long =
        dao.insertLayer(layer.toEntity())

    override suspend fun updateLayer(layer: SoundscapeLayer) =
        dao.updateLayer(layer.toEntity())

    override suspend fun deleteLayer(layer: SoundscapeLayer) =
        dao.deleteLayer(layer.toEntity())
}

private fun SoundscapeCategoryEntity.toDomain() = SoundscapeCategory(
    id = id, name = name, parentCategory = parentCategory, createdAt = createdAt,
)

private fun SoundscapeCategory.toEntity() = SoundscapeCategoryEntity(
    id = id, name = name, parentCategory = parentCategory, createdAt = createdAt,
)

private fun SoundscapeLayerEntity.toDomain() = SoundscapeLayer(
    id = id, categoryId = categoryId, name = name,
    trackFilePath = trackFilePath, intensity = intensity, mix = mix, durationMs = durationMs,
)

private fun SoundscapeLayer.toEntity() = SoundscapeLayerEntity(
    id = id, categoryId = categoryId, name = name,
    trackFilePath = trackFilePath, intensity = intensity, mix = mix, durationMs = durationMs,
)
