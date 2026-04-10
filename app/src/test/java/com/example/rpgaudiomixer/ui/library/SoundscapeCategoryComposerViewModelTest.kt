package com.example.rpgaudiomixer.ui.library

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import com.example.rpgaudiomixer.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class SoundscapeCategoryComposerViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun addImportedTrack_defaults_to_intensity_i_and_full_mix() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSoundscapeRepository()
        val viewModel = SoundscapeCategoryComposerViewModel(categoryId = 4L, repository = repository)
        advanceUntilIdle()

        // Act
        viewModel.addImportedTrack(name = "thunderstorm.mp3", filePath = "content://thunderstorm")
        advanceUntilIdle()

        // Assert
        val successState = viewModel.uiState.value as SoundscapeCategoryComposerUiState.Success
        assertThat(successState.tracks).containsExactly(
            SoundscapeTrack(
                id = 1L,
                categoryId = 4L,
                name = "thunderstorm.mp3",
                filePath = "content://thunderstorm",
                intensityLevel = IntensityLevel.I,
                mixVolumePercent = 100,
                displayOrder = 0,
            ),
        )
    }

    @Test
    fun saveComposition_persists_the_modified_tracks_and_clears_unsaved_changes() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSoundscapeRepository(
            initialTracks = listOf(
                SoundscapeTrack(
                    id = 1L,
                    categoryId = 4L,
                    name = "light_rain.mp3",
                    filePath = "content://light-rain",
                    intensityLevel = IntensityLevel.I,
                    mixVolumePercent = 100,
                    displayOrder = 0,
                ),
            ),
        )
        val viewModel = SoundscapeCategoryComposerViewModel(categoryId = 4L, repository = repository)
        advanceUntilIdle()

        // Act
        viewModel.updateTrackIntensity(trackId = 1L, intensityLevel = IntensityLevel.III)
        viewModel.updateTrackMix(trackId = 1L, mixVolumePercent = 60)
        viewModel.saveComposition()
        advanceUntilIdle()

        // Assert
        val savedTrack = repository.savedTracks.single()
        assertThat(savedTrack.intensityLevel).isEqualTo(IntensityLevel.III)
        assertThat(savedTrack.mixVolumePercent).isEqualTo(60)
        assertThat((viewModel.uiState.value as SoundscapeCategoryComposerUiState.Success).hasUnsavedChanges).isFalse()
    }

    private class FakeSoundscapeRepository(
        initialTracks: List<SoundscapeTrack> = emptyList(),
    ) : SoundscapeRepository {
        private val categoryFlow = MutableStateFlow(
            SoundscapeCategory(
                id = 4L,
                name = "Weather",
                themeLabel = "Environment",
                iconResId = null,
                isDemoContent = false,
                levelOneCount = 0,
                levelTwoCount = 0,
                levelThreeCount = 0,
            ),
        )
        private val trackFlow = MutableStateFlow(initialTracks)
        val savedTracks: List<SoundscapeTrack>
            get() = trackFlow.value

        override fun observeCategories(): Flow<List<SoundscapeCategory>> = flowOf(listOf(categoryFlow.value))

        override fun observeCategory(categoryId: Long): Flow<SoundscapeCategory?> = categoryFlow

        override fun observeTracks(categoryId: Long): Flow<List<SoundscapeTrack>> = trackFlow

        override fun observeMostPlayedTrack(): Flow<com.example.rpgaudiomixer.domain.model.MostPlayedSoundscapeTrack?> {
            return MutableStateFlow(null)
        }

        override suspend fun createCategory(name: String): Long = 4L

        override suspend fun deleteCategory(categoryId: Long) = Unit

        override suspend fun saveTracks(categoryId: Long, tracks: List<SoundscapeTrack>) {
            trackFlow.value = tracks
        }

        override suspend fun installDemoSoundscapes() = Unit
    }
}
