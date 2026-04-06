package com.example.rpgaudiomixer.ui.home

import app.cash.turbine.test
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
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
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val campaignRepository: CampaignRepository = mockk(relaxed = true)

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
        every { campaignRepository.observeAll() } returns flowOf(emptyList())

        // Act
        val viewModel = HomeViewModel(campaignRepository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(HomeUiState.Loading::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits NoCampaigns when campaign list is empty`() = runTest {
        // Arrange
        every { campaignRepository.observeAll() } returns flowOf(emptyList())

        // Act
        val viewModel = HomeViewModel(campaignRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(HomeUiState.NoCampaigns::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Success with most recently played campaign`() = runTest {
        // Arrange
        val oldCampaign = Campaign(id = 1, name = "Old Campaign", lastPlayedAt = 1000L)
        val recentCampaign = Campaign(id = 2, name = "Recent Campaign", lastPlayedAt = 5000L)
        every { campaignRepository.observeAll() } returns flowOf(listOf(oldCampaign, recentCampaign))

        // Act
        val viewModel = HomeViewModel(campaignRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(HomeUiState.Success::class.java)
            assertThat((state as HomeUiState.Success).activeCampaign).isEqualTo(recentCampaign)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
