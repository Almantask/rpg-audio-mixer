package com.example.rpgaudiomixer.domain.model

enum class IntensityLevel(
    val level: Int,
    val label: String,
) {
    I(level = 1, label = "I"),
    II(level = 2, label = "II"),
    III(level = 3, label = "III"),
    ;

    companion object {
        fun fromLevel(level: Int): IntensityLevel = entries.firstOrNull { it.level == level } ?: I
    }
}
