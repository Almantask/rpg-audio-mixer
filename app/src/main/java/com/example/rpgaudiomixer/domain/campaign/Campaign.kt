package com.example.rpgaudiomixer.domain.campaign

data class Campaign(
    val id: Long = 0,
    val name: String,
    val coverArtUri: String? = null,
    val lastPlayedAt: Long = System.currentTimeMillis(),
    val lastOpenedSceneId: Long? = null,
    val deletedAt: Long? = null
)
