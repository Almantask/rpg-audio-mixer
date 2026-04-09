package com.example.rpgaudiomixer.data.local

data class SceneFxSummaryEntity(
    val sceneId: Long,
    val fxTrackId: Long,
    val name: String,
    val filePath: String,
    val tags: String,
    val durationMs: Long,
    val playCount: Int,
    val isDemo: Boolean,
    val displayOrder: Int,
)
