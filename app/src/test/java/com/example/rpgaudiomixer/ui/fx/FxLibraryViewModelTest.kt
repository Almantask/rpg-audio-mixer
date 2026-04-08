package com.example.rpgaudiomixer.ui.fx

import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.repository.FxRepository
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
class FxLibraryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fxRepository: FxRepository = mockk(relaxed = true)
    private lateinit var viewModel: FxLibraryViewModel

    private val sampleTrack1 = FxTrack(
        id = 1L,
        name = "Wolf Howl",
        filePath = "/path/to/wolf_howl.mp3",
        tags = listOf("Combat", "Nature"),
        createdAt = 1234567890L
    )

    private val sampleTrack2 = FxTrack(
        id = 2L,
        name = "Thunder Crack",
        filePath = "/path/to/thunder.mp3",
        tags = listOf("Nature", "Weather"),
        createdAt = 1234567800L
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        // Arrange
        every { fxRepository.observeAll() } returns flowOf(emptyList())

        // Act
        viewModel = FxLibraryViewModel(fxRepository)

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(FxLibraryUiState.Loading::class.java)
    }

    @Test
    fun `loadFxTracks emits Success state with tracks`() = runTest {
        // Arrange
        val tracks = listOf(sampleTrack1, sampleTrack2)
        every { fxRepository.observeAll() } returns flowOf(tracks)

        // Act
        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxLibraryUiState.Success::class.java)
        assertThat((state as FxLibraryUiState.Success).fxTracks).isEqualTo(tracks)
    }

    @Test
    fun `loadFxTracks emits Success state with empty list when no tracks`() = runTest {
        // Arrange
        every { fxRepository.observeAll() } returns flowOf(emptyList())

        // Act
        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxLibraryUiState.Success::class.java)
        assertThat((state as FxLibraryUiState.Success).fxTracks).isEmpty()
    }

    @Test
    fun `loadFxTracks emits Error state when repository throws exception`() = runTest {
        // Arrange
        val errorMessage = "Database error"
        every { fxRepository.observeAll() } returns flowOf()
        coEvery { fxRepository.observeAll() } throws RuntimeException(errorMessage)

        // Act
        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxLibraryUiState.Error::class.java)
        assertThat((state as FxLibraryUiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `search with blank query calls observeAll`() = runTest {
        // Arrange
        every { fxRepository.observeAll() } returns flowOf(emptyList())
        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.search("")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertThat(viewModel.searchQuery.value).isEqualTo("")
    }

    @Test
    fun `search with query calls repository search`() = runTest {
        // Arrange
        every { fxRepository.observeAll() } returns flowOf(emptyList())
        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        val searchResults = listOf(sampleTrack1)
        every { fxRepository.search("wolf") } returns flowOf(searchResults)

        // Act
        viewModel.search("wolf")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertThat(viewModel.searchQuery.value).isEqualTo("wolf")
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxLibraryUiState.Success::class.java)
        assertThat((state as FxLibraryUiState.Success).fxTracks).isEqualTo(searchResults)
    }

    @Test
    fun `search emits Error state when repository throws exception`() = runTest {
        // Arrange
        every { fxRepository.observeAll() } returns flowOf(emptyList())
        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        val errorMessage = "Search failed"
        every { fxRepository.search(any()) } returns flowOf()
        coEvery { fxRepository.search(any()) } throws RuntimeException(errorMessage)

        // Act
        viewModel.search("test")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxLibraryUiState.Error::class.java)
        assertThat((state as FxLibraryUiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `importFxTrack calls repository create`() = runTest {
        // Arrange
        every { fxRepository.observeAll() } returns flowOf(emptyList())
        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { fxRepository.create(any(), any(), any()) } returns 3L

        // Act
        viewModel.importFxTrack("New Track", "/path/to/track.mp3", listOf("Combat"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            fxRepository.create("New Track", "/path/to/track.mp3", listOf("Combat"))
        }
    }

    @Test
    fun `importFxTrack emits Error state when repository throws exception`() = runTest {
        // Arrange
        every { fxRepository.observeAll() } returns flowOf(emptyList())
        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        val errorMessage = "Failed to import"
        coEvery { fxRepository.create(any(), any(), any()) } throws RuntimeException(errorMessage)

        // Act
        viewModel.importFxTrack("New Track", "/path/to/track.mp3")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxLibraryUiState.Error::class.java)
        assertThat((state as FxLibraryUiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `deleteFxTrack calls repository delete`() = runTest {
        // Arrange
        every { fxRepository.observeAll() } returns flowOf(emptyList())
        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.deleteFxTrack(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { fxRepository.delete(1L) }
    }

    @Test
    fun `deleteFxTrack emits Error state when repository throws exception`() = runTest {
        // Arrange
        every { fxRepository.observeAll() } returns flowOf(emptyList())
        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        val errorMessage = "Failed to delete"
        coEvery { fxRepository.delete(any()) } throws RuntimeException(errorMessage)

        // Act
        viewModel.deleteFxTrack(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxLibraryUiState.Error::class.java)
        assertThat((state as FxLibraryUiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `clearError reloads tracks when state is Error`() = runTest {
        // Arrange
        every { fxRepository.observeAll() } returns flowOf(emptyList())
        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { fxRepository.delete(any()) } throws RuntimeException("Error")
        viewModel.deleteFxTrack(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FxLibraryUiState.Success::class.java)
    }
}
