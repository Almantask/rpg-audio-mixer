package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.delay

class SceneAudioEngine(
    private val trackFactory: TrackFactory,
    private val maxConcurrentCategories: Int = DEFAULT_MAX_CONCURRENT_SOUNDSCAPES,
) {
    private val categoryPlayers = linkedMapOf<Long, CategoryPlayer>()
    private val categoryMixLevels = mutableMapOf<Long, Float>()
    private val playbackOrder = ArrayDeque<Long>()
    private var masterVolume: Float = 1f
    var activeSceneId: Long? = null
        private set
    private var activeSceneCategoryIds: Set<Long> = emptySet()

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
        val normalized = mixVolume.coerceIn(0f, 1f)
        categoryMixLevels[categoryId] = normalized
        addCategory(categoryId).setMixVolume(normalized)
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
        categoryMixLevels.remove(categoryId)
        playbackOrder.remove(categoryId)
        activeSceneCategoryIds = activeSceneCategoryIds - categoryId
    }

    fun releaseAll() {
        categoryPlayers.values.forEach { player ->
            player.release()
        }
        categoryPlayers.clear()
        categoryMixLevels.clear()
        playbackOrder.clear()
        activeSceneCategoryIds = emptySet()
        activeSceneId = null
    }

    suspend fun startScene(
        sceneId: Long,
        categories: List<ScenePlaybackCategory>,
        stepDelayMillis: Long = DEFAULT_SCENE_FADE_STEP_DELAY_MILLIS,
    ) {
        categories.forEach { category ->
            setCategoryMix(category.categoryId, 0f)
            playCategory(category.categoryId, category.trackPath)
        }
        fadeCategories(
            startMixByCategory = categories.associate { it.categoryId to 0f },
            endMixByCategory = categories.associate { it.categoryId to it.targetMixVolume },
            stepDelayMillis = stepDelayMillis,
        )
        activeSceneId = sceneId
        activeSceneCategoryIds = categories.map { it.categoryId }.toSet()
    }

    suspend fun switchToScene(
        sceneId: Long,
        categories: List<ScenePlaybackCategory>,
        stepDelayMillis: Long = DEFAULT_SCENE_FADE_STEP_DELAY_MILLIS,
    ) {
        val previousCategoryIds = activeSceneCategoryIds
        val targetCategoryIds = categories.map { it.categoryId }.toSet()
        val categoriesToFadeOut = previousCategoryIds - targetCategoryIds
        val fadeOutStarts = categoriesToFadeOut.associateWith { categoryId ->
            categoryMixLevels[categoryId] ?: 1f
        }

        categories.forEach { category ->
            setCategoryMix(category.categoryId, 0f)
            playCategory(category.categoryId, category.trackPath)
        }

        fadeCategories(
            startMixByCategory = fadeOutStarts + categories.associate { it.categoryId to 0f },
            endMixByCategory = categoriesToFadeOut.associateWith { 0f } +
                categories.associate { it.categoryId to it.targetMixVolume },
            stepDelayMillis = stepDelayMillis,
        )

        categoriesToFadeOut.forEach { categoryId ->
            stopCategory(categoryId)
            removeCategory(categoryId)
        }

        activeSceneId = sceneId
        activeSceneCategoryIds = targetCategoryIds
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

    private suspend fun fadeCategories(
        startMixByCategory: Map<Long, Float>,
        endMixByCategory: Map<Long, Float>,
        stepDelayMillis: Long,
    ) {
        (1..DEFAULT_SCENE_FADE_STEPS).forEach { step ->
            val progress = step.toFloat() / DEFAULT_SCENE_FADE_STEPS.toFloat()
            endMixByCategory.forEach { (categoryId, endMix) ->
                val startMix = startMixByCategory[categoryId] ?: 0f
                val nextMix = startMix + ((endMix - startMix) * progress)
                setCategoryMix(categoryId, nextMix)
            }
            if (stepDelayMillis > 0L) {
                delay(stepDelayMillis)
            }
        }
    }

    companion object {
        const val DEFAULT_MAX_CONCURRENT_SOUNDSCAPES: Int = 10
        const val DEFAULT_SCENE_FADE_STEPS: Int = 5
        const val DEFAULT_SCENE_FADE_STEP_DELAY_MILLIS: Long = 80L
    }
}

data class ScenePlaybackCategory(
    val categoryId: Long,
    val trackPath: String,
    val targetMixVolume: Float,
)
