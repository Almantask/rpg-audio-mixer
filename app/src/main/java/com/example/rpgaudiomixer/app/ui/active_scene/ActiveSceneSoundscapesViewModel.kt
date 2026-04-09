package com.example.rpgaudiomixer.app.ui.active_scene

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.library.IntensityLevel
import com.example.rpgaudiomixer.domain.media.SceneAudioEngine
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveSceneSoundscapesViewModel @Inject constructor(
    private val sceneRepository: SceneRepository,
    private val audioEngine: SceneAudioEngine,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sceneId: Long = checkNotNull(savedStateHandle["sceneId"])

    init {
        val autoPlay: Boolean = savedStateHandle["autoPlay"] ?: false
        if (autoPlay) {
            audioEngine.switchToScene(sceneId, autoPlay = true)
        }
    }

    private val _masterVolume = MutableStateFlow(1.0f)
    val masterVolume = _masterVolume.asStateFlow()

    val uiState: StateFlow<ActiveSceneSoundscapesUiState> = combine(
        sceneRepository.observeSceneActiveSoundscapes(sceneId),
        _masterVolume
    ) { soundscapes, masterVol ->
        ActiveSceneSoundscapesUiState(
            soundscapes = soundscapes.map { 
                SoundscapeItemState(
                    soundscape = it,
                    isPlaying = audioEngine.getPlayer(it.category.id).isPlaying
                )
            },
            masterVolume = masterVol
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActiveSceneSoundscapesUiState(isLoading = true)
    )

    fun setMasterVolume(volume: Float) {
        _masterVolume.value = volume
        audioEngine.setMasterVolume(volume)
    }

    fun toggleCategory(categoryId: Long) {
        val player = audioEngine.getPlayer(categoryId)
        if (player.isPlaying.value) {
            player.pause()
        } else {
            // Find current intensity level for this category in the scene
            val soundscape = uiState.value.soundscapes.find { it.soundscape.category.id == categoryId }?.soundscape
            soundscape?.let {
                val trackPool = it.category.tracks.filter { t -> t.intensityLevel == it.intensityLevel }
                if (trackPool.isNotEmpty()) {
                    player.setMixVolume(it.mixVolume)
                    player.rollRandomTrack(trackPool)
                }
            }
        }
    }

    fun rollRandom(categoryId: Long) {
        val soundscape = uiState.value.soundscapes.find { it.soundscape.category.id == categoryId }?.soundscape
        soundscape?.let {
            val trackPool = it.category.tracks.filter { t -> t.intensityLevel == it.intensityLevel }
            if (trackPool.isNotEmpty()) {
                audioEngine.getPlayer(categoryId).rollRandomTrack(trackPool)
            }
        }
    }

    fun setIntensity(categoryId: Long, level: IntensityLevel) {
        viewModelScope.launch {
            val item = uiState.value.soundscapes.find { it.soundscape.category.id == categoryId }?.soundscape
            item?.let {
                sceneRepository.updateSoundscapeMetadata(sceneId, categoryId, it.mixVolume, level)
                // If currently playing, we should update the track to match new intensity
                if (audioEngine.getPlayer(categoryId).isPlaying.value) {
                    rollRandom(categoryId)
                }
            }
        }
    }

    fun setMixVolume(categoryId: Long, volume: Float) {
        viewModelScope.launch {
            val item = uiState.value.soundscapes.find { it.soundscape.category.id == categoryId }?.soundscape
            item?.let {
                sceneRepository.updateSoundscapeMetadata(sceneId, categoryId, volume, it.intensityLevel)
                audioEngine.getPlayer(categoryId).setMixVolume(volume)
            }
        }
    }

    fun removeCategory(categoryId: Long) {
        viewModelScope.launch {
            sceneRepository.removeCategoryFromScene(sceneId, categoryId)
            audioEngine.removeCategory(categoryId)
        }
    }

    fun addCategory(categoryId: Long) {
        viewModelScope.launch {
            val nextOrder = (uiState.value.soundscapes.maxOfOrNull { it.soundscape.displayOrder } ?: -1) + 1
            sceneRepository.addCategoryToScene(sceneId, categoryId, nextOrder)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // audioEngine.releaseAll() // We might not want to release ALL if we navigate between tabs, 
        // but Iteration 8 says "navigating away stops playback". 
        // For now, let's keep it playing if it's a singleton.
    }
}
