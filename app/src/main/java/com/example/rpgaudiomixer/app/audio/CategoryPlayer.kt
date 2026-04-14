package com.example.rpgaudiomixer.app.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Manages audio playback for **one** soundscape category.
 *
 * Uses a double-buffer strategy (two underlying players) to perform
 * smooth cross-fade transitions when the intensity level changes during playback.
 *
 * @param trackPool         tracks grouped by intensity level
 * @param playerFactory     factory for creating controllable audio players
 * @param scope             coroutine scope used for cross-fade animations
 * @param crossfadeDurationMs total cross-fade duration in milliseconds
 * @param crossfadeSteps    number of discrete volume steps during a cross-fade
 * @param random            random source for track selection (inject seeded instance in tests)
 */
class CategoryPlayer(
    private val trackPool: CategoryTrackPool,
    private val playerFactory: AudioPlayerFactory,
    private val scope: CoroutineScope,
    private val crossfadeDurationMs: Long = DEFAULT_CROSSFADE_DURATION_MS,
    private val crossfadeSteps: Int = DEFAULT_CROSSFADE_STEPS,
    private val random: Random = Random,
) {
    private var currentIntensity: Int = 1
    private var masterVolume: Float = 1.0f
    private var mixVolume: Float = 1.0f

    // Double buffer
    private var activePlayer: AudioPlayerControl? = null
    private var fadingOutPlayer: AudioPlayerControl? = null
    private var crossfadeJob: Job? = null

    /** `true` while a track is actively playing (paused counts as not-playing). */
    var isPlaying: Boolean = false
        private set

    // ---- Public API ----

    /** Start playing a track from the current intensity pool. */
    fun play() {
        val trackPath = selectTrack(currentIntensity) ?: return
        stop()
        activePlayer = playerFactory.createLoopingPlayer(trackPath).apply {
            setVolume(calculateVolume())
            play()
        }
        isPlaying = true
    }

    /** Pause playback; cancels any in-progress cross-fade. */
    fun pause() {
        crossfadeJob?.cancel()
        crossfadeJob = null
        fadingOutPlayer?.stop()
        fadingOutPlayer?.release()
        fadingOutPlayer = null
        activePlayer?.pause()
        isPlaying = false
    }

    /** Stop playback and release all underlying players. */
    fun stop() {
        crossfadeJob?.cancel()
        crossfadeJob = null
        activePlayer?.stop()
        activePlayer?.release()
        activePlayer = null
        fadingOutPlayer?.stop()
        fadingOutPlayer?.release()
        fadingOutPlayer = null
        isPlaying = false
    }

    /**
     * Change the intensity level (1, 2, or 3).
     *
     * If the category is currently playing, a cross-fade transition
     * to a track from the new intensity pool is started automatically.
     */
    fun setIntensity(level: Int) {
        require(level in 1..MAX_INTENSITY) { "Intensity must be between 1 and $MAX_INTENSITY" }
        if (level == currentIntensity) return
        currentIntensity = level
        if (isPlaying) {
            crossfadeToNewTrack()
        }
    }

    /** Set this category's mix volume (0.0–1.0). */
    fun setVolume(mix: Float) {
        require(mix in 0.0f..1.0f) { "Mix must be between 0.0 and 1.0" }
        mixVolume = mix
        if (crossfadeJob == null) {
            activePlayer?.setVolume(calculateVolume())
        }
    }

    /** Set the master atmosphere volume that scales this category's output. */
    fun setMasterVolume(volume: Float) {
        masterVolume = volume
        if (crossfadeJob == null) {
            activePlayer?.setVolume(calculateVolume())
        }
    }

    /** Release all resources. The player should not be reused after this call. */
    fun release() {
        stop()
    }

    // ---- Internal helpers ----

    /**
     * Volume formula: `finalVolume = masterVolume × cubicGain(mix)`
     *
     * The cubic curve provides a perceptually linear loudness response.
     */
    internal fun calculateVolume(): Float =
        masterVolume * VolumeUtil.cubicVolume(mixVolume)

    private fun crossfadeToNewTrack() {
        val trackPath = selectTrack(currentIntensity) ?: return

        // Cancel any in-progress cross-fade
        crossfadeJob?.cancel()

        // Clean up any player that was already fading out
        fadingOutPlayer?.stop()
        fadingOutPlayer?.release()

        // Current active becomes the fading-out player
        fadingOutPlayer = activePlayer

        // Create the new active player, starting at zero volume
        val targetVolume = calculateVolume()
        activePlayer = playerFactory.createLoopingPlayer(trackPath).apply {
            setVolume(0f)
            play()
        }

        // Animate the cross-fade
        val fadingOut = fadingOutPlayer
        val fadingIn = activePlayer
        crossfadeJob = scope.launch {
            val stepDelayMs = crossfadeDurationMs / crossfadeSteps
            for (step in 1..crossfadeSteps) {
                val progress = step.toFloat() / crossfadeSteps
                fadingIn?.setVolume(targetVolume * progress)
                fadingOut?.setVolume(targetVolume * (1f - progress))
                delay(stepDelayMs)
            }
            fadingOut?.stop()
            fadingOut?.release()
            if (fadingOutPlayer === fadingOut) {
                fadingOutPlayer = null
            }
            crossfadeJob = null
        }
    }

    private fun selectTrack(intensity: Int): String? {
        val tracks = trackPool.tracksByIntensity[intensity]
        if (tracks.isNullOrEmpty()) return null
        return tracks[random.nextInt(tracks.size)]
    }

    companion object {
        const val DEFAULT_CROSSFADE_DURATION_MS = 2000L
        const val DEFAULT_CROSSFADE_STEPS = 20
        const val MAX_INTENSITY = 3
    }
}
