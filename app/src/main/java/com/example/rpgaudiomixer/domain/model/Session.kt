package com.example.rpgaudiomixer.domain.model

/**
 * Domain model for a game session.
 *
 * Represents a single game session within a campaign.
 */
data class Session(
    val id: Long,
    val campaignId: Long,
    val name: String,
    val date: Long,
    val coverArtUri: String? = null
)
