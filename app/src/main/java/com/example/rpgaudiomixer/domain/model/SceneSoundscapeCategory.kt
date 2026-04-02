package com.example.rpgaudiomixer.domain.model

/**
 * A SoundscapeCategory as attached to a Scene, with per-scene volume and sort order.
 */
data class SceneSoundscapeCategory(
    val id: Long = 0L,
    val sceneId: Long,
    val category: SoundscapeCategory,
    val mixVolume: Float = 1.0f,
    val sortOrder: Int = 0,
)
