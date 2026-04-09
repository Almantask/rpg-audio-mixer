package com.example.rpgaudiomixer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val coverArtUri: String? = null,
    val lastPlayedAt: Long = System.currentTimeMillis(),
    val lastOpenedSceneId: Long? = null
)
