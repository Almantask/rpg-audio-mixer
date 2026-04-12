package com.example.rpgaudiomixer.app.screens.campaigns

import com.example.rpgaudiomixer.app.domain.model.Campaign
import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
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
class CampaignsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val campaignFlow = MutableSharedFlow<List<Campaign>>(replay = 1)
    private val mockRepository: CampaignRepository = mockk {
        every { observeAll() } returns campaignFlow
        coEvery { createCampaign(any(), any()) } returns Unit
        coEvery { deleteCampaign(any()) } returns Unit
        coEvery { deleteAll() } returns Unit
    }

    private lateinit var viewModel: CampaignsViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CampaignsViewModel(mockRepository)
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
        assertThat(state).isEqualTo(CampaignsUiState.Loading)
    }

    @Test
    fun `uiState emits Success when repository emits campaigns`() = runTest(testDispatcher) {
        // Arrange
        val campaigns = listOf(Campaign(id = 1, name = "Dark Forest"))
        // WhileSubscribed(5000) only activates the upstream when there is an active subscriber
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        campaignFlow.emit(campaigns)

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(CampaignsUiState.Success(campaigns))
    }

    @Test
    fun `uiState emits Success with empty list when repository emits empty list`() = runTest(testDispatcher) {
        // Arrange
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        campaignFlow.emit(emptyList())

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(CampaignsUiState.Success(emptyList()))
    }

    @Test
    fun `uiState emits Error when repository throws`() = runTest(testDispatcher) {
        // Arrange
        val errorMessage = "DB failure"
        val errorRepository: CampaignRepository = mockk {
            every { observeAll() } returns flow { throw RuntimeException(errorMessage) }
        }
        val errorViewModel = CampaignsViewModel(errorRepository)
        backgroundScope.launch(UnconfinedTestDispatcher()) { errorViewModel.uiState.collect {} }

        // Assert
        assertThat(errorViewModel.uiState.value).isEqualTo(CampaignsUiState.Error(errorMessage))
    }

    @Test
    fun `createCampaign delegates to repository`() = runTest(testDispatcher) {
        // Act
        viewModel.createCampaign("Dragon Keep")

        // Assert
        coVerify { mockRepository.createCampaign("Dragon Keep", null) }
    }

    @Test
    fun `createCampaign with coverArtUri delegates to repository`() = runTest(testDispatcher) {
        // Act
        viewModel.createCampaign("Dark Forest", "content://cover/1")

        // Assert
        coVerify { mockRepository.createCampaign("Dark Forest", "content://cover/1") }
    }

    @Test
    fun `deleteCampaign delegates to repository`() = runTest(testDispatcher) {
        // Arrange
        val campaign = Campaign(id = 42, name = "Ancient Ruins")

        // Act
        viewModel.deleteCampaign(campaign)

        // Assert
        coVerify { mockRepository.deleteCampaign(campaign) }
    }
}
