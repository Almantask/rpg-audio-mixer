package com.example.rpgaudiomixer.app.data.soundscape

import com.example.rpgaudiomixer.app.data.local.dao.SoundscapeCategoryDao
import com.example.rpgaudiomixer.app.data.local.entities.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.app.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.app.domain.repository.SoundscapeCategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundscapeCategoryRepositoryImpl @Inject constructor(
    private val soundscapeCategoryDao: SoundscapeCategoryDao
) : SoundscapeCategoryRepository {

    override fun observeAll(): Flow<List<SoundscapeCategory>> {
        return soundscapeCategoryDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeDeleted(): Flow<List<SoundscapeCategory>> {
        return soundscapeCategoryDao.observeDeleted().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createCategory(name: String, sortOrder: Int): Long {
        val entity = SoundscapeCategoryEntity(
            name = name,
            sortOrder = sortOrder
        )
        return soundscapeCategoryDao.upsert(entity)
    }

    override suspend fun updateCategory(category: SoundscapeCategory) {
        soundscapeCategoryDao.upsert(category.toEntity())
    }

    override suspend fun deleteCategory(id: Long) {
        soundscapeCategoryDao.softDelete(id)
    }

    override suspend fun restoreCategory(id: Long) {
        soundscapeCategoryDao.restore(id)
    }

    override suspend fun permanentlyDeleteCategory(id: Long) {
        soundscapeCategoryDao.permanentlyDelete(id)
    }

    private fun SoundscapeCategoryEntity.toDomain() = SoundscapeCategory(
        id = id,
        name = name,
        sortOrder = sortOrder,
        isDeleted = isDeleted,
        deletedAt = deletedAt
    )

    private fun SoundscapeCategory.toEntity() = SoundscapeCategoryEntity(
        id = id,
        name = name,
        sortOrder = sortOrder,
        isDeleted = isDeleted,
        deletedAt = deletedAt
    )
}
