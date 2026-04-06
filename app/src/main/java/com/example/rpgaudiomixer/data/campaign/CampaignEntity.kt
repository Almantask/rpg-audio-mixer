package com.example.rpgaudiomixer.data.campaign

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rpgaudiomixer.domain.model.Campaign

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val coverArtUri: String? = null,
    val lastPlayedAt: Long = 0L,
)

fun CampaignEntity.toDomain(): Campaign = Campaign(
    id = id,
    name = name,
    coverArtUri = coverArtUri,
    lastPlayedAt = lastPlayedAt,
)

fun Campaign.toEntity(): CampaignEntity = CampaignEntity(
    id = id,
    name = name,
    coverArtUri = coverArtUri,
    lastPlayedAt = lastPlayedAt,
)
