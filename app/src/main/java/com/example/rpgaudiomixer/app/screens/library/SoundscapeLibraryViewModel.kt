package com.example.rpgaudiomixer.app.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoundscapeLibraryViewModel @Inject constructor(
    private val repository: SoundscapeRepository
) : ViewModel() {

    val categories: StateFlow<List<SoundscapeCategory>> = repository.observeAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createCategory(name: String) {
        viewModelScope.launch {
            repository.upsertCategory(SoundscapeCategory(name = name))
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            repository.softDeleteCategory(id)
        }
    }
}
