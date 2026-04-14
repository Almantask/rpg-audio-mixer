package com.example.rpgaudiomixer.app.screens.activescene

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.domain.model.AudioTrack
import com.example.rpgaudiomixer.app.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.app.domain.repository.AudioTrackRepository
import com.example.rpgaudiomixer.app.domain.repository.SoundscapeCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddSoundscapeUiState(
    val availableTracks: List<AudioTrack> = emptyList(),
    val addedCategories: List<SoundscapeCategory> = emptyList(),
)

@HiltViewModel
class AddSoundscapeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val audioTrackRepository: AudioTrackRepository,
    private val soundscapeCategoryRepository: SoundscapeCategoryRepository,
) : ViewModel() {

    private val sceneId: Long = checkNotNull(savedStateHandle["sceneId"])

    val uiState: StateFlow<AddSoundscapeUiState> = combine(
        audioTrackRepository.observeAll(),
        soundscapeCategoryRepository.observeByScene(sceneId),
    ) { tracks, categories ->
        AddSoundscapeUiState(
            availableTracks = tracks,
            addedCategories = categories,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AddSoundscapeUiState())

    fun addSoundscape(trackDisplayName: String) {
        viewModelScope.launch {
            soundscapeCategoryRepository.addCategory(sceneId, trackDisplayName)
        }
    }
}
