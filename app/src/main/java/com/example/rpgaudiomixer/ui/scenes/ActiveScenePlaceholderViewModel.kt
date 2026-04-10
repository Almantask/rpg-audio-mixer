package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ActiveScenePlaceholderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository,
) : ViewModel() {

    private val sceneId: Long = checkNotNull(savedStateHandle[MainNavDestination.SCENE_ID_ARG])

    val autoplay: Boolean = savedStateHandle[MainNavDestination.AUTOPLAY_ARG] ?: false

    private val _scene = MutableStateFlow<Scene?>(null)
    val scene: StateFlow<Scene?> = _scene.asStateFlow()

    init {
        viewModelScope.launch {
            _scene.value = sceneRepository.getScene(sceneId)
        }
    }
}
