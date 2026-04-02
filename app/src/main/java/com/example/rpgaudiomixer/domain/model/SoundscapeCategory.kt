package com.example.rpgaudiomixer.domain.model

/**
 * A SoundscapeCategory groups tracks by intensity.
 * [tracksByIntensity] is populated when fully loaded.
 * For lightweight list views, only id and name are needed.
 */
data class SoundscapeCategory(
    val id: Long = 0L,
    val name: String,
    val tracksByIntensity: Map<IntensityLevel, List<Track>> = emptyMap(),
) {
    val totalTrackCount: Int get() = tracksByIntensity.values.sumOf { it.size }

    fun tracksFor(level: IntensityLevel): List<Track> = tracksByIntensity[level] ?: emptyList()

    fun hasTracksFor(level: IntensityLevel): Boolean = tracksFor(level).isNotEmpty()
}
