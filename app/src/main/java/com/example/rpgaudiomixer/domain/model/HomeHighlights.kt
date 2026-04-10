package com.example.rpgaudiomixer.domain.model

data class FeaturedSoundscapeTrack(
    val trackName: String,
    val categoryName: String,
    val playCount: Int,
)

data class FeaturedFxTrack(
    val trackName: String,
    val categoryName: String,
    val playCount: Int,
)
