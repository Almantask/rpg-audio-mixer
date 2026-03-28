package com.example.rpgaudiomixer.domain.model

data class FxEffect(
    val id: Long = 0,
    val name: String,
    val trackFilePath: String,
    val tags: List<String> = emptyList(),
    val durationMs: Long = 0L,
    val playCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
)
