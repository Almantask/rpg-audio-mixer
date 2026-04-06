package com.example.rpgaudiomixer.ui.campaigns

import app.cash.turbine.test
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
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CampaignsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: CampaignRepository = mockk(relaxed = true)

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
        every { repository.observeAll() } returns flowOf(emptyList())

        // Act
        val viewModel = CampaignsViewModel(repository)

        // Assert
        viewModel.uiState.test {
            val first = awaitItem()
            assertThat(first).isInstanceOf(CampaignsUiState.Loading::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Success with campaigns when repository returns data`() = runTest {
        // Arrange
        val campaigns = listOf(
            Campaign(id = 1, name = "The Lost Mines", lastPlayedAt = 1000L),
            Campaign(id = 2, name = "Curse of Strahd", lastPlayedAt = 2000L),
        )
        every { repository.observeAll() } returns flowOf(campaigns)

        // Act
        val viewModel = CampaignsViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(CampaignsUiState.Success::class.java)
            assertThat((state as CampaignsUiState.Success).campaigns).isEqualTo(campaigns)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Success with empty list when repository returns empty`() = runTest {
        // Arrange
        every { repository.observeAll() } returns flowOf(emptyList())

        // Act
        val viewModel = CampaignsViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(CampaignsUiState.Success::class.java)
            assertThat((state as CampaignsUiState.Success).campaigns).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `createCampaign calls repository create with correct arguments`() = runTest {
        // Arrange
        every { repository.observeAll() } returns flowOf(emptyList())
        coEvery { repository.create(any(), any()) } returns Campaign(id = 1, name = "New Campaign")
        val viewModel = CampaignsViewModel(repository)

        // Act
        viewModel.createCampaign(name = "New Campaign", coverArtUri = null)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.create("New Campaign", null) }
    }

    @Test
    fun `deleteCampaign calls repository delete with correct id`() = runTest {
        // Arrange
        every { repository.observeAll() } returns flowOf(emptyList())
        val viewModel = CampaignsViewModel(repository)

        // Act
        viewModel.deleteCampaign(id = 42L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.delete(42L) }
    }
}
