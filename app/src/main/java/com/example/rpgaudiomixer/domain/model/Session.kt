package com.example.rpgaudiomixer.domain.model

data class Session(
    val id: Long = 0L,
    val campaignId: Long,
    val name: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val coverArtUri: String? = null,
    val sceneCount: Int = 0,
    val lastOpenedSceneId: Long? = null,
    val lastOpenedAtMillis: Long? = null,
)
