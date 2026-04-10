package com.example.rpgaudiomixer.ui.campaigns

import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class CampaignsViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun createCampaign_adds_a_trimmed_campaign_to_the_success_state() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeCampaignRepository()
        val viewModel = CampaignsViewModel(repository)

        // Act
        viewModel.createCampaign(name = "  The Shattered Throne  ", coverArtUri = "content://cover")
        advanceUntilIdle()

        // Assert
        val successState = viewModel.uiState.value as CampaignsUiState.Success
        assertThat(successState.campaigns).containsExactly(
            Campaign(
                id = 1L,
                name = "The Shattered Throne",
                coverArtUri = "content://cover",
                lastPlayedAt = 0L,
            ),
        )
    }

    @Test
    fun deleteCampaign_removes_the_campaign_from_the_success_state() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeCampaignRepository(
            initialCampaigns = listOf(
                Campaign(id = 1L, name = "Old Campaign", coverArtUri = null, lastPlayedAt = 10L),
                Campaign(id = 2L, name = "New Campaign", coverArtUri = null, lastPlayedAt = 20L),
            ),
        )
        val viewModel = CampaignsViewModel(repository)

        // Act
        viewModel.deleteCampaign(2L)
        advanceUntilIdle()

        // Assert
        val successState = viewModel.uiState.value as CampaignsUiState.Success
        assertThat(successState.campaigns).containsExactly(
            Campaign(id = 1L, name = "Old Campaign", coverArtUri = null, lastPlayedAt = 10L),
        )
    }

    private class FakeCampaignRepository(
        initialCampaigns: List<Campaign> = emptyList(),
    ) : CampaignRepository {
        private val campaigns = MutableStateFlow(initialCampaigns.sortedByDescending { it.lastPlayedAt })
        private var nextId = (initialCampaigns.maxOfOrNull { it.id } ?: 0L) + 1L

        override fun observeCampaigns(): Flow<List<Campaign>> = campaigns

        override fun observeCampaign(campaignId: Long): Flow<Campaign?> = campaigns.map { allCampaigns ->
            allCampaigns.firstOrNull { it.id == campaignId }
        }

        override fun observeActiveCampaign(): Flow<Campaign?> = campaigns.map { it.firstOrNull() }

        override suspend fun createCampaign(name: String, coverArtUri: String?): Long {
            val id = nextId++
            campaigns.value = (campaigns.value + Campaign(id, name, coverArtUri, 0L))
                .sortedByDescending { it.lastPlayedAt }
            return id
        }

        override suspend fun deleteCampaign(campaignId: Long) {
            campaigns.value = campaigns.value.filterNot { it.id == campaignId }
        }

        override suspend fun markCampaignPlayed(campaignId: Long, playedAtMillis: Long) {
            campaigns.value = campaigns.value
                .map { campaign ->
                    if (campaign.id == campaignId) {
                        campaign.copy(lastPlayedAt = playedAtMillis)
                    } else {
                        campaign
                    }
                }
                .sortedByDescending { it.lastPlayedAt }
        }
    }
}
