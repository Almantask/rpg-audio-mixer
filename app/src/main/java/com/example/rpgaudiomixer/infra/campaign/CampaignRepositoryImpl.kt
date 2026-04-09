package com.example.rpgaudiomixer.infra.campaign

import com.example.rpgaudiomixer.domain.campaign.Campaign
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CampaignRepositoryImpl @Inject constructor(
    private val campaignDao: CampaignDao
) : CampaignRepository {
    override fun observeAll(): Flow<List<Campaign>> {
        return campaignDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun upsert(campaign: Campaign) {
        withContext(Dispatchers.IO) {
            campaignDao.upsert(campaign.toEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(Dispatchers.IO) {
            campaignDao.delete(id)
        }
    }
}

private fun CampaignEntity.toDomain(): Campaign = Campaign(
    id = id,
    name = name,
    coverArtUri = coverArtUri,
    lastPlayedAt = lastPlayedAt
)

private fun Campaign.toEntity(): CampaignEntity = CampaignEntity(
    id = id,
    name = name,
    coverArtUri = coverArtUri,
    lastPlayedAt = lastPlayedAt
)
