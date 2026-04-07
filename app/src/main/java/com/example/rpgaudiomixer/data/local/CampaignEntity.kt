package com.example.rpgaudiomixer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Campaign entity for Room database.
 *
 * Represents a tabletop RPG campaign with cover art and last played timestamp.
 */
@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val coverArtUri: String? = null,

    val lastPlayedAt: Long = System.currentTimeMillis()
)
