package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.SceneSoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Orchestrates multiple soundscape categories for a scene.
 * Holds a map of categoryId -> CategoryPlayer.
 */
class SceneAudioEngine(
    private val trackFactory: TrackFactory,
    private val soundscapeRepository: SoundscapeRepository
) {
    private val _categoryPlayers = mutableMapOf<Long, CategoryPlayer>()
    private var masterAtmosphereVolume: Float = 1.0f
    private var crossfadeJob: Job? = null

    /**
     * Gets the player for a category, creating it if it doesn't exist.
     */
    fun getPlayer(categoryId: Long): CategoryPlayer {
        return _categoryPlayers.getOrPut(categoryId) {
            CategoryPlayer(trackFactory, soundscapeRepository).apply {
                setMasterVolume(masterAtmosphereVolume)
            }
        }
    }

    /**
     * Explicitly adds a category to the engine.
     */
    fun addCategory(categoryId: Long) {
        getPlayer(categoryId)
    }

    /**
     * Removes and releases a category from the engine.
     */
    fun removeCategory(categoryId: Long) {
        _categoryPlayers.remove(categoryId)?.release()
    }

    /**
     * Sets the global master atmosphere volume (0.0 to 1.0).
     */
    fun setMasterVolume(volume: Float) {
        masterAtmosphereVolume = volume
        _categoryPlayers.values.forEach { it.setMasterVolume(volume) }
    }

    /**
     * Stops all active categories.
     */
    fun stopAll() {
        _categoryPlayers.values.forEach { it.stop() }
    }

    /**
     * Releases all players and clears the map.
     */
    fun releaseAll() {
        _categoryPlayers.values.forEach { it.release() }
        _categoryPlayers.clear()
    }

    /**
     * Smoothly crossfades from current tracks to a new scene's tracks.
     */
    fun crossfadeToScene(
        scope: CoroutineScope,
        newCategories: List<SceneSoundscapeCategory>,
        newTracks: Map<Long, SoundscapeTrack>,
        durationMs: Long = 2500
    ) {
        crossfadeJob?.cancel()
        crossfadeJob = scope.launch(Dispatchers.Default) {
            val oldPlayers = _categoryPlayers.values.toList()
            _categoryPlayers.clear()

            // 1. Prepare and start new players at 0 volume
            newCategories.forEach { sceneCat ->
                val player = getPlayer(sceneCat.category.id)
                player.setMixVolume(sceneCat.mixVolume)
                player.setMasterVolume(0f) // Start silent
                val track = newTracks[sceneCat.category.id]
                if (track != null) {
                    player.playTrack(track)
                }
            }

            // 2. Animate volumes
            val startTime = System.currentTimeMillis()
            while (isActive && System.currentTimeMillis() - startTime < durationMs) {
                val progress = (System.currentTimeMillis() - startTime).toFloat() / durationMs
                
                // Fade out old
                oldPlayers.forEach { it.setMasterVolume(masterAtmosphereVolume * (1.0f - progress)) }
                
                // Fade in new
                _categoryPlayers.values.forEach { it.setMasterVolume(masterAtmosphereVolume * progress) }
                
                delay(16)
            }

            // 3. Final snap and cleanup
            if (isActive) {
                oldPlayers.forEach { it.stop(); it.release() }
                _categoryPlayers.values.forEach { it.setMasterVolume(masterAtmosphereVolume) }
            }
        }
    }
}
