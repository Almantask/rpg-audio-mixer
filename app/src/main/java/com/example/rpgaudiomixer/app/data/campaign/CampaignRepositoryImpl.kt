package com.example.rpgaudiomixer.app.data.campaign

import com.example.rpgaudiomixer.app.data.local.dao.CampaignDao
import com.example.rpgaudiomixer.app.data.local.entities.CampaignEntity
import com.example.rpgaudiomixer.app.domain.model.Campaign
import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CampaignRepositoryImpl @Inject constructor(
    private val campaignDao: CampaignDao
) : CampaignRepository {

    override fun observeAll(): Flow<List<Campaign>> {
        return campaignDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeDeleted(): Flow<List<Campaign>> {
        return campaignDao.observeDeleted().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createCampaign(name: String, coverArtUri: String?) {
        val entity = CampaignEntity(
            name = name,
            coverArtUri = coverArtUri,
            lastPlayedAt = System.currentTimeMillis()
        )
        campaignDao.upsert(entity)
    }

    override suspend fun deleteCampaign(campaignId: Long) {
        campaignDao.softDelete(campaignId)
    }

    override suspend fun restoreCampaign(id: Long) {
        campaignDao.restore(id)
    }

    override suspend fun permanentlyDeleteCampaign(id: Long) {
        campaignDao.permanentlyDelete(id)
    }

    override suspend fun deleteAll() {
        campaignDao.deleteAll()
    }

    private fun CampaignEntity.toDomain() = Campaign(
        id = id,
        name = name,
        coverArtUri = coverArtUri,
        lastPlayedAt = lastPlayedAt,
        isDeleted = isDeleted,
        deletedAt = deletedAt
    )
}
