package com.example.rpgaudiomixer.app.domain.model

data class SoundscapeCategory(
    val id: Long = 0,
    val sceneId: Long,
    val name: String,
    val position: Int = 0
)
