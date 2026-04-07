package com.example.rpgaudiomixer.domain.media

class SceneAudioEngine(
    private val trackFactory: TrackFactory
) {
    private val categoryPlayers = mutableMapOf<Long, CategoryPlayer>()
    private var _masterVolume: Float = 1.0f

    val masterVolume: Float
        get() = _masterVolume

    fun setMasterVolume(volume: Float) {
        _masterVolume = volume.coerceIn(0f, 1f)
        categoryPlayers.values.forEach { player ->
            player.setMasterVolume(_masterVolume)
        }
    }

    fun addCategory(categoryId: Long): CategoryPlayer {
        return categoryPlayers.getOrPut(categoryId) {
            CategoryPlayer(trackFactory).apply {
                setMasterVolume(_masterVolume)
            }
        }
    }

    fun removeCategory(categoryId: Long) {
        categoryPlayers[categoryId]?.release()
        categoryPlayers.remove(categoryId)
    }

    fun getPlayer(categoryId: Long): CategoryPlayer? {
        return categoryPlayers[categoryId]
    }

    fun releaseAll() {
        categoryPlayers.values.forEach { it.release() }
        categoryPlayers.clear()
    }

    suspend fun switchToScene(newSceneId: Long, fadeOutDurationMs: Long = 2000) {
        // Simplified - just release all for now
        // Full implementation would include crossfade logic
        releaseAll()
    }
}
