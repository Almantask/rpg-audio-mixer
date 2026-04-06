package com.example.rpgaudiomixer.ui.library.fx

import app.cash.turbine.test
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.FxTrack
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
    private val repository: FxRepository = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        // Arrange
        every { repository.observeAll() } returns flowOf(emptyList())
        every { repository.search(any()) } returns flowOf(emptyList())

        // Act
        val viewModel = FxLibraryViewModel(repository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(FxLibraryUiState.Loading::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Success with tracks when repository returns data`() = runTest {
        // Arrange
        val tracks = listOf(
            FxTrack(id = 1, name = "Thunder Crack", filePath = "/audio/thunder.mp3"),
            FxTrack(id = 2, name = "Wolf Howl", filePath = "/audio/wolf.mp3"),
        )
        every { repository.observeAll() } returns flowOf(tracks)
        every { repository.search(any()) } returns flowOf(emptyList())

        // Act
        val viewModel = FxLibraryViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(FxLibraryUiState.Success::class.java)
            assertThat((state as FxLibraryUiState.Success).tracks).isEqualTo(tracks)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteTrack calls repository delete`() = runTest {
        // Arrange
        every { repository.observeAll() } returns flowOf(emptyList())
        every { repository.search(any()) } returns flowOf(emptyList())
        val viewModel = FxLibraryViewModel(repository)

        // Act
        viewModel.deleteTrack(id = 11L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.delete(11L) }
    }

    @Test
    fun `search uses search query to filter tracks`() = runTest {
        // Arrange
        val filteredTracks = listOf(FxTrack(id = 3, name = "Thunder", filePath = "/a"))
        every { repository.observeAll() } returns flowOf(emptyList())
        every { repository.search("Thunder") } returns flowOf(filteredTracks)

        val viewModel = FxLibraryViewModel(repository)

        // Act
        viewModel.search("Thunder")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(FxLibraryUiState.Success::class.java)
            assertThat((state as FxLibraryUiState.Success).tracks).isEqualTo(filteredTracks)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
