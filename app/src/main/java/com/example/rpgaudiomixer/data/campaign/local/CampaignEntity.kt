package com.example.rpgaudiomixer.data.campaign.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val coverArtUri: String?,
    val lastPlayedAt: Long,
)
