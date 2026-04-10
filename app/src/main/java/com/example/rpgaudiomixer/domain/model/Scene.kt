package com.example.rpgaudiomixer.domain.model

data class Scene(
    val id: Long = 0,
    val name: String,
    val description: String?,
    val tags: List<String>,
    val soundscapeCount: Int = 0,
)
