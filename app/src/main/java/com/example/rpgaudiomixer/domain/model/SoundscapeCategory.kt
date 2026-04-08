package com.example.rpgaudiomixer.domain.model

data class SoundscapeCategory(
    val id: Long,
    val name: String,
    val iconResId: Int?,
    val themeLabel: String?,
    val levelOneTrackCount: Int,
    val levelTwoTrackCount: Int,
    val levelThreeTrackCount: Int,
)
