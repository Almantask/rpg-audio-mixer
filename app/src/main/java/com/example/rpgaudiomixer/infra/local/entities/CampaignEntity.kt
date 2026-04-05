package com.example.rpgaudiomixer.infra.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rpgaudiomixer.domain.model.Campaign

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val coverArtUri: String?,
    val lastPlayedAt: Long,
    val deletedAt: Long? = null
) {
    fun toDomain() = Campaign(
        id = id,
        name = name,
        coverArtUri = coverArtUri,
        lastPlayedAt = lastPlayedAt,
        deletedAt = deletedAt
    )
    
    companion object {
        fun fromDomain(campaign: Campaign) = CampaignEntity(
            id = campaign.id,
            name = campaign.name,
            coverArtUri = campaign.coverArtUri,
            lastPlayedAt = campaign.lastPlayedAt,
            deletedAt = campaign.deletedAt
        )
    }
}
