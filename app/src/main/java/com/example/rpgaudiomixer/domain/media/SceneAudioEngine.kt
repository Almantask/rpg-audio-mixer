package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SceneAudioEngine(
    private val categoryPlayerFactory: () -> CategoryPlaybackController,
    private val fadeStepDurationMs: Long = 250L,
    private val fadeStepCount: Int = 10,
    private val fadeDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : SceneAudioController {
    private val switchMutex = Mutex()
    private val categoryPlayers = linkedMapOf<Long, CategoryPlaybackController>()
    private val categoryMixVolumes = linkedMapOf<Long, Float>()
    private var masterVolume: Float = 1f
    private var currentSceneCategoryIds: Set<Long> = emptySet()

    override var activeSceneId: Long? = null
        private set

    override fun addCategory(categoryId: Long) {
        getOrCreateCategoryPlayer(categoryId)
        applyVolume(categoryId)
    }

    override fun play(categoryId: Long, trackPath: String) {
        val categoryPlayer = getOrCreateCategoryPlayer(categoryId)
        applyVolume(categoryId)
        categoryPlayer.play(trackPath)
    }

    override fun pause(categoryId: Long) {
        categoryPlayers[categoryId]?.pause()
    }

    override fun resume(categoryId: Long) {
        categoryPlayers[categoryId]?.resume()
    }

    override fun stop(categoryId: Long) {
        categoryPlayers[categoryId]?.stop()
    }

    override fun rollRandomTrack(categoryId: Long, pool: List<SoundscapeTrack>): SoundscapeTrack? {
        val categoryPlayer = getOrCreateCategoryPlayer(categoryId)
        applyVolume(categoryId)
        return categoryPlayer.rollRandomTrack(pool)
    }

    override fun setCategoryMixVolume(categoryId: Long, mixVolume: Float) {
        categoryMixVolumes[categoryId] = mixVolume.coerceIn(0f, 1f)
        applyVolume(categoryId)
    }

    override fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        categoryPlayers.keys.forEach(::applyVolume)
    }

    override suspend fun switchToScene(newSceneId: Long, categories: List<ScenePlaybackRequest>) {
        switchMutex.withLock {
            withContext(fadeDispatcher) {
                val outgoingCategoryIds = categoryPlayers.keys.toSet()
                val incomingRequestsByCategory = categories.associateBy(ScenePlaybackRequest::categoryId)

                incomingRequestsByCategory.values.forEach { request ->
                    categoryMixVolumes[request.categoryId] = request.mixVolume.coerceIn(0f, 1f)
                    val player = getOrCreateCategoryPlayer(request.categoryId)
                    player.play(request.trackPath)
                    player.setMixVolume(0f)
                }

                repeat(fadeStepCount) { step ->
                    val progress = (step + 1) / fadeStepCount.toFloat()
                    outgoingCategoryIds.forEach { categoryId ->
                        val startingVolume = categoryMixVolumes[categoryId] ?: 1f
                        categoryPlayers[categoryId]?.setMixVolume(masterVolume * startingVolume * (1f - progress))
                    }
                    incomingRequestsByCategory.values.forEach { request ->
                        categoryPlayers[request.categoryId]?.setMixVolume(
                            masterVolume * request.mixVolume.coerceIn(0f, 1f) * progress,
                        )
                    }
                    delay(fadeStepDurationMs)
                }

                (outgoingCategoryIds - incomingRequestsByCategory.keys).forEach { categoryId ->
                    removeCategory(categoryId)
                }
                currentSceneCategoryIds = incomingRequestsByCategory.keys
                activeSceneId = newSceneId
            }
        }
    }

    override fun removeCategory(categoryId: Long) {
        categoryPlayers.remove(categoryId)?.release()
        categoryMixVolumes.remove(categoryId)
    }

    override fun releaseAll() {
        categoryPlayers.values.forEach(CategoryPlaybackController::release)
        categoryPlayers.clear()
        categoryMixVolumes.clear()
        currentSceneCategoryIds = emptySet()
        activeSceneId = null
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
