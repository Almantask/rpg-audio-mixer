package com.example.rpgaudiomixer.data.repository

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

    override suspend fun create(name: String, coverUri: String?): Long {
        val entity = CampaignEntity(
            name = name,
            coverArtUri = coverUri,
            lastPlayedAt = System.currentTimeMillis()
        )
        return campaignDao.upsert(entity)
    }

    override suspend fun delete(id: Long) {
        val entity = campaignDao.getById(id)
        if (entity != null) {
            campaignDao.delete(entity)
        }
    }

    override suspend fun updateLastPlayed(id: Long) {
        val entity = campaignDao.getById(id)
        if (entity != null) {
            campaignDao.upsert(entity.copy(lastPlayedAt = System.currentTimeMillis()))
        }
    }

    private fun CampaignEntity.toDomain() = Campaign(
        id = id,
        name = name,
        coverArtUri = coverArtUri,
        lastPlayedAt = lastPlayedAt
    )
}
