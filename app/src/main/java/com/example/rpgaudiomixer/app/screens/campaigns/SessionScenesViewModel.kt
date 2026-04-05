package com.example.rpgaudiomixer.app.screens.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionScenesViewModel @Inject constructor(
    private val repository: SceneRepository
) : ViewModel() {

    private val _sessionId = MutableStateFlow<Long>(-1)
    
    val sessionScenes: StateFlow<List<Scene>> = _sessionId
        .flatMapLatest { id ->
            repository.observeScenesBySession(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGlobalScenes: StateFlow<List<Scene>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSessionId(id: Long) {
        _sessionId.value = id
    }

    fun linkScene(sceneId: Long) {
        viewModelScope.launch {
            repository.linkToSession(_sessionId.value, sceneId)
        }
    }

    fun unlinkScene(sceneId: Long) {
        viewModelScope.launch {
            repository.unlinkFromSession(_sessionId.value, sceneId)
        }
    }
}
