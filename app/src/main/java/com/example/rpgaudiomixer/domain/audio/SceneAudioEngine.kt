package com.example.rpgaudiomixer.domain.audio

import com.example.rpgaudiomixer.domain.media.TrackFactory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages multiple CategoryPlayers for a scene's soundscape playback.
 *
 * Each category plays one looping track at a time with individual MIX volumes.
 * Master volume affects all categories proportionally.
 */
class SceneAudioEngine(
    private val trackFactory: TrackFactory
) {
    private val categoryPlayers = mutableMapOf<Long, CategoryPlayer>()
    private var _masterVolume: Float = 1.0f

    val masterVolume: Float
        get() = _masterVolume

    fun addCategory(categoryId: Long) {
        if (!categoryPlayers.containsKey(categoryId)) {
            categoryPlayers[categoryId] = CategoryPlayer(categoryId, trackFactory)
        }
    }

    fun removeCategory(categoryId: Long) {
        categoryPlayers[categoryId]?.release()
        categoryPlayers.remove(categoryId)
    }

    fun playCategory(categoryId: Long, trackPath: String) {
        categoryPlayers[categoryId]?.play(trackPath)
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
     * and fading in new categories.
     *
     * Note: Full implementation with coroutine-based fade requires CoroutineScope.
     * This is a simplified version that immediately switches.
     */
    fun switchToScene(newSceneCategories: Map<Long, String>) {
        releaseAll()
        newSceneCategories.forEach { (categoryId, trackPath) ->
            addCategory(categoryId)
            playCategory(categoryId, trackPath)
        }
    }
}
