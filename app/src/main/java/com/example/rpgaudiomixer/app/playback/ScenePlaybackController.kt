package com.example.rpgaudiomixer.app.playback

import com.example.rpgaudiomixer.domain.media.SceneAudioEngine
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val CROSSFADE_STEPS = 6
private const val CROSSFADE_STEP_DELAY_MS = 120L

data class ScenePlaybackState(
    val currentSceneId: Long? = null,
    val currentSceneName: String? = null,
    val previousSceneId: Long? = null,
    val previousSceneName: String? = null,
    val isCrossfading: Boolean = false,
    val activeSceneIds: Set<Long> = emptySet(),
)

@Singleton
class ScenePlaybackController @Inject constructor(
    private val sceneRepository: SceneRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val sceneAudioEngine: SceneAudioEngine,
    private val mixedMusicPlayer: MixedMusicPlayer,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow(ScenePlaybackState())
    val state: StateFlow<ScenePlaybackState> = _state.asStateFlow()

    private var crossfadeJob: Job? = null

    suspend fun playScene(sceneId: Long) {
        crossfadeJob?.cancel()
        crossfadeJob = scope.launch {
            val targetScene = sceneRepository.observeScene(sceneId).first() ?: return@launch
            val targetSoundscapes = sceneRepository.observeSceneSoundscapes(sceneId).first()
            val currentSceneId = _state.value.currentSceneId
            if (currentSceneId == null || currentSceneId == sceneId) {
                startScene(targetScene, targetSoundscapes)
            } else {
                val previousScene = sceneRepository.observeScene(currentSceneId).first()
                val previousSoundscapes = sceneRepository.observeSceneSoundscapes(currentSceneId).first()
                crossfade(previousScene, previousSoundscapes, targetScene, targetSoundscapes)
            }
        }
        crossfadeJob?.join()
    }

    fun syncAtmosphereVolume(sceneId: Long, volumePercent: Int) {
        if (_state.value.currentSceneId == sceneId) {
            sceneAudioEngine.setMasterVolume(volumePercent.coerceIn(0, 100) / 100f)
        }
    }

    private suspend fun startScene(scene: Scene, soundscapes: List<SceneSoundscape>) {
        stopSceneCategories(exceptSceneId = null)
        sceneAudioEngine.setMasterVolume(scene.atmosphereVolumePercent / 100f)
        mixedMusicPlayer.playLoopingSound("scene:${scene.id}")
        startSceneCategories(soundscapes, fadeFromZero = true)
        _state.value = ScenePlaybackState(
            currentSceneId = scene.id,
            currentSceneName = scene.name,
            activeSceneIds = setOf(scene.id),
        )
    }

    private suspend fun crossfade(
        previousScene: Scene?,
        previousSoundscapes: List<SceneSoundscape>,
        targetScene: Scene,
        targetSoundscapes: List<SceneSoundscape>,
    ) {
        mixedMusicPlayer.playLoopingSound("scene:${targetScene.id}")
        startSceneCategories(targetSoundscapes, fadeFromZero = true)
        _state.value = ScenePlaybackState(
            currentSceneId = targetScene.id,
            currentSceneName = targetScene.name,
            previousSceneId = previousScene?.id,
            previousSceneName = previousScene?.name,
            isCrossfading = true,
            activeSceneIds = buildSet {
                previousScene?.id?.let(::add)
                add(targetScene.id)
            },
        )

        repeat(CROSSFADE_STEPS) { stepIndex ->
            val progress = (stepIndex + 1) / CROSSFADE_STEPS.toFloat()
            previousSoundscapes.forEach { soundscape ->
                sceneAudioEngine.categoryPlayer(soundscape.categoryId)
                    ?.setMixVolume((soundscape.mixVolumePercent / 100f) * (1f - progress))
            }
            targetSoundscapes.forEach { soundscape ->
                sceneAudioEngine.categoryPlayer(soundscape.categoryId)
                    ?.setMixVolume((soundscape.mixVolumePercent / 100f) * progress)
            }
            delay(CROSSFADE_STEP_DELAY_MS)
        }

        previousSoundscapes.forEach { soundscape ->
            sceneAudioEngine.stopCategory(soundscape.categoryId)
        }
        sceneAudioEngine.setMasterVolume(targetScene.atmosphereVolumePercent / 100f)
        _state.value = ScenePlaybackState(
            currentSceneId = targetScene.id,
            currentSceneName = targetScene.name,
            previousSceneId = previousScene?.id,
            previousSceneName = previousScene?.name,
            activeSceneIds = setOf(targetScene.id),
        )
    }

    private suspend fun startSceneCategories(soundscapes: List<SceneSoundscape>, fadeFromZero: Boolean) {
        soundscapes.forEach { soundscape ->
            val category = soundscapeRepository.observeCategory(soundscape.categoryId).first() ?: return@forEach
            val pool = category.tracks.filter { track -> track.intensityLevel == soundscape.intensityLevel }
            val track = pool.firstOrNull() ?: category.tracks.firstOrNull() ?: return@forEach
            soundscapeRepository.incrementPlayCount(track.id)
            sceneAudioEngine.addCategory(soundscape.categoryId, if (fadeFromZero) 0f else soundscape.mixVolumePercent / 100f)
            sceneAudioEngine.playCategory(soundscape.categoryId, track.filePath)
            if (fadeFromZero) {
                sceneAudioEngine.categoryPlayer(soundscape.categoryId)?.setMixVolume(0f)
            }
        }
        if (fadeFromZero) {
            repeat(CROSSFADE_STEPS) { stepIndex ->
                val progress = (stepIndex + 1) / CROSSFADE_STEPS.toFloat()
                soundscapes.forEach { soundscape ->
                    sceneAudioEngine.categoryPlayer(soundscape.categoryId)
                        ?.setMixVolume((soundscape.mixVolumePercent / 100f) * progress)
                }
                delay(CROSSFADE_STEP_DELAY_MS)
            }
        }
    }

    private fun stopSceneCategories(exceptSceneId: Long?) {
        if (_state.value.activeSceneIds.any { it != exceptSceneId }) {
            sceneAudioEngine.releaseAll()
        }
    }
}
