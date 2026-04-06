package com.example.rpgaudiomixer.domain.audio

import com.example.rpgaudiomixer.domain.media.TrackPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SceneAudioEngine(
    private val trackFactory: (filePath: String) -> TrackPlayer,
) {
    private val players = mutableMapOf<Long, CategoryPlayer>()

    private val _masterVolume = MutableStateFlow(1.0f)
    val masterVolume: StateFlow<Float> = _masterVolume.asStateFlow()

    fun addCategory(categoryId: Long) {
        if (!players.containsKey(categoryId)) {
            players[categoryId] = CategoryPlayer(trackFactory)
        }
    }

    fun removeCategory(categoryId: Long) {
        players[categoryId]?.release()
        players.remove(categoryId)
    }

    fun getPlayer(categoryId: Long): CategoryPlayer? = players[categoryId]

    fun setMasterVolume(volume: Float) {
        _masterVolume.value = volume
        players.values.forEach { it.setMixVolume(volume) }
    }

    fun releaseAll() {
        players.values.forEach { it.release() }
        players.clear()
    }
}
