package com.example.rpgaudiomixer.domain.model

data class SoundscapeCategory(
    val id: Long = 0,
    val name: String,
    val iconResId: Int? = null,
    val themeLabel: String? = null
)
