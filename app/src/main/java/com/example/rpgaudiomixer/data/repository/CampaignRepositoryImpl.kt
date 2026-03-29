package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.local.CampaignEntity
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CampaignRepositoryImpl @Inject constructor(
    private val dao: CampaignDao
) : CampaignRepository {
    override fun observeAll(): Flow<List<Campaign>> =
        dao.observeAll().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun upsert(campaign: Campaign): Long =
        dao.upsert(campaign.toEntity())

    override suspend fun delete(campaign: Campaign) =
        dao.delete(campaign.toEntity())
}

private fun CampaignEntity.toDomain() = Campaign(
    id = id,
    name = name,
    coverArtUri = coverArtUri,
    lastPlayed = lastPlayed
)

private fun Campaign.toEntity() = CampaignEntity(
    id = id,
    name = name,
    coverArtUri = coverArtUri,
    lastPlayed = lastPlayed
)
