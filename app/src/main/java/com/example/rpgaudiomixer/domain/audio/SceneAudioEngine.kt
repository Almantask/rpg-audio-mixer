package com.example.rpgaudiomixer.domain.audio

import com.example.rpgaudiomixer.domain.media.TrackFactory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Manages multiple CategoryPlayers for a scene's soundscape playback.
 *
 * Each category plays one looping track at a time with individual MIX volumes.
 * Master volume affects all categories proportionally.
 */
class SceneAudioEngine(
    private val trackFactory: TrackFactory,
    private val soundscapeRepository: SoundscapeRepository,
    private val coroutineScope: CoroutineScope
) {
    private val categoryPlayers = mutableMapOf<Long, CategoryPlayer>()
    private var _masterVolume: Float = 1.0f
    private var fadeJob: Job? = null

    val masterVolume: Float
        get() = _masterVolume

    fun addCategory(categoryId: Long) {
        if (!categoryPlayers.containsKey(categoryId)) {
            categoryPlayers[categoryId] = CategoryPlayer(
                categoryId,
                trackFactory,
                soundscapeRepository,
                coroutineScope
            )
        }
    }

    fun removeCategory(categoryId: Long) {
        categoryPlayers[categoryId]?.release()
        categoryPlayers.remove(categoryId)
    }

    fun playCategory(categoryId: Long, trackId: Long, trackPath: String) {
        categoryPlayers[categoryId]?.play(trackId, trackPath)
    }

    fun pauseCategory(categoryId: Long) {
        categoryPlayers[categoryId]?.pause()
    }

    fun resumeCategory(categoryId: Long) {
        categoryPlayers[categoryId]?.resume()
    }

    fun stopCategory(categoryId: Long) {
        categoryPlayers[categoryId]?.stop()
    }

    fun rollRandomTrack(categoryId: Long, pool: List<SoundscapeTrack>) {
        categoryPlayers[categoryId]?.rollRandomTrack(pool)
    }

    fun setCategoryMixVolume(categoryId: Long, volume: Float) {
        categoryPlayers[categoryId]?.setMixVolume(volume)
    }

    fun setMasterVolume(volume: Float) {
        _masterVolume = volume.coerceIn(0f, 1f)
        categoryPlayers.values.forEach { it.setMasterVolume(_masterVolume) }
    }

    fun getCategoryIsPlaying(categoryId: Long): StateFlow<Boolean>? {
        return categoryPlayers[categoryId]?.isPlaying
    }

    fun releaseAll() {
        categoryPlayers.values.forEach { it.release() }
        categoryPlayers.clear()
    }

    /**
     * Crossfade to a new scene by gradually fading out current categories
     * and fading in new categories over 2-3 seconds.
     */
    fun switchToScene(newSceneCategories: Map<Long, Triple<Long, String, Float>>) {
        fadeJob?.cancel()

        val fadeDurationMs = 2500L // 2.5 seconds
        val steps = 25 // 100ms per step
        val stepDelayMs = fadeDurationMs / steps

        // Store current players for fade-out
        val oldPlayers = categoryPlayers.toMap()

        // Create new players and start them muted
        val newPlayers = mutableMapOf<Long, CategoryPlayer>()
        newSceneCategories.forEach { (categoryId, trackData) ->
            val (trackId, trackPath, mixVolume) = trackData
            val player = CategoryPlayer(
                categoryId,
                trackFactory,
                soundscapeRepository,
                coroutineScope
            )
            player.setMixVolume(mixVolume)
            player.setMasterVolume(0f) // Start muted
            player.play(trackId, trackPath)
            newPlayers[categoryId] = player
        }

        // Perform crossfade
        fadeJob = coroutineScope.launch {
            for (step in 0..steps) {
                val progress = step.toFloat() / steps

                // Fade out old players
                oldPlayers.values.forEach { player ->
                    player.setMasterVolume(_masterVolume * (1f - progress))
                }

                // Fade in new players
                newPlayers.values.forEach { player ->
                    player.setMasterVolume(_masterVolume * progress)
                }

                if (step < steps) {
                    delay(stepDelayMs)
                }
            }

            // Clean up old players
            oldPlayers.values.forEach { it.release() }
            categoryPlayers.clear()

            // Set new players as active
            categoryPlayers.putAll(newPlayers)
        }
    }

    /**
     * Start playback with fade-in (used when opening a scene with autoplay).
     */
    fun startPlaybackWithFadeIn(sceneCategories: Map<Long, Triple<Long, String, Float>>) {
        fadeJob?.cancel()
        releaseAll()

        val fadeDurationMs = 2500L // 2.5 seconds
        val steps = 25 // 100ms per step
        val stepDelayMs = fadeDurationMs / steps

        // Create players and start them muted
        sceneCategories.forEach { (categoryId, trackData) ->
            val (trackId, trackPath, mixVolume) = trackData
            val player = CategoryPlayer(
                categoryId,
                trackFactory,
                soundscapeRepository,
                coroutineScope
            )
            player.setMixVolume(mixVolume)
            player.setMasterVolume(0f) // Start muted
            player.play(trackId, trackPath)
            categoryPlayers[categoryId] = player
        }

        // Perform fade-in
        fadeJob = coroutineScope.launch {
            for (step in 0..steps) {
                val progress = step.toFloat() / steps

                categoryPlayers.values.forEach { player ->
                    player.setMasterVolume(_masterVolume * progress)
                }

                if (step < steps) {
                    delay(stepDelayMs)
                }
            }
        }
    }
}
