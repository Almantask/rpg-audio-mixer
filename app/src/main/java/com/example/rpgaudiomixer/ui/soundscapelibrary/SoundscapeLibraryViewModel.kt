package com.example.rpgaudiomixer.ui.soundscapelibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoundscapeLibraryViewModel @Inject constructor(
    private val soundscapeRepository: SoundscapeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SoundscapeLibraryUiState>(SoundscapeLibraryUiState.Loading)
    val uiState: StateFlow<SoundscapeLibraryUiState> = _uiState.asStateFlow()

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            soundscapeRepository.observeAllCategories()
                .catch { e ->
                    _uiState.value = SoundscapeLibraryUiState.Error(e.message ?: "Unknown error")
                }
                .collect { categories ->
                    _uiState.value = SoundscapeLibraryUiState.Success(categories)
                }
        }
    }

    fun createCategory(name: String, iconResId: Int? = null, themeLabel: String? = null) {
        viewModelScope.launch {
            try {
                soundscapeRepository.createCategory(name, iconResId, themeLabel)
                hideCreateDialog()
            } catch (e: Exception) {
                _uiState.value = SoundscapeLibraryUiState.Error(e.message ?: "Failed to create category")
            }
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            try {
                soundscapeRepository.deleteCategory(id)
            } catch (e: Exception) {
                _uiState.value = SoundscapeLibraryUiState.Error(e.message ?: "Failed to delete category")
            }
        }
    }

    fun showCreateDialog() {
        _showCreateDialog.value = true
    }

    fun hideCreateDialog() {
        _showCreateDialog.value = false
    }
}
