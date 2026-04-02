package com.example.rpgaudiomixer.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.FXTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.repository.LibraryRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddToSceneUiState(
    val sceneName: String = "",
    val items: List<Any> = emptyList(),   // List<SoundscapeCategory> or List<FXTrack>
    val selectedIds: Set<Long> = emptySet(),
    val isSaving: Boolean = false,
)

@HiltViewModel
class AddToSceneViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository,
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    val sceneId: Long = checkNotNull(savedStateHandle["sceneId"])
    val mode: String = savedStateHandle["mode"] ?: "soundscape"  // "soundscape" | "fx"

    private val _uiState = MutableStateFlow(AddToSceneUiState())
    val uiState: StateFlow<AddToSceneUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val scene = sceneRepository.getSceneById(sceneId)
            _uiState.value = _uiState.value.copy(sceneName = scene?.name ?: "")
        }
        if (mode == "soundscape") {
            viewModelScope.launch {
                libraryRepository.getAllCategories().collect { cats ->
                    _uiState.value = _uiState.value.copy(items = cats)
                }
            }
        } else {
            viewModelScope.launch {
                libraryRepository.getAllFXTracks().collect { fx ->
                    _uiState.value = _uiState.value.copy(items = fx)
                }
            }
        }
    }

    fun toggleSelection(id: Long) {
        val current = _uiState.value.selectedIds
        _uiState.value = _uiState.value.copy(
            selectedIds = if (id in current) current - id else current + id
        )
    }

    fun confirmSelection() {
        val ids = _uiState.value.selectedIds
        _uiState.value = _uiState.value.copy(isSaving = true)
        viewModelScope.launch {
            ids.forEach { id ->
                if (mode == "soundscape") {
                    sceneRepository.addCategoryToScene(sceneId, id)
                } else {
                    sceneRepository.addFXToScene(sceneId, id)
                }
            }
            _uiState.value = _uiState.value.copy(isSaving = false, selectedIds = emptySet())
        }
    }
}
