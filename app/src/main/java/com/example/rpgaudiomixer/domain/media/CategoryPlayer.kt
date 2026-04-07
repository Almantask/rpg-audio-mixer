package com.example.rpgaudiomixer.domain.media

import kotlinx.coroutines.flow.StateFlow

/**
 * Manages audio playback for a single soundscape category.
 *
 * A CategoryPlayer holds one TrackPlayer at a time and manages its lifecycle.
 * It supports playing, pausing, resuming, stopping tracks, and adjusting the MIX volume.
 */
interface CategoryPlayer {
    /**
     * Play a specific track from the category.
     * If another track is playing, it will be stopped first.
     */
    fun playTrack(trackPath: String)

    /**
     * Pause the currently playing track.
     */
    fun pauseTrack()

    /**
     * Resume the paused track.
     */
    fun resumeTrack()

    /**
     * Stop the currently playing track and release resources.
     */
    fun stopTrack()

    /**
     * Roll a random track from the provided pool and play it.
     */
    fun rollRandomTrack(trackPool: List<String>)

    /**
     * Set the MIX volume for this category (0.0 to 1.0).
     * This will be multiplied with the master volume.
     */
    fun setMixVolume(volume: Float)

    /**
     * Observable state indicating whether a track is currently playing.
     */
    val isPlaying: StateFlow<Boolean>

    /**
     * Release all resources.
     */
    fun release()
}
