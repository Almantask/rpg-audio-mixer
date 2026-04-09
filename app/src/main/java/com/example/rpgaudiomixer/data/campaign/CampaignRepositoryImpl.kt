package com.example.rpgaudiomixer.data.campaign

import com.example.rpgaudiomixer.data.campaign.local.CampaignDao
import com.example.rpgaudiomixer.data.campaign.local.CampaignEntity
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CampaignRepositoryImpl @Inject constructor(
    private val campaignDao: CampaignDao,
) : CampaignRepository {

    override fun observeCampaigns(): Flow<List<Campaign>> = campaignDao.observeAll()
        .map { campaigns -> campaigns.map(CampaignEntity::toDomain) }

    override fun observeCampaign(id: Long): Flow<Campaign?> = campaignDao.observeById(id)
        .map { campaign -> campaign?.toDomain() }

    override fun observeMostRecentCampaign(): Flow<Campaign?> = campaignDao.observeMostRecent()
        .map { campaign -> campaign?.toDomain() }

    override suspend fun upsertCampaign(campaign: Campaign): Long = campaignDao.upsert(campaign.toEntity())

    override suspend fun updateLastPlayedAt(id: Long, lastPlayedAt: Long) {
        campaignDao.updateLastPlayedAt(id, lastPlayedAt)
    }

    override suspend fun deleteCampaign(id: Long) {
        campaignDao.deleteById(id)
    }

    override suspend fun clearAll() {
        campaignDao.clearAll()
    }
}

private fun CampaignEntity.toDomain(): Campaign = Campaign(
    id = id,
    name = name,
    coverArtUri = coverArtUri,
    lastPlayedAt = lastPlayedAt,
)

private fun Campaign.toEntity(): CampaignEntity = CampaignEntity(
    id = id,
    name = name,
    coverArtUri = coverArtUri,
    lastPlayedAt = lastPlayedAt,
)
