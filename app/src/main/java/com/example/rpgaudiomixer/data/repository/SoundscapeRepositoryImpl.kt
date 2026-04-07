package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.local.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.data.local.SoundscapeTrackDao
import com.example.rpgaudiomixer.data.local.SoundscapeTrackEntity
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of SoundscapeRepository using Room.
 */
class SoundscapeRepositoryImpl @Inject constructor(
    private val categoryDao: SoundscapeCategoryDao,
    private val trackDao: SoundscapeTrackDao
) : SoundscapeRepository {

    override fun observeAllCategories(): Flow<List<SoundscapeCategory>> {
        return categoryDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createCategory(name: String, iconResId: Int?, themeLabel: String?): Long {
        val entity = SoundscapeCategoryEntity(
            name = name,
            iconResId = iconResId,
            themeLabel = themeLabel
        )
        return categoryDao.upsert(entity)
    }

    override suspend fun updateCategory(category: SoundscapeCategory) {
        categoryDao.upsert(category.toEntity())
    }

    override suspend fun deleteCategory(id: Long) {
        categoryDao.delete(id)
    }

    override suspend fun getCategoryById(id: Long): SoundscapeCategory? {
        return categoryDao.getById(id)?.toDomain()
    }

    override fun observeTracksByCategory(categoryId: Long): Flow<List<SoundscapeTrack>> {
        return trackDao.observeByCategory(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeTracksByCategoryAndIntensity(
        categoryId: Long,
        intensity: IntensityLevel
    ): Flow<List<SoundscapeTrack>> {
        return trackDao.observeByCategoryAndIntensity(categoryId, intensity.value).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createTrack(
        categoryId: Long,
        name: String,
        filePath: String,
        intensityLevel: IntensityLevel,
        mixVolume: Float
    ): Long {
        val entity = SoundscapeTrackEntity(
            categoryId = categoryId,
            name = name,
            filePath = filePath,
            intensityLevel = intensityLevel.value,
            mixVolume = mixVolume
        )
        return trackDao.upsert(entity)
    }

    override suspend fun updateTrack(track: SoundscapeTrack) {
        trackDao.upsert(track.toEntity())
    }

    override suspend fun deleteTrack(id: Long) {
        trackDao.delete(id)
    }

    override suspend fun getTrackById(id: Long): SoundscapeTrack? {
        return trackDao.getById(id)?.toDomain()
    }

    override suspend fun getTrackCountByIntensity(categoryId: Long, intensity: IntensityLevel): Int {
        return categoryDao.getTrackCountByIntensity(categoryId, intensity.value)
    }

    override suspend fun incrementPlayCount(trackId: Long) {
        trackDao.incrementPlayCount(trackId)
    }

    override fun observeMostPlayedTrack(): Flow<SoundscapeTrack?> {
        return trackDao.observeMostPlayed().map { it?.toDomain() }
    }

    private fun SoundscapeCategoryEntity.toDomain() = SoundscapeCategory(
        id = id,
        name = name,
        iconResId = iconResId,
        themeLabel = themeLabel
    )

    private fun SoundscapeCategory.toEntity() = SoundscapeCategoryEntity(
        id = id,
        name = name,
        iconResId = iconResId,
        themeLabel = themeLabel
    )

    private fun SoundscapeTrackEntity.toDomain() = SoundscapeTrack(
        id = id,
        categoryId = categoryId,
        name = name,
        filePath = filePath,
        intensityLevel = IntensityLevel.fromValue(intensityLevel),
        mixVolume = mixVolume
    )

    private fun SoundscapeTrack.toEntity() = SoundscapeTrackEntity(
        id = id,
        categoryId = categoryId,
        name = name,
        filePath = filePath,
        intensityLevel = intensityLevel.value,
        mixVolume = mixVolume
    )
}
