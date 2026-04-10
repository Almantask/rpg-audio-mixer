package com.example.rpgaudiomixer.ui.home

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
class HomeViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun activeCampaign_is_the_most_recently_played_campaign() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeCampaignRepository(
            campaigns = listOf(
                Campaign(id = 1L, name = "Old Campaign", coverArtUri = null, lastPlayedAt = 10L),
                Campaign(id = 2L, name = "Curse of Strahd", coverArtUri = null, lastPlayedAt = 20L),
            ),
        )

        // Act
        val viewModel = HomeViewModel(repository)
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.activeCampaign?.name).isEqualTo("Curse of Strahd")
    }

    @Test
    fun empty_repository_exposes_an_empty_home_state() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeCampaignRepository()

        // Act
        val viewModel = HomeViewModel(repository)
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.activeCampaign).isNull()
    }

    private class FakeCampaignRepository(
        campaigns: List<Campaign> = emptyList(),
    ) : CampaignRepository {
        private val campaignFlow = MutableStateFlow(campaigns.sortedByDescending { it.lastPlayedAt })

        override fun observeCampaigns(): Flow<List<Campaign>> = campaignFlow

        override fun observeCampaign(campaignId: Long): Flow<Campaign?> = campaignFlow.map { campaigns ->
            campaigns.firstOrNull { it.id == campaignId }
        }

        override fun observeActiveCampaign(): Flow<Campaign?> = campaignFlow.map { it.firstOrNull() }

        override suspend fun createCampaign(name: String, coverArtUri: String?): Long {
            error("Not needed in this test")
        }

        override suspend fun deleteCampaign(campaignId: Long) {
            error("Not needed in this test")
        }

        override suspend fun markCampaignPlayed(campaignId: Long, playedAtMillis: Long) {
            error("Not needed in this test")
        }
    }
}
