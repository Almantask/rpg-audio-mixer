package com.example.rpgaudiomixer.domain.model

data class SoundscapeCategory(
    val id: Long = 0,
    val name: String,
    val parentCategory: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)
