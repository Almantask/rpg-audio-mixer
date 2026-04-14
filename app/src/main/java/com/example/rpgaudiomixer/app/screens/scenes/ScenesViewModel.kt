package com.example.rpgaudiomixer.app.screens.scenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.domain.model.Scene
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ScenesUiState {
    data object Loading : ScenesUiState
    data class Success(val scenes: List<Scene>) : ScenesUiState
    data class Error(val message: String) : ScenesUiState
}

@HiltViewModel
class ScenesViewModel @Inject constructor(
    private val repository: SceneRepository
) : ViewModel() {

    val uiState: StateFlow<ScenesUiState> = repository.observeAll()
        .map<List<Scene>, ScenesUiState> { ScenesUiState.Success(it) }
        .catch { emit(ScenesUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ScenesUiState.Loading)

    fun createScene(name: String) {
        viewModelScope.launch { repository.createScene(name) }
    }

    fun deleteScene(scene: Scene) {
        viewModelScope.launch { repository.deleteScene(scene) }
    }

    fun cloneScene(scene: Scene, newName: String) {
        viewModelScope.launch { repository.cloneScene(scene.id, newName) }
    }
}
