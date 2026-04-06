package com.example.rpgaudiomixer.ui.scenes

import app.cash.turbine.test
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
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
class ScenesFilterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: SceneRepository = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `filterByTag returns only scenes with that tag`() = runTest {
        // Arrange
        val scenes = listOf(
            Scene(id = 1, name = "Tavern Brawl", tags = listOf("Tavern", "Combat")),
            Scene(id = 2, name = "Dark Forest", tags = listOf("Forest")),
            Scene(id = 3, name = "Another Tavern", tags = listOf("Tavern")),
        )
        every { repository.observeAll() } returns flowOf(scenes)
        val viewModel = ScenesViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.filterByTag("Tavern")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(ScenesUiState.Success::class.java)
            val filtered = (state as ScenesUiState.Success).scenes
            assertThat(filtered).hasSize(2)
            assertThat(filtered.all { "Tavern" in it.tags }).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearFilter restores all scenes`() = runTest {
        // Arrange
        val scenes = listOf(
            Scene(id = 1, name = "Tavern Brawl", tags = listOf("Tavern")),
            Scene(id = 2, name = "Dark Forest", tags = listOf("Forest")),
        )
        every { repository.observeAll() } returns flowOf(scenes)
        val viewModel = ScenesViewModel(repository)
        viewModel.filterByTag("Tavern")
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.clearFilter()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat((state as ScenesUiState.Success).scenes).hasSize(2)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
