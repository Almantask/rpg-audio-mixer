package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SceneAudioEngine @Inject constructor(
    private val trackFactory: TrackFactory,
) {
    private val categoryPlayers = mutableMapOf<Long, CategoryPlayer>()
    private var currentMasterVolume: Float = 1f

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

    fun releaseAll() {
        categoryPlayers.values.forEach { player ->
            player.release()
        }
        categoryPlayers.clear()
    }
}
