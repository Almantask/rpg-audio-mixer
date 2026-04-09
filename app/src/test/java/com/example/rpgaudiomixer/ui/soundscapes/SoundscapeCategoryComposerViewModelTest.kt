package com.example.rpgaudiomixer.ui.soundscapes

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SoundscapeCategoryComposerViewModelTest {

    @Test
    fun init_exposes_category_and_tracks_from_the_repository() = runTest {
        // Arrange
        val repository = FakeSoundscapeRepository()
        repository.categoryFlow.value = SoundscapeCategory(
            id = 3L,
            name = "Weather",
            iconResId = null,
            themeLabel = "Environment",
            levelOneTrackCount = 1,
            levelTwoTrackCount = 0,
            levelThreeTrackCount = 0,
        )
        repository.tracksFlow.value = listOf(
            SoundscapeTrack(
                id = 8L,
                categoryId = 3L,
                name = "Light Rain",
                filePath = "/files/light_rain.mp3",
                intensityLevel = IntensityLevel.I,
                mixVolume = 1f,
            )
        )

        // Act
        val viewModel = SoundscapeCategoryComposerViewModel(
            categoryId = 3L,
            soundscapeRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(
            SoundscapeCategoryComposerUiState(
                isLoading = false,
                category = repository.categoryFlow.value,
                tracks = repository.tracksFlow.value,
            )
        )
    }

    @Test
    fun importTrack_adds_a_new_default_track_and_marks_unsaved_changes() = runTest {
        // Arrange
        val repository = FakeSoundscapeRepository()
        val viewModel = SoundscapeCategoryComposerViewModel(
            categoryId = 3L,
            soundscapeRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.importTrack("content://audio/thunder")
        advanceUntilIdle()

        // Assert
        assertThat(repository.importRequests).containsExactly(3L to "content://audio/thunder")
        assertThat(viewModel.uiState.value.tracks).containsExactly(
            SoundscapeTrack(
                id = 15L,
                categoryId = 3L,
                name = "Thunderstorm",
                filePath = "/files/thunderstorm.mp3",
                intensityLevel = IntensityLevel.I,
                mixVolume = 1f,
            )
        )
        assertThat(viewModel.uiState.value.hasUnsavedChanges).isTrue()
    }

    @Test
    fun updateTrackIntensity_changes_the_selected_track_in_the_draft() = runTest {
        // Arrange
        val repository = FakeSoundscapeRepository()
        repository.tracksFlow.value = listOf(
            SoundscapeTrack(
                id = 8L,
                categoryId = 3L,
                name = "Light Rain",
                filePath = "/files/light_rain.mp3",
                intensityLevel = IntensityLevel.I,
                mixVolume = 1f,
            )
        )
        val viewModel = SoundscapeCategoryComposerViewModel(
            categoryId = 3L,
            soundscapeRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.updateTrackIntensity(trackId = 8L, intensityLevel = IntensityLevel.III)

        // Assert
        assertThat(viewModel.uiState.value.tracks.single().intensityLevel).isEqualTo(IntensityLevel.III)
        assertThat(viewModel.uiState.value.hasUnsavedChanges).isTrue()
    }

    @Test
    fun updateTrackMix_changes_the_selected_track_in_the_draft() = runTest {
        // Arrange
        val repository = FakeSoundscapeRepository()
        repository.tracksFlow.value = listOf(
            SoundscapeTrack(
                id = 8L,
                categoryId = 3L,
                name = "Light Rain",
                filePath = "/files/light_rain.mp3",
                intensityLevel = IntensityLevel.I,
                mixVolume = 1f,
            )
        )
        val viewModel = SoundscapeCategoryComposerViewModel(
            categoryId = 3L,
            soundscapeRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.updateTrackMix(trackId = 8L, mixVolume = 0.6f)

        // Assert
        assertThat(viewModel.uiState.value.tracks.single().mixVolume).isEqualTo(0.6f)
        assertThat(viewModel.uiState.value.hasUnsavedChanges).isTrue()
    }

    @Test
    fun removeTrack_deletes_the_track_from_the_draft() = runTest {
        // Arrange
        val repository = FakeSoundscapeRepository()
        repository.tracksFlow.value = listOf(
            SoundscapeTrack(
                id = 8L,
                categoryId = 3L,
                name = "Light Rain",
                filePath = "/files/light_rain.mp3",
                intensityLevel = IntensityLevel.I,
                mixVolume = 1f,
            )
        )
        val viewModel = SoundscapeCategoryComposerViewModel(
            categoryId = 3L,
            soundscapeRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.removeTrack(trackId = 8L)

        // Assert
        assertThat(viewModel.uiState.value.tracks).isEmpty()
        assertThat(viewModel.uiState.value.hasUnsavedChanges).isTrue()
    }

    @Test
    fun saveComposition_persists_the_draft_and_requests_navigation_back() = runTest {
        // Arrange
        val repository = FakeSoundscapeRepository()
        repository.tracksFlow.value = listOf(
            SoundscapeTrack(
                id = 8L,
                categoryId = 3L,
                name = "Light Rain",
                filePath = "/files/light_rain.mp3",
                intensityLevel = IntensityLevel.I,
                mixVolume = 1f,
            )
        )
        val viewModel = SoundscapeCategoryComposerViewModel(
            categoryId = 3L,
            soundscapeRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()
        viewModel.updateTrackMix(trackId = 8L, mixVolume = 0.6f)

        // Act
        viewModel.saveComposition()
        advanceUntilIdle()

        // Assert
        assertThat(repository.savedTracks.single()).containsExactly(
            SoundscapeTrack(
                id = 8L,
                categoryId = 3L,
                name = "Light Rain",
                filePath = "/files/light_rain.mp3",
                intensityLevel = IntensityLevel.I,
                mixVolume = 0.6f,
            )
        )
        assertThat(viewModel.uiState.value.shouldNavigateBack).isTrue()
        assertThat(viewModel.uiState.value.hasUnsavedChanges).isFalse()
    }

    @Test
    fun requestNavigateBack_shows_discard_dialog_when_unsaved_changes_exist() = runTest {
        // Arrange
        val repository = FakeSoundscapeRepository()
        repository.tracksFlow.value = listOf(
            SoundscapeTrack(
                id = 8L,
                categoryId = 3L,
                name = "Light Rain",
                filePath = "/files/light_rain.mp3",
                intensityLevel = IntensityLevel.I,
                mixVolume = 1f,
            )
        )
        val viewModel = SoundscapeCategoryComposerViewModel(
            categoryId = 3L,
            soundscapeRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()
        viewModel.updateTrackMix(trackId = 8L, mixVolume = 0.6f)

        // Act
        viewModel.requestNavigateBack()

        // Assert
        assertThat(viewModel.uiState.value.isDiscardChangesDialogVisible).isTrue()
        assertThat(viewModel.uiState.value.shouldNavigateBack).isFalse()
    }

    @Test
    fun discardChanges_resets_the_draft_and_navigates_back() = runTest {
        // Arrange
        val repository = FakeSoundscapeRepository()
        repository.tracksFlow.value = listOf(
            SoundscapeTrack(
                id = 8L,
                categoryId = 3L,
                name = "Light Rain",
                filePath = "/files/light_rain.mp3",
                intensityLevel = IntensityLevel.I,
                mixVolume = 1f,
            )
        )
        val viewModel = SoundscapeCategoryComposerViewModel(
            categoryId = 3L,
            soundscapeRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()
        viewModel.updateTrackMix(trackId = 8L, mixVolume = 0.6f)
        viewModel.requestNavigateBack()

        // Act
        viewModel.discardChanges()

        // Assert
        assertThat(viewModel.uiState.value.tracks).containsExactlyElementsOf(repository.tracksFlow.value)
        assertThat(viewModel.uiState.value.shouldNavigateBack).isTrue()
        assertThat(viewModel.uiState.value.isDiscardChangesDialogVisible).isFalse()
    }

    private class FakeSoundscapeRepository : SoundscapeRepository {
        val categoryFlow = MutableStateFlow<SoundscapeCategory?>(null)
        val tracksFlow = MutableStateFlow<List<SoundscapeTrack>>(emptyList())

        val importRequests = mutableListOf<Pair<Long, String>>()
        val savedTracks = mutableListOf<List<SoundscapeTrack>>()

        override fun observeCategories(): Flow<List<SoundscapeCategory>> = MutableStateFlow(emptyList())

        override fun observeCategory(categoryId: Long): Flow<SoundscapeCategory?> = categoryFlow

        override fun observeTracks(categoryId: Long): Flow<List<SoundscapeTrack>> = tracksFlow

        override fun observeHasDemoSoundscapes(): Flow<Boolean> = MutableStateFlow(false)

        override suspend fun createCategory(name: String): Long = 0L

        override suspend fun deleteCategory(categoryId: Long) = Unit

        override suspend fun importTrack(categoryId: Long, sourceUri: String): SoundscapeTrack {
            importRequests += categoryId to sourceUri
            return SoundscapeTrack(
                id = 15L,
                categoryId = categoryId,
                name = "Thunderstorm",
                filePath = "/files/thunderstorm.mp3",
                intensityLevel = IntensityLevel.I,
                mixVolume = 1f,
            )
        }

        override suspend fun saveTracks(categoryId: Long, tracks: List<SoundscapeTrack>) {
            savedTracks += tracks
        }

        override suspend fun seedDemoSoundscapes() = Unit

        override suspend fun incrementTrackPlayCount(trackId: Long) = Unit
    }
}
