package com.example.rpgaudiomixer.ui.sessions

import androidx.lifecycle.SavedStateHandle
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class SessionsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val sessionRepository: SessionRepository = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    private lateinit var viewModel: SessionsViewModel

    private val campaignId = "campaign-1"

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { savedStateHandle.get<String>("campaignId") } returns campaignId
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() {
        // Arrange & Act
        viewModel = SessionsViewModel(sessionRepository, savedStateHandle)

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(SessionsUiState.Loading::class.java)
    }

    @Test
    fun `when sessions exist for campaign, state shows session list sorted by date desc`() = runTest {
        // Arrange
        val now = Instant.now()
        val sessions = listOf(
            Session(id = "1", campaignId = campaignId, name = "Session 1", date = now),
            Session(id = "2", campaignId = campaignId, name = "Session 2", date = now.minusSeconds(3600))
        )
        coEvery { sessionRepository.getSessionsByCampaign(campaignId) } returns sessions

        // Act
        viewModel = SessionsViewModel(sessionRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as SessionsUiState.Success
        assertThat(state.sessions).hasSize(2)
        assertThat(state.sessions[0].name).isEqualTo("Session 1")
        assertThat(state.sessions[1].name).isEqualTo("Session 2")
    }

    @Test
    fun `when no sessions exist for campaign, state shows empty list`() = runTest {
        // Arrange
        coEvery { sessionRepository.getSessionsByCampaign(campaignId) } returns emptyList()

        // Act
        viewModel = SessionsViewModel(sessionRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as SessionsUiState.Success
        assertThat(state.sessions).isEmpty()
    }

    @Test
    fun `createSession calls repository with campaign ID and refreshes list`() = runTest {
        // Arrange
        val now = Instant.now()
        val existingSessions = listOf(
            Session(id = "1", campaignId = campaignId, name = "Existing Session", date = now)
        )
        val newSession = Session(id = "2", campaignId = campaignId, name = "New Session", date = now)

        coEvery { sessionRepository.getSessionsByCampaign(campaignId) } returns existingSessions andThen listOf(newSession, existingSessions[0])
        coEvery { sessionRepository.createSession(campaignId, "New Session") } returns newSession

        viewModel = SessionsViewModel(sessionRepository, campaignId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.createSession("New Session")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { sessionRepository.createSession(campaignId, "New Session") }
        val state = viewModel.uiState.value as SessionsUiState.Success
        assertThat(state.sessions).hasSize(2)
        assertThat(state.sessions[0].name).isEqualTo("New Session")
    }

    @Test
    fun `deleteSession calls repository and refreshes list`() = runTest {
        // Arrange
        val now = Instant.now()
        val sessions = listOf(
            Session(id = "1", campaignId = campaignId, name = "Session 1", date = now),
            Session(id = "2", campaignId = campaignId, name = "Session 2", date = now.minusSeconds(3600))
        )
        coEvery { sessionRepository.getSessionsByCampaign(campaignId) } returns sessions andThen listOf(sessions[1])
        coEvery { sessionRepository.deleteSession("1") } returns Unit

        viewModel = SessionsViewModel(sessionRepository, campaignId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.deleteSession("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { sessionRepository.deleteSession("1") }
        val state = viewModel.uiState.value as SessionsUiState.Success
        assertThat(state.sessions).hasSize(1)
        assertThat(state.sessions[0].id).isEqualTo("2")
    }

    @Test
    fun `when repository throws error, state shows error`() = runTest {
        // Arrange
        val errorMessage = "Database error"
        coEvery { sessionRepository.getSessionsByCampaign(campaignId) } throws Exception(errorMessage)

        // Act
        viewModel = SessionsViewModel(sessionRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as SessionsUiState.Error
        assertThat(state.message).isEqualTo(errorMessage)
    }

    @Test
    fun `showCreateDialog updates dialog state`() {
        // Arrange
        coEvery { sessionRepository.getSessionsByCampaign(campaignId) } returns emptyList()
        viewModel = SessionsViewModel(sessionRepository, campaignId)

        // Act
        viewModel.showCreateDialog()

        // Assert
        assertThat(viewModel.showCreateDialog.value).isTrue()
    }

    @Test
    fun `hideCreateDialog updates dialog state`() {
        // Arrange
        coEvery { sessionRepository.getSessionsByCampaign(campaignId) } returns emptyList()
        viewModel = SessionsViewModel(sessionRepository, campaignId)
        viewModel.showCreateDialog()

        // Act
        viewModel.hideCreateDialog()

        // Assert
        assertThat(viewModel.showCreateDialog.value).isFalse()
    }
}
