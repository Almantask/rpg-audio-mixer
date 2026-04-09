package com.example.rpgaudiomixer.domain.media

data class ScenePlaybackRequest(
    val categoryId: Long,
    val trackPath: String,
    val mixVolume: Float,
)
