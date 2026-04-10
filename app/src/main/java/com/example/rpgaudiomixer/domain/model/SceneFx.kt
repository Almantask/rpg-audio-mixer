package com.example.rpgaudiomixer.domain.model

data class SceneFx(
    val sceneId: Long,
    val fxTrackId: Long,
    val displayOrder: Int,
    val fxTrack: FxTrack,
)
