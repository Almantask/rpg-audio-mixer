package com.example.rpgaudiomixer.domain.audio

import com.example.rpgaudiomixer.domain.model.IntensityLevel

/**
 * Manages all audio for a single active scene.
 * One instance per scene, created by the ViewModel, released on clear.
 */
interface SceneAudioController {

    // ── Soundscape ────────────────────────────────────────────────────────────

    /**
     * Start (or restart) playback for [categoryId] at [intensityLevel].
     * Picks a random track from that level. If only one track exists, plays it.
     */
    fun playCategory(categoryId: Long, intensityLevel: IntensityLevel, filePaths: List<String>)

    /** Stop looping playback for [categoryId]. */
    fun stopCategory(categoryId: Long)

    /** @return true if [categoryId] is currently playing. */
    fun isCategoryPlaying(categoryId: Long): Boolean

    /** Current track name being played for [categoryId], or null. */
    fun currentTrackForCategory(categoryId: Long): String?

    /** Set category-level volume (0f..1f); applied multiplicatively with master. */
    fun setCategoryVolume(categoryId: Long, volume: Float)

    /** Set the master soundscape volume (0f..1f). */
    fun setMasterSoundscapeVolume(volume: Float)

    // ── Soundboard ────────────────────────────────────────────────────────────

    /**
     * Play a one-shot FX sound. Starting again while playing re-triggers.
     */
    fun playFX(fxId: Long, filePath: String)

    /** Stop a specific FX. */
    fun stopFX(fxId: Long)

    /** @return true if FX [fxId] is currently playing. */
    fun isFxPlaying(fxId: Long): Boolean

    /** Set the master soundboard volume (0f..1f). */
    fun setMasterFXVolume(volume: Float)

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /** Stop all audio and release all ExoPlayer instances. */
    fun release()
}
