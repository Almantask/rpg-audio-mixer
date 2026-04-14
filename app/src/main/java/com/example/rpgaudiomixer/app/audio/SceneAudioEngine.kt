package com.example.rpgaudiomixer.app.audio

import kotlinx.coroutines.CoroutineScope
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
        categoryPlayers.values.forEach { it.setMasterVolume(volume) }
    }

    /** Set the master FX volume applied to subsequent one-shot plays. */
    fun setMasterFxVolume(volume: Float) {
        masterFxVolume = volume
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
        val player = playerFactory.createOneShotPlayer(trackPath).apply {
            setVolume(VolumeUtil.cubicVolume(masterFxVolume))
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
}
