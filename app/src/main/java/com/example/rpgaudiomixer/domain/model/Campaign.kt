package com.example.rpgaudiomixer.domain.model

/**
 * Campaign domain model
 * Represents a complete story arc (plain Kotlin, no Room annotations)
 */
data class Campaign(
    val id: Long = 0,
    val name: String,
    val coverArtUri: String? = null,
    val lastPlayedAt: Long = System.currentTimeMillis()
)
