package com.example.rpgaudiomixer.domain.scene

data class Scene(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val deletedAt: Long? = null
)
