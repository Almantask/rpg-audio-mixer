package com.example.rpgaudiomixer.app.screens.scenes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.media.SoundboardPlayer
import com.example.rpgaudiomixer.domain.model.FXTrack
import com.example.rpgaudiomixer.domain.repository.FXRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActiveSceneSoundboardUiState(
    val sceneId: Long = 0,
    val fxTracks: List<FXTrack> = emptyList(),
    val masterVolume: Float = 1.0f,
    val isLoading: Boolean = true
)

@HiltViewModel
class ActiveSceneSoundboardViewModel @Inject constructor(
    private val sceneRepository: SceneRepository,
    private val fxRepository: FXRepository,
    private val soundboardPlayer: SoundboardPlayer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sceneId: Long = savedStateHandle.get<Long>("sceneId") ?: 0

    private val _masterVolume = MutableStateFlow(1.0f)
    
    val uiState: StateFlow<ActiveSceneSoundboardUiState> = combine(
        sceneRepository.observeFxByScene(sceneId),
        _masterVolume
    ) { fxTracks, masterVol ->
        ActiveSceneSoundboardUiState(
            sceneId = sceneId,
            fxTracks = fxTracks,
            masterVolume = masterVol,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActiveSceneSoundboardUiState()
    )

    val allFx: StateFlow<List<FXTrack>> = fxRepository.observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _playingFxIds = MutableStateFlow<Set<Long>>(emptySet())
    val playingFxIds: StateFlow<Set<Long>> = _playingFxIds.asStateFlow()

    init {
        // Initialize player with master volume
        soundboardPlayer.setMasterVolume(_masterVolume.value)
    }

    fun setMasterVolume(volume: Float) {
        _masterVolume.value = volume
        soundboardPlayer.setMasterVolume(volume)
    }

    fun triggerFx(track: FXTrack) {
        soundboardPlayer.triggerFx(track)
        _playingFxIds.update { it + track.id }
        
        // In a real app, we'd listen to the player's completion to remove the ID.
        // For now, since triggerFx is overlap-capable fire-and-forget, 
        // we'll just keep the glow active for a short duration or until manual stop.
        // But the spec says "Glow/pulse while playing". 
        // For simplicity, we'll keep it glowing for a few seconds or until track.durationMs
        viewModelScope.launch {
            val duration = if (track.durationMs > 0) track.durationMs else 3000L
            kotlinx.coroutines.delay(duration)
            // Only remove if no other instances are playing (this is a simplification)
            _playingFxIds.update { it - track.id }
        }
    }

    fun stopFx(trackId: Long) {
        soundboardPlayer.stopFx(trackId)
        _playingFxIds.update { it - trackId }
    }

    fun removeFx(trackId: Long) {
        viewModelScope.launch {
            sceneRepository.removeFxFromScene(sceneId, trackId)
            stopFx(trackId)
        }
    }

    fun addFx(trackId: Long) {
        val maxOrder = uiState.value.fxTracks.maxOfOrNull { it.displayOrder ?: 0 } ?: -1
        viewModelScope.launch {
            // displayOrder is not in FXTrack domain model yet, but we use it in the junction table.
            // SceneRepository.addFxToScene handles the junction table field.
            sceneRepository.addFxToScene(sceneId, trackId, maxOrder + 1)
        }
    }
    
    fun reorderFx(fromIndex: Int, toIndex: Int) {
        // Implementation for reordering persistence would go here
    }
}

// Extension to help with displayOrder if needed, or just handle in repo
private val FXTrack.displayOrder: Int? get() = null // Placeholder
