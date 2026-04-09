package com.example.rpgaudiomixer.domain.model

data class Session(
    val id: Long,
    val campaignId: Long,
    val name: String,
    val date: Long,
    val coverArtUri: String?,
    val sceneCount: Int,
    val lastOpenedSceneId: Long? = null,
    val lastOpenedAt: Long? = null,
)
