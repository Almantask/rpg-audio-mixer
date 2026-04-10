package com.example.rpgaudiomixer.domain.model

data class FxTrack(
    val id: Long = 0,
    val name: String,
    val filePath: String,
    val tags: List<String>,
    val durationMs: Long,
    val playCount: Int,
)
