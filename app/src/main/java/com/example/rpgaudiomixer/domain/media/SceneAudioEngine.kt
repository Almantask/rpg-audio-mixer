package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.library.SoundscapeRepository
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.concurrent.ConcurrentHashMap

class SceneAudioEngine(
    private val trackFactory: TrackFactory,
    private val sceneRepository: SceneRepository,
    private val soundscapeRepository: SoundscapeRepository
) {
    private val engineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val categoryPlayers = ConcurrentHashMap<Long, CategoryPlayer>()
    private var masterVolume: Float = 1f

    fun getPlayer(categoryId: Long): CategoryPlayer {
        return categoryPlayers.getOrPut(categoryId) {
            CategoryPlayer(trackFactory, soundscapeRepository).also {
                it.setMasterVolume(masterVolume)
            }
        }
    }

    fun setMasterVolume(volume: Float) {
        this.masterVolume = volume
        categoryPlayers.values.forEach { it.setMasterVolume(volume) }
    }

    fun removeCategory(categoryId: Long) {
        categoryPlayers.remove(categoryId)?.release()
    }

    fun releaseAll() {
        categoryPlayers.values.forEach { it.release() }
        categoryPlayers.clear()
        engineScope.cancel()
    }

    fun switchToScene(sceneId: Long, autoPlay: Boolean = true) {
        engineScope.launch {
            // 1. Fade out current players
            val currentPlayers = categoryPlayers.values.toList()
            launch {
                currentPlayers.forEach { player ->
                    launch {
                        player.fadeVolume(0f, 2000L)
                        player.stop()
                    }
                }
            }

            if (autoPlay) {
                // 2. Load and start new scene categories
                val soundscapes = sceneRepository.observeSceneActiveSoundscapes(sceneId).first()
                soundscapes.forEach { item ->
                    val player = getPlayer(item.category.id)
                    val trackPool = item.category.tracks.filter { it.intensityLevel == item.intensityLevel }
                    if (trackPool.isNotEmpty()) {
                        player.setMixVolume(0f) // Start from zero
                        player.rollRandomTrack(trackPool)
                        launch {
                            player.fadeVolume(item.mixVolume, 2000L)
                        }
                    }
                }
            }
        }
    }
}
