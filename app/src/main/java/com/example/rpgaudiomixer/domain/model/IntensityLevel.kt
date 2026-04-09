package com.example.rpgaudiomixer.domain.model

/**
 * Intensity levels for soundscape tracks.
 * Represents the three tiers of intensity: calm (I), moderate (II), and intense (III).
 */
enum class IntensityLevel(val level: Int, val displayName: String) {
    I(1, "I"),
    II(2, "II"),
    III(3, "III");

    companion object {
        fun fromLevel(level: Int): IntensityLevel? {
            return entries.firstOrNull { it.level == level }
        }
    }
}
