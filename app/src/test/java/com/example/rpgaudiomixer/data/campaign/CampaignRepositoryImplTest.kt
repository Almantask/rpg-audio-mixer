package com.example.rpgaudiomixer.data.campaign

import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.local.CampaignEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CampaignRepositoryImplTest {

    private val dao = FakeCampaignDao()
    private val repository = CampaignRepositoryImpl(
        campaignDao = dao,
        currentTimeProvider = { 1_726_000_000_000L },
    )

    @Test
    fun observeCampaigns_maps_entities_to_domain_models() = runTest {
        // Arrange
        dao.emitCampaigns(
            listOf(
                CampaignEntity(
                    id = 2L,
                    name = "Curse of Strahd",
                    coverArtUri = "content://cover/2",
                    lastPlayedAt = 200L,
                ),
                CampaignEntity(
                    id = 1L,
                    name = "The Shattered Throne",
                    coverArtUri = null,
                    lastPlayedAt = 100L,
                ),
            )
        )

        // Act
        val result = repository.observeCampaigns().first()

        // Assert
        assertThat(result).containsExactly(
            com.example.rpgaudiomixer.domain.model.Campaign(
                id = 2L,
                name = "Curse of Strahd",
                coverArtUri = "content://cover/2",
                lastPlayedAt = 200L,
            ),
            com.example.rpgaudiomixer.domain.model.Campaign(
                id = 1L,
                name = "The Shattered Throne",
                coverArtUri = null,
                lastPlayedAt = 100L,
            ),
        )
    }

    @Test
    fun observeCampaign_returns_a_mapped_domain_model_for_the_requested_id() = runTest {
        // Arrange
        dao.emitCampaign(7L, CampaignEntity(7L, "The Wild Beyond", null, 300L))

        // Act
        val result = repository.observeCampaign(7L).first()

        // Assert
        assertThat(result).isEqualTo(
            com.example.rpgaudiomixer.domain.model.Campaign(
                id = 7L,
                name = "The Wild Beyond",
                coverArtUri = null,
                lastPlayedAt = 300L,
            )
        )
    }

    @Test
    fun createCampaign_inserts_a_campaign_entity_with_the_current_timestamp() = runTest {
        // Arrange
        val name = "The Shattered Throne"
        val coverArtUri = "content://cover/new"

        // Act
        repository.createCampaign(name = name, coverArtUri = coverArtUri)

        // Assert
        assertThat(dao.upsertedCampaigns).containsExactly(
            CampaignEntity(
                id = 0L,
                name = name,
                coverArtUri = coverArtUri,
                lastPlayedAt = 1_726_000_000_000L,
            )
        )
    }

    @Test
    fun deleteCampaign_deletes_the_requested_campaign_id() = runTest {
        // Arrange
        val campaignId = 9L

        // Act
        repository.deleteCampaign(campaignId)

        // Assert
        assertThat(dao.deletedCampaignIds).containsExactly(campaignId)
    }

    private class FakeCampaignDao : CampaignDao {
        private val campaignsFlow = MutableStateFlow<List<CampaignEntity>>(emptyList())
        private val campaignFlows = mutableMapOf<Long, MutableStateFlow<CampaignEntity?>>()

        val upsertedCampaigns = mutableListOf<CampaignEntity>()
        val deletedCampaignIds = mutableListOf<Long>()

        fun emitCampaigns(campaigns: List<CampaignEntity>) {
            campaignsFlow.value = campaigns
        }

        fun emitCampaign(id: Long, campaign: CampaignEntity?) {
            campaignFlows.getOrPut(id) { MutableStateFlow(null) }.value = campaign
        }

        override fun observeAll(): Flow<List<CampaignEntity>> = campaignsFlow

        override fun observeById(campaignId: Long): Flow<CampaignEntity?> =
            campaignFlows.getOrPut(campaignId) { MutableStateFlow(null) }

        override suspend fun upsert(campaign: CampaignEntity): Long {
            upsertedCampaigns += campaign
            return campaign.id
        }

        override suspend fun deleteById(campaignId: Long) {
            deletedCampaignIds += campaignId
        }
    }
}
