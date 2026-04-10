package com.example.rpgaudiomixer.data.campaign

import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.local.CampaignEntity
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CampaignRepositoryImpl @Inject constructor(
    private val campaignDao: CampaignDao,
) : CampaignRepository {

    override fun observeCampaigns(): Flow<List<Campaign>> {
        return campaignDao.observeAll().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun observeCampaign(campaignId: Long): Flow<Campaign?> {
        return campaignDao.observeById(campaignId).map { entity -> entity?.toDomainModel() }
    }

    override fun observeActiveCampaign(): Flow<Campaign?> {
        return observeCampaigns().map { campaigns -> campaigns.firstOrNull() }
    }

    override suspend fun createCampaign(name: String, coverArtUri: String?): Long {
        return campaignDao.upsert(
            CampaignEntity(
                name = name.trim(),
                coverArtUri = coverArtUri,
                lastPlayedAt = 0L,
            ),
        )
    }

    override suspend fun deleteCampaign(campaignId: Long) {
        campaignDao.deleteById(campaignId)
    }

    override suspend fun markCampaignPlayed(campaignId: Long, playedAtMillis: Long) {
        campaignDao.updateLastPlayedAt(campaignId, playedAtMillis)
    }
}

private fun CampaignEntity.toDomainModel(): Campaign {
    return Campaign(
        id = id,
        name = name,
        coverArtUri = coverArtUri,
        lastPlayedAt = lastPlayedAt,
    )
}
