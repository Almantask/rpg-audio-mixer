package com.example.rpgaudiomixer.app.screens.sessions

import androidx.lifecycle.SavedStateHandle
import com.example.rpgaudiomixer.app.domain.model.Session
import com.example.rpgaudiomixer.app.domain.repository.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val campaignId = 42L
    private val sessionFlow = MutableSharedFlow<List<Session>>(replay = 1)
    private val mockRepository: SessionRepository = mockk {
        every { observeByCampaign(campaignId) } returns sessionFlow
        coEvery { createSession(any(), any(), any(), any()) } returns 1L
        coEvery { softDelete(any()) } returns Unit
        coEvery { deleteAll() } returns Unit
    }

    private val savedStateHandle = SavedStateHandle(mapOf("campaignId" to campaignId))

    private lateinit var viewModel: SessionsViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SessionsViewModel(mockRepository, savedStateHandle)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest(testDispatcher) {
        // Arrange — viewModel created in setUp, no emissions from repo yet

        // Act
        val state = viewModel.uiState.value

        // Assert
        assertThat(state).isEqualTo(SessionsUiState.Loading)
    }

    @Test
    fun `uiState emits Success when repository emits sessions`() = runTest(testDispatcher) {
        // Arrange
        val sessions = listOf(
            Session(id = 1, campaignId = campaignId, name = "Session 1", date = 1000L)
        )
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        sessionFlow.emit(sessions)

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(SessionsUiState.Success(sessions))
    }

    @Test
    fun `uiState emits Success with empty list when repository emits empty list`() = runTest(testDispatcher) {
        // Arrange
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        sessionFlow.emit(emptyList())

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(SessionsUiState.Success(emptyList()))
    }

    @Test
    fun `uiState emits Error when repository throws`() = runTest(testDispatcher) {
        // Arrange
        val errorMessage = "DB failure"
        val errorRepository: SessionRepository = mockk {
            every { observeByCampaign(campaignId) } returns flow {
                throw IllegalStateException(errorMessage)
            }
        }
        val errorViewModel = SessionsViewModel(errorRepository, savedStateHandle)
        backgroundScope.launch(UnconfinedTestDispatcher()) { errorViewModel.uiState.collect {} }

        // Act — state is collected above

        // Assert
        assertThat(errorViewModel.uiState.value).isEqualTo(SessionsUiState.Error(errorMessage))
    }

    @Test
    fun `createSession delegates to repository`() = runTest(testDispatcher) {
        // Arrange — viewModel created in setUp

        // Act
        viewModel.createSession("Dragon's Lair")

        // Assert
        coVerify { mockRepository.createSession(campaignId, "Dragon's Lair", null, any()) }
    }

    @Test
    fun `createSession with coverArtUri delegates to repository`() = runTest(testDispatcher) {
        // Arrange — viewModel created in setUp

        // Act
        viewModel.createSession("Dark Cavern", "content://cover/2")

        // Assert
        coVerify { mockRepository.createSession(campaignId, "Dark Cavern", "content://cover/2", any()) }
    }

    @Test
    fun `deleteSession delegates to repository with soft delete`() = runTest(testDispatcher) {
        // Arrange
        val sessionId = 7L

        // Act
        viewModel.deleteSession(sessionId)

        // Assert
        coVerify { mockRepository.softDelete(sessionId) }
    }
}
