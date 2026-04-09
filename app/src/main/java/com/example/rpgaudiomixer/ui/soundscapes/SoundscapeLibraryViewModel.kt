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

data class CategoryWithCounts(
    val category: SoundscapeCategory,
    val countI: Int,
    val countII: Int,
    val countIII: Int
)

sealed interface SoundscapeLibraryUiState {
    data object Loading : SoundscapeLibraryUiState
    data class Success(val categoriesWithCounts: List<CategoryWithCounts>) : SoundscapeLibraryUiState
    data class Error(val message: String) : SoundscapeLibraryUiState
}

@HiltViewModel
class SoundscapeLibraryViewModel @Inject constructor(
    private val soundscapeRepository: SoundscapeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SoundscapeLibraryUiState>(SoundscapeLibraryUiState.Loading)
    val uiState: StateFlow<SoundscapeLibraryUiState> = _uiState.asStateFlow()

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

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
                    // For each category, fetch track counts for each intensity level
                    val categoriesWithCounts = categories.map { category ->
                        CategoryWithCounts(
                            category = category,
                            countI = soundscapeRepository.getTrackCountByCategoryAndIntensity(
                                category.id,
                                IntensityLevel.I
                            ),
                            countII = soundscapeRepository.getTrackCountByCategoryAndIntensity(
                                category.id,
                                IntensityLevel.II
                            ),
                            countIII = soundscapeRepository.getTrackCountByCategoryAndIntensity(
                                category.id,
                                IntensityLevel.III
                            )
                        )
                    }
                    _uiState.value = SoundscapeLibraryUiState.Success(categoriesWithCounts)
                }
        }
    }

    fun showCreateDialog() {
        _showCreateDialog.value = true
    }

    fun hideCreateDialog() {
        _showCreateDialog.value = false
    }

    fun createCategory(name: String) {
        viewModelScope.launch {
            try {
                soundscapeRepository.createCategory(name)
                hideCreateDialog()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to create category"
            }
        }
    }

    fun deleteCategory(category: SoundscapeCategory) {
        viewModelScope.launch {
            try {
                soundscapeRepository.deleteCategory(category)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete category"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
