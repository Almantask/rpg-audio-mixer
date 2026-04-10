package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.SoundscapeTrack

class SceneAudioEngine(
    private val trackFactory: TrackFactory,
    private val maxConcurrentCategories: Int = DEFAULT_MAX_CONCURRENT_SOUNDSCAPES,
) {
    private val categoryPlayers = linkedMapOf<Long, CategoryPlayer>()
    private val playbackOrder = ArrayDeque<Long>()
    private var masterVolume: Float = 1f

    fun addCategory(categoryId: Long): CategoryPlayer {
        return categoryPlayers.getOrPut(categoryId) {
            CategoryPlayer(trackFactory = trackFactory).also { player ->
                player.setMasterVolume(masterVolume)
            }
        }
    }

    fun getCategoryPlayer(categoryId: Long): CategoryPlayer? = categoryPlayers[categoryId]

    fun playCategory(categoryId: Long, trackPath: String) {
        val player = addCategory(categoryId)
        enforceConcurrencyLimit(categoryId, player.isPlaying.value)
        player.play(trackPath)
        recordPlaybackStart(categoryId)
    }

    fun rollRandomTrack(categoryId: Long, pool: List<SoundscapeTrack>): Result<SoundscapeTrack> {
        val player = addCategory(categoryId)
        enforceConcurrencyLimit(categoryId, player.isPlaying.value)
        return player.rollRandomTrack(pool).onSuccess {
            recordPlaybackStart(categoryId)
        }
    }

    fun setCategoryMix(categoryId: Long, mixVolume: Float) {
        addCategory(categoryId).setMixVolume(mixVolume)
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        categoryPlayers.values.forEach { player ->
            player.setMasterVolume(masterVolume)
        }
    }

    fun pauseCategory(categoryId: Long) {
        categoryPlayers[categoryId]?.pause()
        playbackOrder.remove(categoryId)
    }

    fun stopCategory(categoryId: Long) {
        categoryPlayers[categoryId]?.stop()
        playbackOrder.remove(categoryId)
    }

    fun removeCategory(categoryId: Long) {
        categoryPlayers.remove(categoryId)?.release()
        playbackOrder.remove(categoryId)
    }

    fun releaseAll() {
        categoryPlayers.values.forEach { player ->
            player.release()
        }
        categoryPlayers.clear()
        playbackOrder.clear()
    }

    private fun enforceConcurrencyLimit(categoryId: Long, isAlreadyPlaying: Boolean) {
        if (isAlreadyPlaying || playbackOrder.size < maxConcurrentCategories) {
            return
        }

        val oldestCategoryId = playbackOrder.removeFirstOrNull() ?: return
        if (oldestCategoryId == categoryId) {
            return
        }

        categoryPlayers[oldestCategoryId]?.stop()
    }

    private fun recordPlaybackStart(categoryId: Long) {
        playbackOrder.remove(categoryId)
        playbackOrder.addLast(categoryId)
    }

    companion object {
        const val DEFAULT_MAX_CONCURRENT_SOUNDSCAPES: Int = 10
    }
}
