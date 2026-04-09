package com.example.rpgaudiomixer.app.ui.scenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.scene.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScenesViewModel @Inject constructor(
    private val repository: SceneRepository
) : ViewModel() {

    val uiState: StateFlow<ScenesUiState> = repository.observeAll()
        .map { ScenesUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ScenesUiState.Loading
        )

    fun createScene(name: String, description: String? = null, tags: List<String> = emptyList()) {
        viewModelScope.launch {
            val scene = Scene(
                name = name,
                description = description,
                tags = tags
            )
            repository.upsert(scene)
        }
    }

    fun deleteScene(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }
}
