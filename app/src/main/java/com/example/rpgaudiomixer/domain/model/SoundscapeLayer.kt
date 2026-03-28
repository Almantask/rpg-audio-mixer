package com.example.rpgaudiomixer.domain.model

data class SoundscapeLayer(
    val id: Long = 0,
    val categoryId: Long,
    val name: String,
    val trackFilePath: String,
    val intensity: Int = 1,    // 1=Level I, 2=Level II, 3=Level III
    val mix: Float = 0.8f,
    val durationMs: Long = 0L,
)
