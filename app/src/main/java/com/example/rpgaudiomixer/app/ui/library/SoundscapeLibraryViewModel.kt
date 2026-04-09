package com.example.rpgaudiomixer.app.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.library.SoundscapeCategory
import com.example.rpgaudiomixer.domain.library.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoundscapeLibraryViewModel @Inject constructor(
    private val repository: SoundscapeRepository
) : ViewModel() {

    val uiState: StateFlow<SoundscapeLibraryUiState> = repository.observeCategories()
        .map { SoundscapeLibraryUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SoundscapeLibraryUiState.Loading
        )

    fun createCategory(name: String) {
        viewModelScope.launch {
            repository.upsertCategory(SoundscapeCategory(name = name))
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            repository.deleteCategory(id)
        }
    }
}
