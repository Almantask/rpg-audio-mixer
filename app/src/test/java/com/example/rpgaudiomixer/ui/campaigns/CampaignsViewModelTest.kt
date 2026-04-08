package com.example.rpgaudiomixer.ui.campaigns

import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.ui.common.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CampaignsViewModelTest {

    @Test
    fun init_exposes_success_with_empty_campaigns_when_repository_is_empty() = runTest {
        // Arrange
        val repository = FakeCampaignRepository()

        // Act
        val viewModel = CampaignsViewModel(
            campaignRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(UiState.Success(emptyList<Campaign>()))
    }

    @Test
    fun uiState_updates_when_the_repository_emits_campaigns() = runTest {
        // Arrange
        val repository = FakeCampaignRepository()
        val viewModel = CampaignsViewModel(
            campaignRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        repository.emitCampaigns(
            listOf(
                Campaign(id = 2L, name = "Curse of Strahd", coverArtUri = null, lastPlayedAt = 200L),
                Campaign(id = 1L, name = "The Shattered Throne", coverArtUri = null, lastPlayedAt = 100L),
            )
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(
            UiState.Success(
                listOf(
                    Campaign(id = 2L, name = "Curse of Strahd", coverArtUri = null, lastPlayedAt = 200L),
                    Campaign(id = 1L, name = "The Shattered Throne", coverArtUri = null, lastPlayedAt = 100L),
                )
            )
        )
    }

    @Test
    fun createCampaign_trims_the_name_and_delegates_to_the_repository() = runTest {
        // Arrange
        val repository = FakeCampaignRepository()
        val viewModel = CampaignsViewModel(
            campaignRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.createCampaign("  The Shattered Throne  ", "content://cover/1")
        advanceUntilIdle()

        // Assert
        assertThat(repository.createdCampaigns).containsExactly(
            CreateCampaignRequest(
                name = "The Shattered Throne",
                coverArtUri = "content://cover/1",
            )
        )
    }

    @Test
    fun createCampaign_ignores_blank_names() = runTest {
        // Arrange
        val repository = FakeCampaignRepository()
        val viewModel = CampaignsViewModel(
            campaignRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.createCampaign("   ", null)
        advanceUntilIdle()

        // Assert
        assertThat(repository.createdCampaigns).isEmpty()
    }

    @Test
    fun deleteCampaign_delegates_to_the_repository() = runTest {
        // Arrange
        val repository = FakeCampaignRepository()
        val viewModel = CampaignsViewModel(
            campaignRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.deleteCampaign(7L)
        advanceUntilIdle()

        // Assert
        assertThat(repository.deletedCampaignIds).containsExactly(7L)
    }

    private class FakeCampaignRepository : CampaignRepository {
        private val campaignsFlow = MutableStateFlow<List<Campaign>>(emptyList())

        val createdCampaigns = mutableListOf<CreateCampaignRequest>()
        val deletedCampaignIds = mutableListOf<Long>()

        fun emitCampaigns(campaigns: List<Campaign>) {
            campaignsFlow.value = campaigns
        }

        override fun observeCampaigns(): Flow<List<Campaign>> = campaignsFlow

        override fun observeCampaign(campaignId: Long): Flow<Campaign?> =
            MutableStateFlow(campaignsFlow.value.firstOrNull { it.id == campaignId })

        override suspend fun createCampaign(name: String, coverArtUri: String?): Long {
            createdCampaigns += CreateCampaignRequest(name = name, coverArtUri = coverArtUri)
            return createdCampaigns.size.toLong()
        }

        override suspend fun deleteCampaign(campaignId: Long) {
            deletedCampaignIds += campaignId
        }
    }

    private data class CreateCampaignRequest(
        val name: String,
        val coverArtUri: String?,
    )
}
