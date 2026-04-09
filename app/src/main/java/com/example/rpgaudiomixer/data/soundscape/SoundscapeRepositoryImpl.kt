package com.example.rpgaudiomixer.data.soundscape

import com.example.rpgaudiomixer.data.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.local.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.data.local.SoundscapeCategorySummaryEntity
import com.example.rpgaudiomixer.data.local.SoundscapeTrackDao
import com.example.rpgaudiomixer.data.local.SoundscapeTrackEntity
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
    private val importedAudioStorage: ImportedAudioStorage,
) : SoundscapeRepository {
    private var currentTimeProvider: () -> Long = System::currentTimeMillis

    internal constructor(
        categoryDao: SoundscapeCategoryDao,
        trackDao: SoundscapeTrackDao,
        importedAudioStorage: ImportedAudioStorage,
        currentTimeProvider: () -> Long,
    ) : this(
        categoryDao = categoryDao,
        trackDao = trackDao,
        importedAudioStorage = importedAudioStorage,
    ) {
        this.currentTimeProvider = currentTimeProvider
    }

    override fun observeCategories(): Flow<List<SoundscapeCategory>> =
        categoryDao.observeAll().map { categories ->
            categories.map(SoundscapeCategorySummaryEntity::toDomain)
        }

    override fun observeCategory(categoryId: Long): Flow<SoundscapeCategory?> =
        combine(
            categoryDao.observeById(categoryId),
            observeCategories(),
        ) { category, categories ->
            val matchingSummary = categories.firstOrNull { it.id == categoryId }
            if (matchingSummary != null) {
                matchingSummary
            } else {
                category?.toDomain()
            }
        }

    override fun observeTracks(categoryId: Long): Flow<List<SoundscapeTrack>> =
        trackDao.observeByCategory(categoryId).map { tracks ->
            tracks.map(SoundscapeTrackEntity::toDomain)
        }

    override fun observeHasDemoSoundscapes(): Flow<Boolean> = trackDao.observeDemoContentAvailable()

    override suspend fun createCategory(name: String): Long = categoryDao.upsert(
        SoundscapeCategoryEntity(
            name = name,
            iconResId = null,
            themeLabel = null,
        )
    )

    override suspend fun deleteCategory(categoryId: Long) {
        categoryDao.softDeleteById(categoryId = categoryId, deletedAt = currentTimeProvider())
    }

    override suspend fun importTrack(categoryId: Long, sourceUri: String): SoundscapeTrack {
        val importedAudio = importedAudioStorage.importAudio(sourceUri)
        return SoundscapeTrack(
            id = 0L,
            categoryId = categoryId,
            name = importedAudio.displayName,
            filePath = importedAudio.storedPath,
            intensityLevel = IntensityLevel.I,
            mixVolume = 1f,
            playCount = 0,
        )
    }

    override suspend fun saveTracks(categoryId: Long, tracks: List<SoundscapeTrack>) {
        if (tracks.isEmpty()) {
            trackDao.deleteByCategoryId(categoryId)
        } else {
            val persistedTrackIds = tracks.map { track ->
                trackDao.upsert(track.toEntity())
            }
            trackDao.deleteByCategoryIdExcept(categoryId = categoryId, keepTrackIds = persistedTrackIds)
        }
    }

    override suspend fun seedDemoSoundscapes() {
        val demoCategories = demoCategoryBlueprints()
        val savedCategories = demoCategories.map { blueprint ->
            val savedCategoryId = categoryDao.upsert(
                SoundscapeCategoryEntity(
                    name = blueprint.name,
                    iconResId = null,
                    themeLabel = blueprint.themeLabel,
                )
            )
            savedCategoryId to blueprint
        }

        val demoTracks = savedCategories.flatMap { (categoryId, blueprint) ->
            blueprint.trackNames.mapIndexed { index, trackName ->
                SoundscapeTrackEntity(
                    categoryId = categoryId,
                    name = trackName,
                    filePath = "demo://$trackName",
                    intensityLevel = when (index % 3) {
                        0 -> IntensityLevel.I.persistedValue
                        1 -> IntensityLevel.II.persistedValue
                        else -> IntensityLevel.III.persistedValue
                    },
                    mixVolume = 1f,
                    isDemo = true,
                )
            }
        }

        trackDao.upsertAll(demoTracks)
    }

    override suspend fun incrementTrackPlayCount(trackId: Long) {
        trackDao.incrementPlayCount(trackId)
    }
}

private fun SoundscapeCategorySummaryEntity.toDomain(): SoundscapeCategory = SoundscapeCategory(
    id = id,
    name = name,
    iconResId = iconResId,
    themeLabel = themeLabel,
    levelOneTrackCount = levelOneTrackCount,
    levelTwoTrackCount = levelTwoTrackCount,
    levelThreeTrackCount = levelThreeTrackCount,
)

private fun SoundscapeCategoryEntity.toDomain(): SoundscapeCategory = SoundscapeCategory(
    id = id,
    name = name,
    iconResId = iconResId,
    themeLabel = themeLabel,
    levelOneTrackCount = 0,
    levelTwoTrackCount = 0,
    levelThreeTrackCount = 0,
)

private fun SoundscapeTrackEntity.toDomain(): SoundscapeTrack = SoundscapeTrack(
    id = id,
    categoryId = categoryId,
    name = name,
    filePath = filePath,
    intensityLevel = IntensityLevel.fromPersistedValue(intensityLevel),
    mixVolume = mixVolume,
    playCount = playCount,
)

private fun SoundscapeTrack.toEntity(): SoundscapeTrackEntity = SoundscapeTrackEntity(
    id = id.takeIf { it > 0L } ?: 0L,
    categoryId = categoryId,
    name = name,
    filePath = filePath,
    intensityLevel = intensityLevel.persistedValue,
    mixVolume = mixVolume,
    playCount = playCount,
    isDemo = false,
)

private data class DemoCategoryBlueprint(
    val name: String,
    val themeLabel: String,
    val trackNames: List<String>,
)

private fun demoCategoryBlueprints(): List<DemoCategoryBlueprint> = listOf(
    DemoCategoryBlueprint("Weather", "Environment", List(10) { index -> "weather_demo_${index + 1}.mp3" }),
    DemoCategoryBlueprint("Interior", "Atmosphere", List(10) { index -> "interior_demo_${index + 1}.mp3" }),
    DemoCategoryBlueprint("Monsters", "Creatures", List(10) { index -> "monsters_demo_${index + 1}.mp3" }),
    DemoCategoryBlueprint("Arcane", "Mystic", List(10) { index -> "arcane_demo_${index + 1}.mp3" }),
    DemoCategoryBlueprint("Forest", "Nature", List(10) { index -> "forest_demo_${index + 1}.mp3" }),
    DemoCategoryBlueprint("Sea", "Nature", List(10) { index -> "sea_demo_${index + 1}.mp3" }),
    DemoCategoryBlueprint("City", "Civilisation", List(10) { index -> "city_demo_${index + 1}.mp3" }),
    DemoCategoryBlueprint("Dungeon", "Adventure", List(10) { index -> "dungeon_demo_${index + 1}.mp3" }),
    DemoCategoryBlueprint("Combat", "Conflict", List(10) { index -> "combat_demo_${index + 1}.mp3" }),
    DemoCategoryBlueprint("Mystery", "Atmosphere", List(10) { index -> "mystery_demo_${index + 1}.mp3" }),
)
