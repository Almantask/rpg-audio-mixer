package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.*
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import com.example.rpgaudiomixer.infra.local.dao.*
import com.example.rpgaudiomixer.infra.local.entities.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class SoundscapeRepositoryImpl @Inject constructor(
    private val categoryDao: SoundscapeCategoryDao,
    private val trackDao: SoundscapeTrackDao
) : SoundscapeRepository {

    override fun observeAllCategories(): Flow<List<SoundscapeCategory>> {
        return categoryDao.observeAll().flatMapLatest { categories ->
            if (categories.isEmpty()) return@flatMapLatest flowOf(emptyList())
            
            val flows = categories.map { category ->
                observeTrackCountsByIntensity(category.id).map { counts ->
                    category.toDomain(counts)
                }
            }
            combine(flows) { it.toList() }
        }
    }

    override fun observeDeletedCategories(): Flow<List<SoundscapeCategory>> {
        return categoryDao.observeDeleted().flatMapLatest { categories ->
            if (categories.isEmpty()) return@flatMapLatest flowOf(emptyList())
            
            val flows = categories.map { category ->
                observeTrackCountsByIntensity(category.id).map { counts ->
                    category.toDomain(counts)
                }
            }
            combine(flows) { it.toList() }
        }
    }

    override fun observeTracksByCategory(categoryId: Long): Flow<List<SoundscapeTrack>> {
        return trackDao.observeByCategoryId(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeTrackCountsByIntensity(categoryId: Long): Flow<Map<IntensityLevel, Int>> {
        return trackDao.observeTrackCountsByIntensity(categoryId).map { counts ->
            val resultMap = mutableMapOf(
                IntensityLevel.I to 0,
                IntensityLevel.II to 0,
                IntensityLevel.III to 0
            )
            counts.forEach { count ->
                when (count.intensityLevel) {
                    1 -> resultMap[IntensityLevel.I] = count.count
                    2 -> resultMap[IntensityLevel.II] = count.count
                    3 -> resultMap[IntensityLevel.III] = count.count
                }
            }
            resultMap
        }
    }

    override fun observeCategoryPlayCounts(): Flow<Map<Long, Int>> {
        return trackDao.observeCategoryPlayCounts().map { counts ->
            counts.associate { it.categoryId to it.count }
        }
    }

    override fun observeMostPlayed(): Flow<SoundscapeTrack?> {
        return trackDao.observeMostPlayed().map { it?.toDomain() }
    }

    override suspend fun incrementTrackPlayCount(id: Long) {
        trackDao.incrementPlayCount(id)
    }

    override suspend fun upsertCategory(category: SoundscapeCategory) {
        categoryDao.upsert(SoundscapeCategoryEntity.fromDomain(category))
    }

    override suspend fun softDeleteCategory(id: Long) {
        categoryDao.softDelete(id, System.currentTimeMillis())
    }

    override suspend fun restoreCategory(id: Long) {
        categoryDao.restore(id)
    }

    override suspend fun permanentDeleteCategory(id: Long) {
        categoryDao.permanentDelete(id)
    }

    override suspend fun purgeOldDeletedCategories(threshold: Long) {
        categoryDao.purgeOldDeleted(threshold)
    }

    override suspend fun upsertTrack(track: SoundscapeTrack) {
        trackDao.upsert(SoundscapeTrackEntity.fromDomain(track))
    }

    override suspend fun deleteTrack(id: Long) {
        trackDao.deleteById(id)
    }
}
