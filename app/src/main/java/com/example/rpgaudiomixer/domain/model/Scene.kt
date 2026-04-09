package com.example.rpgaudiomixer.domain.model

data class Scene(
    val id: Long = 0L,
    val name: String,
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val soundscapeCategoryNames: List<String> = emptyList(),
    val soundboardEffectNames: List<String> = emptyList(),
    val atmosphereVolumePercent: Int = 100,
    val soundboardVolumePercent: Int = 100,
)
