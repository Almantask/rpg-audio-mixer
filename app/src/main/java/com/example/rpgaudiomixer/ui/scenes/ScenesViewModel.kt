package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface ScenesUiState {
    data object Loading : ScenesUiState
    data class Success(val scenes: List<Scene>) : ScenesUiState
    data class Error(val message: String) : ScenesUiState
}

@HiltViewModel
class ScenesViewModel @Inject constructor(
    private val sceneRepository: SceneRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ScenesUiState>(ScenesUiState.Loading)
    val uiState: StateFlow<ScenesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sceneRepository.observeScenes()
                .catch { throwable ->
                    _uiState.value = ScenesUiState.Error(
                        throwable.message ?: "Unable to load scenes.",
                    )
                }
                .collect { scenes ->
                    _uiState.value = ScenesUiState.Success(scenes)
                }
        }
    }

    fun createScene(
        name: String,
        description: String?,
        tags: List<String>,
    ) {
        if (name.trim().isBlank()) {
            return
        }

        viewModelScope.launch {
            sceneRepository.createScene(name.trim(), description, normalizeSceneTags(tags))
        }
    }

    fun deleteScene(sceneId: Long) {
        viewModelScope.launch {
            sceneRepository.deleteScene(sceneId)
        }
    }

    private fun normalizeSceneTags(tags: List<String>): List<String> {
        return tags.mapNotNull { rawTag ->
            val trimmedTag = rawTag.trim()
            if (trimmedTag.isBlank()) {
                null
            } else {
                predefinedSceneTags.firstOrNull { predefinedTag ->
                    predefinedTag.equals(trimmedTag, ignoreCase = true)
                } ?: trimmedTag
            }
        }.distinctBy { normalizedTag -> normalizedTag.lowercase() }
    }
}
