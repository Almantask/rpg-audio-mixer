package com.example.rpgaudiomixer.data.soundscape

import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryLibraryRow
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackEntity
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@Singleton
class SoundscapeRepositoryImpl @Inject constructor(
    private val categoryDao: SoundscapeCategoryDao,
    private val trackDao: SoundscapeTrackDao,
) : SoundscapeRepository {
    override fun observeCategories(): Flow<List<SoundscapeCategory>> = categoryDao.observeLibrary()
        .map { rows -> rows.map(SoundscapeCategoryLibraryRow::toDomain) }

    override fun observeCategory(categoryId: Long): Flow<SoundscapeCategory?> = combine(
        categoryDao.observeById(categoryId),
        trackDao.observeByCategory(categoryId),
    ) { category, tracks ->
        category?.toDomain(tracks.map(SoundscapeTrackEntity::toDomain))
    }

    override fun hasDemoSoundscapes(): Flow<Boolean> = trackDao.hasDemoTracks()

    override suspend fun createCategory(name: String): Long = categoryDao.upsert(
        SoundscapeCategoryEntity(
            name = name,
            themeLabel = null,
            iconName = null,
        ),
    )

    override suspend fun updateCategory(category: SoundscapeCategory) {
        categoryDao.upsert(category.toEntity())
    }

    override suspend fun deleteCategory(categoryId: Long) {
        categoryDao.deleteById(categoryId)
    }

    override suspend fun upsertTrack(track: SoundscapeTrack): Long = trackDao.upsert(track.toEntity())

    override suspend fun deleteTrack(trackId: Long) {
        trackDao.deleteById(trackId)
    }

    override suspend fun replaceTracks(categoryId: Long, tracks: List<SoundscapeTrack>) {
        val persistedIds = mutableListOf<Long>()
        tracks.forEach { track ->
            val persistedId = trackDao.upsert(track.copy(categoryId = categoryId).toEntity())
            persistedIds += if (track.id == 0L) persistedId else track.id
        }
        if (persistedIds.isEmpty()) {
            trackDao.deleteByCategory(categoryId)
        } else {
            trackDao.deleteMissingFromCategory(categoryId, persistedIds)
        }
    }

    override suspend fun downloadDemoSoundscapes() {
        val categories = listOf("Weather", "Interior", "Monsters", "Travel")
        categories.forEachIndexed { categoryIndex, categoryName ->
            val categoryId = createCategory(categoryName)
            repeat(25) { index ->
                val intensity = IntensityLevel.entries[(index + categoryIndex) % IntensityLevel.entries.size]
                upsertTrack(
                    SoundscapeTrack(
                        categoryId = categoryId,
                        name = "$categoryName Demo ${index + 1}",
                        filePath = "demo://$categoryName/${index + 1}",
                        intensityLevel = intensity,
                        mixVolumePercent = 100,
                    ),
                )
            }
        }
    }

    override suspend fun clearAll() {
        trackDao.clearAll()
        categoryDao.clearAll()
    }
}

private fun SoundscapeCategoryLibraryRow.toDomain(): SoundscapeCategory = SoundscapeCategory(
    id = id,
    name = name,
    themeLabel = themeLabel,
    iconName = iconName,
    intensityCounts = mapOf(
        IntensityLevel.I to levelICount,
        IntensityLevel.II to levelIICount,
        IntensityLevel.III to levelIIICount,
    ),
)

private fun SoundscapeCategoryEntity.toDomain(tracks: List<SoundscapeTrack>): SoundscapeCategory = SoundscapeCategory(
    id = id,
    name = name,
    themeLabel = themeLabel,
    iconName = iconName,
    tracks = tracks,
)

private fun SoundscapeTrackEntity.toDomain(): SoundscapeTrack = SoundscapeTrack(
    id = id,
    categoryId = categoryId,
    name = name,
    filePath = filePath,
    intensityLevel = IntensityLevel.fromLevel(intensityLevel),
    mixVolumePercent = mixVolumePercent,
)

private fun SoundscapeCategory.toEntity(): SoundscapeCategoryEntity = SoundscapeCategoryEntity(
    id = id,
    name = name,
    themeLabel = themeLabel,
    iconName = iconName,
)

private fun SoundscapeTrack.toEntity(): SoundscapeTrackEntity = SoundscapeTrackEntity(
    id = if (id < 0L) 0L else id,
    categoryId = categoryId,
    name = name,
    filePath = filePath,
    intensityLevel = intensityLevel.level,
    mixVolumePercent = mixVolumePercent,
)
