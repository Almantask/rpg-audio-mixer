package com.example.rpgaudiomixer.ui.library

import app.cash.turbine.test
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import com.example.rpgaudiomixer.ui.library.soundscapes.SoundscapeLibraryUiState
import com.example.rpgaudiomixer.ui.library.soundscapes.SoundscapeLibraryViewModel
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
class SoundscapeLibraryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: SoundscapeRepository = mockk(relaxed = true)

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
        every { repository.observeAllCategories() } returns flowOf(emptyList())

        // Act
        val viewModel = SoundscapeLibraryViewModel(repository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SoundscapeLibraryUiState.Loading::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Success with categories when repository returns data`() = runTest {
        // Arrange
        val categories = listOf(
            SoundscapeCategory(id = 1, name = "Weather"),
            SoundscapeCategory(id = 2, name = "Monsters"),
        )
        every { repository.observeAllCategories() } returns flowOf(categories)

        // Act
        val viewModel = SoundscapeLibraryViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(SoundscapeLibraryUiState.Success::class.java)
            assertThat((state as SoundscapeLibraryUiState.Success).categories).isEqualTo(categories)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteCategory calls repository deleteCategory`() = runTest {
        // Arrange
        every { repository.observeAllCategories() } returns flowOf(emptyList())
        val viewModel = SoundscapeLibraryViewModel(repository)

        // Act
        viewModel.deleteCategory(id = 5L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.deleteCategory(5L) }
    }
}
