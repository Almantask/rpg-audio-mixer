package com.example.rpgaudiomixer.domain.model

enum class IntensityLevel {
    LOW, MEDIUM, HIGH;

    fun toSliderValue(): Float = when (this) {
        LOW -> 0f
        MEDIUM -> 0.5f
        HIGH -> 1f
    }

    companion object {
        fun fromString(value: String): IntensityLevel = when (value.lowercase()) {
            "low" -> LOW
            "medium" -> MEDIUM
            "high" -> HIGH
            else -> throw IllegalArgumentException("Unknown intensity level: $value")
        }

        fun fromSliderValue(value: Float): IntensityLevel = when (value) {
            0f -> LOW
            1f -> HIGH
            else -> MEDIUM
        }
    }
}
