package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import java.time.Instant
import java.util.UUID

class InMemoryCampaignRepository : CampaignRepository {

    private val campaigns = mutableListOf<Campaign>()

    override suspend fun getMostRecentlyPlayedCampaign(): Campaign? {
        return campaigns.maxByOrNull { it.lastPlayedAt ?: Instant.MIN }
    }

    override suspend fun getAllCampaigns(): List<Campaign> {
        return campaigns.sortedByDescending { it.lastPlayedAt ?: Instant.MIN }
    }

    override suspend fun getCampaignById(id: String): Campaign? {
        return campaigns.find { it.id == id }
    }

    override suspend fun createCampaign(name: String): Campaign {
        val newCampaign = Campaign(
            id = UUID.randomUUID().toString(),
            name = name,
            lastPlayedAt = Instant.now()
        )
        campaigns.add(newCampaign)
        return newCampaign
    }

    override suspend fun updateCampaign(campaign: Campaign) {
        val index = campaigns.indexOfFirst { it.id == campaign.id }
        if (index != -1) {
            campaigns[index] = campaign
        }
    }

    override suspend fun deleteCampaign(id: String) {
        campaigns.removeAll { it.id == id }
    }
}
