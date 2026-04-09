package com.example.rpgaudiomixer.data.campaign

import com.example.rpgaudiomixer.data.campaign.local.CampaignDao
import com.example.rpgaudiomixer.data.campaign.local.CampaignEntity
import com.example.rpgaudiomixer.data.campaign.local.asDomain
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CampaignRepositoryImpl @Inject constructor(
    private val campaignDao: CampaignDao,
) : CampaignRepository {

    override fun observeAll(): Flow<List<Campaign>> {
        return campaignDao.observeAll().map { entities ->
            entities.map { it.asDomain() }
        }
    }

    override suspend fun createCampaign(name: String, coverArtUri: String?): Long {
        return campaignDao.upsert(
            CampaignEntity(
                name = name,
                coverArtUri = coverArtUri,
                lastPlayedAt = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun deleteCampaign(id: Long) {
        campaignDao.delete(id)
    }

    override suspend fun getCampaign(id: Long): Campaign? {
        return campaignDao.getById(id)?.asDomain()
    }
}
