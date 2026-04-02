package com.example.rpgaudiomixer.domain.model

data class SceneFXTrack(
    val id: Long = 0L,
    val sceneId: Long,
    val fxTrack: FXTrack,
    val sortOrder: Int = 0,
)
