package com.example.rpgaudiomixer.ui.activescene

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.audio.SceneAudioEngine
import com.example.rpgaudiomixer.domain.model.ActiveSceneCategory
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.domain.repository.SceneSoundscapeRepository
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ActiveSceneSoundscapesUiState {
    data object Loading : ActiveSceneSoundscapesUiState
    data class Success(
        val categories: List<ActiveSceneCategory>,
        val masterVolume: Float
    ) : ActiveSceneSoundscapesUiState
    data class Error(val message: String) : ActiveSceneSoundscapesUiState
}

@HiltViewModel
class ActiveSceneSoundscapesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneSoundscapeRepository: SceneSoundscapeRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val sceneAudioEngine: SceneAudioEngine,
    private val campaignRepository: CampaignRepository
) : ViewModel() {

    private val sceneId: Long = savedStateHandle.get<String>("sceneId")?.toLongOrNull()
        ?: throw IllegalArgumentException("sceneId is required")

    private val autoplay: Boolean = savedStateHandle.get<Boolean>("autoplay") ?: false
    private val campaignId: Long? = savedStateHandle.get<String>("campaignId")?.toLongOrNull()

    private val _masterVolume = MutableStateFlow(1.0f)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val uiState: StateFlow<ActiveSceneSoundscapesUiState> = combine(
        sceneSoundscapeRepository.observeByScene(sceneId),
        _masterVolume
    ) { crossRefs, masterVolume ->
        val categories = crossRefs.map { crossRef ->
            val category = soundscapeRepository.getCategoryById(crossRef.categoryId)
            val tracks = soundscapeRepository.getTracksByCategoryAndIntensity(
                crossRef.categoryId,
                crossRef.intensityLevel
            )

            ActiveSceneCategory(
                categoryId = crossRef.categoryId,
                categoryName = category?.name ?: "Unknown",
                displayOrder = crossRef.displayOrder,
                mixVolume = crossRef.mixVolume,
                intensityLevel = IntensityLevel.fromValue(crossRef.intensityLevel),
                isPlaying = false, // Will be updated from audio engine
                currentTrackName = null,
                availableTracks = tracks
            )
        }

        ActiveSceneSoundscapesUiState.Success(
            categories = categories,
            masterVolume = masterVolume
        )
    }
        .catch { e ->
            emit(ActiveSceneSoundscapesUiState.Error(e.message ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ActiveSceneSoundscapesUiState.Loading
        )

    init {
        // Update campaign lastPlayedAt if campaignId is provided
        campaignId?.let { id ->
            viewModelScope.launch {
                try {
                    campaignRepository.updateLastPlayedAt(id, sceneId)
                } catch (e: Exception) {
                    // Silently fail - this is a non-critical tracking feature
                }
            }
        }

        // If autoplay is enabled, start playback with fade-in
        if (autoplay) {
            viewModelScope.launch {
                try {
                    // Wait for initial data to load
                    uiState.collect { state ->
                        if (state is ActiveSceneSoundscapesUiState.Success && state.categories.isNotEmpty()) {
                            // Build the scene categories map
                            val sceneCategories = state.categories.mapNotNull { category ->
                                if (category.availableTracks.isNotEmpty()) {
                                    val randomTrack = category.availableTracks.random()
                                    category.categoryId to (randomTrack.filePath to category.mixVolume)
                                } else {
                                    null
                                }
                            }.toMap()

                            if (sceneCategories.isNotEmpty()) {
                                sceneAudioEngine.startPlaybackWithFadeIn(sceneCategories)
                            }

                            // Cancel collection after starting playback once
                            return@collect
                        }
                    }
                } catch (e: Exception) {
                    _errorMessage.value = e.message ?: "Failed to start autoplay"
                }
            }
        }
    }

    fun setMasterVolume(volume: Float) {
        _masterVolume.value = volume.coerceIn(0f, 1f)
        sceneAudioEngine.setMasterVolume(volume)
    }

    fun playCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                val currentState = uiState.value
                if (currentState is ActiveSceneSoundscapesUiState.Success) {
                    val category = currentState.categories.find { it.categoryId == categoryId }
                    if (category != null && category.availableTracks.isNotEmpty()) {
                        val randomTrack = category.availableTracks.random()
                        sceneAudioEngine.addCategory(categoryId)
                        sceneAudioEngine.playCategory(categoryId, randomTrack.filePath)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to play category"
            }
        }
    }

    fun pauseCategory(categoryId: Long) {
        sceneAudioEngine.pauseCategory(categoryId)
    }

    fun resumeCategory(categoryId: Long) {
        sceneAudioEngine.resumeCategory(categoryId)
    }

    fun rollRandom(categoryId: Long) {
        viewModelScope.launch {
            try {
                val currentState = uiState.value
                if (currentState is ActiveSceneSoundscapesUiState.Success) {
                    val category = currentState.categories.find { it.categoryId == categoryId }
                    if (category != null) {
                        sceneAudioEngine.rollRandomTrack(categoryId, category.availableTracks)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to roll random track"
            }
        }
    }

    fun setIntensity(categoryId: Long, intensityLevel: IntensityLevel) {
        viewModelScope.launch {
            try {
                sceneSoundscapeRepository.updateIntensityLevel(sceneId, categoryId, intensityLevel.value)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to set intensity"
            }
        }
    }

    fun setMix(categoryId: Long, volume: Float) {
        viewModelScope.launch {
            try {
                sceneSoundscapeRepository.updateMixVolume(sceneId, categoryId, volume)
                sceneAudioEngine.setCategoryMixVolume(categoryId, volume)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to set mix volume"
            }
        }
    }

    fun reorderCategories(categoryIds: List<Long>) {
        viewModelScope.launch {
            try {
                sceneSoundscapeRepository.updateDisplayOrders(sceneId, categoryIds)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to reorder categories"
            }
        }
    }

    fun removeCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                sceneSoundscapeRepository.removeCategoryFromScene(sceneId, categoryId)
                sceneAudioEngine.removeCategory(categoryId)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to remove category"
            }
        }
    }

    fun addCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                val currentState = uiState.value
                if (currentState is ActiveSceneSoundscapesUiState.Success) {
                    val nextDisplayOrder = currentState.categories.maxOfOrNull { it.displayOrder }?.plus(1) ?: 0
                    sceneSoundscapeRepository.addCategoryToScene(sceneId, categoryId, nextDisplayOrder)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to add category"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        sceneAudioEngine.releaseAll()
    }
}
