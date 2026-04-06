package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ScenesViewModel @Inject constructor(
    private val repository: SceneRepository,
) : ViewModel() {

    private val _allScenes = MutableStateFlow<List<Scene>>(emptyList())
    private val _tagFilter = MutableStateFlow<String?>(null)
    private val _uiState = MutableStateFlow<ScenesUiState>(ScenesUiState.Loading)
    val uiState: StateFlow<ScenesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.observeAll()
                    .catch { e -> emit(emptyList<Scene>().also { _uiState.value = ScenesUiState.Error(e.message ?: "Unknown error") }) },
                _tagFilter,
            ) { scenes, tag ->
                _allScenes.value = scenes
                if (tag == null) scenes
                else scenes.filter { tag in it.tags }
            }
                .collect { filtered ->
                    _uiState.value = ScenesUiState.Success(filtered)
                }
        }
    }

    fun createScene(name: String, description: String?, tags: List<String>) {
        viewModelScope.launch {
            runCatching { repository.create(name, description, tags) }
                .onFailure { e -> _uiState.value = ScenesUiState.Error(e.message ?: "Unknown error") }
        }
    }

    fun deleteScene(id: Long) {
        viewModelScope.launch {
            runCatching { repository.delete(id) }
                .onFailure { e -> _uiState.value = ScenesUiState.Error(e.message ?: "Unknown error") }
        }
    }

    fun filterByTag(tag: String) {
        _tagFilter.value = tag
    }

    fun clearFilter() {
        _tagFilter.value = null
    }
}
