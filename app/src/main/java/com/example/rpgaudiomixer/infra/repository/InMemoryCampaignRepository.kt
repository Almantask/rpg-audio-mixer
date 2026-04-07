package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository

class InMemoryCampaignRepository : CampaignRepository {
    override suspend fun getMostRecentlyPlayedCampaign(): Campaign? = null
    override suspend fun getAllCampaigns(): List<Campaign> = emptyList()
    override suspend fun getCampaignById(id: String): Campaign? = null
}
