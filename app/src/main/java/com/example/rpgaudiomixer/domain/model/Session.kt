package com.example.rpgaudiomixer.domain.model

data class Session(
    val id: Long = 0,
    val campaignId: Long,
    val name: String,
    val date: Long,
    val coverArtUri: String? = null
)
