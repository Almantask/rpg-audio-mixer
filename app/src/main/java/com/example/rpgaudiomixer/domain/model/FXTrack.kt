package com.example.rpgaudiomixer.domain.model

data class FXTrack(
    val id: Long = 0L,
    val name: String,
    val filePath: String,
    val tags: List<String> = emptyList(),
)
