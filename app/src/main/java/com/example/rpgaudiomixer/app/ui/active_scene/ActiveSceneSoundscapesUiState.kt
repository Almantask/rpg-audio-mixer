package com.example.rpgaudiomixer.app.ui.active_scene

import com.example.rpgaudiomixer.domain.scene.SceneActiveSoundscape
import kotlinx.coroutines.flow.StateFlow

data class ActiveSceneSoundscapesUiState(
    val isLoading: Boolean = false,
    val soundscapes: List<SoundscapeItemState> = emptyList(),
    val masterVolume: Float = 1.0f,
    val errorMessage: String? = null
)

data class SoundscapeItemState(
    val soundscape: SceneActiveSoundscape,
    val isPlaying: StateFlow<Boolean>
)
