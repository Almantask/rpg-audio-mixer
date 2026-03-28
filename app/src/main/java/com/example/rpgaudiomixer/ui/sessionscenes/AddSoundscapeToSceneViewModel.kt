package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.storage.SceneRepository
import com.example.rpgaudiomixer.domain.storage.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddSoundscapeUiState(
    val categories: List<SoundscapeCategory> = emptyList(),
    val addedCategoryIds: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class AddSoundscapeToSceneViewModel @Inject constructor(
    private val soundscapeRepository: SoundscapeRepository,
    private val sceneRepository: SceneRepository,
) : ViewModel() {

    fun uiState(sceneId: Long): StateFlow<AddSoundscapeUiState> =
        soundscapeRepository.getAllCategories()
            .map { cats ->
                AddSoundscapeUiState(categories = cats.sortedBy { it.name }, isLoading = false)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AddSoundscapeUiState(),
            )

    fun addSoundscape(sceneId: Long, categoryId: Long) {
        viewModelScope.launch {
            sceneRepository.addSoundscapeToScene(sceneId, categoryId)
        }
    }
}
