package com.example.rpgaudiomixer.infra.library

import com.example.rpgaudiomixer.domain.library.IntensityLevel
import com.example.rpgaudiomixer.domain.library.SoundscapeCategory
import com.example.rpgaudiomixer.domain.library.SoundscapeRepository
import com.example.rpgaudiomixer.domain.library.SoundscapeTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SoundscapeRepositoryImpl @Inject constructor(
    private val categoryDao: SoundscapeCategoryDao,
    private val trackDao: SoundscapeTrackDao
) : SoundscapeRepository {

    override fun observeCategories(): Flow<List<SoundscapeCategory>> {
        return categoryDao.observeAll().flatMapLatest { categories ->
            if (categories.isEmpty()) return@flatMapLatest kotlinx.coroutines.flow.flowOf(emptyList())
            
            val categoryFlows = categories.map { category ->
                trackDao.observeByCategoryId(category.id).map { tracks ->
                    category.toDomain(tracks.map { it.toDomain() })
                }
            }
            combine(categoryFlows) { it.toList() }
        }
    }

    override fun observeCategory(id: Long): Flow<SoundscapeCategory?> {
        return categoryDao.observeById(id).flatMapLatest { category ->
            if (category == null) return@flatMapLatest kotlinx.coroutines.flow.flowOf(null)
            trackDao.observeByCategoryId(category.id).map { tracks ->
                category.toDomain(tracks.map { it.toDomain() })
            }
        }
    }

    override fun observeTracksByCategory(categoryId: Long): Flow<List<SoundscapeTrack>> {
        return trackDao.observeByCategoryId(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeMostPlayedTrack(): Flow<SoundscapeTrack?> {
        return trackDao.getMostPlayed().map { it?.toDomain() }
    }

    override suspend fun getCategoryById(id: Long): SoundscapeCategory? {
        return withContext(Dispatchers.IO) {
            categoryDao.getById(id)?.toDomain(emptyList())
        }
    }

    override fun observeDeletedCategories(): Flow<List<SoundscapeCategory>> {
        return categoryDao.observeDeleted().flatMapLatest { categories ->
            if (categories.isEmpty()) return@flatMapLatest kotlinx.coroutines.flow.flowOf(emptyList())
            val categoryFlows = categories.map { category ->
                trackDao.observeByCategoryId(category.id).map { tracks ->
                    category.toDomain(tracks.map { it.toDomain() })
                }
            }
            combine(categoryFlows) { it.toList() }
        }
    }

    override suspend fun softDeleteCategory(id: Long) {
        withContext(Dispatchers.IO) {
            categoryDao.softDelete(id, System.currentTimeMillis())
        }
    }

    override suspend fun restoreCategory(id: Long) {
        withContext(Dispatchers.IO) {
            categoryDao.restore(id)
        }
    }

    override suspend fun upsertCategory(category: SoundscapeCategory): Long {
        return withContext(Dispatchers.IO) {
            categoryDao.upsert(category.toEntity())
        }
    }

    override suspend fun deleteCategory(id: Long) {
        withContext(Dispatchers.IO) {
            categoryDao.delete(id)
        }
    }

    override suspend fun upsertTrack(track: SoundscapeTrack) {
        withContext(Dispatchers.IO) {
            trackDao.upsert(track.toEntity())
        }
    }

    override suspend fun deleteTrack(id: Long) {
        withContext(Dispatchers.IO) {
            trackDao.delete(id)
        }
    }

    override suspend fun incrementTrackPlayCount(trackId: Long) {
        withContext(Dispatchers.IO) {
            trackDao.incrementPlayCount(trackId)
        }
    }
}

private fun SoundscapeCategoryEntity.toDomain(tracks: List<SoundscapeTrack>) = SoundscapeCategory(
    id = id,
    name = name,
    iconResId = iconResId,
    themeLabel = themeLabel,
    tracks = tracks,
    deletedAt = deletedAt
)

private fun SoundscapeCategory.toEntity() = SoundscapeCategoryEntity(
    id = id,
    name = name,
    iconResId = iconResId,
    themeLabel = themeLabel,
    deletedAt = deletedAt
)

private fun SoundscapeTrackEntity.toDomain() = SoundscapeTrack(
    id = id,
    categoryId = categoryId,
    name = name,
    filePath = filePath,
    intensityLevel = IntensityLevel.fromInt(intensityLevel),
    mixVolume = mixVolume,
    playCount = playCount
)

private fun SoundscapeTrack.toEntity() = SoundscapeTrackEntity(
    id = id,
    categoryId = categoryId,
    name = name,
    filePath = filePath,
    intensityLevel = intensityLevel.value,
    mixVolume = mixVolume,
    playCount = playCount
)
