package com.example.rpgaudiomixer.ui.campaigns

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
class CampaignsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val campaignRepository: CampaignRepository = mockk()

    private lateinit var viewModel: CampaignsViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() {
        // Arrange & Act
        viewModel = CampaignsViewModel(campaignRepository)

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(CampaignsUiState.Loading::class.java)
    }

    @Test
    fun `when campaigns exist, state shows campaign list`() = runTest {
        // Arrange
        val campaigns = listOf(
            Campaign(id = "1", name = "Campaign 1", lastPlayedAt = Instant.now()),
            Campaign(id = "2", name = "Campaign 2", lastPlayedAt = Instant.now().minusSeconds(3600))
        )
        coEvery { campaignRepository.getAllCampaigns() } returns campaigns

        // Act
        viewModel = CampaignsViewModel(campaignRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as CampaignsUiState.Success
        assertThat(state.campaigns).hasSize(2)
        assertThat(state.campaigns[0].name).isEqualTo("Campaign 1")
        assertThat(state.campaigns[1].name).isEqualTo("Campaign 2")
    }

    @Test
    fun `when no campaigns exist, state shows empty list`() = runTest {
        // Arrange
        coEvery { campaignRepository.getAllCampaigns() } returns emptyList()

        // Act
        viewModel = CampaignsViewModel(campaignRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as CampaignsUiState.Success
        assertThat(state.campaigns).isEmpty()
    }

    @Test
    fun `createCampaign calls repository and refreshes list`() = runTest {
        // Arrange
        val existingCampaigns = listOf(
            Campaign(id = "1", name = "Existing Campaign", lastPlayedAt = Instant.now())
        )
        val newCampaign = Campaign(id = "2", name = "New Campaign", lastPlayedAt = Instant.now())

        coEvery { campaignRepository.getAllCampaigns() } returns existingCampaigns andThen listOf(newCampaign, existingCampaigns[0])
        coEvery { campaignRepository.createCampaign("New Campaign") } returns newCampaign

        viewModel = CampaignsViewModel(campaignRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.createCampaign("New Campaign")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { campaignRepository.createCampaign("New Campaign") }
        val state = viewModel.uiState.value as CampaignsUiState.Success
        assertThat(state.campaigns).hasSize(2)
        assertThat(state.campaigns[0].name).isEqualTo("New Campaign")
    }

    @Test
    fun `deleteCampaign calls repository and refreshes list`() = runTest {
        // Arrange
        val campaigns = listOf(
            Campaign(id = "1", name = "Campaign 1", lastPlayedAt = Instant.now()),
            Campaign(id = "2", name = "Campaign 2", lastPlayedAt = Instant.now())
        )
        coEvery { campaignRepository.getAllCampaigns() } returns campaigns andThen listOf(campaigns[1])
        coEvery { campaignRepository.deleteCampaign("1") } returns Unit

        viewModel = CampaignsViewModel(campaignRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.deleteCampaign("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { campaignRepository.deleteCampaign("1") }
        val state = viewModel.uiState.value as CampaignsUiState.Success
        assertThat(state.campaigns).hasSize(1)
        assertThat(state.campaigns[0].id).isEqualTo("2")
    }

    @Test
    fun `when repository throws error, state shows error`() = runTest {
        // Arrange
        val errorMessage = "Database error"
        coEvery { campaignRepository.getAllCampaigns() } throws Exception(errorMessage)

        // Act
        viewModel = CampaignsViewModel(campaignRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as CampaignsUiState.Error
        assertThat(state.message).isEqualTo(errorMessage)
    }

    @Test
    fun `showCreateDialog updates dialog state`() {
        // Arrange
        coEvery { campaignRepository.getAllCampaigns() } returns emptyList()
        viewModel = CampaignsViewModel(campaignRepository)

        // Act
        viewModel.showCreateDialog()

        // Assert
        assertThat(viewModel.showCreateDialog.value).isTrue()
    }

    @Test
    fun `hideCreateDialog updates dialog state`() {
        // Arrange
        coEvery { campaignRepository.getAllCampaigns() } returns emptyList()
        viewModel = CampaignsViewModel(campaignRepository)
        viewModel.showCreateDialog()

        // Act
        viewModel.hideCreateDialog()

        // Assert
        assertThat(viewModel.showCreateDialog.value).isFalse()
    }
}
