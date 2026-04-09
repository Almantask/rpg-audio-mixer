package com.example.rpgaudiomixer.app.ui.sessions

import androidx.lifecycle.SavedStateHandle
import com.example.rpgaudiomixer.domain.session.Session
import com.example.rpgaudiomixer.domain.session.SessionRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CampaignSessionsViewModelTest {

    private val repository = mockk<SessionRepository>(relaxed = true)
    private lateinit var viewModel: CampaignSessionsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    private val campaignId = 1L

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { repository.observeByCampaign(campaignId) } returns flowOf(emptyList())
        val savedStateHandle = SavedStateHandle(mapOf("campaignId" to campaignId))
        viewModel = CampaignSessionsViewModel(repository, savedStateHandle)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state eventually becomes Success empty`() = runTest {
        val state = viewModel.uiState.first { it is SessionsUiState.Success }
        assertThat(state).isInstanceOf(SessionsUiState.Success::class.java)
        assertThat((state as SessionsUiState.Success).sessions).isEmpty()
    }

    @Test
    fun `observes sessions for specific campaign`() = runTest {
        val sessions = listOf(Session(name = "Session 1", campaignId = campaignId, date = 123L))
        every { repository.observeByCampaign(campaignId) } returns flowOf(sessions)
        
        // Re-init to trigger collection
        val savedStateHandle = SavedStateHandle(mapOf("campaignId" to campaignId))
        viewModel = CampaignSessionsViewModel(repository, savedStateHandle)
        
        val state = viewModel.uiState.first { it is SessionsUiState.Success }
        assertThat(state).isInstanceOf(SessionsUiState.Success::class.java)
        assertThat((state as SessionsUiState.Success).sessions).containsExactlyElementsOf(sessions)
    }

    @Test
    fun `createSession calls repository upsert`() = runTest {
        val name = "New Session"
        
        viewModel.createSession(name)
        
        coVerify { repository.upsert(match { it.name == name && it.campaignId == campaignId }) }
    }

    @Test
    fun `deleteSession calls repository delete`() = runTest {
        val id = 123L
        
        viewModel.deleteSession(id)
        
        coVerify { repository.delete(id) }
    }
}
