package com.example.rpgaudiomixer.app.screens.activescene

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.app.domain.repository.SoundscapeCategoryRepository
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed interface ActiveSceneUiState {
    data object Loading : ActiveSceneUiState
    data class Success(
        val categories: List<SoundscapeCategory>,
        val playingCategories: Set<String>,
    ) : ActiveSceneUiState
    data class Error(val message: String) : ActiveSceneUiState
}

@HiltViewModel
class ActiveSceneViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: SoundscapeCategoryRepository,
    private val musicPlayer: MixedMusicPlayer,
) : ViewModel() {

    private val sceneId: Long = checkNotNull(savedStateHandle["sceneId"])
    private val _playingCategories = MutableStateFlow<Set<String>>(emptySet())

    val uiState: StateFlow<ActiveSceneUiState> = combine(
        categoryRepository.observeByScene(sceneId),
        _playingCategories,
    ) { cats, playing ->
        ActiveSceneUiState.Success(cats, playing) as ActiveSceneUiState
    }
        .catch { emit(ActiveSceneUiState.Error(it.message ?: "Unknown")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ActiveSceneUiState.Loading)

    fun toggleCategory(category: SoundscapeCategory) {
        val id = category.name
        _playingCategories.update { current ->
            if (id in current) {
                musicPlayer.pauseLoopingSound(id)
                current - id
            } else {
                musicPlayer.playLoopingSound(id)
                current + id
            }
        }
    }
}
