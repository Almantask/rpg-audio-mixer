package com.example.rpgaudiomixer.domain.session

data class Session(
    val id: Long = 0,
    val campaignId: Long,
    val name: String,
    val date: Long,
    val coverArtUri: String? = null,
    val lastPlayedAt: Long = 0,
    val deletedAt: Long? = null
)
