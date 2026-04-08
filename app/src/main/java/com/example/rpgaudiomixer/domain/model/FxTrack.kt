package com.example.rpgaudiomixer.domain.model

/**
 * Represents a sound effect (FX) track in the audio library.
 *
 * @param id Unique identifier for the track
 * @param name Display name of the track
 * @param filePath File path or URI to the audio file
 * @param tags List of tags for categorization (e.g., "Combat", "Ambient", "Magic")
 * @param durationMs Duration of the track in milliseconds
 * @param playCount Number of times this track has been played
 */
data class FxTrack(
    val id: String,
    val name: String,
    val filePath: String,
    val tags: List<String> = emptyList(),
    val durationMs: Long = 0L,
    val playCount: Int = 0
)
