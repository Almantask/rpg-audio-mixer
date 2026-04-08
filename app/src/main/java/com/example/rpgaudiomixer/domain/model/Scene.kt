package com.example.rpgaudiomixer.domain.model

import java.time.Instant

data class Scene(
    val id: String,
    val name: String,
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val lastOpenedAt: Instant? = null
)
