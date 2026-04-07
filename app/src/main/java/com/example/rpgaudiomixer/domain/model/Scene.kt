package com.example.rpgaudiomixer.domain.model

/**
 * Domain model for an audio scene.
 *
 * Scenes are reusable audio configurations that can be linked to multiple sessions.
 */
data class Scene(
    val id: Long,
    val name: String,
    val description: String? = null,
    val tags: List<String> = emptyList()
)
