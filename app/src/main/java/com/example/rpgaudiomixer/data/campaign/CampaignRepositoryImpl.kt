package com.example.rpgaudiomixer.data.campaign

import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.local.CampaignEntity
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of CampaignRepository
 * Maps between entity and domain models
 */
class CampaignRepositoryImpl @Inject constructor(
    private val campaignDao: CampaignDao
) : CampaignRepository {

    override fun observeAll(): Flow<List<Campaign>> =
        campaignDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getById(id: Long): Campaign? =
        campaignDao.getById(id)?.toDomain()

    override suspend fun upsert(campaign: Campaign): Long =
        campaignDao.upsert(campaign.toEntity())

    override suspend fun deleteById(id: Long) =
        campaignDao.deleteById(id)
}

/**
 * Extension functions for mapping between Entity and Domain models
 */
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
