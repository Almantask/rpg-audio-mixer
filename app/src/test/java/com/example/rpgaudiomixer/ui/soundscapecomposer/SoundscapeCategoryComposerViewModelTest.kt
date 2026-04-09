package com.example.rpgaudiomixer.ui.soundscapecomposer

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SoundscapeCategoryComposerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val soundscapeRepository: SoundscapeRepository = mockk()

    private lateinit var viewModel: SoundscapeCategoryComposerViewModel

    private val categoryId = "category-1"

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() {
        // Arrange
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns null

        // Act
        viewModel = SoundscapeCategoryComposerViewModel(soundscapeRepository, categoryId)

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(SoundscapeComposerUiState.Loading::class.java)
    }

    @Test
    fun `when category exists, state shows category and tracks`() = runTest {
        // Arrange
        val category = SoundscapeCategory(id = categoryId, name = "Forest")
        val tracks = listOf(
            SoundscapeTrack("1", categoryId, "Moonroots", "/path.mp3", IntensityLevel.I, 0.8f),
            SoundscapeTrack("2", categoryId, "Oak Forest", "/path.mp3", IntensityLevel.II, 1.0f)
        )
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns category
        coEvery { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(tracks)

        // Act
        viewModel = SoundscapeCategoryComposerViewModel(soundscapeRepository, categoryId)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as SoundscapeComposerUiState.Success
        assertThat(state.category.name).isEqualTo("Forest")
        assertThat(state.tracks).hasSize(2)
        assertThat(state.tracks[0].name).isEqualTo("Moonroots")
        assertThat(state.tracks[1].name).isEqualTo("Oak Forest")
    }

    @Test
    fun `when category not found, state shows error`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns null

        // Act
        viewModel = SoundscapeCategoryComposerViewModel(soundscapeRepository, categoryId)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as SoundscapeComposerUiState.Error
        assertThat(state.message).contains("not found")
    }

    @Test
    fun `createTrack creates a new track`() = runTest {
        // Arrange
        val category = SoundscapeCategory(id = categoryId, name = "Combat")
        val newTrack = SoundscapeTrack("new", categoryId, "Battle", "/path.mp3", IntensityLevel.III)
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns category
        coEvery { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        coEvery {
            soundscapeRepository.createTrack(categoryId, "Battle", "/path.mp3", IntensityLevel.III, 1.0f)
        } returns newTrack
        viewModel = SoundscapeCategoryComposerViewModel(soundscapeRepository, categoryId)
        advanceUntilIdle()

        // Act
        viewModel.createTrack("Battle", "/path.mp3", IntensityLevel.III, 1.0f)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            soundscapeRepository.createTrack(categoryId, "Battle", "/path.mp3", IntensityLevel.III, 1.0f)
        }
    }

    @Test
    fun `updateTrack updates existing track`() = runTest {
        // Arrange
        val category = SoundscapeCategory(id = categoryId, name = "Mystery")
        val track = SoundscapeTrack("1", categoryId, "Echoes", "/path.mp3", IntensityLevel.I, 0.7f)
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns category
        coEvery { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(listOf(track))
        coEvery { soundscapeRepository.updateTrack(any()) } returns Unit
        viewModel = SoundscapeCategoryComposerViewModel(soundscapeRepository, categoryId)
        advanceUntilIdle()

        val updatedTrack = track.copy(intensityLevel = IntensityLevel.II, mixVolume = 0.9f)

        // Act
        viewModel.updateTrack(updatedTrack)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { soundscapeRepository.updateTrack(updatedTrack) }
    }

    @Test
    fun `deleteTrack deletes the track`() = runTest {
        // Arrange
        val category = SoundscapeCategory(id = categoryId, name = "Boss")
        val track = SoundscapeTrack("1", categoryId, "Storm Ritual", "/path.mp3", IntensityLevel.I)
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns category
        coEvery { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(listOf(track))
        coEvery { soundscapeRepository.deleteTrack("1") } returns Unit
        viewModel = SoundscapeCategoryComposerViewModel(soundscapeRepository, categoryId)
        advanceUntilIdle()

        // Act
        viewModel.deleteTrack("1")
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { soundscapeRepository.deleteTrack("1") }
    }

    @Test
    fun `showImportDialog updates dialog state`() = runTest {
        // Arrange
        val category = SoundscapeCategory(id = categoryId, name = "Forest")
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns category
        coEvery { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        viewModel = SoundscapeCategoryComposerViewModel(soundscapeRepository, categoryId)

        // Act
        viewModel.showImportDialog()

        // Assert
        assertThat(viewModel.showImportDialog.value).isTrue()
    }

    @Test
    fun `hideImportDialog updates dialog state`() = runTest {
        // Arrange
        val category = SoundscapeCategory(id = categoryId, name = "Forest")
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns category
        coEvery { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        viewModel = SoundscapeCategoryComposerViewModel(soundscapeRepository, categoryId)
        viewModel.showImportDialog()

        // Act
        viewModel.hideImportDialog()

        // Assert
        assertThat(viewModel.showImportDialog.value).isFalse()
    }
}
