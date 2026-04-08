package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.SoundscapeTrack

class SceneAudioEngine(
    private val categoryPlayerFactory: () -> CategoryPlaybackController,
) {
    private val categoryPlayers = linkedMapOf<Long, CategoryPlaybackController>()
    private val categoryMixVolumes = linkedMapOf<Long, Float>()
    private var masterVolume: Float = 1f

    fun addCategory(categoryId: Long) {
        getOrCreateCategoryPlayer(categoryId)
        applyVolume(categoryId)
    }

    fun play(categoryId: Long, trackPath: String) {
        val categoryPlayer = getOrCreateCategoryPlayer(categoryId)
        applyVolume(categoryId)
        categoryPlayer.play(trackPath)
    }

    fun pause(categoryId: Long) {
        categoryPlayers[categoryId]?.pause()
    }

    fun resume(categoryId: Long) {
        categoryPlayers[categoryId]?.resume()
    }

    fun stop(categoryId: Long) {
        categoryPlayers[categoryId]?.stop()
    }

    fun rollRandomTrack(categoryId: Long, pool: List<SoundscapeTrack>): SoundscapeTrack? {
        val categoryPlayer = getOrCreateCategoryPlayer(categoryId)
        applyVolume(categoryId)
        return categoryPlayer.rollRandomTrack(pool)
    }

    fun setCategoryMixVolume(categoryId: Long, mixVolume: Float) {
        categoryMixVolumes[categoryId] = mixVolume.coerceIn(0f, 1f)
        applyVolume(categoryId)
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        categoryPlayers.keys.forEach(::applyVolume)
    }

    fun removeCategory(categoryId: Long) {
        categoryPlayers.remove(categoryId)?.release()
        categoryMixVolumes.remove(categoryId)
    }

    fun releaseAll() {
        categoryPlayers.values.forEach(CategoryPlaybackController::release)
        categoryPlayers.clear()
        categoryMixVolumes.clear()
    }

    private fun getOrCreateCategoryPlayer(categoryId: Long): CategoryPlaybackController {
        return categoryPlayers.getOrPut(categoryId) {
            categoryMixVolumes.putIfAbsent(categoryId, 1f)
            categoryPlayerFactory()
        }
    }

    private fun applyVolume(categoryId: Long) {
        val mixVolume = categoryMixVolumes[categoryId] ?: 1f
        categoryPlayers[categoryId]?.setMixVolume(masterVolume * mixVolume)
    }
}
