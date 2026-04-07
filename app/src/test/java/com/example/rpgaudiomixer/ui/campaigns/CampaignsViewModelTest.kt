package com.example.rpgaudiomixer.ui.campaigns

import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CampaignsViewModelTest {

    private val campaignRepository: CampaignRepository = mockk(relaxed = true)
    private lateinit var viewModel: CampaignsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `initial state is Loading`() {
        // Arrange
        every { campaignRepository.observeAll() } returns flowOf(emptyList())

        // Act
        viewModel = CampaignsViewModel(campaignRepository)

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(CampaignsUiState.Loading::class.java)
    }

    @Test
    fun `loadCampaigns updates state to Success with campaigns`() = runTest(testDispatcher) {
        // Arrange
        val campaigns = listOf(
            Campaign(1, "Campaign 1", null, 1000),
            Campaign(2, "Campaign 2", "uri", 2000)
        )
        every { campaignRepository.observeAll() } returns flowOf(campaigns)

        // Act
        viewModel = CampaignsViewModel(campaignRepository)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(CampaignsUiState.Success::class.java)
        assertThat((state as CampaignsUiState.Success).campaigns).isEqualTo(campaigns)
    }

    @Test
    fun `loadCampaigns updates state to Error when repository throws exception`() = runTest(testDispatcher) {
        // Arrange
        every { campaignRepository.observeAll() } returns flowOf()
        coEvery { campaignRepository.observeAll() } throws RuntimeException("Database error")

        // Act
        viewModel = CampaignsViewModel(campaignRepository)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(CampaignsUiState.Error::class.java)
        assertThat((state as CampaignsUiState.Error).message).contains("Database error")
    }

    @Test
    fun `createCampaign calls repository create`() = runTest(testDispatcher) {
        // Arrange
        every { campaignRepository.observeAll() } returns flowOf(emptyList())
        coEvery { campaignRepository.create(any(), any()) } returns 1L
        viewModel = CampaignsViewModel(campaignRepository)
        advanceUntilIdle()

        // Act
        viewModel.createCampaign("New Campaign", "cover_uri")
        advanceUntilIdle()

        // Assert
        coVerify { campaignRepository.create("New Campaign", "cover_uri") }
    }

    @Test
    fun `createCampaign updates state to Error when repository throws exception`() = runTest(testDispatcher) {
        // Arrange
        every { campaignRepository.observeAll() } returns flowOf(emptyList())
        coEvery { campaignRepository.create(any(), any()) } throws RuntimeException("Create failed")
        viewModel = CampaignsViewModel(campaignRepository)
        advanceUntilIdle()

        // Act
        viewModel.createCampaign("Test", null)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(CampaignsUiState.Error::class.java)
        assertThat((state as CampaignsUiState.Error).message).contains("Failed to create campaign")
    }

    @Test
    fun `deleteCampaign calls repository delete`() = runTest(testDispatcher) {
        // Arrange
        every { campaignRepository.observeAll() } returns flowOf(emptyList())
        coEvery { campaignRepository.delete(any()) } returns Unit
        viewModel = CampaignsViewModel(campaignRepository)
        advanceUntilIdle()

        // Act
        viewModel.deleteCampaign(5L)
        advanceUntilIdle()

        // Assert
        coVerify { campaignRepository.delete(5L) }
    }

    @Test
    fun `deleteCampaign updates state to Error when repository throws exception`() = runTest(testDispatcher) {
        // Arrange
        every { campaignRepository.observeAll() } returns flowOf(emptyList())
        coEvery { campaignRepository.delete(any()) } throws RuntimeException("Delete failed")
        viewModel = CampaignsViewModel(campaignRepository)
        advanceUntilIdle()

        // Act
        viewModel.deleteCampaign(5L)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(CampaignsUiState.Error::class.java)
        assertThat((state as CampaignsUiState.Error).message).contains("Failed to delete campaign")
    }
}
