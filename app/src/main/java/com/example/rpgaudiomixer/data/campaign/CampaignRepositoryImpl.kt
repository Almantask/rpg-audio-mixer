package com.example.rpgaudiomixer.data.campaign

import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.local.CampaignEntity
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class CampaignRepositoryImpl @Inject constructor(
    private val campaignDao: CampaignDao,
) : CampaignRepository {
    private var currentTimeProvider: () -> Long = System::currentTimeMillis

    internal constructor(
        campaignDao: CampaignDao,
        currentTimeProvider: () -> Long,
    ) : this(campaignDao) {
        this.currentTimeProvider = currentTimeProvider
    }

    override fun observeCampaigns(): Flow<List<Campaign>> =
        campaignDao.observeAll().map { entities -> entities.map(CampaignEntity::toDomain) }

    override fun observeCampaign(campaignId: Long): Flow<Campaign?> =
        campaignDao.observeById(campaignId).map { entity -> entity?.toDomain() }

    override suspend fun createCampaign(name: String, coverArtUri: String?): Long =
        campaignDao.upsert(
            CampaignEntity(
                name = name,
                coverArtUri = coverArtUri,
                lastPlayedAt = currentTimeProvider(),
            )
        )

    override suspend fun deleteCampaign(campaignId: Long) {
        campaignDao.softDeleteById(campaignId = campaignId, deletedAt = currentTimeProvider())
    }
}

private fun CampaignEntity.toDomain(): Campaign = Campaign(
    id = id,
    name = name,
    coverArtUri = coverArtUri,
    lastPlayedAt = lastPlayedAt,
)
