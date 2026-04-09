package com.example.rpgaudiomixer.data.campaign

import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.local.CampaignEntity
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CampaignRepositoryImpl @Inject constructor(
    private val campaignDao: CampaignDao
) : CampaignRepository {

    override fun observeAll(): Flow<List<Campaign>> {
        return campaignDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long): Campaign? {
        return campaignDao.getById(id)?.toDomain()
    }

    override suspend fun create(name: String, coverArtUri: String?): Long {
        val entity = CampaignEntity(
            name = name,
            coverArtUri = coverArtUri,
            lastPlayedAt = System.currentTimeMillis()
        )
        return campaignDao.upsert(entity)
    }

    override suspend fun update(campaign: Campaign) {
        campaignDao.upsert(campaign.toEntity())
    }

    override suspend fun delete(campaign: Campaign) {
        campaignDao.delete(campaign.toEntity())
    }

    override suspend fun updateLastPlayedAt(campaignId: Long, sceneId: Long) {
        val campaign = campaignDao.getById(campaignId)
        if (campaign != null) {
            val updatedCampaign = campaign.copy(
                lastPlayedAt = System.currentTimeMillis(),
                lastOpenedSceneId = sceneId
            )
            campaignDao.upsert(updatedCampaign)
        }
    }

    private fun CampaignEntity.toDomain() = Campaign(
        id = id,
        name = name,
        coverArtUri = coverArtUri,
        lastPlayedAt = lastPlayedAt
    )

    private fun Campaign.toEntity() = CampaignEntity(
        id = id,
        name = name,
        coverArtUri = coverArtUri,
        lastPlayedAt = lastPlayedAt
    )
}
