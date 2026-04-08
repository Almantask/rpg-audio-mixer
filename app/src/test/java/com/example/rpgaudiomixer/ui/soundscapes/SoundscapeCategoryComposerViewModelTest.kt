package com.example.rpgaudiomixer.ui.soundscapes

import androidx.lifecycle.SavedStateHandle
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
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
    private val soundscapeRepository: SoundscapeRepository = mockk(relaxed = true)
    private lateinit var viewModel: SoundscapeCategoryComposerViewModel

    private val categoryId = 1L
    private val sampleCategory = SoundscapeCategory(
        id = categoryId,
        name = "Forest",
        iconResId = null,
        themeLabel = null
    )

    private val sampleTrack1 = SoundscapeTrack(
        id = 1L,
        categoryId = categoryId,
        name = "birds.mp3",
        filePath = "/path/to/birds.mp3",
        intensityLevel = IntensityLevel.I,
        mixVolume = 0.75f
    )

    private val sampleTrack2 = SoundscapeTrack(
        id = 2L,
        categoryId = categoryId,
        name = "wind.mp3",
        filePath = "/path/to/wind.mp3",
        intensityLevel = IntensityLevel.II,
        mixVolume = 0.60f
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(categoryId: Long = 1L): SoundscapeCategoryComposerViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("categoryId" to categoryId))
        return SoundscapeCategoryComposerViewModel(soundscapeRepository, savedStateHandle)
    }

    @Test
    fun `initial state is Loading`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns sampleCategory
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())

        // Act
        viewModel = createViewModel()

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(SoundscapeCategoryComposerUiState.Loading::class.java)
    }

    @Test
    fun `loadCategoryAndTracks emits Success state with category and tracks`() = runTest {
        // Arrange
        val tracks = listOf(sampleTrack1, sampleTrack2)
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns sampleCategory
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(tracks)

        // Act
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(SoundscapeCategoryComposerUiState.Success::class.java)
        val successState = state as SoundscapeCategoryComposerUiState.Success
        assertThat(successState.category).isEqualTo(sampleCategory)
        assertThat(successState.tracks).isEqualTo(tracks)
    }

    @Test
    fun `loadCategoryAndTracks emits Success state with empty tracks list`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns sampleCategory
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())

        // Act
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(SoundscapeCategoryComposerUiState.Success::class.java)
        val successState = state as SoundscapeCategoryComposerUiState.Success
        assertThat(successState.tracks).isEmpty()
    }

    @Test
    fun `loadCategoryAndTracks emits Error state when category not found`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns null

        // Act
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(SoundscapeCategoryComposerUiState.Error::class.java)
        assertThat((state as SoundscapeCategoryComposerUiState.Error).message).isEqualTo("Category not found")
    }

    @Test
    fun `loadCategoryAndTracks emits Error state when repository throws exception`() = runTest {
        // Arrange
        val errorMessage = "Database error"
        coEvery { soundscapeRepository.getCategoryById(categoryId) } throws RuntimeException(errorMessage)

        // Act
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(SoundscapeCategoryComposerUiState.Error::class.java)
        assertThat((state as SoundscapeCategoryComposerUiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `addTrack calls repository createTrack with correct parameters`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns sampleCategory
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { soundscapeRepository.createTrack(any(), any(), any(), any(), any()) } returns 3L

        // Act
        viewModel.addTrack("thunder.mp3", "/path/to/thunder.mp3", IntensityLevel.III, 0.80f)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            soundscapeRepository.createTrack(categoryId, "thunder.mp3", "/path/to/thunder.mp3", IntensityLevel.III, 0.80f)
        }
    }

    @Test
    fun `addTrack uses default mixVolume when not specified`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns sampleCategory
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { soundscapeRepository.createTrack(any(), any(), any(), any(), any()) } returns 3L

        // Act
        viewModel.addTrack("thunder.mp3", "/path/to/thunder.mp3", IntensityLevel.III)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            soundscapeRepository.createTrack(categoryId, "thunder.mp3", "/path/to/thunder.mp3", IntensityLevel.III, 0.75f)
        }
    }

    @Test
    fun `addTrack emits Error state when repository throws exception`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns sampleCategory
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        val errorMessage = "Failed to add track"
        coEvery { soundscapeRepository.createTrack(any(), any(), any(), any(), any()) } throws RuntimeException(errorMessage)

        // Act
        viewModel.addTrack("thunder.mp3", "/path/to/thunder.mp3", IntensityLevel.III)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(SoundscapeCategoryComposerUiState.Error::class.java)
        assertThat((state as SoundscapeCategoryComposerUiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `updateTrackIntensity calls repository updateTrack with new intensity`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns sampleCategory
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.updateTrackIntensity(sampleTrack1, IntensityLevel.III)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            soundscapeRepository.updateTrack(sampleTrack1.copy(intensityLevel = IntensityLevel.III))
        }
    }

    @Test
    fun `updateTrackIntensity emits Error state when repository throws exception`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns sampleCategory
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        val errorMessage = "Failed to update intensity"
        coEvery { soundscapeRepository.updateTrack(any()) } throws RuntimeException(errorMessage)

        // Act
        viewModel.updateTrackIntensity(sampleTrack1, IntensityLevel.III)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(SoundscapeCategoryComposerUiState.Error::class.java)
        assertThat((state as SoundscapeCategoryComposerUiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `updateTrackVolume calls repository updateTrack with new volume`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns sampleCategory
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.updateTrackVolume(sampleTrack1, 0.90f)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            soundscapeRepository.updateTrack(sampleTrack1.copy(mixVolume = 0.90f))
        }
    }

    @Test
    fun `updateTrackVolume emits Error state when repository throws exception`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns sampleCategory
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        val errorMessage = "Failed to update volume"
        coEvery { soundscapeRepository.updateTrack(any()) } throws RuntimeException(errorMessage)

        // Act
        viewModel.updateTrackVolume(sampleTrack1, 0.90f)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(SoundscapeCategoryComposerUiState.Error::class.java)
        assertThat((state as SoundscapeCategoryComposerUiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `deleteTrack calls repository deleteTrack`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns sampleCategory
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.deleteTrack(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { soundscapeRepository.deleteTrack(1L) }
    }

    @Test
    fun `deleteTrack emits Error state when repository throws exception`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns sampleCategory
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        val errorMessage = "Failed to delete track"
        coEvery { soundscapeRepository.deleteTrack(any()) } throws RuntimeException(errorMessage)

        // Act
        viewModel.deleteTrack(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(SoundscapeCategoryComposerUiState.Error::class.java)
        assertThat((state as SoundscapeCategoryComposerUiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `clearError reloads category and tracks when state is Error`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.getCategoryById(categoryId) } returns sampleCategory
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { soundscapeRepository.deleteTrack(any()) } throws RuntimeException("Error")
        viewModel.deleteTrack(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(SoundscapeCategoryComposerUiState.Success::class.java)
    }
}
