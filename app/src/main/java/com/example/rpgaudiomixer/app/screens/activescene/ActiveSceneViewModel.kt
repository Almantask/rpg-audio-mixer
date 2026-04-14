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
        val autoPlay: Boolean = false,
        val isLocked: Boolean = false,
        val masterIntensity: Int = 1,
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
    private val _autoPlay = MutableStateFlow(false)
    private val _isLocked = MutableStateFlow(false)
    private val _masterIntensity = MutableStateFlow(1)

    val uiState: StateFlow<ActiveSceneUiState> = combine(
        categoryRepository.observeByScene(sceneId),
        _playingCategories,
        combine(_autoPlay, _isLocked, _masterIntensity) { a, l, m -> Triple(a, l, m) },
    ) { cats, playing, (autoPlay, locked, masterIntensity) ->
        ActiveSceneUiState.Success(
            categories = cats,
            playingCategories = playing,
            autoPlay = autoPlay,
            isLocked = locked,
            masterIntensity = masterIntensity,
        ) as ActiveSceneUiState
    }
        .catch { emit(ActiveSceneUiState.Error(it.message ?: "Unknown")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ActiveSceneUiState.Loading)

    init {
        val autoPlayArg: Boolean = savedStateHandle["autoPlay"] ?: false
        if (autoPlayArg) enableAutoPlay()
    }

    fun toggleCategory(category: SoundscapeCategory) {
        if (_isLocked.value) return
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

    fun triggerD20(category: SoundscapeCategory) {
        if (_isLocked.value) return
        val id = category.name
        musicPlayer.playRandomTrack(id)
        _playingCategories.update { it + id }
    }

    fun enableAutoPlay() {
        _autoPlay.value = true
    }

    // ── Session Lock ────────────────────────────────────────────────────────

    fun lock() {
        _isLocked.value = true
    }

    fun unlock() {
        _isLocked.value = false
    }

    // ── Master Controls ─────────────────────────────────────────────────────

    fun globalStop() {
        musicPlayer.stopAll()
        _playingCategories.value = emptySet()
    }

    fun setMasterIntensity(level: Int) {
        _masterIntensity.value = level
    }
}
