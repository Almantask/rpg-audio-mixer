package com.example.rpgaudiomixer.domain.model

enum class TrackType {
    LOOPABLE,
    FX
}

data class TrackStats(
    val trackId: String,
    val name: String,
    val type: TrackType,
    val playCount: Int
)
