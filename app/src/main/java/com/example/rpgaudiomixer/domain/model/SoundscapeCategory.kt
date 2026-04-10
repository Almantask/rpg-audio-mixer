package com.example.rpgaudiomixer.domain.model

data class SoundscapeCategory(
    val id: Long,
    val name: String,
    val themeLabel: String?,
    val iconResId: Int?,
    val isDemoContent: Boolean,
    val levelOneCount: Int,
    val levelTwoCount: Int,
    val levelThreeCount: Int,
    val totalPlayCount: Int,
)
