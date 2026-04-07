package com.example.rpgaudiomixer.test.acceptance.fakes

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository

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
        return campaigns.toList()
    }

    override suspend fun getCampaignById(id: String): Campaign? {
        return campaigns.find { it.id == id }
    }
}
