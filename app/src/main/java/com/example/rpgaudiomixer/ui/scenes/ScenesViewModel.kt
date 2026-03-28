package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.storage.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SceneSort { NAME, LAST_USED }

data class ScenesUiState(
    val scenes: List<Scene> = emptyList(),
    val searchQuery: String = "",
    val sort: SceneSort = SceneSort.NAME,
    val isLoading: Boolean = true,
)

@HiltViewModel
class ScenesViewModel @Inject constructor(
    private val sceneRepository: SceneRepository,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val sort = MutableStateFlow(SceneSort.NAME)

    val uiState: StateFlow<ScenesUiState> = combine(
        sceneRepository.getAllScenes(),
        searchQuery,
        sort,
    ) { scenes, query, currentSort ->
        val filtered = if (query.isBlank()) scenes
        else scenes.filter { it.name.contains(query, ignoreCase = true) }

        val sorted = when (currentSort) {
            SceneSort.NAME -> filtered.sortedBy { it.name }
            SceneSort.LAST_USED -> filtered.sortedByDescending { it.createdAt }
        }

        ScenesUiState(scenes = sorted, searchQuery = query, sort = currentSort, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScenesUiState(),
    )

    fun onSearchQueryChanged(q: String) { searchQuery.value = q }
    fun onSortChanged(s: SceneSort) { sort.value = s }

    fun addScene(name: String, description: String) {
        viewModelScope.launch {
            sceneRepository.insert(Scene(name = name, description = description))
        }
    }

    fun deleteScene(scene: Scene) {
        viewModelScope.launch { sceneRepository.delete(scene) }
    }
}
