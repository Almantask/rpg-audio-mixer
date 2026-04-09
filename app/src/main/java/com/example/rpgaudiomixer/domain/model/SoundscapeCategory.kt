package com.example.rpgaudiomixer.domain.model

data class SoundscapeCategory(
    val id: Long = 0L,
    val name: String,
    val themeLabel: String? = null,
    val iconName: String? = null,
    val tracks: List<SoundscapeTrack> = emptyList(),
) {
    val intensityCounts: Map<IntensityLevel, Int>
        get() = IntensityLevel.entries.associateWith { intensity ->
            tracks.count { track -> track.intensityLevel == intensity }
        }
}
