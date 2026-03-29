package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.local.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.repository.SoundscapeCategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SoundscapeCategoryRepositoryImpl @Inject constructor(
    private val dao: SoundscapeCategoryDao
) : SoundscapeCategoryRepository {
    override fun observeAll(): Flow<List<SoundscapeCategory>> =
        dao.observeAll().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun upsert(category: SoundscapeCategory): Long =
        dao.upsert(category.toEntity())

    override suspend fun delete(category: SoundscapeCategory) =
        dao.delete(category.toEntity())
}

private fun SoundscapeCategoryEntity.toDomain() = SoundscapeCategory(
    id = id,
    name = name,
    intensityLevels = emptyList() // For now, not loading levels
)

private fun SoundscapeCategory.toEntity() = SoundscapeCategoryEntity(
    id = id,
    name = name
)
