package com.example.rpgaudiomixer.ui.library.soundscapes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.common.UiState
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryWithCounts(
    val category: SoundscapeCategory,
    val trackCountByLevel: Map<IntensityLevel, Int>
)

@HiltViewModel
class SoundscapeLibraryViewModel @Inject constructor(
    private val soundscapeRepository: SoundscapeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<CategoryWithCounts>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<CategoryWithCounts>>> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                soundscapeRepository.observeAllCategories().collect { categories ->
                    val categoriesWithCounts = categories.map { category ->
                        // Get tracks for this category to count by intensity
                        val tracks = soundscapeRepository.observeTracksByCategory(category.id).first()
                        val countByLevel = IntensityLevel.entries.associateWith { level ->
                            tracks.count { it.intensityLevel == level }
                        }
                        CategoryWithCounts(category, countByLevel)
                    }
                    _uiState.value = UiState.Success(categoriesWithCounts)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load soundscape categories")
            }
        }
    }

    fun createCategory(name: String) {
        viewModelScope.launch {
            try {
                soundscapeRepository.createCategory(name, null, null)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to create category")
            }
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                soundscapeRepository.deleteCategory(categoryId)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to delete category")
            }
        }
    }
}
