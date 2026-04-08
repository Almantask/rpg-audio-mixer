package com.example.rpgaudiomixer.domain.model

enum class IntensityLevel(
    val persistedValue: Int,
    val label: String,
) {
    I(
        persistedValue = 1,
        label = "I",
    ),
    II(
        persistedValue = 2,
        label = "II",
    ),
    III(
        persistedValue = 3,
        label = "III",
    ),
    ;

    companion object {
        fun fromPersistedValue(value: Int): IntensityLevel = entries.firstOrNull { it.persistedValue == value } ?: I
    }
}
