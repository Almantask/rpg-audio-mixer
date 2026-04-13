package com.example.rpgaudiomixer.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val coverArtUri: String? = null,
    val lastPlayedAt: Long,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null
)
