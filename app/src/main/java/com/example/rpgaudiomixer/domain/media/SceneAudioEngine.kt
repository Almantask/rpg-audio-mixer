package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlin.random.Random

class SceneAudioEngine(
    private val trackFactory: TrackFactory,
    private val random: Random = Random.Default,
    private val maxConcurrentCategories: Int = 10,
) {
    private val categoryPlayers = linkedMapOf<Long, CategoryPlayer>()
    private val startedCategoryIds = ArrayDeque<Long>()
    private var masterVolume: Float = 1f

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        categoryPlayers.values.forEach { player ->
            player.setMasterVolume(masterVolume)
        }
    }

    fun addCategory(id: Long, mixVolume: Float = 1f): CategoryPlayer {
        return categoryPlayers.getOrPut(id) {
            CategoryPlayer(trackFactory = trackFactory, random = random).also { player ->
                player.setMasterVolume(masterVolume)
                player.setMixVolume(mixVolume)
            }
        }.also { player ->
            player.setMasterVolume(masterVolume)
            player.setMixVolume(mixVolume)
        }
    }

    fun removeCategory(id: Long) {
        categoryPlayers.remove(id)?.release()
        startedCategoryIds.remove(id)
    }

    fun playCategory(id: Long, trackPath: String) {
        val player = addCategory(id)
        ensureCategoryConcurrency(id)
        player.play(trackPath)
        markCategoryAsMostRecent(id)
    }

    fun rollRandomTrack(id: Long, pool: List<SoundscapeTrack>): SoundscapeTrack? {
        if (pool.isEmpty()) return null
        ensureCategoryConcurrency(id)
        val player = addCategory(id)
        val selectedTrack = player.rollRandomTrack(pool)
        if (selectedTrack != null) {
            markCategoryAsMostRecent(id)
        }
        return selectedTrack
    }

    fun pauseCategory(id: Long) {
        categoryPlayers[id]?.pause()
        startedCategoryIds.remove(id)
    }

    fun resumeCategory(id: Long) {
        val player = categoryPlayers[id] ?: return
        ensureCategoryConcurrency(id)
        player.resume()
        if (player.isPlaying.value) {
            markCategoryAsMostRecent(id)
        }
    }

    fun stopCategory(id: Long) {
        categoryPlayers[id]?.stop()
        startedCategoryIds.remove(id)
    }

    fun categoryPlayer(id: Long): CategoryPlayer? = categoryPlayers[id]

    fun releaseAll() {
        categoryPlayers.values.forEach(CategoryPlayer::release)
        categoryPlayers.clear()
        startedCategoryIds.clear()
    }

    fun currentlyPlayingCategoryIds(): List<Long> = startedCategoryIds.toList()

    private fun ensureCategoryConcurrency(requestedId: Long) {
        if (requestedId in startedCategoryIds) {
            startedCategoryIds.remove(requestedId)
            return
        }
        if (startedCategoryIds.size < maxConcurrentCategories) return

        val oldestCategoryId = startedCategoryIds.removeFirstOrNull() ?: return
        categoryPlayers[oldestCategoryId]?.stop()
    }

    private fun markCategoryAsMostRecent(id: Long) {
        startedCategoryIds.remove(id)
        if (categoryPlayers[id]?.isPlaying?.value == true) {
            startedCategoryIds.addLast(id)
        }
    }
}
