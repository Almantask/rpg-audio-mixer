package com.example.rpgaudiomixer.ui.fx

import androidx.lifecycle.SavedStateHandle
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.repository.FxRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FxEditViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fxRepository: FxRepository = mockk(relaxed = true)
    private lateinit var viewModel: FxEditViewModel

    private val trackId = 1L
    private val sampleTrack = FxTrack(
        id = trackId,
        name = "Wolf Howl",
        filePath = "/path/to/wolf_howl.mp3",
        tags = listOf("Combat", "Nature"),
        createdAt = 1234567890L
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(trackId: Long = 1L): FxEditViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("trackId" to trackId))
        return FxEditViewModel(fxRepository, savedStateHandle)
    }

    @Test
    fun `initial state is Loading`() = runTest {
        // Arrange
        coEvery { fxRepository.getById(trackId) } returns sampleTrack

        // Act
        viewModel = createViewModel()

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(FxEditUiState.Loading::class.java)
    }

    @Test
    fun `loadTrack emits Success state with track`() = runTest {
        // Arrange
        coEvery { fxRepository.getById(trackId) } returns sampleTrack

        // Act
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxEditUiState.Success::class.java)
        assertThat((state as FxEditUiState.Success).track).isEqualTo(sampleTrack)
    }

    @Test
    fun `loadTrack emits Error state when track not found`() = runTest {
        // Arrange
        coEvery { fxRepository.getById(trackId) } returns null

        // Act
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxEditUiState.Error::class.java)
        assertThat((state as FxEditUiState.Error).message).isEqualTo("FX track not found")
    }

    @Test
    fun `loadTrack emits Error state when repository throws exception`() = runTest {
        // Arrange
        val errorMessage = "Database error"
        coEvery { fxRepository.getById(trackId) } throws RuntimeException(errorMessage)

        // Act
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxEditUiState.Error::class.java)
        assertThat((state as FxEditUiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `updateName updates track name in Success state`() = runTest {
        // Arrange
        coEvery { fxRepository.getById(trackId) } returns sampleTrack
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.updateName("New Name")

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxEditUiState.Success::class.java)
        assertThat((state as FxEditUiState.Success).track.name).isEqualTo("New Name")
    }

    @Test
    fun `addTag adds new tag to track`() = runTest {
        // Arrange
        coEvery { fxRepository.getById(trackId) } returns sampleTrack
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.addTag("Horror")

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxEditUiState.Success::class.java)
        assertThat((state as FxEditUiState.Success).track.tags).containsExactly("Combat", "Nature", "Horror")
    }

    @Test
    fun `addTag does not add duplicate tag`() = runTest {
        // Arrange
        coEvery { fxRepository.getById(trackId) } returns sampleTrack
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.addTag("Combat")

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxEditUiState.Success::class.java)
        assertThat((state as FxEditUiState.Success).track.tags).containsExactly("Combat", "Nature")
    }

    @Test
    fun `removeTag removes tag from track`() = runTest {
        // Arrange
        coEvery { fxRepository.getById(trackId) } returns sampleTrack
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.removeTag("Combat")

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxEditUiState.Success::class.java)
        assertThat((state as FxEditUiState.Success).track.tags).containsExactly("Nature")
    }

    @Test
    fun `save calls repository update with current track`() = runTest {
        // Arrange
        coEvery { fxRepository.getById(trackId) } returns sampleTrack
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateName("Updated Name")

        // Act
        viewModel.save()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            fxRepository.update(sampleTrack.copy(name = "Updated Name"))
        }
    }

    @Test
    fun `save emits Error state when repository throws exception`() = runTest {
        // Arrange
        coEvery { fxRepository.getById(trackId) } returns sampleTrack
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        val errorMessage = "Failed to save"
        coEvery { fxRepository.update(any()) } throws RuntimeException(errorMessage)

        // Act
        viewModel.save()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxEditUiState.Error::class.java)
        assertThat((state as FxEditUiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `delete calls repository delete`() = runTest {
        // Arrange
        coEvery { fxRepository.getById(trackId) } returns sampleTrack
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.delete()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { fxRepository.delete(trackId) }
    }

    @Test
    fun `delete emits Error state when repository throws exception`() = runTest {
        // Arrange
        coEvery { fxRepository.getById(trackId) } returns sampleTrack
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        val errorMessage = "Failed to delete"
        coEvery { fxRepository.delete(any()) } throws RuntimeException(errorMessage)

        // Act
        viewModel.delete()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxEditUiState.Error::class.java)
        assertThat((state as FxEditUiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `clearError reloads track when state is Error`() = runTest {
        // Arrange
        coEvery { fxRepository.getById(trackId) } returns sampleTrack
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { fxRepository.delete(any()) } throws RuntimeException("Error")
        viewModel.delete()
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxEditUiState.Success::class.java)
        assertThat((state as FxEditUiState.Success).track).isEqualTo(sampleTrack)
    }
}
