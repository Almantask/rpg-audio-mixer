package com.example.rpgaudiomixer.domain.model

data class Session(
    val id: Long,
    val campaignId: Long,
    val name: String,
    val dateMillis: Long,
    val coverArtUri: String?,
    val sceneCount: Int,
)
