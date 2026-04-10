package com.example.rpgaudiomixer.data.soundscape

import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackEntity
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SoundscapeRepositoryImpl @Inject constructor(
    private val categoryDao: SoundscapeCategoryDao,
    private val trackDao: SoundscapeTrackDao
) : SoundscapeRepository {

    override fun observeAllCategories(): Flow<List<SoundscapeCategory>> {
        return categoryDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeCategoryById(categoryId: Long): Flow<SoundscapeCategory?> {
        return categoryDao.observeById(categoryId).map { it?.toDomain() }
    }

    override suspend fun getCategoryById(categoryId: Long): SoundscapeCategory? {
        return categoryDao.getById(categoryId)?.toDomain()
    }

    override fun observeTracksByCategory(categoryId: Long): Flow<List<SoundscapeTrack>> {
        return trackDao.observeByCategory(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeTracksByIntensity(categoryId: Long, level: Int): Flow<List<SoundscapeTrack>> {
        return trackDao.observeByIntensity(categoryId, level).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTrackById(trackId: Long): SoundscapeTrack? {
        return trackDao.getById(trackId)?.toDomain()
    }

    override suspend fun createCategory(category: SoundscapeCategory): Long {
        return categoryDao.insert(category.toEntity())
    }

    override suspend fun updateCategory(category: SoundscapeCategory) {
        categoryDao.update(category.toEntity())
    }

    override suspend fun deleteCategory(categoryId: Long) {
        categoryDao.delete(categoryId)
    }

    override suspend fun createTrack(track: SoundscapeTrack): Long {
        return trackDao.insert(track.toEntity())
    }

    override suspend fun updateTrack(track: SoundscapeTrack) {
        trackDao.update(track.toEntity())
    }

    override suspend fun deleteTrack(trackId: Long) {
        trackDao.delete(trackId)
    }

    override suspend fun incrementTrackPlayCount(trackId: Long) {
        trackDao.incrementPlayCount(trackId)
    }

    private fun SoundscapeCategoryEntity.toDomain(): SoundscapeCategory {
        return SoundscapeCategory(
            id = id,
            name = name,
            iconResId = iconResId,
            themeLabel = themeLabel
        )
    }

    private fun SoundscapeCategory.toEntity(): SoundscapeCategoryEntity {
        return SoundscapeCategoryEntity(
            id = id,
            name = name,
            iconResId = iconResId,
            themeLabel = themeLabel
        )
    }

    private fun SoundscapeTrackEntity.toDomain(): SoundscapeTrack {
        return SoundscapeTrack(
            id = id,
            categoryId = categoryId,
            name = name,
            filePath = filePath,
            intensityLevel = IntensityLevel.fromLevel(intensityLevel),
            mixVolume = mixVolume,
            playCount = playCount
        )
    }

    private fun SoundscapeTrack.toEntity(): SoundscapeTrackEntity {
        return SoundscapeTrackEntity(
            id = id,
            categoryId = categoryId,
            name = name,
            filePath = filePath,
            intensityLevel = intensityLevel.level,
            mixVolume = mixVolume,
            playCount = playCount
        )
    }
}
