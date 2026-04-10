package com.example.rpgaudiomixer.infra.campaign

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val coverArtUri: String?,
    val lastPlayedAt: Long,
    val lastOpenedSceneId: Long? = null,
    val deletedAt: Long? = null
)
