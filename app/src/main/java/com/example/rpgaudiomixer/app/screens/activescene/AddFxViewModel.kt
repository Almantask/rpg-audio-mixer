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

data class AddFxUiState(
    val availableTracks: List<AudioTrack> = emptyList(),
    val addedFx: List<SoundscapeCategory> = emptyList(),
)

@HiltViewModel
class AddFxViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val audioTrackRepository: AudioTrackRepository,
    private val soundscapeCategoryRepository: SoundscapeCategoryRepository,
) : ViewModel() {

    private val sceneId: Long = checkNotNull(savedStateHandle["sceneId"])

    val uiState: StateFlow<AddFxUiState> = combine(
        audioTrackRepository.observeAll(),
        soundscapeCategoryRepository.observeFxByScene(sceneId),
    ) { tracks, fxList ->
        AddFxUiState(
            availableTracks = tracks,
            addedFx = fxList,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AddFxUiState())

    fun addFx(trackDisplayName: String) {
        viewModelScope.launch {
            soundscapeCategoryRepository.addFx(sceneId, trackDisplayName)
        }
    }
}
