package com.example.rpgaudiomixer.domain.model

/**
 * Scene domain model
 * Represents a reusable location/moment (plain Kotlin, no Room annotations)
 */
data class Scene(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val tags: List<String> = emptyList()
)
