package com.example.rpgaudiomixer.data.soundscape

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SoundscapeRepositoryImpl @Inject constructor(
    private val categoryDao: SoundscapeCategoryDao,
    private val trackDao: SoundscapeTrackDao,
) : SoundscapeRepository {

    override fun observeAllCategories(): Flow<List<SoundscapeCategory>> =
        categoryDao.observeAll().flatMapLatest { categories ->
            if (categories.isEmpty()) {
                kotlinx.coroutines.flow.flowOf(emptyList())
            } else {
                val trackFlows = categories.map { cat ->
                    trackDao.observeByCategory(cat.id).map { tracks ->
                        cat.toDomain(tracks.map { it.toDomain() })
                    }
                }
                combine(trackFlows) { it.toList() }
            }
        }

    override fun observeCategory(id: Long): Flow<SoundscapeCategory?> =
        categoryDao.observeById(id).flatMapLatest { entity ->
            if (entity == null) {
                kotlinx.coroutines.flow.flowOf(null)
            } else {
                trackDao.observeByCategory(id).map { tracks ->
                    entity.toDomain(tracks.map { it.toDomain() })
                }
            }
        }

    override suspend fun createCategory(name: String): SoundscapeCategory {
        val entity = SoundscapeCategoryEntity(name = name)
        val id = categoryDao.upsert(entity)
        return entity.copy(id = id).toDomain()
    }

    override suspend fun deleteCategory(id: Long) {
        categoryDao.delete(id)
    }

    override suspend fun addTrack(
        categoryId: Long,
        name: String,
        filePath: String,
        intensityLevel: IntensityLevel,
        mixVolume: Float,
    ): SoundscapeTrack {
        val entity = SoundscapeTrackEntity(
            categoryId = categoryId,
            name = name,
            filePath = filePath,
            intensityLevel = intensityLevel.value,
            mixVolume = mixVolume,
        )
        val id = trackDao.upsert(entity)
        return entity.copy(id = id).toDomain()
    }

    override suspend fun updateTrack(track: SoundscapeTrack) {
        trackDao.update(track.toEntity())
    }

    override suspend fun deleteTrack(id: Long) {
        trackDao.delete(id)
    }
}
