package com.example.rpgaudiomixer.domain.model

data class Session(
    val id: Long = 0,
    val campaignId: Long,
    val name: String,
    val dateMillis: Long,
    val coverArtUri: String?,
    val sceneCount: Int = 0,
)
