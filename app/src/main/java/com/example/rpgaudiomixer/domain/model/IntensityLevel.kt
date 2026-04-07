package com.example.rpgaudiomixer.domain.model

enum class IntensityLevel(val value: Int, val label: String) {
    I(1, "I"),
    II(2, "II"),
    III(3, "III");

    companion object {
        fun fromValue(value: Int): IntensityLevel {
            return entries.find { it.value == value } ?: I
        }
    }
}
