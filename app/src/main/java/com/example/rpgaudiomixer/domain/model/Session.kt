package com.example.rpgaudiomixer.domain.model

/**
 * Session domain model
 * Represents an individual play night within a campaign (plain Kotlin, no Room annotations)
 */
data class Session(
    val id: Long = 0,
    val campaignId: Long,
    val name: String,
    val date: Long = System.currentTimeMillis(),
    val coverArtUri: String? = null
)
