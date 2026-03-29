package com.example.rpgaudiomixer.domain.model

/**
 * Core data models for RPG Audio Mixer
 */
data class Campaign(
    val id: Long,
    val name: String,
    val coverArtUri: String?,
    val lastPlayed: Long
)

data class Session(
    val id: Long,
    val campaignId: Long,
    val name: String,
    val date: Long
)

data class Scene(
    val id: Long,
    val name: String,
    val description: String?,
    val tags: List<String>
)

data class SoundscapeCategory(
    val id: Long,
    val name: String,
    val intensityLevels: List<IntensityLevel>
)

data class IntensityLevel(
    val level: Int,
    val tracks: List<Track>
)

data class Track(
    val id: Long,
    val name: String,
    val uri: String,
    val artworkUri: String?
)

data class FX(
    val id: Long,
    val name: String,
    val uri: String,
    val tags: List<String>
)
