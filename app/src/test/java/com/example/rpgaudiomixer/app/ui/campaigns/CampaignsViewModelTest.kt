package com.example.rpgaudiomixer.app.ui.campaigns

import com.example.rpgaudiomixer.domain.campaign.Campaign
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

@OptIn(ExperimentalCoroutinesApi::class)
class CampaignsViewModelTest {

    private val repository = mockk<CampaignRepository>(relaxed = true)
    private lateinit var viewModel: CampaignsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { repository.observeAll() } returns flowOf(emptyList())
        viewModel = CampaignsViewModel(repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state eventually becomes Success empty`() = runTest {
        val state = viewModel.uiState.first { it is CampaignsUiState.Success }
        assertThat(state).isInstanceOf(CampaignsUiState.Success::class.java)
        assertThat((state as CampaignsUiState.Success).campaigns).isEmpty()
    }

    @Test
    fun `observes campaigns from repository`() = runTest {
        val campaigns = listOf(Campaign(name = "Test Campaign"))
        every { repository.observeAll() } returns flowOf(campaigns)
        
        // Re-init to trigger collection of the new flow
        viewModel = CampaignsViewModel(repository)
        
        val state = viewModel.uiState.first { it is CampaignsUiState.Success }
        assertThat(state).isInstanceOf(CampaignsUiState.Success::class.java)
        assertThat((state as CampaignsUiState.Success).campaigns).containsExactlyElementsOf(campaigns)
    }

    @Test
    fun `createCampaign calls repository upsert`() = runTest {
        val name = "New Campaign"
        val coverUri = "uri"
        
        viewModel.createCampaign(name, coverUri)
        
        coVerify { repository.upsert(match { it.name == name && it.coverArtUri == coverUri }) }
    }

    @Test
    fun `deleteCampaign calls repository delete`() = runTest {
        val id = 123L
        
        viewModel.deleteCampaign(id)
        
        coVerify { repository.delete(id) }
    }
}
