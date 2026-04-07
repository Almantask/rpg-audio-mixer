package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Campaign

interface CampaignRepository {
    suspend fun getMostRecentlyPlayedCampaign(): Campaign?
    suspend fun getAllCampaigns(): List<Campaign>
    suspend fun getCampaignById(id: String): Campaign?
    suspend fun createCampaign(name: String): Campaign
    suspend fun updateCampaign(campaign: Campaign)
    suspend fun deleteCampaign(id: String)
}
