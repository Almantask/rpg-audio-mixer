package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.infra.local.dao.CampaignDao
import com.example.rpgaudiomixer.infra.local.entities.CampaignEntity
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

    override fun observeLatest(): Flow<Campaign?> {
        return campaignDao.observeLatest().map { it?.toDomain() }
    }

    override fun observeDeleted(): Flow<List<Campaign>> {
        return campaignDao.observeDeleted().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateLastPlayed(id: Long) {
        campaignDao.updateLastPlayed(id, System.currentTimeMillis())
    }

    override suspend fun upsert(campaign: Campaign) {
        campaignDao.upsert(CampaignEntity.fromDomain(campaign))
    }

    override suspend fun softDelete(id: Long) {
        campaignDao.softDelete(id, System.currentTimeMillis())
    }

    override suspend fun restore(id: Long) {
        campaignDao.restore(id)
    }

    override suspend fun permanentDelete(id: Long) {
        campaignDao.permanentDelete(id)
    }

    override suspend fun purgeOldDeleted(threshold: Long) {
        campaignDao.purgeOldDeleted(threshold)
    }
}
