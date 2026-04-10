package com.example.rpgaudiomixer.domain.model

enum class IntensityLevel(
    val dbValue: Int,
    val label: String,
) {
    I(1, "I"),
    II(2, "II"),
    III(3, "III");

    companion object {
        fun fromDbValue(value: Int): IntensityLevel {
            return entries.firstOrNull { it.dbValue == value } ?: I
        }
    }
}
