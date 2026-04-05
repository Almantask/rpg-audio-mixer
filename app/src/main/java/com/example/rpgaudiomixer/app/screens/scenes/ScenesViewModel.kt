package com.example.rpgaudiomixer.app.screens.scenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScenesViewModel @Inject constructor(
    private val repository: SceneRepository
) : ViewModel() {

    val scenes: StateFlow<List<Scene>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createScene(name: String, description: String?, tags: List<String>) {
        viewModelScope.launch {
            repository.upsert(Scene(name = name, description = description, tags = tags))
        }
    }

    fun deleteScene(id: Long) {
        viewModelScope.launch {
            repository.softDelete(id)
        }
    }
}
