package com.example.rpgaudiomixer.domain.model

enum class IntensityLevel(val label: String, val index: Int) {
    I("I", 0),
    II("II", 1),
    III("III", 2);

    companion object {
        fun fromIndex(index: Int): IntensityLevel = entries[index.coerceIn(0, 2)]
    }
}
