package com.example.rpgaudiomixer.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryWithCounts(
    val category: SoundscapeCategory,
    val level1Count: Int,
    val level2Count: Int,
    val level3Count: Int
)

sealed class SoundscapeLibraryUiState {
    data object Loading : SoundscapeLibraryUiState()
    data class Success(val categories: List<CategoryWithCounts>) : SoundscapeLibraryUiState()
    data class Error(val message: String) : SoundscapeLibraryUiState()
}

@HiltViewModel
class SoundscapeLibraryViewModel @Inject constructor(
    private val soundscapeRepository: SoundscapeRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SoundscapeLibraryUiState> = combine(
        soundscapeRepository.observeAllCategories(),
        _errorMessage
    ) { categories, error ->
        if (error != null) {
            SoundscapeLibraryUiState.Error(error)
        } else {
            val categoriesWithCounts = categories.map { category ->
                val tracks = soundscapeRepository.observeTracksByCategory(category.id)
                    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
                    .value

                CategoryWithCounts(
                    category = category,
                    level1Count = tracks.count { it.intensityLevel == IntensityLevel.I },
                    level2Count = tracks.count { it.intensityLevel == IntensityLevel.II },
                    level3Count = tracks.count { it.intensityLevel == IntensityLevel.III }
                )
            }
            SoundscapeLibraryUiState.Success(categoriesWithCounts)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SoundscapeLibraryUiState.Loading
    )

    fun createCategory(name: String) {
        viewModelScope.launch {
            try {
                val category = SoundscapeCategory(name = name)
                soundscapeRepository.createCategory(category)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create category: ${e.message}"
            }
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                soundscapeRepository.deleteCategory(categoryId)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete category: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
