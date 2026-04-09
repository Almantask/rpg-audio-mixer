package com.example.rpgaudiomixer.app.ui.library

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.library.IntensityLevel
import com.example.rpgaudiomixer.domain.library.SoundscapeRepository
import com.example.rpgaudiomixer.domain.library.SoundscapeTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoundscapeCategoryComposerViewModel @Inject constructor(
    private val repository: SoundscapeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId: Long = checkNotNull(savedStateHandle["categoryId"])

    private val categoryState = flow {
        emit(repository.getCategoryById(categoryId))
    }

    private val tracksState = repository.observeTracksByCategory(categoryId)

    val uiState: StateFlow<SoundscapeCategoryComposerUiState> = combine(
        categoryState,
        tracksState
    ) { category, tracks ->
        if (category == null) {
            SoundscapeCategoryComposerUiState.Error("Category not found")
        } else {
            SoundscapeCategoryComposerUiState.Success(category, tracks)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SoundscapeCategoryComposerUiState.Loading
    )

    fun addTrack(name: String, filePath: String) {
        viewModelScope.launch {
            repository.upsertTrack(
                SoundscapeTrack(
                    categoryId = categoryId,
                    name = name,
                    filePath = filePath,
                    intensityLevel = IntensityLevel.I
                )
            )
        }
    }

    fun deleteTrack(id: Long) {
        viewModelScope.launch {
            repository.deleteTrack(id)
        }
    }

    fun updateTrackIntensity(track: SoundscapeTrack, level: IntensityLevel) {
        viewModelScope.launch {
            repository.upsertTrack(track.copy(intensityLevel = level))
        }
    }

    fun updateTrackVolume(track: SoundscapeTrack, volume: Float) {
        viewModelScope.launch {
            repository.upsertTrack(track.copy(mixVolume = volume))
        }
    }
}
