package com.example.rpgaudiomixer.ui.soundscapes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Soundscape Library screen.
 */
sealed class SoundscapeLibraryUiState {
    object Loading : SoundscapeLibraryUiState()
    data class Success(val categories: List<CategoryWithTrackCounts>) : SoundscapeLibraryUiState()
    data class Error(val message: String) : SoundscapeLibraryUiState()
}

/**
 * Category with track count breakdown by intensity level.
 */
data class CategoryWithTrackCounts(
    val category: SoundscapeCategory,
    val levelICounts: Int,
    val levelIICounts: Int,
    val levelIIICounts: Int
) {
    val totalTracks: Int = levelICounts + levelIICounts + levelIIICounts
}

/**
 * ViewModel for Soundscape Library screen.
 *
 * Manages soundscape category list and CRUD operations.
 */
@HiltViewModel
class SoundscapeLibraryViewModel @Inject constructor(
    private val soundscapeRepository: SoundscapeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SoundscapeLibraryUiState>(SoundscapeLibraryUiState.Loading)
    val uiState: StateFlow<SoundscapeLibraryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            soundscapeRepository.observeAllCategories()
                .catch { e ->
                    _uiState.value = SoundscapeLibraryUiState.Error(
                        e.message ?: "Failed to load soundscape categories"
                    )
                }
                .collect { categories ->
                    val categoriesWithCounts = categories.map { category ->
                        val levelI = soundscapeRepository.getTrackCountByIntensity(category.id, IntensityLevel.I)
                        val levelII = soundscapeRepository.getTrackCountByIntensity(category.id, IntensityLevel.II)
                        val levelIII = soundscapeRepository.getTrackCountByIntensity(category.id, IntensityLevel.III)
                        CategoryWithTrackCounts(category, levelI, levelII, levelIII)
                    }
                    _uiState.value = SoundscapeLibraryUiState.Success(categoriesWithCounts)
                }
        }
    }

    /**
     * Create a new soundscape category.
     */
    fun createCategory(name: String) {
        viewModelScope.launch {
            try {
                soundscapeRepository.createCategory(name)
            } catch (e: Exception) {
                _uiState.value = SoundscapeLibraryUiState.Error(
                    e.message ?: "Failed to create category"
                )
            }
        }
    }

    /**
     * Delete a soundscape category by ID.
     */
    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            try {
                soundscapeRepository.deleteCategory(id)
            } catch (e: Exception) {
                _uiState.value = SoundscapeLibraryUiState.Error(
                    e.message ?: "Failed to delete category"
                )
            }
        }
    }

    /**
     * Clear the error state.
     */
    fun clearError() {
        if (_uiState.value is SoundscapeLibraryUiState.Error) {
            viewModelScope.launch {
                loadCategories()
            }
        }
    }
}
