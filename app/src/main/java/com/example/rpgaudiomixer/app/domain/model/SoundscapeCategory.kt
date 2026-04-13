package com.example.rpgaudiomixer.app.domain.model

data class SoundscapeCategory(
    val id: Long = 0,
    val name: String,
    val sortOrder: Int = 0,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null
)
