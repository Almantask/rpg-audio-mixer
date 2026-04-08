package com.example.rpgaudiomixer.data.local

data class SessionSummaryEntity(
    val id: Long,
    val campaignId: Long,
    val name: String,
    val date: Long,
    val coverArtUri: String?,
    val sceneCount: Int,
)
