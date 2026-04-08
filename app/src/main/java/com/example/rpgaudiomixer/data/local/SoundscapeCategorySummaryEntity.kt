package com.example.rpgaudiomixer.data.local

data class SoundscapeCategorySummaryEntity(
    val id: Long,
    val name: String,
    val iconResId: Int?,
    val themeLabel: String?,
    val levelOneTrackCount: Int,
    val levelTwoTrackCount: Int,
    val levelThreeTrackCount: Int,
)
