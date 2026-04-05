package com.example.rpgaudiomixer.domain.model

data class FXTrack(
    val id: Long = 0,
    val name: String,
    val filePath: String,
    val tags: List<String> = emptyList(),
    val isOneShot: Boolean = true,
    val durationMs: Long = 0,
    val playCount: Int = 0,
    val deletedAt: Long? = null
)
