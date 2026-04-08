package com.example.rpgaudiomixer.domain.model

data class Scene(
    val id: Long,
    val name: String,
    val description: String?,
    val tags: List<String>,
    val soundscapeCount: Int,
)
