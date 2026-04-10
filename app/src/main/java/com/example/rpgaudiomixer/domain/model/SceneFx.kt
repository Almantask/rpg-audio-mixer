package com.example.rpgaudiomixer.domain.model

data class SceneFx(
    val sceneId: Long,
    val fxTrackId: Long,
    val name: String,
    val filePath: String,
    val tags: List<String>,
    val durationMs: Long,
    val playCount: Int,
    val isDemoContent: Boolean,
    val displayOrder: Int,
)
