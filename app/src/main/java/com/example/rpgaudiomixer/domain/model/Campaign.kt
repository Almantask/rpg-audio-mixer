package com.example.rpgaudiomixer.domain.model

/**
 * Domain model for a Campaign.
 *
 * Plain Kotlin data class without Room annotations.
 * Represents a tabletop RPG campaign.
 */
data class Campaign(
    val id: Long = 0,
    val name: String,
    val coverArtUri: String? = null,
    val lastPlayedAt: Long = System.currentTimeMillis()
)
