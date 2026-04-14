package com.example.rpgaudiomixer.app.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Orchestrates multiple [CategoryPlayer] instances and one-shot FX playback
 * for a loaded soundscape scene.
 *
 * @param playerFactory factory for creating controllable audio players
 * @param scope         coroutine scope shared with child [CategoryPlayer]s for cross-fades
 * @param random        random source for track selection (inject a seeded instance in tests)
 */
class SceneAudioEngine(
    private val playerFactory: AudioPlayerFactory,
    private val scope: CoroutineScope,
    private val random: Random = Random,
) {
    private val categoryPlayers = mutableMapOf<Long, CategoryPlayer>()
    private val activeFxPlayers = mutableListOf<AudioPlayerControl>()
    private var masterAtmosphereVolume: Float = 1.0f
    private var masterFxVolume: Float = 1.0f
    private var fxJitterEnabled: Boolean = false
    private var duckingLevel: Float = 1.0f
    private var duckJob: Job? = null

    private companion object {
        const val DUCK_VOLUME = 0.3f
        const val DUCK_FADE_MS = 200L
        const val DUCK_RESTORE_MS = 600L
    }

    // ---- Scene lifecycle ----

    /** Prepare category players for the given pools. Releases any previously loaded scene. */
    fun loadScene(categories: List<CategoryTrackPool>) {
        release()
        categories.forEach { pool ->
            categoryPlayers[pool.categoryId] = CategoryPlayer(
                trackPool = pool,
                playerFactory = playerFactory,
                scope = scope,
                random = random,
            )
        }
    }

    // ---- Master volume ----

    /** Set the master atmosphere volume that scales all category outputs. */
    fun setMasterAtmosphereVolume(volume: Float) {
        masterAtmosphereVolume = volume
        applyDuckingToAllCategories()
    }

    /** Set the master FX volume applied to subsequent one-shot plays. */
    fun setMasterFxVolume(volume: Float) {
        masterFxVolume = volume
    }

    /** Enable or disable pitch/volume jitter on FX playback. */
    fun setFxJitterEnabled(enabled: Boolean) {
        fxJitterEnabled = enabled
    }

    // ---- Category controls ----

    /** Start (or resume) playback for the given category. */
    fun playCategorySound(categoryId: Long) {
        categoryPlayers[categoryId]?.play()
    }

    /** Pause playback for the given category. */
    fun pauseCategory(categoryId: Long) {
        categoryPlayers[categoryId]?.pause()
    }

    /** Change the intensity level for the given category; triggers cross-fade if playing. */
    fun setCategoryIntensity(categoryId: Long, level: Int) {
        categoryPlayers[categoryId]?.setIntensity(level)
    }

    /** Set the mix (category-level) volume for the given category. */
    fun setCategoryMix(categoryId: Long, mix: Float) {
        categoryPlayers[categoryId]?.setVolume(mix)
    }

    // ---- One-shot FX ----

    /** Play a one-shot FX sound at the current master FX volume. */
    fun playFx(trackPath: String) {
        duckSoundscapes()
        val baseVolume = VolumeUtil.cubicVolume(masterFxVolume)
        val jitteredVolume = if (fxJitterEnabled) {
            val jitter = 1f + (random.nextFloat() - 0.5f) * 0.1f // ±5%
            (baseVolume * jitter).coerceIn(0f, 1f)
        } else baseVolume
        val player = playerFactory.createOneShotPlayer(trackPath).apply {
            setVolume(jitteredVolume)
            play()
        }
        activeFxPlayers.add(player)
    }

    // ---- Panic / cleanup ----

    /** Stop all categories and FX immediately (panic button). */
    fun stopAll() {
        categoryPlayers.values.forEach { it.stop() }
        activeFxPlayers.forEach {
            it.stop()
            it.release()
        }
        activeFxPlayers.clear()
    }

    /** Release all resources. The engine should not be reused after this call. */
    fun release() {
        stopAll()
        categoryPlayers.values.forEach { it.release() }
        categoryPlayers.clear()
    }

    private fun duckSoundscapes() {
        duckJob?.cancel()
        duckingLevel = DUCK_VOLUME
        applyDuckingToAllCategories()
        duckJob = scope.launch {
            delay(DUCK_FADE_MS)
            // Restore after FX finishes (approximate)
            delay(DUCK_RESTORE_MS)
            duckingLevel = 1.0f
            applyDuckingToAllCategories()
        }
    }

    private fun applyDuckingToAllCategories() {
        categoryPlayers.values.forEach { it.setMasterVolume(masterAtmosphereVolume * duckingLevel) }
    }
}
