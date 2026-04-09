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
    val editSceneId: Long? = null,
    val editName: String = "",
    val editTags: Set<String> = emptySet(),
    val customTagDraft: String = "",
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
                    editSceneId = draft.editSceneId,
                    editName = draft.editName,
                    editTags = draft.editTags,
                    customTagDraft = draft.customTagDraft,
                    errorMessage = draft.errorMessage,
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    fun openCreateDialog() {
        draftState.value = SceneDraft(isOpen = true)
    }

    fun dismissCreateDialog() {
        draftState.value = draftState.value.copy(
            isOpen = false,
            name = "",
            errorMessage = null,
        )
    }

    fun updateDraftName(name: String) {
        draftState.update { it.copy(name = name, errorMessage = null) }
    }

    fun updateEditName(name: String) {
        draftState.update { it.copy(editName = name, errorMessage = null) }
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

    fun openEditDialog(scene: Scene) {
        draftState.value = draftState.value.copy(
            editSceneId = scene.id,
            editName = scene.name,
            editTags = scene.tags.toSet(),
            customTagDraft = "",
            errorMessage = null,
        )
    }

    fun dismissEditDialog() {
        draftState.value = draftState.value.copy(
            editSceneId = null,
            editName = "",
            editTags = emptySet(),
            customTagDraft = "",
            errorMessage = null,
        )
    }

    fun toggleEditTag(tag: String) {
        draftState.update { state ->
            state.copy(editTags = if (tag in state.editTags) state.editTags - tag else state.editTags + tag)
        }
    }

    fun updateCustomTagDraft(tag: String) {
        draftState.update { it.copy(customTagDraft = tag, errorMessage = null) }
    }

    fun addCustomTag() {
        val customTag = draftState.value.customTagDraft.trim()
        if (customTag.isBlank()) {
            draftState.update { it.copy(errorMessage = "Tags cannot be blank.") }
            return
        }
        draftState.update { state ->
            state.copy(
                editTags = state.editTags + customTag,
                customTagDraft = "",
                errorMessage = null,
            )
        }
    }

    fun saveEdit() {
        val currentDraft = draftState.value
        val sceneId = currentDraft.editSceneId ?: return
        val updatedName = currentDraft.editName.trim()
        if (updatedName.isBlank()) {
            draftState.update { it.copy(errorMessage = "Every scene needs a name.") }
            return
        }
        val scene = _uiState.value.scenes.firstOrNull { it.id == sceneId } ?: return
        viewModelScope.launch {
            sceneRepository.upsertScene(
                scene.copy(
                    name = updatedName,
                    tags = currentDraft.editTags.toList().sorted(),
                ),
            )
            dismissEditDialog()
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
    val editSceneId: Long? = null,
    val editName: String = "",
    val editTags: Set<String> = emptySet(),
    val customTagDraft: String = "",
    val errorMessage: String? = null,
)
