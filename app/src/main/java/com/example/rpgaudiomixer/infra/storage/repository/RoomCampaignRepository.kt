package com.example.rpgaudiomixer.infra.storage.repository

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.storage.CampaignRepository
import com.example.rpgaudiomixer.infra.storage.db.dao.CampaignDao
import com.example.rpgaudiomixer.infra.storage.db.entity.CampaignEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomCampaignRepository @Inject constructor(
    private val dao: CampaignDao,
) : CampaignRepository {

    override fun getAllCampaigns(): Flow<List<Campaign>> =
        dao.getAllCampaigns().map { it.map(CampaignEntity::toDomain) }

    override fun getCampaignById(id: Long): Flow<Campaign?> =
        dao.getCampaignById(id).map { it?.toDomain() }

    override suspend fun insert(campaign: Campaign): Long =
        dao.insert(campaign.toEntity())

    override suspend fun update(campaign: Campaign) =
        dao.update(campaign.toEntity())

    override suspend fun delete(campaign: Campaign) =
        dao.delete(campaign.toEntity())
}

private fun CampaignEntity.toDomain() = Campaign(
    id = id,
    name = name,
    description = description,
    coverArtUri = coverArtUri,
    lastPlayedAt = lastPlayedAt,
    createdAt = createdAt,
)

private fun Campaign.toEntity() = CampaignEntity(
    id = id,
    name = name,
    description = description,
    coverArtUri = coverArtUri,
    lastPlayedAt = lastPlayedAt,
    createdAt = createdAt,
)
