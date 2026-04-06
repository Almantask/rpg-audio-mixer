package com.example.rpgaudiomixer.ui.scenes

import app.cash.turbine.test
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
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
class ScenesViewModelTest {

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
    fun `initial state is Loading`() = runTest {
        // Arrange
        every { repository.observeAll() } returns flowOf(emptyList())

        // Act
        val viewModel = ScenesViewModel(repository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(ScenesUiState.Loading::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Success with scenes when repository returns data`() = runTest {
        // Arrange
        val scenes = listOf(
            Scene(id = 1, name = "Tavern Brawl", tags = listOf("Tavern", "Combat")),
            Scene(id = 2, name = "Dark Forest", tags = listOf("Forest")),
        )
        every { repository.observeAll() } returns flowOf(scenes)

        // Act
        val viewModel = ScenesViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(ScenesUiState.Success::class.java)
            assertThat((state as ScenesUiState.Success).scenes).isEqualTo(scenes)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteScene calls repository delete with correct id`() = runTest {
        // Arrange
        every { repository.observeAll() } returns flowOf(emptyList())
        val viewModel = ScenesViewModel(repository)

        // Act
        viewModel.deleteScene(id = 99L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.delete(99L) }
    }
}
