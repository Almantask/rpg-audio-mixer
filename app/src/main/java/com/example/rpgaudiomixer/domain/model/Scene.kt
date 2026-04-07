package com.example.rpgaudiomixer.domain.model

data class Scene(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val tags: List<String> = emptyList()
)
