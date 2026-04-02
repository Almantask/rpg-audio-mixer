package com.example.rpgaudiomixer.domain.model

data class Session(
    val id: Long = 0L,
    val campaignId: Long,
    val name: String,
    val coverArtUri: String? = null,
    val date: Long = System.currentTimeMillis(),
)
