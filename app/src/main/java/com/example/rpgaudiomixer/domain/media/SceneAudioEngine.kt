package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Singleton
class SceneAudioEngine @Inject constructor(
    private val trackFactory: TrackFactory,
) {
    private val categoryPlayers = mutableMapOf<Long, CategoryPlayer>()
    private val engineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var currentMasterVolume: Float = 1f
    private var activeSceneId: Long? = null
    private var transitionJob: Job? = null

    val masterVolume: Float
        get() = currentMasterVolume

    fun addCategory(categoryId: Long): CategoryPlayer {
        return categoryPlayers.getOrPut(categoryId) {
            CategoryPlayer(trackFactory = trackFactory).also { player ->
                player.setMasterVolume(currentMasterVolume)
            }
        }
    }

    fun removeCategory(categoryId: Long) {
        categoryPlayers.remove(categoryId)?.release()
    }

    fun setMasterVolume(volume: Float) {
        currentMasterVolume = volume.coerceIn(0f, 1f)
        categoryPlayers.values.forEach { player ->
            player.setMasterVolume(currentMasterVolume)
        }
    }

    fun setCategoryMixVolume(categoryId: Long, mixVolume: Float) {
        addCategory(categoryId).setMixVolume(mixVolume)
    }

    fun playCategoryTrack(categoryId: Long, track: SoundscapeTrack) {
        addCategory(categoryId).play(track)
    }

    fun playCategoryTrack(
        categoryId: Long,
        track: SoundscapeTrack,
        fadeInDurationMs: Long,
    ) {
        val player = addCategory(categoryId)
        player.setTransitionVolume(if (fadeInDurationMs > 0L) 0f else 1f)
        player.play(track)
        animateTransitionVolume(player, start = playerVolumeStart(fadeInDurationMs), end = 1f, durationMs = fadeInDurationMs)
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

    fun rollRandomTrack(categoryId: Long, pool: List<SoundscapeTrack>): SoundscapeTrack? {
        return addCategory(categoryId).rollRandomTrack(pool)
    }

    fun rollRandomTrack(
        categoryId: Long,
        pool: List<SoundscapeTrack>,
        fadeInDurationMs: Long,
    ): SoundscapeTrack? {
        val player = addCategory(categoryId)
        player.setTransitionVolume(if (fadeInDurationMs > 0L) 0f else 1f)
        val track = player.rollRandomTrack(pool)
        if (track != null) {
            animateTransitionVolume(player, start = playerVolumeStart(fadeInDurationMs), end = 1f, durationMs = fadeInDurationMs)
        }
        return track
    }

    fun switchToScene(
        newSceneId: Long,
        preserveCategoryIds: Set<Long> = emptySet(),
        durationMs: Long = DEFAULT_TRANSITION_MS,
    ) {
        if (activeSceneId == newSceneId) return
        activeSceneId = newSceneId

        val fadingPlayers = categoryPlayers
            .filterKeys { categoryId -> categoryId !in preserveCategoryIds }
            .toMap()
        if (fadingPlayers.isEmpty()) return

        transitionJob?.cancel()
        transitionJob = engineScope.launch {
            animate(
                durationMs = durationMs,
                onProgress = { progress ->
                    val volume = 1f - progress
                    fadingPlayers.values.forEach { player ->
                        player.setTransitionVolume(volume)
                    }
                },
                onEnd = {
                    fadingPlayers.forEach { (categoryId, player) ->
                        if (categoryPlayers[categoryId] === player) {
                            player.stop()
                            player.release()
                            categoryPlayers.remove(categoryId)
                        }
                    }
                },
            )
        }
    }

    fun releaseAll() {
        transitionJob?.cancel()
        engineScope.coroutineContext.cancelChildren()
        categoryPlayers.values.forEach { player ->
            player.release()
        }
        categoryPlayers.clear()
        activeSceneId = null
    }

    private fun animateTransitionVolume(
        player: CategoryPlayer,
        start: Float,
        end: Float,
        durationMs: Long,
    ) {
        if (durationMs <= 0L) {
            player.setTransitionVolume(end)
            return
        }

        engineScope.launch {
            animate(
                durationMs = durationMs,
                onProgress = { progress ->
                    player.setTransitionVolume(lerp(start, end, progress))
                },
            )
        }
    }

    private suspend fun animate(
        durationMs: Long,
        onProgress: (Float) -> Unit,
        onEnd: (() -> Unit)? = null,
    ) {
        if (durationMs <= 0L) {
            onProgress(1f)
            onEnd?.invoke()
            return
        }

        val stepCount = 12
        val stepDelayMs = (durationMs / stepCount).coerceAtLeast(1L)
        repeat(stepCount) { index ->
            onProgress((index + 1) / stepCount.toFloat())
            delay(stepDelayMs)
        }
        onEnd?.invoke()
    }

    private fun lerp(start: Float, end: Float, progress: Float): Float {
        return start + ((end - start) * progress.coerceIn(0f, 1f))
    }

    private fun playerVolumeStart(fadeInDurationMs: Long): Float = if (fadeInDurationMs > 0L) 0f else 1f

    companion object {
        const val DEFAULT_TRANSITION_MS: Long = 2_500L
    }
}
