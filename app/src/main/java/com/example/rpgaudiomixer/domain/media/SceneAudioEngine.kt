package com.example.rpgaudiomixer.domain.media

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages audio playback for multiple soundscape categories in a scene.
 *
 * The SceneAudioEngine holds a map of category ID to CategoryPlayer instances,
 * and provides a master volume control that affects all categories.
 */
interface SceneAudioEngine {
    /**
     * Get or create a CategoryPlayer for the given category ID.
     */
    fun getCategoryPlayer(categoryId: String): CategoryPlayer

    /**
     * Add a new category to the engine.
     */
    fun addCategory(categoryId: String)

    /**
     * Remove a category from the engine and release its resources.
     */
    fun removeCategory(categoryId: String)

    /**
     * Set the master volume for all soundscape categories (0.0 to 1.0).
     * This is multiplied with each category's MIX volume.
     */
    fun setMasterVolume(volume: Float)

    /**
     * Get the current master volume.
     */
    val masterVolume: StateFlow<Float>

    /**
     * Release all category players and clean up resources.
     */
    fun releaseAll()
}
