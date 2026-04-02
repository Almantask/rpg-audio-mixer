package com.example.rpgaudiomixer.domain.model

data class Scene(
    val id: Long = 0L,
    val name: String,
    val tags: List<String> = emptyList(),
    val masterAtmosphereVolume: Float = 0.8f,
    val masterSoundboardVolume: Float = 0.8f,
)
