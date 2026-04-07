package com.example.rpgaudiomixer.domain.model

/**
 * Intensity level for soundscape tracks.
 *
 * Represents the three intensity levels: I (ambient), II (moderate), III (intense)
 */
enum class IntensityLevel(val value: Int, val displayName: String) {
    I(1, "I"),
    II(2, "II"),
    III(3, "III");

    companion object {
        fun fromValue(value: Int): IntensityLevel {
            return entries.find { it.value == value } ?: I
        }
    }
}

/**
 * Domain model for a soundscape category.
 *
 * A category groups related soundscape tracks (e.g. "Forest", "Tavern", "Combat").
 */
data class SoundscapeCategory(
    val id: Long,
    val name: String,
    val iconResId: Int? = null,
    val themeLabel: String? = null
)

/**
 * Domain model for a soundscape track.
 *
 * A track is a looping audio file with an intensity level and mix volume.
 */
data class SoundscapeTrack(
    val id: Long,
    val categoryId: Long,
    val name: String,
    val filePath: String,
    val intensityLevel: IntensityLevel,
    val mixVolume: Float = 0.75f
)
