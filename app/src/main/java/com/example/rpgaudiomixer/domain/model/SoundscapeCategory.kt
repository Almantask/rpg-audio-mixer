package com.example.rpgaudiomixer.domain.model

/**
 * Represents a soundscape category containing related atmospheric tracks.
 * Examples: "Forest", "Combat", "Mystery", "Boss"
 */
data class SoundscapeCategory(
    val id: String,
    val name: String,
    val iconResId: Int? = null,
    val themeLabel: String? = null,
    val trackCountByLevel: Map<IntensityLevel, Int> = emptyMap()
)
