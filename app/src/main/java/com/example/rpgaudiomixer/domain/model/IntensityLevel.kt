package com.example.rpgaudiomixer.domain.model

enum class IntensityLevel(val level: Int, val displayName: String) {
    I(1, "I"),
    II(2, "II"),
    III(3, "III");

    companion object {
        fun fromLevel(level: Int): IntensityLevel {
            return entries.find { it.level == level } ?: I
        }
    }
}
