package com.example.rpgaudiomixer.data.soundscape

import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategorySummaryRow
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeMostPlayedTrackRow
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackEntity
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.MostPlayedSoundscapeTrack
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SoundscapeRepositoryImpl @Inject constructor(
    private val categoryDao: SoundscapeCategoryDao,
    private val trackDao: SoundscapeTrackDao,
) : SoundscapeRepository {

    override fun observeCategories(): Flow<List<SoundscapeCategory>> {
        return categoryDao.observeCategorySummaries().map { rows ->
            rows.map { it.toDomainModel() }
        }
    }

    override fun observeCategory(categoryId: Long): Flow<SoundscapeCategory?> {
        return categoryDao.observeById(categoryId).map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun observeTracks(categoryId: Long): Flow<List<SoundscapeTrack>> {
        return trackDao.observeByCategory(categoryId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun observeMostPlayedTrack(): Flow<MostPlayedSoundscapeTrack?> {
        return trackDao.observeMostPlayedTrack().map { row -> row?.toDomainModel() }
    }

    override suspend fun createCategory(name: String): Long {
        return categoryDao.insert(
                SoundscapeCategoryEntity(
                    name = name.trim(),
                    iconResId = null,
                    themeLabel = null,
                    isDemoContent = false,
                    deletedAt = null,
                ),
            )
    }

    override suspend fun deleteCategory(categoryId: Long, deletedAtMillis: Long) {
        categoryDao.softDeleteById(categoryId, deletedAtMillis)
    }

    override suspend fun saveTracks(categoryId: Long, tracks: List<SoundscapeTrack>) {
        trackDao.deleteByCategoryId(categoryId)
        trackDao.insertAll(
            tracks.mapIndexed { index, track ->
                SoundscapeTrackEntity(
                    id = track.id,
                    categoryId = categoryId,
                    name = track.name.trim(),
                    filePath = track.filePath,
                    intensityLevel = track.intensityLevel.dbValue,
                    mixVolumePercent = track.mixVolumePercent,
                    displayOrder = index,
                    playCount = track.playCount,
                )
            },
        )
    }

    override suspend fun installDemoSoundscapes() {
        if (categoryDao.demoCategoryCount() > 0) {
            return
        }

        val demoDefinitions = listOf(
            "Weather" to "Environment",
            "Interior" to "Atmosphere",
            "Monsters" to "Creatures",
            "City" to "Settlements",
            "Dungeon" to "Depths",
        )

        val categoryIds = categoryDao.insertAll(
            demoDefinitions.map { (name, themeLabel) ->
                SoundscapeCategoryEntity(
                    name = name,
                    iconResId = null,
                    themeLabel = themeLabel,
                    isDemoContent = true,
                    deletedAt = null,
                )
            },
        )

        val demoTracks = buildList {
            categoryIds.forEachIndexed { categoryIndex, categoryId ->
                repeat(20) { trackIndex ->
                    add(
                        SoundscapeTrackEntity(
                            categoryId = categoryId,
                            name = "demo_${categoryIndex + 1}_${trackIndex + 1}.mp3",
                            filePath = "demo://soundscapes/${categoryIndex + 1}/${trackIndex + 1}",
                            intensityLevel = when {
                                trackIndex < 7 -> IntensityLevel.I.dbValue
                                trackIndex < 14 -> IntensityLevel.II.dbValue
                                else -> IntensityLevel.III.dbValue
                            },
                            mixVolumePercent = 100,
                            displayOrder = trackIndex,
                            playCount = 0,
                        ),
                    )
                }
            }
        }

        trackDao.insertAll(demoTracks)
    }
}

private fun SoundscapeCategorySummaryRow.toDomainModel(): SoundscapeCategory {
    return SoundscapeCategory(
        id = id,
        name = name,
        themeLabel = themeLabel,
        iconResId = iconResId,
        isDemoContent = isDemoContent,
        levelOneCount = levelOneCount,
        levelTwoCount = levelTwoCount,
        levelThreeCount = levelThreeCount,
    )
}

private fun SoundscapeCategoryEntity.toDomainModel(): SoundscapeCategory {
    return SoundscapeCategory(
        id = id,
        name = name,
        themeLabel = themeLabel,
        iconResId = iconResId,
        isDemoContent = isDemoContent,
        levelOneCount = 0,
        levelTwoCount = 0,
        levelThreeCount = 0,
    )
}

private fun SoundscapeTrackEntity.toDomainModel(): SoundscapeTrack {
    return SoundscapeTrack(
        id = id,
        categoryId = categoryId,
        name = name,
        filePath = filePath,
        intensityLevel = IntensityLevel.fromDbValue(intensityLevel),
        mixVolumePercent = mixVolumePercent,
        displayOrder = displayOrder,
        playCount = playCount,
    )
}

private fun SoundscapeMostPlayedTrackRow.toDomainModel(): MostPlayedSoundscapeTrack {
    return MostPlayedSoundscapeTrack(
        id = id,
        categoryId = categoryId,
        categoryName = categoryName,
        name = name,
        filePath = filePath,
        intensityLevel = IntensityLevel.fromDbValue(intensityLevel),
        mixVolumePercent = mixVolumePercent,
        displayOrder = displayOrder,
        playCount = playCount,
    )
}
