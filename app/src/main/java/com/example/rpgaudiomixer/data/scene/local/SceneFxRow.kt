package com.example.rpgaudiomixer.data.scene.local

data class SceneFxRow(
    val sceneId: Long,
    val fxTrackId: Long,
    val name: String,
    val filePath: String,
    val tags: String,
    val durationMs: Long,
    val playCount: Int,
    val isDemoContent: Boolean,
    val displayOrder: Int,
)
