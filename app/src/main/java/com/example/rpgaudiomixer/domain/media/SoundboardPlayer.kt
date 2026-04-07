package com.example.rpgaudiomixer.domain.media

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Manages audio playback for soundboard sound effects.
 *
 * The SoundboardPlayer supports playing multiple one-shot sounds simultaneously
 * with overlap and re-triggering. Each sound plays independently.
 */
interface SoundboardPlayer {
    /**
     * Trigger a sound effect. Creates a new player instance that overlaps with any existing playback.
     * Returns an instance ID that can be used to stop this specific playback.
     */
    fun triggerFx(fxTrackPath: String): String

    /**
     * Stop a specific sound effect instance.
     */
    fun stopFx(instanceId: String)

    /**
     * Stop all currently playing sound effects.
     */
    fun stopAll()

    /**
     * Set the master volume for all soundboard effects (0.0 to 1.0).
     */
    fun setMasterVolume(volume: Float)

    /**
     * Get the current master volume.
     */
    val masterVolume: StateFlow<Float>

    /**
     * Get the count of currently active sound effect instances.
     */
    val activeInstanceCount: StateFlow<Int>

    /**
     * Release all resources.
     */
    fun releaseAll()
}
