package com.example.rpgaudiomixer.data.session.local

data class SessionWithSceneCount(
    val id: Long,
    val campaignId: Long,
    val name: String,
    val dateMillis: Long,
    val coverArtUri: String?,
    val sceneCount: Int,
)
