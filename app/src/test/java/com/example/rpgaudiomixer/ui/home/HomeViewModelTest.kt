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
package com.example.rpgaudiomixer.ui.home

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import app.cash.turbine.test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val campaignRepository: CampaignRepository = mockk(relaxed = true)

    @Test
    fun `emits loading then success with campaign`() = runTest {
        // Arrange
        val campaign = Campaign(1, "Test Campaign", null, 123L)
        coEvery { campaignRepository.observeAll() } returns flowOf(listOf(campaign))

        // Act
        val viewModel = HomeViewModel(campaignRepository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(HomeUiState.Loading::class.java)
            val success = awaitItem()
            assertThat(success).isInstanceOf(HomeUiState.Success::class.java)
            assertThat((success as HomeUiState.Success).campaign).isEqualTo(campaign)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits loading then success with no campaign`() = runTest {
        // Arrange
        coEvery { campaignRepository.observeAll() } returns flowOf(emptyList())

        // Act
        val viewModel = HomeViewModel(campaignRepository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(HomeUiState.Loading::class.java)
            val success = awaitItem()
            assertThat(success).isInstanceOf(HomeUiState.Success::class.java)
            assertThat((success as HomeUiState.Success).campaign).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits error when repository throws`() = runTest {
        // Arrange
        val errorMsg = "fail"
        coEvery { campaignRepository.observeAll() } returns kotlinx.coroutines.flow.flow { throw RuntimeException(errorMsg) }

        // Act
        val viewModel = HomeViewModel(campaignRepository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(HomeUiState.Loading::class.java)
            val error = awaitItem()
            assertThat(error).isInstanceOf(HomeUiState.Error::class.java)
            assertThat((error as HomeUiState.Error).message).contains(errorMsg)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
