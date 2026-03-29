package com.example.rpgaudiomixer.domain.model

import java.util.UUID

data class Campaign(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String? = null,
    val coverUri: String? = null,
    val lastPlayedAt: Long = 0L,
    val sessionIds: List<String> = emptyList()
)

data class Session(
    val id: String = UUID.randomUUID().toString(),
    val campaignId: String,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastPlayedAt: Long = 0L,
    val sceneIds: List<String> = emptyList()
)

data class Scene(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val tags: List<String> = emptyList(),
    val soundscapeCategoryIds: List<String> = emptyList(),
    val lastPlayedAt: Long = 0L
)

data class SoundscapeCategory(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val intensityLevel: Int = 1,
    val layerTrackIds: List<String> = emptyList(),
    val mix: Float = 1f,
    val playCount: Int = 0
)

data class SoundEffect(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val trackId: String,
    val tags: List<String> = emptyList(),
    val playCount: Int = 0
)
