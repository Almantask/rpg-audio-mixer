package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.trash.SceneTrashRepository
import com.example.rpgaudiomixer.domain.trash.TrashVaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScenesUiState(
    val isLoading: Boolean = true,
    val scenes: List<Scene> = emptyList(),
    val showCreateDialog: Boolean = false,
    val draftName: String = "",
    val errorMessage: String? = null,
)

@HiltViewModel
class ScenesViewModel @Inject constructor(
    private val sceneRepository: SceneRepository,
    private val sceneTrashRepository: SceneTrashRepository,
    private val trashVaultRepository: TrashVaultRepository,
) : ViewModel() {
    private val draftState = MutableStateFlow(SceneDraft())
    private val _uiState = MutableStateFlow(ScenesUiState())
    val uiState: StateFlow<ScenesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(sceneRepository.observeScenes(), draftState) { scenes, draft ->
                ScenesUiState(
                    isLoading = false,
                    scenes = scenes,
                    showCreateDialog = draft.isOpen,
                    draftName = draft.name,
                    errorMessage = draft.errorMessage,
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    fun openCreateDialog() {
        draftState.value = SceneDraft(isOpen = true)
    }

    fun dismissCreateDialog() {
        draftState.value = SceneDraft()
    }

    fun updateDraftName(name: String) {
        draftState.update { it.copy(name = name, errorMessage = null) }
    }

    fun confirmCreateScene() {
        val draft = draftState.value
        if (draft.name.isBlank()) {
            draftState.update { it.copy(errorMessage = "Every scene needs a name.") }
            return
        }

        viewModelScope.launch {
            sceneRepository.upsertScene(Scene(name = draft.name.trim()))
            dismissCreateDialog()
        }
    }

    fun deleteScene(scene: Scene) {
        viewModelScope.launch {
            trashVaultRepository.trashScene(scene.id)
            sceneRepository.deleteScene(scene.id)
            sceneTrashRepository.recordDeletedScene(scene.name)
        }
    }

    fun clearError() {
        draftState.update { it.copy(errorMessage = null) }
    }
}

private data class SceneDraft(
    val isOpen: Boolean = false,
    val name: String = "",
    val errorMessage: String? = null,
)
