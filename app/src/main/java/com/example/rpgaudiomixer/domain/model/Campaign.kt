package com.example.rpgaudiomixer.domain.model

data class Campaign(
    val id: Long,
    val name: String,
    val coverArtUri: String?,
    val lastPlayedAt: Long,
)
