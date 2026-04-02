package com.example.rpgaudiomixer.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.Track
import com.example.rpgaudiomixer.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ComposerUiState(
    val categoryName: String = "",
    val tracks: List<Track> = emptyList(),
    val isSaved: Boolean = false,
    val isNewCategory: Boolean = false,
)

@HiltViewModel
class SoundscapeComposerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    /** categoryId == -1L means create new */
    val categoryId: Long = savedStateHandle["categoryId"] ?: -1L

    private val _uiState = MutableStateFlow(ComposerUiState(isNewCategory = categoryId == -1L))
    val uiState: StateFlow<ComposerUiState> = _uiState.asStateFlow()

    init {
        if (categoryId != -1L) {
            viewModelScope.launch {
                val category = libraryRepository.getCategoryById(categoryId) ?: return@launch
                _uiState.value = _uiState.value.copy(
                    categoryName = category.name,
                    tracks = libraryRepository.getTracksForCategory(categoryId),
                )
            }
        }
    }

    fun setCategoryName(name: String) {
        _uiState.value = _uiState.value.copy(categoryName = name)
    }

    fun addTrack(name: String, filePath: String, intensityLevel: IntensityLevel) {
        viewModelScope.launch {
            val resolvedCategoryId = ensureCategoryExists()
            val track = Track(
                categoryId = resolvedCategoryId,
                name = name,
                filePath = filePath,
                intensityLevel = intensityLevel,
            )
            libraryRepository.upsertTrack(track)
            reloadTracks(resolvedCategoryId)
        }
    }

    fun updateTrackVolume(trackId: Long, volume: Float) {
        viewModelScope.launch {
            libraryRepository.updateTrackMixVolume(trackId, volume)
            reloadTracks()
        }
    }

    fun deleteTrack(trackId: Long) {
        viewModelScope.launch {
            libraryRepository.deleteTrack(trackId)
            reloadTracks()
        }
    }

    fun save() {
        viewModelScope.launch {
            ensureCategoryExists()
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }

    private suspend fun ensureCategoryExists(): Long {
        return if (categoryId == -1L) {
            val name = _uiState.value.categoryName.ifBlank { "New Category" }
            libraryRepository.upsertCategory(SoundscapeCategory(name = name))
        } else {
            val name = _uiState.value.categoryName
            libraryRepository.upsertCategory(SoundscapeCategory(id = categoryId, name = name))
            categoryId
        }
    }

    private suspend fun reloadTracks(targetCategoryId: Long? = categoryId.takeIf { it != -1L }) {
        if (targetCategoryId == null) return
        _uiState.value = _uiState.value.copy(
            tracks = libraryRepository.getTracksForCategory(targetCategoryId)
        )
    }
}
