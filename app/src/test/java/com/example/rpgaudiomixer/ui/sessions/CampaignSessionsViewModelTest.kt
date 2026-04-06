package com.example.rpgaudiomixer.ui.sessions

import app.cash.turbine.test
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.session.SessionRepository
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
class CampaignSessionsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: SessionRepository = mockk(relaxed = true)

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
        every { repository.observeByCampaign(any()) } returns flowOf(emptyList())

        // Act
        val viewModel = CampaignSessionsViewModel(repository, campaignId = 1L)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SessionsUiState.Loading::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Success with sessions when repository returns data`() = runTest {
        // Arrange
        val sessions = listOf(
            Session(id = 1, campaignId = 1L, name = "Session 1", date = 1000L),
            Session(id = 2, campaignId = 1L, name = "Session 2", date = 2000L),
        )
        every { repository.observeByCampaign(1L) } returns flowOf(sessions)

        // Act
        val viewModel = CampaignSessionsViewModel(repository, campaignId = 1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(SessionsUiState.Success::class.java)
            assertThat((state as SessionsUiState.Success).sessions).isEqualTo(sessions)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteSession calls repository delete`() = runTest {
        // Arrange
        every { repository.observeByCampaign(any()) } returns flowOf(emptyList())
        val viewModel = CampaignSessionsViewModel(repository, campaignId = 1L)

        // Act
        viewModel.deleteSession(id = 7L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.delete(7L) }
    }
}
