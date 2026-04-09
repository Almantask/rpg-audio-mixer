package com.example.rpgaudiomixer.domain.model

/**
 * Represents an FX track that is part of an active scene's soundboard.
 *
 * @property fxTrackId The ID of the FX track
 * @property name The name of the FX track
 * @property filePath The file path to the audio file
 * @property displayOrder The order in which this FX appears in the soundboard grid
 * @property isPlaying Whether this FX is currently playing (one or more instances)
 * @property activeInstanceCount The number of active playing instances of this FX
 */
data class ActiveSceneFx(
    val fxTrackId: Long,
    val name: String,
    val filePath: String,
    val displayOrder: Int,
    val isPlaying: Boolean = false,
    val activeInstanceCount: Int = 0
)
