package com.example.rpgaudiomixer.domain.model

/**
 * Domain model for an FX (sound effect) track.
 *
 * FX tracks are one-shot audio clips that can be triggered in soundboards.
 */
data class FxTrack(
    val id: Long,
    val name: String,
    val filePath: String,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
