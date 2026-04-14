package com.example.rpgaudiomixer.app.audio

/**
 * Tracks available for a single soundscape category, grouped by intensity level.
 *
 * @property categoryId   database identifier of the category
 * @property categoryName human-readable name (e.g. "Forest", "Rain")
 * @property tracksByIntensity intensity level (1, 2, 3) → list of track file paths
 */
data class CategoryTrackPool(
    val categoryId: Long,
    val categoryName: String,
    val tracksByIntensity: Map<Int, List<String>>,
)
