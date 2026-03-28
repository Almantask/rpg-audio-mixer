package com.example.rpgaudiomixer.domain.model

/** Represents a soundscape category attached to a scene, with per-scene MIX volume and active intensity. */
data class SceneSoundscape(
    val sceneId: Long,
    val category: SoundscapeCategory,
    val mixVolume: Float = 0.5f,
    val activeIntensity: Int = 1,
    val order: Int = 0,
)
