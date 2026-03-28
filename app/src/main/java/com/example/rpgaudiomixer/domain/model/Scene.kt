package com.example.rpgaudiomixer.domain.model

data class Scene(
    val id: Long = 0,
    val name: String,
    val description: String,
    val tags: List<String> = emptyList(),
    val coverArtUri: String? = null,
    val soundboardMasterVolume: Float = 0.8f,
    val atmosphereMasterVolume: Float = 0.8f,
    val playCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
)
