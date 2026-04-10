package com.example.rpgaudiomixer.data.soundscape

import com.example.rpgaudiomixer.data.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.local.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.data.local.SoundscapeCategorySummaryEntity
import com.example.rpgaudiomixer.data.local.SoundscapeTrackDao
import com.example.rpgaudiomixer.data.local.SoundscapeTrackEntity
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SoundscapeRepositoryImplTest {

    private val categoryDao = FakeSoundscapeCategoryDao()
    private val trackDao = FakeSoundscapeTrackDao()
    private val audioStorage = FakeImportedAudioStorage()
    private val repository = SoundscapeRepositoryImpl(
        categoryDao = categoryDao,
        trackDao = trackDao,
        importedAudioStorage = audioStorage,
    )

    @Test
    fun observeCategories_maps_category_summaries_to_domain_models() = runTest {
        // Arrange
        categoryDao.emitCategories(
            listOf(
                SoundscapeCategorySummaryEntity(
                    id = 2L,
                    name = "Weather",
                    iconResId = null,
                    themeLabel = "Environment",
                    levelOneTrackCount = 3,
                    levelTwoTrackCount = 5,
                    levelThreeTrackCount = 2,
                ),
                SoundscapeCategorySummaryEntity(
                    id = 1L,
                    name = "Interior",
                    iconResId = null,
                    themeLabel = "Atmosphere",
                    levelOneTrackCount = 0,
                    levelTwoTrackCount = 0,
                    levelThreeTrackCount = 0,
                ),
            )
        )

        // Act
        val result = repository.observeCategories().first()

        // Assert
        assertThat(result).containsExactly(
            SoundscapeCategory(
                id = 2L,
                name = "Weather",
                iconResId = null,
                themeLabel = "Environment",
                levelOneTrackCount = 3,
                levelTwoTrackCount = 5,
                levelThreeTrackCount = 2,
            ),
            SoundscapeCategory(
                id = 1L,
                name = "Interior",
                iconResId = null,
                themeLabel = "Atmosphere",
                levelOneTrackCount = 0,
                levelTwoTrackCount = 0,
                levelThreeTrackCount = 0,
            ),
        )
    }

    @Test
    fun observeTracks_maps_entities_to_domain_models() = runTest {
        // Arrange
        trackDao.emitTracks(
            listOf(
                SoundscapeTrackEntity(
                    id = 9L,
                    categoryId = 2L,
                    name = "thunderstorm.mp3",
                    filePath = "/files/soundscapes/thunderstorm.mp3",
                    intensityLevel = 3,
                    mixVolume = 0.6f,
                    isDemo = false,
                )
            )
        )

        // Act
        val result = repository.observeTracks(categoryId = 2L).first()

        // Assert
        assertThat(result).containsExactly(
            SoundscapeTrack(
                id = 9L,
                categoryId = 2L,
                name = "thunderstorm.mp3",
                filePath = "/files/soundscapes/thunderstorm.mp3",
                intensityLevel = IntensityLevel.III,
                mixVolume = 0.6f,
            )
        )
    }

    @Test
    fun createCategory_inserts_the_expected_entity() = runTest {
        // Arrange
        val name = "Arcane"

        // Act
        repository.createCategory(name = name)

        // Assert
        assertThat(categoryDao.upsertedCategories).containsExactly(
            SoundscapeCategoryEntity(
                id = 0L,
                name = name,
                iconResId = null,
                themeLabel = null,
            )
        )
    }

    @Test
    fun importTrack_copies_audio_and_inserts_a_default_track() = runTest {
        // Arrange
        audioStorage.nextImportedFile = ImportedAudioFile(
            displayName = "thunderstorm.mp3",
            storedPath = "/files/soundscapes/thunderstorm.mp3",
        )

        // Act
        val result = repository.importTrack(
            categoryId = 4L,
            sourceUri = "content://sound/thunderstorm",
        )

        // Assert
        assertThat(result).isEqualTo(
            SoundscapeTrack(
                id = 0L,
                categoryId = 4L,
                name = "thunderstorm.mp3",
                filePath = "/files/soundscapes/thunderstorm.mp3",
                intensityLevel = IntensityLevel.I,
                mixVolume = 1f,
            )
        )
        assertThat(trackDao.upsertedTracks).isEmpty()
        assertThat(audioStorage.importedUris).containsExactly("content://sound/thunderstorm")
    }

    @Test
    fun saveTracks_upserts_all_tracks_and_deletes_removed_ones() = runTest {
        // Arrange
        val tracks = listOf(
            SoundscapeTrack(
                id = 11L,
                categoryId = 5L,
                name = "Light Rain",
                filePath = "/files/light_rain.mp3",
                intensityLevel = IntensityLevel.I,
                mixVolume = 1f,
            ),
            SoundscapeTrack(
                id = 0L,
                categoryId = 5L,
                name = "Thunder",
                filePath = "/files/thunder.mp3",
                intensityLevel = IntensityLevel.III,
                mixVolume = 0.6f,
            ),
        )

        // Act
        repository.saveTracks(categoryId = 5L, tracks = tracks)

        // Assert
        assertThat(trackDao.upsertedTracks).containsExactly(
            SoundscapeTrackEntity(
                id = 11L,
                categoryId = 5L,
                name = "Light Rain",
                filePath = "/files/light_rain.mp3",
                intensityLevel = 1,
                mixVolume = 1f,
                isDemo = false,
            ),
            SoundscapeTrackEntity(
                id = 0L,
                categoryId = 5L,
                name = "Thunder",
                filePath = "/files/thunder.mp3",
                intensityLevel = 3,
                mixVolume = 0.6f,
                isDemo = false,
            ),
        )
        assertThat(trackDao.deletedTracksExceptArgs).containsExactly(5L to listOf(11L, 42L))
    }

    @Test
    fun incrementTrackPlayCount_delegates_to_the_track_dao() = runTest {
        // Arrange

        // Act
        repository.incrementTrackPlayCount(trackId = 12L)

        // Assert
        assertThat(trackDao.incrementedTrackIds).containsExactly(12L)
    }

    @Test
    fun seedDemoSoundscapes_creates_one_hundred_demo_tracks_and_marks_demo_content_available() = runTest {
        // Arrange

        // Act
        repository.seedDemoSoundscapes()
        val demoContentAvailable = repository.observeHasDemoSoundscapes().first()

        // Assert
        assertThat(categoryDao.upsertedCategories).hasSize(10)
        assertThat(trackDao.upsertedTrackLists.single()).hasSize(100)
        assertThat(trackDao.upsertedTrackLists.single().all { it.isDemo }).isTrue()
        assertThat(demoContentAvailable).isTrue()
    }

    private class FakeSoundscapeCategoryDao : SoundscapeCategoryDao {
        private val categoriesFlow = MutableStateFlow<List<SoundscapeCategorySummaryEntity>>(emptyList())
        private val categoryFlow = MutableStateFlow<SoundscapeCategoryEntity?>(null)
        private val deletedCategoriesFlow = MutableStateFlow<List<SoundscapeCategoryEntity>>(emptyList())

        val upsertedCategories = mutableListOf<SoundscapeCategoryEntity>()
        val deletedCategoryIds = mutableListOf<Long>()

        fun emitCategories(categories: List<SoundscapeCategorySummaryEntity>) {
            categoriesFlow.value = categories
        }

        override fun observeAll(): Flow<List<SoundscapeCategorySummaryEntity>> = categoriesFlow

        override fun observeById(categoryId: Long): Flow<SoundscapeCategoryEntity?> = categoryFlow

        override fun observeDeleted(): Flow<List<SoundscapeCategoryEntity>> = deletedCategoriesFlow

        override suspend fun upsert(category: SoundscapeCategoryEntity): Long {
            upsertedCategories += category
            return if (category.id == 0L) 42L else category.id
        }

        override suspend fun upsertAll(categories: List<SoundscapeCategoryEntity>) {
            upsertedCategories += categories
        }

        override suspend fun deleteById(categoryId: Long) {
            deletedCategoryIds += categoryId
        }

        override suspend fun softDeleteById(categoryId: Long, deletedAt: Long) = Unit

        override suspend fun restoreById(categoryId: Long) = Unit

        override suspend fun deleteAllDeleted() = Unit

        override suspend fun purgeDeletedBefore(cutoffTimeMillis: Long) = Unit
    }

    private class FakeSoundscapeTrackDao : SoundscapeTrackDao {
        private val tracksFlow = MutableStateFlow<List<SoundscapeTrackEntity>>(emptyList())
        private val demoAvailableFlow = MutableStateFlow(false)

        val upsertedTracks = mutableListOf<SoundscapeTrackEntity>()
        val upsertedTrackLists = mutableListOf<List<SoundscapeTrackEntity>>()
        val deletedTrackIds = mutableListOf<Long>()
        val deletedTrackCategoryIds = mutableListOf<Long>()
        val deletedTracksExceptArgs = mutableListOf<Pair<Long, List<Long>>>()
        val incrementedTrackIds = mutableListOf<Long>()

        fun emitTracks(tracks: List<SoundscapeTrackEntity>) {
            tracksFlow.value = tracks
            demoAvailableFlow.value = tracks.any(SoundscapeTrackEntity::isDemo)
        }

        override fun observeByCategory(categoryId: Long): Flow<List<SoundscapeTrackEntity>> = tracksFlow

        override fun observeDemoContentAvailable(): Flow<Boolean> = demoAvailableFlow

        override suspend fun upsert(track: SoundscapeTrackEntity): Long {
            upsertedTracks += track
            demoAvailableFlow.value = track.isDemo || demoAvailableFlow.value
            return if (track.id > 0L) track.id else (40L + upsertedTracks.size)
        }

        override suspend fun upsertAll(tracks: List<SoundscapeTrackEntity>) {
            upsertedTrackLists += tracks
            demoAvailableFlow.value = tracks.any(SoundscapeTrackEntity::isDemo) || demoAvailableFlow.value
        }

        override suspend fun deleteById(trackId: Long) {
            deletedTrackIds += trackId
        }

        override suspend fun deleteByCategoryId(categoryId: Long) {
            deletedTrackCategoryIds += categoryId
        }

        override suspend fun deleteByCategoryIdExcept(categoryId: Long, keepTrackIds: List<Long>) {
            deletedTracksExceptArgs += categoryId to keepTrackIds
        }

        override suspend fun incrementPlayCount(trackId: Long) {
            incrementedTrackIds += trackId
        }
    }

    private class FakeImportedAudioStorage : ImportedAudioStorage {
        var nextImportedFile: ImportedAudioFile = ImportedAudioFile(
            displayName = "default.mp3",
            storedPath = "/files/default.mp3",
        )

        val importedUris = mutableListOf<String>()

        override suspend fun importAudio(sourceUri: String): ImportedAudioFile {
            importedUris += sourceUri
            return nextImportedFile
        }
    }
}
