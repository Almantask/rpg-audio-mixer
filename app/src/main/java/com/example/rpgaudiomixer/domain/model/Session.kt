package com.example.rpgaudiomixer.domain.model

import java.time.Instant

data class Session(
    val id: String,
    val campaignId: String,
    val name: String,
    val date: Instant,
    val coverArtUri: String? = null
)
