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
package com.example.rpgaudiomixer.ui.sessionscenes

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SessionScenesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import app.cash.turbine.test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionScenesViewModelTest {
    private val sessionScenesRepository: SessionScenesRepository = mockk(relaxed = true)

    @Test
    fun `emits loading then success with scenes`() = runTest {
        // Arrange
        val scenes = listOf(
            Scene(1, "Scene 1", null, listOf("tag1")),
            Scene(2, "Scene 2", null, listOf("tag2"))
        )
        coEvery { sessionScenesRepository.observeBySession(1) } returns flowOf(scenes)

        // Act
        val viewModel = SessionScenesViewModel(sessionScenesRepository)
        viewModel.loadScenes(1)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SessionScenesUiState.Loading::class.java)
            val success = awaitItem()
            assertThat(success).isInstanceOf(SessionScenesUiState.Success::class.java)
            assertThat((success as SessionScenesUiState.Success).scenes).isEqualTo(scenes)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits loading then success with no scenes`() = runTest {
        // Arrange
        coEvery { sessionScenesRepository.observeBySession(1) } returns flowOf(emptyList())

        // Act
        val viewModel = SessionScenesViewModel(sessionScenesRepository)
        viewModel.loadScenes(1)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SessionScenesUiState.Loading::class.java)
            val success = awaitItem()
            assertThat(success).isInstanceOf(SessionScenesUiState.Success::class.java)
            assertThat((success as SessionScenesUiState.Success).scenes).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits error when repository throws`() = runTest {
        // Arrange
        val errorMsg = "fail"
        coEvery { sessionScenesRepository.observeBySession(1) } returns kotlinx.coroutines.flow.flow { throw RuntimeException(errorMsg) }

        // Act
        val viewModel = SessionScenesViewModel(sessionScenesRepository)
        viewModel.loadScenes(1)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SessionScenesUiState.Loading::class.java)
            val error = awaitItem()
            assertThat(error).isInstanceOf(SessionScenesUiState.Error::class.java)
            assertThat((error as SessionScenesUiState.Error).message).contains(errorMsg)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
