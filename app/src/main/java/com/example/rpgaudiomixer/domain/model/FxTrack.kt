package com.example.rpgaudiomixer.domain.model

data class FxTrack(
    val id: Long = 0,
    val name: String,
    val filePath: String,
    val tags: List<String> = emptyList(),
    val durationMs: Long = 0L,
    val playCount: Int = 0,
)
