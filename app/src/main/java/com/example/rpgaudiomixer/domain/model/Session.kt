package com.example.rpgaudiomixer.domain.model

data class Session(
    val id: Long = 0,
    val campaignId: Long,
    val name: String,
    val description: String,
    val coverArtUri: String? = null,
    val playedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
)
