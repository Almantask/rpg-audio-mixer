package com.example.rpgaudiomixer.data.campaign.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rpgaudiomixer.domain.model.Campaign

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val coverArtUri: String?,
    val lastPlayedAt: Long,
    val deletedAt: Long? = null,
)

fun CampaignEntity.asDomain(): Campaign = Campaign(
    id = id,
    name = name,
    coverArtUri = coverArtUri,
    lastPlayedAt = lastPlayedAt,
)

fun Campaign.asEntity(): CampaignEntity = CampaignEntity(
    id = id,
    name = name,
    coverArtUri = coverArtUri,
    lastPlayedAt = lastPlayedAt,
)
