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
package com.example.rpgaudiomixer.ui.sessions

import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import app.cash.turbine.test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionsViewModelTest {
    private val sessionRepository: SessionRepository = mockk(relaxed = true)

    @Test
    fun `emits loading then success with sessions`() = runTest {
        // Arrange
        val sessions = listOf(
            Session(1, 1, "S1", 123L),
            Session(2, 1, "S2", 456L)
        )
        coEvery { sessionRepository.observeByCampaign(1) } returns flowOf(sessions)

        // Act
        val viewModel = SessionsViewModel(sessionRepository)
        viewModel.loadSessions(1)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SessionsUiState.Loading::class.java)
            val success = awaitItem()
            assertThat(success).isInstanceOf(SessionsUiState.Success::class.java)
            assertThat((success as SessionsUiState.Success).sessions).isEqualTo(sessions)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits loading then success with no sessions`() = runTest {
        // Arrange
        coEvery { sessionRepository.observeByCampaign(1) } returns flowOf(emptyList())

        // Act
        val viewModel = SessionsViewModel(sessionRepository)
        viewModel.loadSessions(1)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SessionsUiState.Loading::class.java)
            val success = awaitItem()
            assertThat(success).isInstanceOf(SessionsUiState.Success::class.java)
            assertThat((success as SessionsUiState.Success).sessions).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits error when repository throws`() = runTest {
        // Arrange
        val errorMsg = "fail"
        coEvery { sessionRepository.observeByCampaign(1) } returns kotlinx.coroutines.flow.flow { throw RuntimeException(errorMsg) }

        // Act
        val viewModel = SessionsViewModel(sessionRepository)
        viewModel.loadSessions(1)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SessionsUiState.Loading::class.java)
            val error = awaitItem()
            assertThat(error).isInstanceOf(SessionsUiState.Error::class.java)
            assertThat((error as SessionsUiState.Error).message).contains(errorMsg)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
