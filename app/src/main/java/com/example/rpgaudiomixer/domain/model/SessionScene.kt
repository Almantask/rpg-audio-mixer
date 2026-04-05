package com.example.rpgaudiomixer.domain.model

data class Session(
    val id: Long = 0,
    val campaignId: Long,
    val name: String,
    val date: Long = System.currentTimeMillis(),
    val coverArtUri: String? = null,
    val lastOpenedSceneId: Long? = null,
    val deletedAt: Long? = null
)

data class Scene(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val deletedAt: Long? = null
)
