package com.example.rpgaudiomixer.ui.activescene

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.common.UiState
import com.example.rpgaudiomixer.domain.media.SceneAudioEngine
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.repository.SceneSoundscapeRepository
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActiveSceneSoundscapeUiState(
    val categories: List<CategoryState> = emptyList(),
    val masterVolume: Float = 1.0f
)

data class CategoryState(
    val sceneSoundscape: SceneSoundscape,
    val isPlaying: Boolean = false,
    val currentTrackName: String? = null,
    val availableIntensities: Set<IntensityLevel> = emptySet()
)

@HiltViewModel
class ActiveSceneSoundscapesViewModel @Inject constructor(
    private val sceneSoundscapeRepository: SceneSoundscapeRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val audioEngine: SceneAudioEngine
) : ViewModel() {

    private val _sceneId = MutableStateFlow<Long?>(null)

    private val _uiState = MutableStateFlow<UiState<ActiveSceneSoundscapeUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<ActiveSceneSoundscapeUiState>> = _uiState.asStateFlow()

    // Track states for each category
    private val categoryTracks = mutableMapOf<Long, List<SoundscapeTrack>>()

    fun loadScene(sceneId: Long) {
        _sceneId.value = sceneId

        viewModelScope.launch {
            try {
                sceneSoundscapeRepository.observeByScene(sceneId).collect { sceneSoundscapes ->
                    // Load tracks for each category
                    sceneSoundscapes.forEach { sceneSoundscape ->
                        soundscapeRepository.observeTracksByCategory(sceneSoundscape.categoryId)
                            .onEach { tracks ->
                                categoryTracks[sceneSoundscape.categoryId] = tracks
                            }
                            .launchIn(viewModelScope)
                    }

                    // Update UI state
                    val categoryStates = sceneSoundscapes.map { sceneSoundscape ->
                        val tracks = categoryTracks[sceneSoundscape.categoryId] ?: emptyList()
                        val availableIntensities = tracks.map { it.intensityLevel }.toSet()
                        val player = audioEngine.getPlayer(sceneSoundscape.categoryId)

                        CategoryState(
                            sceneSoundscape = sceneSoundscape,
                            isPlaying = player?.isPlaying?.value ?: false,
                            currentTrackName = null, // Will be updated when tracks play
                            availableIntensities = availableIntensities
                        )
                    }

                    _uiState.value = UiState.Success(
                        ActiveSceneSoundscapeUiState(
                            categories = categoryStates,
                            masterVolume = audioEngine.masterVolume
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load scene soundscapes")
            }
        }
    }

    fun setMasterVolume(volume: Float) {
        audioEngine.setMasterVolume(volume)
        updateMasterVolumeInState(volume)
    }

    fun playCategory(categoryId: Long) {
        viewModelScope.launch {
            val sceneId = _sceneId.value ?: return@launch
            val currentState = (_uiState.value as? UiState.Success)?.data ?: return@launch
            val categoryState = currentState.categories.find { it.sceneSoundscape.categoryId == categoryId }
                ?: return@launch

            val player = audioEngine.addCategory(categoryId)
            val tracks = categoryTracks[categoryId] ?: emptyList()
            val filteredTracks = tracks.filter { it.intensityLevel == categoryState.sceneSoundscape.intensityLevel }

            if (filteredTracks.isNotEmpty()) {
                player.setMixVolume(categoryState.sceneSoundscape.mixVolume)
                player.rollRandomTrack(filteredTracks)
                updateCategoryPlayingState(categoryId, true, filteredTracks.random().name)
            }
        }
    }

    fun pauseCategory(categoryId: Long) {
        audioEngine.getPlayer(categoryId)?.pause()
        updateCategoryPlayingState(categoryId, false, null)
    }

    fun resumeCategory(categoryId: Long) {
        audioEngine.getPlayer(categoryId)?.resume()
        updateCategoryPlayingState(categoryId, true, null)
    }

    fun rollRandom(categoryId: Long) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? UiState.Success)?.data ?: return@launch
            val categoryState = currentState.categories.find { it.sceneSoundscape.categoryId == categoryId }
                ?: return@launch

            val tracks = categoryTracks[categoryId] ?: emptyList()
            val filteredTracks = tracks.filter { it.intensityLevel == categoryState.sceneSoundscape.intensityLevel }

            if (filteredTracks.isNotEmpty()) {
                val player = audioEngine.getPlayer(categoryId)
                val randomTrack = filteredTracks.random()
                player?.rollRandomTrack(filteredTracks)
                updateCategoryPlayingState(categoryId, true, randomTrack.name)
            }
        }
    }

    fun setIntensity(categoryId: Long, intensityLevel: IntensityLevel) {
        viewModelScope.launch {
            val sceneId = _sceneId.value ?: return@launch
            sceneSoundscapeRepository.updateIntensityLevel(sceneId, categoryId, intensityLevel)

            // If currently playing, switch to new intensity
            val player = audioEngine.getPlayer(categoryId)
            if (player?.isPlaying?.value == true) {
                val tracks = categoryTracks[categoryId] ?: emptyList()
                val filteredTracks = tracks.filter { it.intensityLevel == intensityLevel }
                if (filteredTracks.isNotEmpty()) {
                    player.rollRandomTrack(filteredTracks)
                    updateCategoryPlayingState(categoryId, true, filteredTracks.random().name)
                }
            }
        }
    }

    fun setMix(categoryId: Long, volume: Float) {
        viewModelScope.launch {
            val sceneId = _sceneId.value ?: return@launch
            sceneSoundscapeRepository.updateMixVolume(sceneId, categoryId, volume)
            audioEngine.getPlayer(categoryId)?.setMixVolume(volume)
        }
    }

    fun reorderCategories(categoryIds: List<Long>) {
        viewModelScope.launch {
            val sceneId = _sceneId.value ?: return@launch
            sceneSoundscapeRepository.updateDisplayOrders(sceneId, categoryIds)
        }
    }

    fun removeCategory(categoryId: Long) {
        viewModelScope.launch {
            val sceneId = _sceneId.value ?: return@launch
            audioEngine.removeCategory(categoryId)
            sceneSoundscapeRepository.remove(sceneId, categoryId)
        }
    }

    fun addCategory(categoryId: Long) {
        viewModelScope.launch {
            val sceneId = _sceneId.value ?: return@launch
            val currentState = (_uiState.value as? UiState.Success)?.data ?: return@launch
            val nextOrder = currentState.categories.size

            sceneSoundscapeRepository.add(
                sceneId = sceneId,
                categoryId = categoryId,
                displayOrder = nextOrder,
                mixVolume = 1.0f,
                intensityLevel = IntensityLevel.I
            )
        }
    }

    private fun updateMasterVolumeInState(volume: Float) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.value = UiState.Success(
            currentState.copy(masterVolume = volume)
        )
    }

    private fun updateCategoryPlayingState(categoryId: Long, isPlaying: Boolean, trackName: String?) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val updatedCategories = currentState.categories.map { categoryState ->
            if (categoryState.sceneSoundscape.categoryId == categoryId) {
                categoryState.copy(
                    isPlaying = isPlaying,
                    currentTrackName = trackName ?: categoryState.currentTrackName
                )
            } else {
                categoryState
            }
        }
        _uiState.value = UiState.Success(
            currentState.copy(categories = updatedCategories)
        )
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.releaseAll()
    }
}
