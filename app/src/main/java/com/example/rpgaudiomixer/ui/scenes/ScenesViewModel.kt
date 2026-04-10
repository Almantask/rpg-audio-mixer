package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class ScenesViewModel @Inject constructor(
    private val sceneRepository: SceneRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Scene>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Scene>>> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        observeScenes()
    }

    fun createScene(
        name: String,
        description: String?,
        tagsText: String,
    ) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            _errorMessage.value = "Scene name is required."
            return
        }

        viewModelScope.launch {
            runCatching {
                sceneRepository.createScene(
                    name = trimmedName,
                    description = description,
                    tags = tagsText.toTagList(),
                )
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to create scene."
            }
        }
    }

    fun deleteScene(sceneId: Long) {
        viewModelScope.launch {
            runCatching {
                sceneRepository.deleteScene(sceneId)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to delete scene."
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    private fun observeScenes() {
        viewModelScope.launch {
            sceneRepository.observeAll()
                .catch { throwable ->
                    _uiState.value = UiState.Error(
                        throwable.message ?: "Unable to load scenes.",
                    )
                }
                .collect { scenes ->
                    _uiState.value = UiState.Success(scenes)
                }
        }
    }
}

private fun String.toTagList(): List<String> {
    return split(",")
        .map { tag -> tag.trim() }
        .filter { tag -> tag.isNotEmpty() }
        .distinct()
}
