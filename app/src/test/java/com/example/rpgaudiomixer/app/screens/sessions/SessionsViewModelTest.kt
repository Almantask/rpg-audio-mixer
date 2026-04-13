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
    private val sessionFlow = MutableSharedFlow<List<Session>>(replay = 1)
    private val mockRepository: SessionRepository = mockk {
        every { observeByCampaign(any()) } returns sessionFlow
        coEvery { createSession(any(), any()) } returns Unit
        coEvery { deleteSession(any()) } returns Unit
        coEvery { deleteAll() } returns Unit
    }

    private val savedStateHandle = SavedStateHandle(mapOf("campaignId" to 42L))
    private lateinit var viewModel: SessionsViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SessionsViewModel(savedStateHandle, mockRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest(testDispatcher) {
        // Arrange — viewModel created in setUp, no emissions yet

        // Act
        val state = viewModel.uiState.value

        // Assert
        assertThat(state).isEqualTo(SessionsUiState.Loading)
    }

    @Test
    fun `uiState emits Success when repository emits sessions`() = runTest(testDispatcher) {
        // Arrange
        val sessions = listOf(Session(id = 1, campaignId = 42, name = "Battle Scene"))
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
        val errorRepo: SessionRepository = mockk {
            every { observeByCampaign(any()) } returns flow { throw IllegalStateException(errorMessage) }
        }
        val errorViewModel = SessionsViewModel(savedStateHandle, errorRepo)
        backgroundScope.launch(UnconfinedTestDispatcher()) { errorViewModel.uiState.collect {} }

        // Assert
        assertThat(errorViewModel.uiState.value).isEqualTo(SessionsUiState.Error(errorMessage))
    }

    @Test
    fun `createSession delegates to repository with correct campaignId`() = runTest(testDispatcher) {
        // Act
        viewModel.createSession("Dungeon Crawl")

        // Assert
        coVerify { mockRepository.createSession(42L, "Dungeon Crawl") }
    }

    @Test
    fun `deleteSession delegates to repository`() = runTest(testDispatcher) {
        // Arrange
        val session = Session(id = 7, campaignId = 42, name = "Ancient Lair")

        // Act
        viewModel.deleteSession(session)

        // Assert
        coVerify { mockRepository.deleteSession(session) }
    }
}
