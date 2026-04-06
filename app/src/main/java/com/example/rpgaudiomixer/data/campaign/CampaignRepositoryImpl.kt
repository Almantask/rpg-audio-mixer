package com.example.rpgaudiomixer.data.campaign

import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CampaignRepositoryImpl @Inject constructor(
    private val dao: CampaignDao,
) : CampaignRepository {

    override fun observeAll(): Flow<List<Campaign>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun create(name: String, coverArtUri: String?): Campaign {
        val entity = CampaignEntity(name = name, coverArtUri = coverArtUri)
        val id = dao.upsert(entity)
        return entity.copy(id = id).toDomain()
    }

    override suspend fun delete(id: Long) {
        dao.delete(id)
    }

    override suspend fun updateLastPlayed(id: Long, timestamp: Long) {
        dao.updateLastPlayed(id, timestamp)
    }
}
