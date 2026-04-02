package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.infra.db.dao.CampaignDao
import com.example.rpgaudiomixer.infra.db.toDomain
import com.example.rpgaudiomixer.infra.db.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomCampaignRepository @Inject constructor(
    private val dao: CampaignDao,
) : CampaignRepository {

    override fun getAllCampaigns(): Flow<List<Campaign>> =
        dao.getAllCampaigns().map { list -> list.map { it.toDomain() } }

    override suspend fun getCampaignById(id: Long): Campaign? =
        dao.getCampaignById(id)?.toDomain()

    override suspend fun upsertCampaign(campaign: Campaign): Long =
        dao.upsertCampaign(campaign.toEntity())

    override suspend fun deleteCampaign(id: Long) =
        dao.deleteCampaign(id)

    override suspend fun touchLastPlayed(id: Long) =
        dao.touchLastPlayed(id, System.currentTimeMillis())

    override fun getMostRecentCampaign(): Flow<Campaign?> =
        dao.getMostRecentCampaign().map { it?.toDomain() }
}
