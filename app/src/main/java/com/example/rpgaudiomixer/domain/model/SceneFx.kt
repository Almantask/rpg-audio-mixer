package com.example.rpgaudiomixer.domain.model

data class SceneFx(
    val sceneId: Long,
    val fxTrackId: Long,
    val name: String,
    val filePath: String,
    val tags: List<String> = emptyList(),
    val durationMs: Long = 0L,
    val playCount: Int = 0,
    val displayOrder: Int,
)
