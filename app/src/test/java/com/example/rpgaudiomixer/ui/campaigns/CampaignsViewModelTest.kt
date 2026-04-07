package com.example.rpgaudiomixer.ui.campaigns

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
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
    private val campaignRepository: CampaignRepository = mockk(relaxed = true)
    private lateinit var viewModel: CampaignsViewModel

    private val sampleCampaign1 = Campaign(
        id = 1L,
        name = "Lost Mines of Phandelver",
        coverArtUri = null,
        lastPlayedAt = 1234567890L
    )

    private val sampleCampaign2 = Campaign(
        id = 2L,
        name = "Curse of Strahd",
        coverArtUri = "content://image/123",
        lastPlayedAt = 1234567800L
    )

    @BeforeEach
    fun setup() {
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
        viewModel = CampaignsViewModel(campaignRepository)

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(CampaignsUiState.Loading::class.java)
    }

    @Test
    fun `loadCampaigns emits Success state with campaigns`() = runTest {
        // Arrange
        val campaigns = listOf(sampleCampaign1, sampleCampaign2)
        every { campaignRepository.observeAll() } returns flowOf(campaigns)

        // Act
        viewModel = CampaignsViewModel(campaignRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(CampaignsUiState.Success::class.java)
        assertThat((state as CampaignsUiState.Success).campaigns).isEqualTo(campaigns)
    }

    @Test
    fun `loadCampaigns emits Success state with empty list when no campaigns`() = runTest {
        // Arrange
        every { campaignRepository.observeAll() } returns flowOf(emptyList())

        // Act
        viewModel = CampaignsViewModel(campaignRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(CampaignsUiState.Success::class.java)
        assertThat((state as CampaignsUiState.Success).campaigns).isEmpty()
    }

    @Test
    fun `loadCampaigns emits Error state when repository throws exception`() = runTest {
        // Arrange
        val errorMessage = "Database error"
        every { campaignRepository.observeAll() } returns flowOf()
        coEvery { campaignRepository.observeAll() } throws RuntimeException(errorMessage)

        // Act
        viewModel = CampaignsViewModel(campaignRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(CampaignsUiState.Error::class.java)
        assertThat((state as CampaignsUiState.Error).message).contains(errorMessage)
    }

    @Test
    fun `createCampaign calls repository create with name and null coverUri`() = runTest {
        // Arrange
        every { campaignRepository.observeAll() } returns flowOf(emptyList())
        viewModel = CampaignsViewModel(campaignRepository)
        val campaignName = "Dragon Heist"

        // Act
        viewModel.createCampaign(campaignName)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { campaignRepository.create(campaignName, null) }
    }

    @Test
    fun `createCampaign calls repository create with name and coverUri`() = runTest {
        // Arrange
        every { campaignRepository.observeAll() } returns flowOf(emptyList())
        viewModel = CampaignsViewModel(campaignRepository)
        val campaignName = "Tomb of Annihilation"
        val coverUri = "content://image/456"

        // Act
        viewModel.createCampaign(campaignName, coverUri)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { campaignRepository.create(campaignName, coverUri) }
    }

    @Test
    fun `createCampaign emits Error state when repository throws exception`() = runTest {
        // Arrange
        every { campaignRepository.observeAll() } returns flowOf(emptyList())
        val errorMessage = "Failed to insert"
        coEvery { campaignRepository.create(any(), any()) } throws RuntimeException(errorMessage)
        viewModel = CampaignsViewModel(campaignRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.createCampaign("Test Campaign")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(CampaignsUiState.Error::class.java)
        assertThat((state as CampaignsUiState.Error).message).contains(errorMessage)
    }

    @Test
    fun `deleteCampaign calls repository delete with correct id`() = runTest {
        // Arrange
        every { campaignRepository.observeAll() } returns flowOf(listOf(sampleCampaign1))
        viewModel = CampaignsViewModel(campaignRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.deleteCampaign(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { campaignRepository.delete(1L) }
    }

    @Test
    fun `deleteCampaign emits Error state when repository throws exception`() = runTest {
        // Arrange
        every { campaignRepository.observeAll() } returns flowOf(listOf(sampleCampaign1))
        val errorMessage = "Failed to delete"
        coEvery { campaignRepository.delete(any()) } throws RuntimeException(errorMessage)
        viewModel = CampaignsViewModel(campaignRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.deleteCampaign(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(CampaignsUiState.Error::class.java)
        assertThat((state as CampaignsUiState.Error).message).contains(errorMessage)
    }

    @Test
    fun `clearError reloads campaigns when in Error state`() = runTest {
        // Arrange
        val campaigns = listOf(sampleCampaign1)
        every { campaignRepository.observeAll() } returns flowOf(campaigns)
        coEvery { campaignRepository.delete(any()) } throws RuntimeException("Error")
        viewModel = CampaignsViewModel(campaignRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Trigger error
        viewModel.deleteCampaign(1L)
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.uiState.value).isInstanceOf(CampaignsUiState.Error::class.java)

        // Act
        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(CampaignsUiState.Success::class.java)
        assertThat((state as CampaignsUiState.Success).campaigns).isEqualTo(campaigns)
    }

    @Test
    fun `clearError does nothing when not in Error state`() = runTest {
        // Arrange
        val campaigns = listOf(sampleCampaign1, sampleCampaign2)
        every { campaignRepository.observeAll() } returns flowOf(campaigns)
        viewModel = CampaignsViewModel(campaignRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val stateBefore = viewModel.uiState.value

        // Act
        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(stateBefore)
    }
}
