import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }
package com.example.rpgaudiomixer.ui.scenes

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import app.cash.turbine.test

@OptIn(ExperimentalCoroutinesApi::class)
class ScenesViewModelTest {
    private val sceneRepository: SceneRepository = mockk(relaxed = true)

    @Test
    fun `emits loading then success with scenes`() = runTest {
        // Arrange
        val scenes = listOf(
            Scene(1, "Scene 1", null, listOf("tag1")),
            Scene(2, "Scene 2", null, listOf("tag2"))
        )
        coEvery { sceneRepository.observeAll() } returns flowOf(scenes)

        // Act
        val viewModel = ScenesViewModel(sceneRepository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(ScenesUiState.Loading::class.java)
            val success = awaitItem()
            assertThat(success).isInstanceOf(ScenesUiState.Success::class.java)
            assertThat((success as ScenesUiState.Success).scenes).isEqualTo(scenes)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits loading then success with no scenes`() = runTest {
        // Arrange
        coEvery { sceneRepository.observeAll() } returns flowOf(emptyList())

        // Act
        val viewModel = ScenesViewModel(sceneRepository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(ScenesUiState.Loading::class.java)
            val success = awaitItem()
            assertThat(success).isInstanceOf(ScenesUiState.Success::class.java)
            assertThat((success as ScenesUiState.Success).scenes).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits error when repository throws`() = runTest {
        // Arrange
        val errorMsg = "fail"
        coEvery { sceneRepository.observeAll() } returns kotlinx.coroutines.flow.flow { throw RuntimeException(errorMsg) }

        // Act
        val viewModel = ScenesViewModel(sceneRepository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(ScenesUiState.Loading::class.java)
            val error = awaitItem()
            assertThat(error).isInstanceOf(ScenesUiState.Error::class.java)
            assertThat((error as ScenesUiState.Error).message).contains(errorMsg)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
