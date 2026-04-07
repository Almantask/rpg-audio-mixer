package com.example.rpgaudiomixer.test.acceptance.fakes

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import java.time.Instant
import java.util.UUID

class FakeCampaignRepository : CampaignRepository {

    private val campaigns = mutableListOf<Campaign>()

    fun setCampaigns(vararg campaigns: Campaign) {
        this.campaigns.clear()
        this.campaigns.addAll(campaigns)
    }

    fun clear() {
        campaigns.clear()
    }

    fun getLatestCampaign(): Campaign? {
        return campaigns.maxByOrNull { it.lastPlayedAt ?: return@maxByOrNull null }
    }

    override suspend fun getMostRecentlyPlayedCampaign(): Campaign? {
        return campaigns.maxByOrNull { it.lastPlayedAt ?: return@maxByOrNull null }
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
