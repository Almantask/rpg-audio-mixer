package com.example.rpgaudiomixer.domain.model

data class Session(
    val id: Long = 0,
    val campaignId: Long,
    val name: String,
    val date: Long = 0L,
    val coverArtUri: String? = null,
)
