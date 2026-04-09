package com.example.rpgaudiomixer.domain.model

data class Campaign(
    val id: Long = 0,
    val name: String,
    val coverArtUri: String?,
    val lastPlayedAt: Long,
)
